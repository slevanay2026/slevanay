package com.example.data.model

data class OttPlaylistEntry(
    val id: String,
    val name: String,
    val url: String
)

data class OttChannelItem(
    val name: String,
    val group: String,
    val logoUrl: String,
    val streamUrl: String
)

object OttM3uParser {
    private val tvgNameRegex = Regex("""tvg-name\s*=\s*"([^"]*)"""", RegexOption.IGNORE_CASE)
    private val tvgLogoRegex = Regex("""tvg-logo\s*=\s*"([^"]*)"""", RegexOption.IGNORE_CASE)
    private val groupTitleRegex = Regex("""group-title\s*=\s*"([^"]*)"""", RegexOption.IGNORE_CASE)

    fun parseM3uContent(rawM3u: String): List<OttChannelItem> {
        val cleaned = rawM3u.removePrefix("\uFEFF")
        val lines = cleaned.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toList()

        val result = mutableListOf<OttChannelItem>()
        var pendingName = ""
        var pendingGroup = ""
        var pendingLogo = ""

        for (line in lines) {
            when {
                line.startsWith("#EXTM3U", ignoreCase = true) -> Unit
                line.startsWith("#EXTINF:", ignoreCase = true) -> {
                    val commaIdx = line.indexOf(',')
                    val meta = if (commaIdx >= 0) line.substring(0, commaIdx) else line
                    val trailingTitle = if (commaIdx >= 0) line.substring(commaIdx + 1).trim() else ""
                    val tvgName = tvgNameRegex.find(meta)?.groupValues?.getOrNull(1)?.trim().orEmpty()
                    pendingName = trailingTitle.ifBlank { tvgName }
                    pendingGroup = groupTitleRegex.find(meta)?.groupValues?.getOrNull(1)?.trim().orEmpty()
                    pendingLogo = tvgLogoRegex.find(meta)?.groupValues?.getOrNull(1)?.trim().orEmpty()
                }
                line.startsWith("#EXTGRP:", ignoreCase = true) -> {
                    val grp = line.substringAfter(':', "").trim()
                    if (pendingGroup.isBlank() && grp.isNotBlank()) {
                        pendingGroup = grp
                    }
                }
                line.startsWith("#") -> Unit
                line.startsWith("http://", ignoreCase = true) ||
                    line.startsWith("https://", ignoreCase = true) ||
                    line.startsWith("rtmp://", ignoreCase = true) ||
                    line.startsWith("rtsp://", ignoreCase = true) -> {
                    val fallbackName = line.substringAfterLast('/').substringBefore('?').ifBlank { "OTT Stream" }
                    result.add(
                        OttChannelItem(
                            name = pendingName.ifBlank { fallbackName },
                            group = pendingGroup.ifBlank { "OTT" },
                            logoUrl = pendingLogo,
                            streamUrl = line
                        )
                    )
                    pendingName = ""
                    pendingGroup = ""
                    pendingLogo = ""
                }
            }
        }
        return result
    }

    fun inferExtension(streamUrl: String): String {
        val path = streamUrl.substringBefore('?').lowercase()
        return when {
            path.endsWith(".m3u8") -> "m3u8"
            path.endsWith(".mp4") -> "mp4"
            path.endsWith(".mkv") -> "mkv"
            path.endsWith(".avi") -> "avi"
            else -> "ts"
        }
    }

    fun isLikelyPlaylistUrl(url: String): Boolean {
        val lower = url.lowercase()
        return lower.contains(".m3u") && !lower.contains(".m3u8") ||
            lower.contains("get.php") ||
            lower.contains("type=m3u")
    }
}
