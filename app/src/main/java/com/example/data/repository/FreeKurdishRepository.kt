package com.example.data.repository

import com.example.data.model.OttM3uParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

enum class FreeKurdishDialect(val id: String) {
    ALL("all"),
    ZAXO_BADINAN("zaxo"),
    SORANI("sorani"),
    KURMANJI("kurmanji"),
    GENERAL("general")
}

data class FreeKurdishChannel(
    val id: String,
    val name: String,
    val category: String,
    val dialect: FreeKurdishDialect,
    val logoUrl: String,
    val streamUrl: String,
    val containerExtension: String,
    val isDirectStream: Boolean = false
)

sealed interface FreeKurdishUiState {
    data object Idle : FreeKurdishUiState
    data object Loading : FreeKurdishUiState
    data class Success(
        val channels: List<FreeKurdishChannel>,
        val noticeMessage: String? = null
    ) : FreeKurdishUiState
    data class Error(val message: String) : FreeKurdishUiState
}

data class KurdishFeedSource(
    val dialect: FreeKurdishDialect,
    val url: String,
    val filterKurdishOnly: Boolean = false
)

object FreeKurdishFeedMerger {
    val FEED_SOURCES: List<KurdishFeedSource> = listOf(
        KurdishFeedSource(
            dialect = FreeKurdishDialect.SORANI,
            url = "https://iptv-org.github.io/iptv/languages/ckb.m3u"
        ),
        KurdishFeedSource(
            dialect = FreeKurdishDialect.KURMANJI,
            url = "https://iptv-org.github.io/iptv/languages/kmr.m3u"
        ),
        KurdishFeedSource(
            dialect = FreeKurdishDialect.GENERAL,
            url = "https://iptv-org.github.io/iptv/languages/kur.m3u"
        ),
        KurdishFeedSource(
            dialect = FreeKurdishDialect.GENERAL,
            url = "https://iptv-org.github.io/iptv/countries/iq.m3u",
            filterKurdishOnly = true
        ),
        KurdishFeedSource(
            dialect = FreeKurdishDialect.GENERAL,
            url = "https://raw.githubusercontent.com/Free-TV/IPTV/master/playlists/playlist_iraq.m3u8",
            filterKurdishOnly = true
        ),
        KurdishFeedSource(
            dialect = FreeKurdishDialect.KURMANJI,
            url = "https://raw.githubusercontent.com/Free-TV/IPTV/master/playlists/playlist_turkey.m3u8",
            filterKurdishOnly = true
        )
    )

