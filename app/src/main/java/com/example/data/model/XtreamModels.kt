package com.example.data.model

import android.net.Uri

enum class ContentType(val dbValue: String) {
    LIVE("LIVE"),
    VOD("VOD"),
    SERIES("SERIES");

    companion object {
        fun fromDb(value: String): ContentType =
            entries.firstOrNull { it.dbValue == value } ?: LIVE
    }
}

enum class VodSortOption(val label: String) {
    DEFAULT("Varsayılan"),
    NAME_ASC("İsme Göre (A-Z)"),
    RATING_DESC("Puana Göre (Yüksek)"),
    RECENT_DESC("En Son Eklenen")
}

data class XtreamAuthResult(
    val status: String,
    val expDate: Long?,
    val isTrial: Boolean,
    val activeCons: Int,
    val maxConnections: Int,
    val serverUrl: String,
    val timezone: String,
    val message: String
)

data class XtreamCategoryDto(
    val categoryId: String,
    val categoryName: String
)

data class XtreamLiveStreamDto(
    val streamId: Int,
    val num: Int,
    val name: String,
    val streamIcon: String,
    val epgChannelId: String,
    val categoryId: String
)

data class XtreamVodStreamDto(
    val streamId: Int,
    val name: String,
    val streamIcon: String,
    val categoryId: String,
    val containerExtension: String,
    val rating: String,
    val added: Long
)

data class XtreamVodInfo(
    val streamId: Int,
    val name: String,
    val posterUrl: String,
    val backdropUrl: String,
    val plot: String,
    val genre: String,
    val director: String,
    val cast: String,
    val releaseDate: String,
    val duration: String,
    val rating: String,
    val containerExtension: String
)

data class XtreamSeriesDto(
    val seriesId: Int,
    val name: String,
    val cover: String,
    val plot: String,
    val cast: String,
    val genre: String,
    val releaseDate: String,
    val rating: String,
    val categoryId: String
)

data class XtreamEpisode(
    val id: String,
    val episodeNum: Int,
    val seasonNum: Int,
    val title: String,
    val containerExtension: String,
    val duration: String,
    val plot: String,
    val coverUrl: String
)

data class XtreamSeriesDetail(
    val seriesId: Int,
    val name: String,
    val coverUrl: String,
    val backdropUrl: String,
    val plot: String,
    val cast: String,
    val genre: String,
    val releaseDate: String,
    val rating: String,
    val seasons: Map<Int, List<XtreamEpisode>>
)

data class XtreamEpgItem(
    val id: String,
    val title: String,
    val description: String,
    val start: String,
    val end: String
)

data class ParsedXtreamCredentials(
    val serverUrl: String,
    val username: String,
    val password: String,
    val suggestedName: String = ""
)

data class PlaybackRequest(
    val profileId: Long,
    val contentType: ContentType,
    val streamId: Int,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val streamUrl: String,
    val containerExtension: String,
    val startPositionMs: Long = 0L,
    val liveChannelIndex: Int = -1
)

object DefaultXtreamPreset {
    const val NAME = "SANAZ"
    const val SERVER_URL = "http://188.72.5.30:8080"
    const val USERNAME = "alsard"
    const val PASSWORD = "555666"
}

object XtreamLinkParser {
    fun normalizeServerUrl(rawUrl: String): String {
        var trimmed = rawUrl.trim()
        if (trimmed.isEmpty()) return ""
        trimmed = when {
            trimmed.startsWith("https://", ignoreCase = true) ->
                "https://" + trimmed.substring(8)
            trimmed.startsWith("http://", ignoreCase = true) ->
                "http://" + trimmed.substring(7)
            else -> "http://$trimmed"
        }
        return trimmed.trimEnd('/')
    }

    /**
     * Parses pasted M3U URLs, player_api.php links, or multi-line Xtream credential blocks.
     */
    fun parseSmartInput(rawText: String): ParsedXtreamCredentials? {
        val text = rawText.trim()
        if (text.isBlank()) return null

        // Case 1: Full URL with query params (get.php or player_api.php)
        val urlRegex = Regex("""https?://[^\s"']+""", RegexOption.IGNORE_CASE)
        val urlMatch = urlRegex.find(text)
        if (urlMatch != null) {
            val matchedUrl = urlMatch.value
            runCatching {
                val uri = Uri.parse(matchedUrl)
                val user = uri.getQueryParameter("username") ?: uri.getQueryParameter("user")
                val pass = uri.getQueryParameter("password") ?: uri.getQueryParameter("pass")
                val scheme = uri.scheme ?: "http"
                val host = uri.host
                val portPart = if (uri.port != -1) ":${uri.port}" else ""
                if (!host.isNullOrBlank() && !user.isNullOrBlank() && !pass.isNullOrBlank()) {
                    return ParsedXtreamCredentials(
                        serverUrl = "$scheme://$host$portPart",
                        username = user.trim(),
                        password = pass.trim(),
                        suggestedName = host
                    )
                }
            }
        }

        // Case 2: Multi-line key/value text (e.g. from provider message)
        val hostLine = Regex(
            """(?:url|server|host|sunucu|adres|portal)\s*[:=]\s*([^\s\n]+)""",
            RegexOption.IGNORE_CASE
        ).find(text)?.groupValues?.getOrNull(1)
        val userLine = Regex(
            """(?:username|user|kullan[ıi]c[ıi](?:\s*ad[ıi])?)\s*[:=]\s*([^\s\n]+)""",
            RegexOption.IGNORE_CASE
        ).find(text)?.groupValues?.getOrNull(1)
        val passLine = Regex(
            """(?:password|pass|[şs]ifre|parola)\s*[:=]\s*([^\s\n]+)""",
            RegexOption.IGNORE_CASE
        ).find(text)?.groupValues?.getOrNull(1)

        if (!hostLine.isNullOrBlank() && !userLine.isNullOrBlank() && !passLine.isNullOrBlank()) {
            val normalized = normalizeServerUrl(hostLine)
            val suggested = runCatching { Uri.parse(normalized).host ?: "" }.getOrDefault("")
            return ParsedXtreamCredentials(
                serverUrl = normalized,
                username = userLine.trim(),
                password = passLine.trim(),
                suggestedName = suggested
            )
        }

        // Case 3: Just a plain server URL without query credentials
        if (urlMatch != null) {
            val uri = runCatching { Uri.parse(urlMatch.value) }.getOrNull()
            if (uri != null && !uri.host.isNullOrBlank()) {
                val portPart = if (uri.port != -1) ":${uri.port}" else ""
                val scheme = uri.scheme ?: "http"
                return ParsedXtreamCredentials(
                    serverUrl = "$scheme://${uri.host}$portPart",
                    username = "",
                    password = "",
                    suggestedName = uri.host ?: ""
                )
            }
        }

        return null
    }
}
