package com.example.data.remote

import android.util.Base64
import com.example.data.model.XtreamAuthResult
import com.example.data.model.XtreamCategoryDto
import com.example.data.model.XtreamEpgItem
import com.example.data.model.XtreamEpisode
import com.example.data.model.XtreamLinkParser
import com.example.data.model.XtreamLiveStreamDto
import com.example.data.model.XtreamSeriesDetail
import com.example.data.model.XtreamSeriesDto
import com.example.data.model.XtreamVodInfo
import com.example.data.model.XtreamVodStreamDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class XtreamApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

class XtreamClient {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    suspend fun authenticate(
        serverUrl: String,
        username: String,
        password: String
    ): XtreamAuthResult = withContext(Dispatchers.IO) {
        val body = executeAction(serverUrl, username, password, action = null)
        val json = runCatching { JSONObject(body) }.getOrNull()
            ?: throw XtreamApiException("Sunucudan geçersiz yanıt alındı. Xtream adresini kontrol edin.")

        val userInfo = json.optJSONObject("user_info")
            ?: throw XtreamApiException("Xtream kullanıcı bilgisi bulunamadı. Giriş bilgilerini doğrulayın.")

        val authFlag = userInfo.optString("auth", "0")
        val status = userInfo.optString("status", "Inactive")
        if (authFlag == "0") {
            val msg = userInfo.optString("message", "Kullanıcı adı veya şifre hatalı.")
            throw XtreamApiException(msg.ifBlank { "Kullanıcı adı veya şifre hatalı." })
        }

        val serverInfo = json.optJSONObject("server_info")
        val resolvedServerUrl = XtreamLinkParser.normalizeServerUrl(serverUrl)
        val timezone = serverInfo?.optString("timezone", "") ?: ""

        XtreamAuthResult(
            status = status,
            expDate = userInfo.optString("exp_date", "").toLongOrNull(),
            isTrial = userInfo.optString("is_trial", "0") == "1",
            activeCons = userInfo.optString("active_cons", "0").toIntOrNull() ?: 0,
            maxConnections = userInfo.optString("max_connections", "1").toIntOrNull() ?: 1,
            serverUrl = resolvedServerUrl,
            timezone = timezone,
            message = userInfo.optString("message", "")
        )
    }

    suspend fun getLiveCategories(
        serverUrl: String,
        username: String,
        password: String
    ): List<XtreamCategoryDto> = fetchCategories(serverUrl, username, password, "get_live_categories")

    suspend fun getVodCategories(
        serverUrl: String,
        username: String,
        password: String
    ): List<XtreamCategoryDto> = fetchCategories(serverUrl, username, password, "get_vod_categories")

    suspend fun getSeriesCategories(
        serverUrl: String,
        username: String,
        password: String
    ): List<XtreamCategoryDto> = fetchCategories(serverUrl, username, password, "get_series_categories")