    // Verified publicly available free-to-air Kurdish HLS broadcasts
    val CURATED_PUBLIC_KURDISH_STREAMS: List<FreeKurdishChannel> = listOf(
        FreeKurdishChannel(
            id = "curated_rudaw_tv",
            name = "Rudaw TV",
            category = "News",
            dialect = FreeKurdishDialect.SORANI,
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/Rudaw_Media_Network_logo.svg/512px-Rudaw_Media_Network_logo.svg.png",
            streamUrl = "https://svs.itworkscdn.net/rudawlive/rudawlive.smil/playlist.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_rudaw_master",
            name = "Rudaw TV HD (Master)",
            category = "News",
            dialect = FreeKurdishDialect.SORANI,
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/Rudaw_Media_Network_logo.svg/512px-Rudaw_Media_Network_logo.svg.png",
            streamUrl = "https://live.rudaw.net/hls/rudaw-tv/master.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_kurdistan_24",
            name = "Kurdistan 24 HD",
            category = "News",
            dialect = FreeKurdishDialect.SORANI,
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Kurdistan_24_logo.svg/512px-Kurdistan_24_logo.svg.png",
            streamUrl = "https://d1x82nydcxndze.cloudfront.net/live/index.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_trt_kurdi",
            name = "TRT Kurdî",
            category = "General",
            dialect = FreeKurdishDialect.KURMANJI,
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/TRT_Kurd%C3%AE_logo.svg/512px-TRT_Kurd%C3%AE_logo.svg.png",
            streamUrl = "https://tv-trtkurdi.medya.trt.com.tr/master.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_waar_tv",
            name = "WAAR TV (Zaxo / Badînî)",
            category = "Zaxo & Badînan",
            dialect = FreeKurdishDialect.ZAXO_BADINAN,
            logoUrl = "",
            streamUrl = "https://live.kwikmotion.com/waarmedialive/waarmedia.smil/playlist.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_duhok_tv",
            name = "Duhok TV (Zaxo / Badînan)",
            category = "Zaxo & Badînan",
            dialect = FreeKurdishDialect.ZAXO_BADINAN,
            logoUrl = "",
            streamUrl = "https://heny007.github.io/hls/duhok.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_kurdistan_tv",
            name = "Kurdistan TV",
            category = "General",
            dialect = FreeKurdishDialect.SORANI,
            logoUrl = "",
            streamUrl = "https://5a3ed7a72ed4b.streamlock.net/live/SMIL:myStream.smil/playlist.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_ava_tv",
            name = "AVA Entertainment TV (Zaxo / Duhok)",
            category = "Zaxo & Badînan",
            dialect = FreeKurdishDialect.ZAXO_BADINAN,
            logoUrl = "",
            streamUrl = "https://familyhls.avatv.live/hls/stream.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_zarok_tv",
            name = "Zarok TV (Kids)",
            category = "Kids",
            dialect = FreeKurdishDialect.KURMANJI,
            logoUrl = "",
            streamUrl = "https://zindikurmanci.zaroktv.com.tr/hls/stream.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_kurdmax",
            name = "KurdMax Show",
            category = "Entertainment",
            dialect = FreeKurdishDialect.SORANI,
            logoUrl = "",
            streamUrl = "https://6476e46b58f91.streamlock.net/liveTrans/KURDB3388/chunklist_w2060950536.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_nrt_tv",
            name = "NRT TV",
            category = "News",
            dialect = FreeKurdishDialect.SORANI,
            logoUrl = "",
            streamUrl = "https://stream.nrttv.com/hls/stream.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_kurdsat",
            name = "Kurdsat TV",
            category = "General",
            dialect = FreeKurdishDialect.SORANI,
            logoUrl = "",
            streamUrl = "https://hlspackager.akamaized.net/live/DB/KURDSAT_HD/HLS/KURDSAT_HD.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_kurdsat_news",
            name = "Kurdsat News",
            category = "News",
            dialect = FreeKurdishDialect.SORANI,
            logoUrl = "",
            streamUrl = "https://hlspackager.akamaized.net/live/DB/KURDSAT_NEWS/HLS/KURDSAT_NEWS.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_ronahi_tv",
            name = "Ronahî TV",
            category = "News",
            dialect = FreeKurdishDialect.KURMANJI,
            logoUrl = "",
            streamUrl = "https://hlspackager.akamaized.net/live/DB/RONAHI_TV/HLS/RONAHI_TV.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_sterk_tv",
            name = "Sterk TV",
            category = "General",
            dialect = FreeKurdishDialect.KURMANJI,
            logoUrl = "",
            streamUrl = "https://5dab1db4b25fe.streamlock.net/sterk/sterktv/chunklist_w558585070.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_zagros_tv",
            name = "Zagros TV HD",
            category = "General",
            dialect = FreeKurdishDialect.SORANI,
            logoUrl = "",
            streamUrl = "https://hlspackager.akamaized.net/live/DB/ZAGROS_TV/HLS/ZAGROS_TV.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_speda_tv",
            name = "Speda TV HD",
            category = "General",
            dialect = FreeKurdishDialect.SORANI,
            logoUrl = "",
            streamUrl = "http://speda.teradek.live:1935/live/spedahd/chunklist.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        ),
        FreeKurdishChannel(
            id = "curated_gali_kurdistan",
            name = "Gali Kurdistan TV (GK)",
            category = "General",
            dialect = FreeKurdishDialect.SORANI,
            logoUrl = "",
            streamUrl = "https://live.host247.net/gk/gksat/chunklist_w1377661411.m3u8",
            containerExtension = "m3u8",
            isDirectStream = true
        )
    )

    private val kurdishChannelKeywords = listOf(
        "kurd", "kurdi", "kurdî", "kurman", "soran", "badin", "badîn",
        "zaxo", "zakho", "zaxotv", "delal", "xabur", "khabur", "badinan", "badînan",
        "akre", "amedi", "semel", "rudaw", "kurdsat", "kurdistan", "k24", "nrt",
        "ava ", "ava tv", "zagros", "speda", "waar", "duhok", "hawler", "erbil",
        "kirkuk", "slemani", "sulaimani", "payam", "gali", "knn", "zarok", "ronahi",
        "sterk", "jin tv", "jintv", "medya", "korek", "vin tv", "net tv",
        "kanal 8", "channel 8", "kurdmax", "biaban", "amoza", "kurdstan"
    )

    fun isKurdishChannelName(name: String, group: String = ""): Boolean {
        val haystack = "${name.lowercase()} ${group.lowercase()}"
        return kurdishChannelKeywords.any { haystack.contains(it) }
    }

    fun inferDialectFromName(name: String, fallback: FreeKurdishDialect): FreeKurdishDialect {
        val lower = name.lowercase()
        return when {
            lower.contains("zaxo") || lower.contains("zakho") ||
                lower.contains("delal") || lower.contains("xabur") ||
                lower.contains("khabur") || lower.contains("duhok") ||
                lower.contains("badin") || lower.contains("badîn") ||
                lower.contains("waar") || lower.contains("ava ") ||
                lower.contains("amedi") || lower.contains("akre") ||
                lower.contains("semel") -> FreeKurdishDialect.ZAXO_BADINAN
            lower.contains("zarok") || lower.contains("trt kurd") ||
                lower.contains("ronahi") || lower.contains("sterk") ||
                lower.contains("kurmanc") -> FreeKurdishDialect.KURMANJI
            lower.contains("soran") || lower.contains("kurdsat") ||
                lower.contains("rudaw") || lower.contains("nrt") ||
                lower.contains("speda") || lower.contains("payam") ||
                lower.contains("kurdistan 24") || lower.contains("kurdmax") ||
                lower.contains("zagros") -> FreeKurdishDialect.SORANI
            else -> fallback
        }
    }

    fun mergePlaylists(
        feeds: List<Pair<FreeKurdishDialect, String>>,
        includeCuratedStreams: Boolean = false
    ): List<FreeKurdishChannel> {
        return mergeAnnotatedPlaylists(
            feeds = feeds.map { (dialect, raw) -> Triple(dialect, false, raw) },
            includeCuratedStreams = includeCuratedStreams
        )
    }

    fun mergeAnnotatedPlaylists(
        feeds: List<Triple<FreeKurdishDialect, Boolean, String>>,
        includeCuratedStreams: Boolean = true
    ): List<FreeKurdishChannel> {
        val byUrl = LinkedHashMap<String, FreeKurdishChannel>()

        if (includeCuratedStreams) {
            CURATED_PUBLIC_KURDISH_STREAMS.forEach { ch ->
                byUrl[ch.streamUrl.trim().lowercase()] = ch
            }
        }

        feeds.forEach { (dialect, filterKurdishOnly, rawM3u) ->
            if (rawM3u.isBlank()) return@forEach
            val parsed = OttM3uParser.parseM3uContent(rawM3u)
            parsed.forEach { item ->
                val url = item.streamUrl.trim()
                if (url.isBlank()) return@forEach
                val cleanName = item.name.trim()
                val cleanCategory = item.group
                    .takeIf { it.isNotBlank() && !it.equals("OTT", ignoreCase = true) }
                    ?: "Kurdish TV"

                if (filterKurdishOnly && !isKurdishChannelName(cleanName, cleanCategory)) {
                    return@forEach
                }

                val normalizedKey = url.lowercase()
                val existing = byUrl[normalizedKey]
                val resolvedDialect = inferDialectFromName(cleanName, dialect)

                if (existing == null) {
                    byUrl[normalizedKey] = FreeKurdishChannel(
                        id = normalizedKey,
                        name = cleanName,
                        category = cleanCategory,
                        dialect = resolvedDialect,
                        logoUrl = item.logoUrl.trim(),
                        streamUrl = url,
                        containerExtension = OttM3uParser.inferExtension(url),
                        isDirectStream = false
                    )
                } else {
                    val preferredDialect = if (existing.dialect == FreeKurdishDialect.GENERAL && resolvedDialect != FreeKurdishDialect.GENERAL) {
                        resolvedDialect
                    } else {
                        existing.dialect
                    }
                    byUrl[normalizedKey] = existing.copy(
                        dialect = preferredDialect,
                        logoUrl = existing.logoUrl.ifBlank { item.logoUrl.trim() },
                        category = if (existing.category == "Kurdish TV") cleanCategory else existing.category
                    )
                }
            }
        }

        return byUrl.values.sortedWith(
            compareByDescending<FreeKurdishChannel> { it.isDirectStream }
                .thenBy { it.name.lowercase() }
        )
    }
}

class FreeKurdishRepository(
    private val okHttpClient: OkHttpClient
) {
    suspend fun fetchFreeKurdishChannels(): Pair<List<FreeKurdishChannel>, String?> = withContext(Dispatchers.IO) {
        val rawFeeds = coroutineScope {
            FreeKurdishFeedMerger.FEED_SOURCES.map { source ->
                async {
                    Triple(
                        source.dialect,
                        source.filterKurdishOnly,
                        runCatching { downloadPlaylist(source.url) }.getOrNull()
                    )
                }
            }.map { it.await() }
        }

        val successfulFeeds = rawFeeds.mapNotNull { (dialect, filterOnly, content) ->
            content?.let { Triple(dialect, filterOnly, it) }
        }

        val merged = FreeKurdishFeedMerger.mergeAnnotatedPlaylists(
            feeds = successfulFeeds,
            includeCuratedStreams = true
        )

        val notice = if (successfulFeeds.isEmpty()) {
            "Çevrimiçi dizinlere ulaşılamadı; dahili ücretsiz Kürtçe canlı yayınlar gösteriliyor."
        } else {
            null
        }
        merged to notice
    }

    private fun downloadPlaylist(url: String): String {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "JheHakarTV/1.0 (Android TV)")
            .build()
        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code}")
            }
            return response.body?.string().orEmpty()
        }
    }
}
