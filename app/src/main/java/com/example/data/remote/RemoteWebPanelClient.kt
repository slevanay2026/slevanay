package com.example.data.remote

import com.example.data.model.OttM3uParser
import com.example.data.model.XtreamLinkParser
import com.example.data.repository.FreeKurdishChannel
import com.example.data.repository.FreeKurdishDialect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

data class RemoteXtreamConfig(
    val name: String,
    val serverUrl: String,
    val username: String,
    val password: String
)

data class RemoteWebsitePanelPayload(
    val panelTitle: String? = null,
    val announcement: String? = null,
    val themeId: String? = null,
    val xtreamConfig: RemoteXtreamConfig? = null,
    val channels: List<FreeKurdishChannel> = emptyList()
)

object RemoteWebPanelParser {
    const val SAMPLE_PANEL_JSON_TEMPLATE: String = """{
  "panelTitle": "JHE HAKAR Web Panel",
  "announcement": "Hoş geldiniz — Yayın listesi güncellendi!",
  "theme": "cyber_obsidian",
  "xtream": {
    "name": "Ana Sunucu",
    "serverUrl": "http://example.com:8080",
    "username": "demo_user",
    "password": "demo_password"
  },
  "channels": [
    {
      "name": "Rudaw TV",
      "url": "https://svs.itworkscdn.net/rudawlive/rudawlive.smil/playlist.m3u8",
      "category": "Haber",
      "dialect": "sorani",
      "logo": ""
    }
  ]
}"""

    fun parseWebsitePayload(rawBody: String, sourceUrl: String): RemoteWebsitePanelPayload {
        val trimmed = rawBody.removePrefix("\uFEFF").trim()
        if (trimmed.isEmpty()) {
            throw IOException("Web sitesinden boş yanıt döndü.")
        }

        // 1. JSON Panel format
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            return if (trimmed.startsWith("[")) {
                parseChannelsArrayPayload(JSONArray(trimmed))
            } else {
                parseJsonObjectPayload(JSONObject(trimmed))
            }
        }

        // 2. Direct M3U / M3U8 playlist hosted on website
        if (trimmed.contains("#EXTINF", ignoreCase = true) || trimmed.startsWith("#EXTM3U", ignoreCase = true)) {
            val m3uChannels = OttM3uParser.parseM3uContent(trimmed).map { item ->
                val cleanCat = item.group.takeIf { it.isNotBlank() && !it.equals("OTT", ignoreCase = true) } ?: "Web Panel"
                FreeKurdishChannel(
                    id = "web_${item.streamUrl.trim().lowercase()}",
                    name = item.name.trim(),
                    category = cleanCat,
                    dialect = FreeKurdishDialect.GENERAL,
                    logoUrl = item.logoUrl.trim(),
                    streamUrl = item.streamUrl.trim(),
                    containerExtension = OttM3uParser.inferExtension(item.streamUrl)
                )
            }
            if (m3uChannels.isNotEmpty()) {
                return RemoteWebsitePanelPayload(
                    panelTitle = sourceUrl.substringAfter("://").substringBefore('/'),
                    channels = m3uChannels
                )
            }
        }

        // 3. Xtream link / credentials embedded on a simple webpage or text file
        val parsedXtream = XtreamLinkParser.parseSmartInput(trimmed)
        if (parsedXtream != null && parsedXtream.username.isNotBlank() && parsedXtream.password.isNotBlank()) {
            return RemoteWebsitePanelPayload(
                panelTitle = parsedXtream.suggestedName.ifBlank { "Web Xtream" },
                xtreamConfig = RemoteXtreamConfig(
                    name = parsedXtream.suggestedName.ifBlank { "Web Panel" },
                    serverUrl = parsedXtream.serverUrl,
                    username = parsedXtream.username,
                    password = parsedXtream.password
                )
            )
        }

