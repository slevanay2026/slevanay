package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.XtreamLinkParser
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
    fun `app name matches NovaTV`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("NovaTV", appName)
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
}