    private suspend fun fetchCategories(
        serverUrl: String,
        username: String,
        password: String,
        action: String
    ): List<XtreamCategoryDto> = withContext(Dispatchers.IO) {
        val body = executeAction(serverUrl, username, password, action)
        val array = runCatching { JSONArray(body) }.getOrNull() ?: return@withContext emptyList()
        buildList(array.length()) {
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.optString("category_id", "").trim()
                val name = obj.optString("category_name", "").trim()
                if (id.isNotEmpty() && name.isNotEmpty()) {
                    add(XtreamCategoryDto(categoryId = id, categoryName = name))
                }
            }
        }
    }

    suspend fun getLiveStreams(
        serverUrl: String,
        username: String,
        password: String
    ): List<XtreamLiveStreamDto> = withContext(Dispatchers.IO) {
        val body = executeAction(serverUrl, username, password, "get_live_streams")
        val array = runCatching { JSONArray(body) }.getOrNull() ?: return@withContext emptyList()
        buildList(array.length()) {
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val streamId = obj.optInt("stream_id", -1)
                if (streamId <= 0) continue
                add(
                    XtreamLiveStreamDto(
                        streamId = streamId,
                        num = obj.optInt("num", i + 1),
                        name = obj.optString("name", "Kanal $streamId").trim(),
                        streamIcon = obj.optString("stream_icon", "").trim(),
                        epgChannelId = obj.optString("epg_channel_id", "").trim(),
                        categoryId = obj.optString("category_id", "0").trim()
                    )
                )
            }
        }
    }

    suspend fun getVodStreams(
        serverUrl: String,
        username: String,
        password: String
    ): List<XtreamVodStreamDto> = withContext(Dispatchers.IO) {
        val body = executeAction(serverUrl, username, password, "get_vod_streams")
        val array = runCatching { JSONArray(body) }.getOrNull() ?: return@withContext emptyList()
        buildList(array.length()) {
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val streamId = obj.optInt("stream_id", -1)
                if (streamId <= 0) continue
                add(
                    XtreamVodStreamDto(
                        streamId = streamId,
                        name = obj.optString("name", "Film $streamId").trim(),
                        streamIcon = obj.optString("stream_icon", "").trim(),
                        categoryId = obj.optString("category_id", "0").trim(),
                        containerExtension = obj.optString("container_extension", "mp4")
                            .ifBlank { "mp4" },
                        rating = obj.optString("rating", "").trim(),
                        added = obj.optString("added", "0").toLongOrNull() ?: 0L
                    )
                )
            }
        }
    }

    suspend fun getVodInfo(
        serverUrl: String,
        username: String,
        password: String,
        vodId: Int
    ): XtreamVodInfo? = withContext(Dispatchers.IO) {
        val body = executeAction(
            serverUrl,
            username,
            password,
            "get_vod_info",
            extraParams = mapOf("vod_id" to vodId.toString())
        )
        val root = runCatching { JSONObject(body) }.getOrNull() ?: return@withContext null
        val info = root.optJSONObject("info") ?: JSONObject()
        val movieData = root.optJSONObject("movie_data") ?: JSONObject()
        val backdrops = info.optJSONArray("backdrop_path")
        val firstBackdrop = if (backdrops != null && backdrops.length() > 0) {
            backdrops.optString(0, "")
        } else ""

        XtreamVodInfo(
            streamId = movieData.optInt("stream_id", vodId),
            name = movieData.optString("name", info.optString("name", "")).trim(),
            posterUrl = info.optString("movie_image", info.optString("cover_big", "")).trim(),
            backdropUrl = firstBackdrop.trim(),
            plot = info.optString("plot", info.optString("description", "")).trim(),
            genre = info.optString("genre", "").trim(),
            director = info.optString("director", "").trim(),
            cast = info.optString("cast", info.optString("actors", "")).trim(),
            releaseDate = info.optString("releasedate", info.optString("release_date", "")).trim(),
            duration = info.optString("duration", "").trim(),
            rating = info.optString("rating", "").trim(),
            containerExtension = movieData.optString("container_extension", "mp4").ifBlank { "mp4" }
        )
    }

    suspend fun getSeries(
        serverUrl: String,
        username: String,
        password: String
    ): List<XtreamSeriesDto> = withContext(Dispatchers.IO) {
        val body = executeAction(serverUrl, username, password, "get_series")
        val array = runCatching { JSONArray(body) }.getOrNull() ?: return@withContext emptyList()
        buildList(array.length()) {
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val seriesId = obj.optInt("series_id", -1)
                if (seriesId <= 0) continue
                add(
                    XtreamSeriesDto(
                        seriesId = seriesId,
                        name = obj.optString("name", "Dizi $seriesId").trim(),
                        cover = obj.optString("cover", "").trim(),
                        plot = obj.optString("plot", "").trim(),
                        cast = obj.optString("cast", "").trim(),
                        genre = obj.optString("genre", "").trim(),
                        releaseDate = obj.optString("releaseDate", obj.optString("release_date", "")).trim(),
                        rating = obj.optString("rating", "").trim(),
                        categoryId = obj.optString("category_id", "0").trim()
                    )
                )
            }
        }
    }

    suspend fun getSeriesInfo(
        serverUrl: String,
        username: String,
        password: String,
        seriesId: Int
    ): XtreamSeriesDetail? = withContext(Dispatchers.IO) {
        val body = executeAction(
            serverUrl,
            username,
            password,
            "get_series_info",
            extraParams = mapOf("series_id" to seriesId.toString())
        )
        val root = runCatching { JSONObject(body) }.getOrNull() ?: return@withContext null
        val info = root.optJSONObject("info") ?: JSONObject()
        val backdrops = info.optJSONArray("backdrop_path")
        val firstBackdrop = if (backdrops != null && backdrops.length() > 0) {
            backdrops.optString(0, "")
        } else ""

        val episodesMap = linkedMapOf<Int, List<XtreamEpisode>>()
        val episodesJson = root.optJSONObject("episodes")
        if (episodesJson != null) {
            val keys = episodesJson.keys()
            val sortedSeasons = mutableListOf<Int>()
            while (keys.hasNext()) {
                keys.next().toIntOrNull()?.let { sortedSeasons.add(it) }
            }
            sortedSeasons.sort()
            for (seasonNumber in sortedSeasons) {
                val epArray = episodesJson.optJSONArray(seasonNumber.toString()) ?: continue
                val episodes = buildList {
                    for (i in 0 until epArray.length()) {
                        val epObj = epArray.optJSONObject(i) ?: continue
                        val epId = epObj.optString("id", "").trim()
                        if (epId.isEmpty()) continue
                        val epInfo = epObj.optJSONObject("info")
                        add(
                            XtreamEpisode(
                                id = epId,
                                episodeNum = epObj.optInt("episode_num", i + 1),
                                seasonNum = epObj.optInt("season", seasonNumber),
                                title = epObj.optString("title", "Bölüm ${i + 1}").trim(),
                                containerExtension = epObj.optString("container_extension", "mp4")
                                    .ifBlank { "mp4" },
                                duration = epInfo?.optString("duration", "") ?: "",
                                plot = epInfo?.optString("plot", "") ?: "",
                                coverUrl = epInfo?.optString("movie_image", "") ?: ""
                            )
                        )
                    }
                }.sortedBy { it.episodeNum }
                if (episodes.isNotEmpty()) {
                    episodesMap[seasonNumber] = episodes
                }
            }
        }

        XtreamSeriesDetail(
            seriesId = seriesId,
            name = info.optString("name", "").trim(),
            coverUrl = info.optString("cover", "").trim(),
            backdropUrl = firstBackdrop.trim(),
            plot = info.optString("plot", "").trim(),
            cast = info.optString("cast", "").trim(),
            genre = info.optString("genre", "").trim(),
            releaseDate = info.optString("releaseDate", info.optString("release_date", "")).trim(),
            rating = info.optString("rating", "").trim(),
            seasons = episodesMap
        )
    }

    suspend fun getShortEpg(
        serverUrl: String,
        username: String,
        password: String,
        streamId: Int,
        limit: Int = 4
    ): List<XtreamEpgItem> = withContext(Dispatchers.IO) {
        val body = runCatching {
            executeAction(
                serverUrl,
                username,
                password,
                "get_short_epg",
                extraParams = mapOf(
                    "stream_id" to streamId.toString(),
                    "limit" to limit.toString()
                )
            )
        }.getOrNull() ?: return@withContext emptyList()

        val root = runCatching { JSONObject(body) }.getOrNull() ?: return@withContext emptyList()
        val list = root.optJSONArray("epg_listings") ?: return@withContext emptyList()
        buildList {
            for (i in 0 until list.length()) {
                val obj = list.optJSONObject(i) ?: continue
                add(
                    XtreamEpgItem(
                        id = obj.optString("id", i.toString()),
                        title = decodeBase64Safe(obj.optString("title", "")),
                        description = decodeBase64Safe(obj.optString("description", "")),
                        start = obj.optString("start", ""),
                        end = obj.optString("end", "")
                    )
                )
            }
        }
    }

    private fun decodeBase64Safe(raw: String): String {
        if (raw.isBlank()) return ""
        return runCatching {
            val decoded = Base64.decode(raw, Base64.DEFAULT)
            val str = String(decoded, Charsets.UTF_8).trim()
            if (str.any { it.isLetterOrDigit() }) str else raw
        }.getOrDefault(raw)
    }

    private fun executeAction(
        serverUrl: String,
        username: String,
        password: String,
        action: String?,
        extraParams: Map<String, String> = emptyMap()
    ): String {
        val normalized = XtreamLinkParser.normalizeServerUrl(serverUrl)
        val baseUrl = "$normalized/player_api.php".toHttpUrlOrNull()
            ?: throw XtreamApiException("Geçersiz sunucu adresi: $serverUrl")

        val urlBuilder = baseUrl.newBuilder()
            .addQueryParameter("username", username.trim())
            .addQueryParameter("password", password.trim())
        if (!action.isNullOrBlank()) {
            urlBuilder.addQueryParameter("action", action)
        }
        extraParams.forEach { (k, v) -> urlBuilder.addQueryParameter(k, v) }

        val request = Request.Builder()
            .url(urlBuilder.build())
            .header("User-Agent", "NovaTV/1.0 (Android TV)")
            .get()
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    throw XtreamApiException(
                        "Sunucu hatası (HTTP ${response.code}). Panel bilgilerini kontrol edin."
                    )
                }
                if (responseBody.isBlank()) {
                    throw XtreamApiException("Sunucudan boş yanıt döndü.")
                }
                return responseBody
            }
        } catch (e: IOException) {
            throw XtreamApiException(
                "Sunucuya bağlanılamadı. İnternet ve sunucu adresini kontrol edin.",
                e
            )
        }
    }
}