        throw IOException("Web sitesi içeriği JSON panel, M3U listesi veya Xtream bağlantısı olarak tanınamadı.")
    }

    private fun parseJsonObjectPayload(root: JSONObject): RemoteWebsitePanelPayload {
        val title = root.optString("panelTitle").ifBlank {
            root.optString("title").ifBlank { null }
        }
        val announcement = root.optString("announcement").ifBlank {
            root.optString("message").ifBlank { null }
        }
        val themeId = root.optString("theme").ifBlank { null }

        val xtreamObj = root.optJSONObject("xtream") ?: root.optJSONObject("portal")
        val xtreamConfig = xtreamObj?.let { obj ->
            val url = XtreamLinkParser.normalizeServerUrl(
                obj.optString("serverUrl").ifBlank { obj.optString("url").ifBlank { obj.optString("host") } }
            )
            val user = obj.optString("username").ifBlank { obj.optString("user") }.trim()
            val pass = obj.optString("password").ifBlank { obj.optString("pass") }.trim()
            val name = obj.optString("name").ifBlank { "Web Panel" }.trim()
            if (url.isNotBlank() && user.isNotBlank() && pass.isNotBlank()) {
                RemoteXtreamConfig(name = name, serverUrl = url, username = user, password = pass)
            } else {
                null
            }
        }

        val channelsArr = root.optJSONArray("channels") ?: root.optJSONArray("streams")
        val channels = if (channelsArr != null) parseChannelsList(channelsArr) else emptyList()

        return RemoteWebsitePanelPayload(
            panelTitle = title,
            announcement = announcement,
            themeId = themeId,
            xtreamConfig = xtreamConfig,
            channels = channels
        )
    }

    private fun parseChannelsArrayPayload(array: JSONArray): RemoteWebsitePanelPayload {
        return RemoteWebsitePanelPayload(channels = parseChannelsList(array))
    }

    private fun parseChannelsList(array: JSONArray): List<FreeKurdishChannel> {
        val result = mutableListOf<FreeKurdishChannel>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val name = obj.optString("name").ifBlank { obj.optString("title") }.trim()
            val url = obj.optString("url").ifBlank { obj.optString("streamUrl") }.trim()
            if (name.isBlank() || url.isBlank()) continue
            val category = obj.optString("category").ifBlank { "Web Panel" }.trim()
            val logo = obj.optString("logo").ifBlank { obj.optString("logoUrl") }.trim()
            val dialectStr = obj.optString("dialect").lowercase()
            val dialect = when (dialectStr) {
                "zaxo", "zakho", "badinan", "badînan" -> FreeKurdishDialect.ZAXO_BADINAN
                "sorani", "ckb" -> FreeKurdishDialect.SORANI
                "kurmanji", "badini", "kmr" -> FreeKurdishDialect.KURMANJI
                else -> FreeKurdishDialect.GENERAL
            }
            result.add(
                FreeKurdishChannel(
                    id = "web_${url.lowercase()}",
                    name = name,
                    category = category,
                    dialect = dialect,
                    logoUrl = logo,
                    streamUrl = url,
                    containerExtension = OttM3uParser.inferExtension(url),
                    isDirectStream = true
                )
            )
        }
        return result
    }

    fun serializeCustomChannels(channels: List<FreeKurdishChannel>): String {
        val arr = JSONArray()
        channels.forEach { ch ->
            arr.put(
                JSONObject().apply {
                    put("name", ch.name)
                    put("url", ch.streamUrl)
                    put("category", ch.category)
                    put("dialect", ch.dialect.id)
                    put("logo", ch.logoUrl)
                }
            )
        }
        return arr.toString()
    }

    fun deserializeCustomChannels(rawJson: String): List<FreeKurdishChannel> {
        if (rawJson.isBlank()) return emptyList()
        return runCatching {
            parseChannelsList(JSONArray(rawJson))
        }.getOrDefault(emptyList())
    }
}

class RemoteWebPanelClient(
    private val okHttpClient: OkHttpClient
) {
    suspend fun fetchPanelFromWebsite(websiteUrl: String): RemoteWebsitePanelPayload = withContext(Dispatchers.IO) {
        val normalizedUrl = normalizeWebsiteUrl(websiteUrl)
        val request = Request.Builder()
            .url(normalizedUrl)
            .header("User-Agent", "JheHakarTV/1.0 (Android TV)")
            .header("Accept", "application/json, text/plain, */*")
            .build()
        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Web sitesi HTTP ${response.code} hatası döndürdü.")
            }
            val body = response.body?.string().orEmpty()
            RemoteWebPanelParser.parseWebsitePayload(body, normalizedUrl)
        }
    }

    companion object {
        fun normalizeWebsiteUrl(rawUrl: String): String {
            val trimmed = rawUrl.trim()
            if (trimmed.isBlank()) throw IOException("Web sitesi adresi boş olamaz.")
            return if (trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true)
            ) {
                trimmed
            } else {
                "https://$trimmed"
            }
        }
    }
}
