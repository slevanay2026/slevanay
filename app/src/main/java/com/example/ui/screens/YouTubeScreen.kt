package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaDrm
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import java.util.UUID
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BroadcastCoral
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SlateSurface

private const val YOUTUBE_RED = 0xFFFF0033
private val WIDEVINE_UUID: UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
private const val SMART_TV_USER_AGENT =
    "Mozilla/5.0 (SMART-TV; LINUX; Tizen 8.0) AppleWebKit/537.36 (KHTML, like Gecko) 126.0.6478.127/8.0 TV Safari/537.36"
private const val DESKTOP_WEB_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"

private enum class YouTubeMode {
    TV_LEANBACK,
    WEB_PLAYER
}

private data class YouTubeCategoryShortcut(
    val label: String,
    val icon: ImageVector,
    val webUrl: String,
    val tvUrl: String
)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeScreen(
    bookmarks: List<Pair<String, String>>,
    onAddBookmark: (title: String, url: String) -> Unit,
    onRemoveBookmark: (url: String) -> Unit,
    onNotify: (String) -> Unit,
    onExitToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val strings = LocalAppStrings.current

    var mode by remember { mutableStateOf(YouTubeMode.WEB_PLAYER) }
    var searchInput by remember { mutableStateOf("") }
    var currentUrl by remember { mutableStateOf("https://www.youtube.com") }
    var currentTitle by remember { mutableStateOf("YouTube") }
    var loadProgress by remember { mutableIntStateOf(0) }
    var canGoBack by remember { mutableStateOf(false) }
    var controlsExpanded by remember { mutableStateOf(true) }
    var webViewEpoch by remember { mutableIntStateOf(0) }
    var webViewError by remember { mutableStateOf<String?>(null) }

    var fullscreenCustomView by remember { mutableStateOf<View?>(null) }
    var customViewCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }

    val shortcuts = remember(strings) {
        listOf(
            YouTubeCategoryShortcut(
                label = strings.youtubeCatHome,
                icon = Icons.Rounded.Home,
                webUrl = "https://www.youtube.com",
                tvUrl = "https://www.youtube.com/tv"
            ),
            YouTubeCategoryShortcut(
                label = strings.youtubeCatLive,
                icon = Icons.Rounded.LiveTv,
                webUrl = "https://www.youtube.com/results?search_query=live+tv+streaming&sp=EgJAAQ%253D%253D",
                tvUrl = "https://www.youtube.com/tv#/results?q=live"
            ),
            YouTubeCategoryShortcut(
                label = strings.youtubeCatNews,
                icon = Icons.Rounded.Newspaper,
                webUrl = "https://www.youtube.com/channel/UCYfdidRxbB8Qhf0Nx7ioOYw",
                tvUrl = "https://www.youtube.com/tv#/browse?c=FEnews"
            ),
            YouTubeCategoryShortcut(
                label = strings.youtubeCatMusic,
                icon = Icons.Rounded.MusicNote,
                webUrl = "https://www.youtube.com/channel/UC-9-kyTW8ZkZNDHQJ6FgpwQ",
                tvUrl = "https://www.youtube.com/tv#/browse?c=FEmusic"
            ),
            YouTubeCategoryShortcut(
                label = strings.youtubeCatSports,
                icon = Icons.Rounded.SportsSoccer,
                webUrl = "https://www.youtube.com/channel/UCEgdi0XIXXZ-qJOFPf4JSKw",
                tvUrl = "https://www.youtube.com/tv#/browse?c=FEsports"
            )
        )
    }

    val webView = remember(context, webViewEpoch) {
        WebView(context).apply {
            val targetWebView = this
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.BLACK)
            setLayerType(View.LAYER_TYPE_NONE, null)
            isFocusable = true
            isFocusableInTouchMode = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)
            }

            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(targetWebView, true)
            }

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                mediaPlaybackRequiresUserGesture = false
                useWideViewPort = true
                loadWithOverviewMode = true
                javaScriptCanOpenWindowsAutomatically = false
                setSupportMultipleWindows(false)
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                userAgentString = if (mode == YouTubeMode.TV_LEANBACK) {
                    SMART_TV_USER_AGENT
                } else {
                    DESKTOP_WEB_USER_AGENT
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    loadProgress = newProgress
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    if (!title.isNullOrBlank()) {
                        currentTitle = title.removeSuffix(" - YouTube").trim().ifBlank { "YouTube" }
                    }
                }

                override fun onPermissionRequest(request: PermissionRequest?) {
                    val req = request ?: return
                    val originHost = req.origin?.host?.lowercase().orEmpty()
                    val isYoutubeOrigin = originHost.endsWith("youtube.com") ||
                        originHost.endsWith("youtube-nocookie.com") ||
                        originHost.endsWith("youtu.be")
                    val hasProtectedMedia = req.resources?.contains(
                        PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID
                    ) == true
                    val widevineSupported = runCatching {
                        MediaDrm.isCryptoSchemeSupported(WIDEVINE_UUID)
                    }.getOrDefault(false)
                    if (isYoutubeOrigin && hasProtectedMedia && widevineSupported) {
                        req.grant(arrayOf(PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID))
                    } else {
                        req.deny()
                    }
                }

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    if (fullscreenCustomView != null) {
                        callback?.onCustomViewHidden()
                        return
                    }
                    fullscreenCustomView = view
                    customViewCallback = callback
                }

                override fun onHideCustomView() {
                    fullscreenCustomView = null
                    customViewCallback?.onCustomViewHidden()
                    customViewCallback = null
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val uri = request?.url ?: return false
                    val scheme = uri.scheme?.lowercase().orEmpty()
                    if (scheme != "http" && scheme != "https") {
                        return true
                    }
                    val host = uri.host?.lowercase().orEmpty()
                    val isGoogleOrYoutube = host.endsWith("youtube.com") ||
                        host.endsWith("youtu.be") ||
                        host.endsWith("youtube-nocookie.com") ||
                        host.endsWith("google.com") ||
                        host.endsWith("accounts.google.com") ||
                        host.endsWith("googlevideo.com") ||
                        host.endsWith("ytimg.com") ||
                        host.endsWith("gstatic.com") ||
                        host.endsWith("googleusercontent.com") ||
                        host.endsWith("ggpht.com")
                    return !isGoogleOrYoutube
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    webViewError = null
                    if (!url.isNullOrBlank()) {
                        currentUrl = url
                    }
                    canGoBack = view?.canGoBack() == true
                }

                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    if (!url.isNullOrBlank()) {
                        currentUrl = url
                    }
                    canGoBack = view?.canGoBack() == true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                        webViewError = error?.description?.toString()?.ifBlank { null }
                            ?: strings.playerErrorTitle
                    }
                }

                override fun onRenderProcessGone(
                    view: WebView?,
                    detail: RenderProcessGoneDetail?
                ): Boolean {
                    fullscreenCustomView = null
                    customViewCallback = null
                    webViewError = strings.playerErrorTitle
                    return true
                }
            }

            loadUrl(currentUrl)
        }
    }

    fun navigateToUrl(targetUrl: String, targetMode: YouTubeMode = mode) {
        mode = targetMode
        webViewError = null
        webView.settings.userAgentString = if (targetMode == YouTubeMode.TV_LEANBACK) {
            SMART_TV_USER_AGENT
        } else {
            DESKTOP_WEB_USER_AGENT
        }
        currentUrl = targetUrl
        webView.loadUrl(targetUrl)
        if (targetMode == YouTubeMode.TV_LEANBACK) {
            webView.requestFocus()
        }
    }

    fun submitQuery(rawQuery: String) {
        val trimmed = rawQuery.trim()
        if (trimmed.isEmpty()) return
        val extractedId = extractYouTubeVideoId(trimmed)
        if (extractedId != null) {
            val watchUrl = if (mode == YouTubeMode.TV_LEANBACK) {
                "https://www.youtube.com/tv#/watch?v=$extractedId"
            } else {
                "https://www.youtube.com/watch?v=$extractedId"
            }
            navigateToUrl(watchUrl)
        } else {
            val encoded = Uri.encode(trimmed)
            val searchUrl = if (mode == YouTubeMode.TV_LEANBACK) {
                "https://www.youtube.com/tv#/results?q=$encoded"
            } else {
                "https://www.youtube.com/results?search_query=$encoded"
            }
            navigateToUrl(searchUrl)
        }
    }

    fun openExternalYouTubeApp() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching {
            context.startActivity(intent)
        }.onFailure {
            onNotify(strings.youtubeAppNotFoundMsg)
        }
    }

    DisposableEffect(lifecycleOwner, webView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> webView.onResume()
                Lifecycle.Event.ON_PAUSE -> webView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            if (fullscreenCustomView != null) {
                customViewCallback?.onCustomViewHidden()
                fullscreenCustomView = null
                customViewCallback = null
            }
            webView.stopLoading()
            webView.onPause()
            webView.destroy()
        }
    }

    BackHandler {
        when {
            fullscreenCustomView != null -> {
                customViewCallback?.onCustomViewHidden()
                fullscreenCustomView = null
                customViewCallback = null
            }
            webView.canGoBack() -> {
                webView.goBack()
            }
            else -> {
                onExitToHome()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Smart TV YouTube Control Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MidnightNavy,
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(YOUTUBE_RED).copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, Color(YOUTUBE_RED).copy(alpha = 0.55f))
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayCircle,
                                    contentDescription = null,
                                    tint = Color(YOUTUBE_RED),
                                    modifier = Modifier
                                        .padding(7.dp)
                                        .size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = strings.youtubeTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = strings.youtubeSubtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = mode == YouTubeMode.TV_LEANBACK,
                                onClick = {
                                    navigateToUrl("https://www.youtube.com/tv", YouTubeMode.TV_LEANBACK)
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Tv, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                label = { Text(strings.youtubeModeTvBtn) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan,
                                    selectedLabelColor = DeepObsidian
                                ),
                                modifier = Modifier.testTag("youtube_mode_tv_chip")
                            )

                            FilterChip(
                                selected = mode == YouTubeMode.WEB_PLAYER,
                                onClick = {
                                    navigateToUrl("https://www.youtube.com", YouTubeMode.WEB_PLAYER)
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                label = { Text(strings.youtubeModeWebBtn) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan,
                                    selectedLabelColor = DeepObsidian
                                ),
                                modifier = Modifier.testTag("youtube_mode_web_chip")
                            )

                            IconButton(
                                onClick = { controlsExpanded = !controlsExpanded },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("youtube_toggle_controls_button")
                            ) {
                                Icon(
                                    imageVector = if (controlsExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = strings.youtubeTitle,
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    if (controlsExpanded) {
                        // Search + Controls (single row on Smart TV/Tablet, 2 clean rows on compact phones)
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val isWideBar = maxWidth >= 680.dp
                            if (isWideBar) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { if (webView.canGoBack()) webView.goBack() },
                                        enabled = canGoBack,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .testTag("youtube_back_button")
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Rounded.ArrowBack,
                                            contentDescription = strings.youtubeBackBtn,
                                            tint = if (canGoBack) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = { webView.reload() },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .testTag("youtube_reload_button")
                                    ) {
                                        Icon(
                                            Icons.Rounded.Refresh,
                                            contentDescription = strings.youtubeReloadBtn,
                                            tint = ElectricCyan
                                        )
                                    }

                                    OutlinedTextField(
                                        value = searchInput,
                                        onValueChange = { searchInput = it },
                                        placeholder = { Text(strings.youtubeSearchHint) },
                                        leadingIcon = {
                                            Icon(Icons.Rounded.Search, contentDescription = null, tint = ElectricCyan)
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                        keyboardActions = KeyboardActions(onSearch = { submitQuery(searchInput) }),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = SlateSurface,
                                            unfocusedContainerColor = SlateSurface,
                                            focusedBorderColor = ElectricCyan,
                                            unfocusedBorderColor = GlassBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("youtube_search_input")
                                    )

                                    Button(
                                        onClick = { submitQuery(searchInput) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricCyan,
                                            contentColor = DeepObsidian
                                        ),
                                        modifier = Modifier
                                            .height(50.dp)
                                            .testTag("youtube_search_button")
                                    ) {
                                        Text(strings.youtubeGoBtn, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { onAddBookmark(currentTitle, currentUrl) },
                                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.65f)),
                                        modifier = Modifier
                                            .height(50.dp)
                                            .testTag("youtube_pin_button")
                                    ) {
                                        Icon(Icons.Rounded.BookmarkAdd, contentDescription = null, tint = AmberGold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.youtubeSaveBookmarkBtn, color = AmberGold)
                                    }

                                    OutlinedButton(
                                        onClick = { openExternalYouTubeApp() },
                                        border = BorderStroke(1.dp, GlassBorder),
                                        modifier = Modifier
                                            .height(50.dp)
                                            .testTag("youtube_open_app_button")
                                    ) {
                                        Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.youtubeOpenAppBtn, color = Color.White)
                                    }
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        IconButton(
                                            onClick = { if (webView.canGoBack()) webView.goBack() },
                                            enabled = canGoBack,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .testTag("youtube_back_button")
                                        ) {
                                            Icon(
                                                Icons.AutoMirrored.Rounded.ArrowBack,
                                                contentDescription = strings.youtubeBackBtn,
                                                tint = if (canGoBack) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        IconButton(
                                            onClick = { webView.reload() },
                                            modifier = Modifier
                                                .size(48.dp)
                                                .testTag("youtube_reload_button")
                                        ) {
                                            Icon(
                                                Icons.Rounded.Refresh,
                                                contentDescription = strings.youtubeReloadBtn,
                                                tint = ElectricCyan
                                            )
                                        }

                                        OutlinedTextField(
                                            value = searchInput,
                                            onValueChange = { searchInput = it },
                                            placeholder = { Text(strings.youtubeSearchHint) },
                                            leadingIcon = {
                                                Icon(Icons.Rounded.Search, contentDescription = null, tint = ElectricCyan)
                                            },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                            keyboardActions = KeyboardActions(onSearch = { submitQuery(searchInput) }),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = SlateSurface,
                                                unfocusedContainerColor = SlateSurface,
                                                focusedBorderColor = ElectricCyan,
                                                unfocusedBorderColor = GlassBorder
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("youtube_search_input")
                                        )

                                        Button(
                                            onClick = { submitQuery(searchInput) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = ElectricCyan,
                                                contentColor = DeepObsidian
                                            ),
                                            modifier = Modifier
                                                .height(50.dp)
                                                .testTag("youtube_search_button")
                                        ) {
                                            Text(strings.youtubeGoBtn, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { onAddBookmark(currentTitle, currentUrl) },
                                            border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.65f)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                                .testTag("youtube_pin_button")
                                        ) {
                                            Icon(Icons.Rounded.BookmarkAdd, contentDescription = null, tint = AmberGold)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(strings.youtubeSaveBookmarkBtn, color = AmberGold, maxLines = 1)
                                        }

                                        OutlinedButton(
                                            onClick = { openExternalYouTubeApp() },
                                            border = BorderStroke(1.dp, GlassBorder),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                                .testTag("youtube_open_app_button")
                                        ) {
                                            Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(strings.youtubeOpenAppBtn, color = Color.White, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }

                        // Quick Categories + Pinned Bookmarks
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(shortcuts) { item ->
                                val target = if (mode == YouTubeMode.TV_LEANBACK) item.tvUrl else item.webUrl
                                FilterChip(
                                    selected = currentUrl == target,
                                    onClick = { navigateToUrl(target) },
                                    leadingIcon = {
                                        Icon(item.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    label = { Text(item.label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(YOUTUBE_RED),
                                        selectedLabelColor = Color.White,
                                        selectedLeadingIconColor = Color.White
                                    )
                                )
                            }

                            items(bookmarks, key = { it.second }) { (title, url) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SlateSurface,
                                    border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.45f))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { navigateToUrl(url) },
                                            border = null,
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = title,
                                                color = AmberGold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        IconButton(
                                            onClick = { onRemoveBookmark(url) },
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = strings.removeFromHistoryDesc,
                                                tint = BroadcastCoral,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Embedded Hardware-Accelerated YouTube Container
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.Black,
                border = BorderStroke(1.dp, GlassBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    key(webViewEpoch) {
                        AndroidView(
                            factory = { webView },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("youtube_webview")
                        )
                    }

                    if (loadProgress in 1..99) {
                        LinearProgressIndicator(
                            progress = { loadProgress / 100f },
                            color = Color(YOUTUBE_RED),
                            trackColor = Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.TopCenter)
                        )
                    }

                    val err = webViewError
                    if (err != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MidnightNavy.copy(alpha = 0.94f),
                            border = BorderStroke(1.dp, BroadcastCoral),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = strings.playerErrorTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = BroadcastCoral,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = err,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                                Button(
                                    onClick = {
                                        webViewError = null
                                        webViewEpoch++
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ElectricCyan,
                                        contentColor = DeepObsidian
                                    )
                                ) {
                                    Icon(Icons.Rounded.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(strings.aiRetryBtn, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // True Fullscreen HTML5 Video Overlay (for Smart TV playback)
        val customView = fullscreenCustomView
        if (customView != null) {
            Dialog(
                onDismissRequest = {
                    customViewCallback?.onCustomViewHidden()
                    fullscreenCustomView = null
                    customViewCallback = null
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            FrameLayout(ctx).apply {
                                (customView.parent as? ViewGroup)?.removeView(customView)
                                addView(
                                    customView,
                                    FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

internal fun extractYouTubeVideoId(input: String): String? {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return null

    // Direct 11-char ID
    if (Regex("^[a-zA-Z0-9_-]{11}$").matches(trimmed)) {
        return trimmed
    }

    val patterns = listOf(
        Regex("""(?:youtube\.com/watch\?.*?v=|youtu\.be/|youtube\.com/embed/|youtube\.com/shorts/|youtube\.com/live/)([a-zA-Z0-9_-]{11})""")
    )
    for (pattern in patterns) {
        val match = pattern.find(trimmed)
        if (match != null) {
            return match.groupValues[1]
        }
    }
    return null
}
