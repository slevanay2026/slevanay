package com.example

import com.example.data.local.ChannelEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.model.AppActivationHelper
import com.example.data.model.DefaultXtreamPreset
import com.example.data.model.OttM3uParser
import com.example.data.model.ParentalControlHelper
import com.example.data.model.RecentChannelsHelper
import com.example.data.model.XtreamLinkParser
import com.example.data.repository.FreeKurdishDialect
import com.example.data.repository.FreeKurdishFeedMerger
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.stringsForLanguage
import com.example.ui.i18n.themeName
import com.example.ui.screens.extractYouTubeVideoId
import com.example.ui.theme.AppThemeOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppStringsAndPortalTest {

    @Test
    fun `all five languages provide complete localized strings and rtl flags`() {
        AppLanguage.entries.forEach { lang ->
            val strings = stringsForLanguage(lang)
            assertTrue("navHome empty for $lang", strings.navHome.isNotBlank())
            assertTrue("navManager empty for $lang", strings.navManager.isNotBlank())
            assertTrue("navYoutube empty for $lang", strings.navYoutube.isNotBlank())
            assertTrue("youtubeTitle empty for $lang", strings.youtubeTitle.isNotBlank())
            assertTrue("recentlyWatchedChannels empty for $lang", strings.recentlyWatchedChannels.isNotBlank())
            assertTrue("recentlyWatchedChannelsSub empty for $lang", strings.recentlyWatchedChannelsSub.isNotBlank())
            assertTrue("recentlyWatchedEmpty empty for $lang", strings.recentlyWatchedEmpty.isNotBlank())
            assertTrue("playerAudioBtn empty for $lang", strings.playerAudioBtn.isNotBlank())
            assertTrue("playerAudioTracksTitle empty for $lang", strings.playerAudioTracksTitle.isNotBlank())
            assertTrue("playerAudioDecoderAuto empty for $lang", strings.playerAudioDecoderAuto.isNotBlank())
            assertTrue("playerAudioDecoderFfmpeg empty for $lang", strings.playerAudioDecoderFfmpeg.isNotBlank())
            assertTrue("parentalSectionTitle empty for $lang", strings.parentalSectionTitle.isNotBlank())
            assertTrue("parentalSetupPinBtn empty for $lang", strings.parentalSetupPinBtn.isNotBlank())
            assertTrue("parentalUnlockBtn empty for $lang", strings.parentalUnlockBtn.isNotBlank())
            assertTrue("parentalLockedNotice empty for $lang", strings.parentalLockedNotice.isNotBlank())
            assertTrue("themeSectionTitle empty for $lang", strings.themeSectionTitle.isNotBlank())
            assertTrue("themeBtnLabel empty for $lang", strings.themeBtnLabel.isNotBlank())
            assertTrue("themeActiveBadge empty for $lang", strings.themeActiveBadge.isNotBlank())
            AppThemeOption.entries.forEach { theme ->
                assertTrue("themeName(${theme.id}) empty for $lang", strings.themeName(theme).isNotBlank())
            }
            assertTrue("activationTitle empty for $lang", strings.activationTitle.isNotBlank())
            assertTrue("activationSubmitBtn empty for $lang", strings.activationSubmitBtn.isNotBlank())
            assertTrue("activationInvalidError empty for $lang", strings.activationInvalidError.isNotBlank())
            assertTrue("navKurdishFree empty for $lang", strings.navKurdishFree.isNotBlank())
            assertTrue("kurdishFreeTitle empty for $lang", strings.kurdishFreeTitle.isNotBlank())
            assertTrue("kurdishFreeBadge empty for $lang", strings.kurdishFreeBadge.isNotBlank())
            assertTrue("kurdishDialectZaxo empty for $lang", strings.kurdishDialectZaxo.isNotBlank())
            assertTrue("webPanelSectionTitle empty for $lang", strings.webPanelSectionTitle.isNotBlank())
            assertTrue("webPanelLocalTitle empty for $lang", strings.webPanelLocalTitle.isNotBlank())
            assertTrue("webPanelRemoteTitle empty for $lang", strings.webPanelRemoteTitle.isNotBlank())
            assertTrue("webPanelConnectSyncBtn empty for $lang", strings.webPanelConnectSyncBtn.isNotBlank())
            assertTrue("webPanelCopyTemplateBtn empty for $lang", strings.webPanelCopyTemplateBtn.isNotBlank())
            assertTrue("aiQuickPrompts empty for $lang", strings.aiQuickPrompts.isNotEmpty())
        }

        assertFalse(AppLanguage.TURKISH.isRtl)
        assertFalse(AppLanguage.ENGLISH.isRtl)
        assertTrue(AppLanguage.ARABIC.isRtl)
        assertTrue(AppLanguage.KURDISH_SORANI.isRtl)
        assertTrue(AppLanguage.KURDISH_BADINI.isRtl)
    }

    @Test
    fun `ready portal url preset is normalized`() {
        assertEquals(
            "http://188.72.5.30:8080",
            XtreamLinkParser.normalizeServerUrl(DefaultXtreamPreset.SERVER_URL)
        )
        assertEquals("alsard", DefaultXtreamPreset.USERNAME)
        assertEquals("555666", DefaultXtreamPreset.PASSWORD)
    }

    @Test
    fun `youtube video id extractor parses standard tv shorts and live urls`() {
        assertEquals("dQw4w9WgXcQ", extractYouTubeVideoId("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
        assertEquals("dQw4w9WgXcQ", extractYouTubeVideoId("https://youtu.be/dQw4w9WgXcQ?si=test"))
        assertEquals("dQw4w9WgXcQ", extractYouTubeVideoId("https://www.youtube.com/live/dQw4w9WgXcQ"))
        assertEquals("dQw4w9WgXcQ", extractYouTubeVideoId("https://www.youtube.com/shorts/dQw4w9WgXcQ"))
        assertEquals("dQw4w9WgXcQ", extractYouTubeVideoId("dQw4w9WgXcQ"))
        assertNull(extractYouTubeVideoId("turkish music live"))
    }

    @Test
    fun `ott m3u parser extracts channels groups logos and stream urls`() {
        val sampleM3u = """
            #EXTM3U
            #EXTINF:-1 tvg-name="TRT 1" tvg-logo="https://example.com/trt1.png" group-title="Ulusal",TRT 1 HD
            https://stream.example.com/live/trt1.m3u8
            #EXTINF:-1 group-title="Haber",Haber Global
            http://stream.example.com:8080/live/user/pass/102.ts
        """.trimIndent()

        val channels = OttM3uParser.parseM3uContent(sampleM3u)
        assertEquals(2, channels.size)
        assertEquals("TRT 1 HD", channels[0].name)
        assertEquals("Ulusal", channels[0].group)
        assertEquals("https://example.com/trt1.png", channels[0].logoUrl)
        assertEquals("https://stream.example.com/live/trt1.m3u8", channels[0].streamUrl)
        assertEquals("Haber Global", channels[1].name)
        assertEquals("Haber", channels[1].group)
        assertEquals("m3u8", OttM3uParser.inferExtension(channels[0].streamUrl))
        assertEquals("ts", OttM3uParser.inferExtension(channels[1].streamUrl))
    }

    @Test
    fun `parental control pin validator and sha256 verifier enforce format and match`() {
        assertFalse(ParentalControlHelper.isValidPinFormat(""))
        assertFalse(ParentalControlHelper.isValidPinFormat("123"))
        assertFalse(ParentalControlHelper.isValidPinFormat("123456789"))
        assertFalse(ParentalControlHelper.isValidPinFormat("12ab"))
        assertTrue(ParentalControlHelper.isValidPinFormat("2580"))
        assertTrue(ParentalControlHelper.isValidPinFormat("12345678"))

        val storedHash = ParentalControlHelper.hashPin("2580")
        assertTrue(ParentalControlHelper.verifyPin("2580", storedHash))
        assertTrue(ParentalControlHelper.verifyPin(" 2580 ", storedHash))
        assertFalse(ParentalControlHelper.verifyPin("0000", storedHash))
        assertFalse(ParentalControlHelper.verifyPin("", storedHash))
        assertFalse(ParentalControlHelper.verifyPin("2580", null))
    }

    @Test
    fun `recent channels helper returns last 5 live channels newest-first excluding hidden and non-live items`() {
        fun channel(streamId: Int, hidden: Boolean = false) = ChannelEntity(
            id = "1_live_$streamId",
            profileId = 1L,
            streamId = streamId,
            num = streamId,
            name = "Channel $streamId",
            streamIcon = "",
            categoryId = "cat1",
            epgChannelId = "",
            isHidden = hidden
        )

        fun history(type: String, streamId: Int, updatedAt: Long) = WatchHistoryEntity(
            id = "1_${type}_$streamId",
            profileId = 1L,
            contentType = type,
            streamId = streamId,
            title = "Title $streamId",
            subtitle = "Live",
            imageUrl = "",
            streamUrl = "http://example.com/$streamId.m3u8",
            containerExtension = "m3u8",
            positionMs = 0L,
            durationMs = 0L,
            updatedAt = updatedAt
        )

        val allChannels = (1..8).map { channel(it, hidden = it == 6) }
        val visibleChannels = allChannels.filter { !it.isHidden }
        val rawHistory = listOf(
            history("VOD", 99, 9000L),
            history("LIVE", 1, 1000L),
            history("LIVE", 2, 2000L),
            history("LIVE", 3, 3000L),
            history("LIVE", 4, 4000L),
            history("LIVE", 5, 5000L),
            history("LIVE", 6, 8000L), // hidden by parental control
            history("LIVE", 7, 7000L),
            history("LIVE", 3, 7500L)  // duplicate older entry should keep newest
        )

        val result = RecentChannelsHelper.filterRecentChannels(
            recentHistory = rawHistory,
            allChannels = allChannels,
            visibleChannels = visibleChannels
        )

        assertEquals(listOf(3, 7, 5, 4, 2), result.map { it.streamId })
    }

    @Test
    fun `theme options resolve from id with fallback and have distinct accent palettes`() {
        assertEquals(AppThemeOption.CYBER_OBSIDIAN, AppThemeOption.fromId(null))
        assertEquals(AppThemeOption.CYBER_OBSIDIAN, AppThemeOption.fromId("unknown_theme"))
        AppThemeOption.entries.forEach { option ->
            assertEquals(option, AppThemeOption.fromId(option.id))
        }
        val uniqueAccents = AppThemeOption.entries.map { it.palette.electricCyan }.toSet()
        assertEquals(AppThemeOption.entries.size, uniqueAccents.size)
        val uniqueIds = AppThemeOption.entries.map { it.id }.toSet()
        assertEquals(AppThemeOption.entries.size, uniqueIds.size)
    }

    @Test
    fun `first-launch activation code accepts only 4312`() {
        assertTrue(AppActivationHelper.verifyCode("4312"))
        assertTrue(AppActivationHelper.verifyCode(" 4312 "))
        assertFalse(AppActivationHelper.verifyCode(""))
        assertFalse(AppActivationHelper.verifyCode("0000"))
        assertFalse(AppActivationHelper.verifyCode("431"))
        assertFalse(AppActivationHelper.verifyCode("1234"))
    }

    @Test
    fun `free kurdish feed merger deduplicates urls and prefers specific dialect`() {
        val ckbM3u = """
            #EXTM3U
            #EXTINF:-1 tvg-logo="https://example.com/rudaw.png" group-title="News",Rudaw HD
            https://live.example.com/rudaw.m3u8
        """.trimIndent()
        val kmrM3u = """
            #EXTM3U
            #EXTINF:-1 group-title="Kids",Zarok TV
            https://live.example.com/zarok.m3u8
        """.trimIndent()
        val kurM3u = """
            #EXTM3U
            #EXTINF:-1 group-title="News",Rudaw HD
            https://live.example.com/rudaw.m3u8
            #EXTINF:-1 group-title="General",Kurdistan TV
            https://live.example.com/ktv.m3u8
        """.trimIndent()

        val merged = FreeKurdishFeedMerger.mergePlaylists(
            listOf(
                FreeKurdishDialect.GENERAL to kurM3u,
                FreeKurdishDialect.SORANI to ckbM3u,
                FreeKurdishDialect.KURMANJI to kmrM3u
            )
        )

        assertEquals(3, merged.size)
        val rudaw = merged.first { it.name == "Rudaw HD" }
        assertEquals(FreeKurdishDialect.SORANI, rudaw.dialect)
        assertEquals("https://example.com/rudaw.png", rudaw.logoUrl)
        val zarok = merged.first { it.name == "Zarok TV" }
        assertEquals(FreeKurdishDialect.KURMANJI, zarok.dialect)
        val ktv = merged.first { it.name == "Kurdistan TV" }
        assertEquals(FreeKurdishDialect.GENERAL, ktv.dialect)
    }

    @Test
    fun `free kurdish merger filters regional playlists for kurdish channels and keeps curated public streams`() {
        val mixedRegionalM3u = """
            #EXTM3U
            #EXTINF:-1 group-title="News",TRT Kurdî
            https://tv-trtkurdi.medya.trt.com.tr/master.m3u8
            #EXTINF:-1 group-title="General",Al Iraqiya
            https://example.com/iraqiya.m3u8
            #EXTINF:-1 group-title="Regional",Duhok TV
            https://example.com/duhok.m3u8
            #EXTINF:-1 group-title="Local",Zaxo TV HD
            https://example.com/zaxotv.m3u8
        """.trimIndent()

        val merged = FreeKurdishFeedMerger.mergeAnnotatedPlaylists(
            feeds = listOf(Triple(FreeKurdishDialect.GENERAL, true, mixedRegionalM3u)),
            includeCuratedStreams = true
        )

        assertTrue(merged.any { it.name == "Duhok TV" && it.dialect == FreeKurdishDialect.ZAXO_BADINAN })
        assertTrue(merged.any { it.name == "Zaxo TV HD" && it.dialect == FreeKurdishDialect.ZAXO_BADINAN })
        assertTrue(merged.any { it.dialect == FreeKurdishDialect.ZAXO_BADINAN && it.name.contains("Zaxo") })
        assertFalse(merged.any { it.name == "Al Iraqiya" })
        // TRT Kurdi URL appears in curated list and regional feed — must not duplicate
        assertEquals(
            1,
            merged.count { it.streamUrl == "https://tv-trtkurdi.medya.trt.com.tr/master.m3u8" }
        )
        assertTrue(merged.size >= FreeKurdishFeedMerger.CURATED_PUBLIC_KURDISH_STREAMS.size)
    }
}
