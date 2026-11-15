package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DefaultXtreamPreset
import com.example.data.model.XtreamLinkParser
import com.example.data.remote.RemoteWebPanelClient
import com.example.data.remote.RemoteWebPanelParser
import com.example.data.repository.FreeKurdishDialect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches JHE HAKAR`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("JHE HAKAR", appName)
    }

    @Test

    fun `smart parser extracts m3u xtream url`() {
        val parsed = XtreamLinkParser.parseSmartInput(
            "http://iptv.example.com:8080/get.php?username=ali123&password=pass456&type=m3u_plus&output=ts"
        )
        assertNotNull(parsed)
        assertEquals("http://iptv.example.com:8080", parsed?.serverUrl)
        assertEquals("ali123", parsed?.username)
        assertEquals("pass456", parsed?.password)
    }

    @Test
    fun `smart parser extracts multiline xtream credentials`() {
        val parsed = XtreamLinkParser.parseSmartInput(
            """
            Host: http://panel.test.tv:2095
            Username: tvuser
            Password: secret99
            """.trimIndent()
        )
        assertNotNull(parsed)
        assertEquals("http://panel.test.tv:2095", parsed?.serverUrl)
        assertEquals("tvuser", parsed?.username)
        assertEquals("secret99", parsed?.password)
    }

    @Test
    fun `preset url normalizes uppercase scheme`() {
        assertEquals(
            DefaultXtreamPreset.SERVER_URL,
            XtreamLinkParser.normalizeServerUrl("HTTP://188.72.5.30:8080/")
        )
    }

    @Test
    fun `remote web panel parser handles json template m3u playlist and xtream links`() {
        val jsonPayload = RemoteWebPanelParser.parseWebsitePayload(
            RemoteWebPanelParser.SAMPLE_PANEL_JSON_TEMPLATE,
            "https://my-panel.example.com/panel.json"
        )
        assertEquals("JHE HAKAR Web Panel", jsonPayload.panelTitle)
        assertEquals("cyber_obsidian", jsonPayload.themeId)
        assertEquals("http://example.com:8080", jsonPayload.xtreamConfig?.serverUrl)
        assertEquals("demo_user", jsonPayload.xtreamConfig?.username)
        assertEquals(1, jsonPayload.channels.size)
        assertEquals("Rudaw TV", jsonPayload.channels.first().name)
        assertEquals(FreeKurdishDialect.SORANI, jsonPayload.channels.first().dialect)

        val m3uPayload = RemoteWebPanelParser.parseWebsitePayload(
            """
                #EXTM3U
                #EXTINF:-1 group-title="Kurdî",AVA TV
                https://live.example.com/ava.m3u8
            """.trimIndent(),
            "https://site.example.com/list.m3u"
        )
        assertEquals(1, m3uPayload.channels.size)
        assertEquals("AVA TV", m3uPayload.channels.first().name)
        assertEquals("Kurdî", m3uPayload.channels.first().category)

        val xtreamPayload = RemoteWebPanelParser.parseWebsitePayload(
            "http://portal.example.com:8080/get.php?username=hak&password=4312",
            "https://site.example.com/xtream.txt"
        )
        assertEquals("http://portal.example.com:8080", xtreamPayload.xtreamConfig?.serverUrl)
        assertEquals("hak", xtreamPayload.xtreamConfig?.username)
        assertEquals("4312", xtreamPayload.xtreamConfig?.password)

        assertEquals(
            "https://panel.example.com/config.json",
            RemoteWebPanelClient.normalizeWebsiteUrl("panel.example.com/config.json")
        )

        val serialized = RemoteWebPanelParser.serializeCustomChannels(jsonPayload.channels)
        val deserialized = RemoteWebPanelParser.deserializeCustomChannels(serialized)
        assertEquals(1, deserialized.size)
        assertEquals("Rudaw TV", deserialized.first().name)
    }
}
