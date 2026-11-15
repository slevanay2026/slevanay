package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.KeyEvent as AndroidKeyEvent
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Forward30
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.data.model.ContentType
import com.example.data.model.PlaybackRequest
import com.example.data.model.XtreamEpgItem
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.BroadcastCoral
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import java.util.Locale

private data class AudioTrackOption(
    val groupIndex: Int,
    val trackIndex: Int,
    val label: String,
    val codecSummary: String,
    val isSelected: Boolean,
    val isSupported: Boolean,
    val mediaTrackGroup: TrackGroup
)

@kotlin.OptIn(ExperimentalLayoutApi::class)
@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    request: PlaybackRequest,
    epgItems: List<XtreamEpgItem>,
    okHttpClient: OkHttpClient,
    onZapChannel: (Int) -> Unit,
    onSaveProgress: (PlaybackRequest, Long, Long) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hostView = LocalView.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val strings = LocalAppStrings.current
    var controlsVisible by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var isPlayerReady by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf<String?>(null) }
    var currentPositionMs by remember { mutableLongStateOf(request.startPositionMs) }
    var totalDurationMs by remember { mutableLongStateOf(0L) }
    var resizeModeIndex by remember { mutableIntStateOf(0) }
    var retryToken by remember { mutableIntStateOf(0) }
    var preferFfmpegDecoder by remember { mutableStateOf(false) }
    var audioMenuExpanded by remember { mutableStateOf(false) }
    var audioTracks by remember { mutableStateOf<List<AudioTrackOption>>(emptyList()) }
    var hasVideoTrack by remember { mutableStateOf(false) }
    var effectiveStreamUrl by remember(request.streamUrl) { mutableStateOf(request.streamUrl) }
    var triedAlternateLiveExt by remember(request.streamUrl) { mutableStateOf(false) }

    val resizeModes = listOf(
        AspectRatioFrameLayout.RESIZE_MODE_FIT to strings.resizeFit,
        AspectRatioFrameLayout.RESIZE_MODE_ZOOM to strings.resizeZoom,
        AspectRatioFrameLayout.RESIZE_MODE_FILL to strings.resizeFill
    )

    val exoPlayer = remember(context, preferFfmpegDecoder) {
        val dataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
            .setUserAgent("IPTVSmartersPlayer")

        val extractorsFactory = DefaultExtractorsFactory()
            .setConstantBitrateSeekingEnabled(true)
            .setTsExtractorFlags(
                DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES or
                    DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS
            )

        // NextRenderersFactory includes FFmpeg software audio decoders (AC3, E-AC3, MP2, DTS, AAC, Opus, etc.)
        val renderersFactory = NextRenderersFactory(context)
            .setEnableDecoderFallback(true)
            .setExtensionRendererMode(
                if (preferFfmpegDecoder) {
                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                } else {
                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
                }
            )

        // Disable Audio Offload and downmix/prefer stereo PCM so TV boxes & emulators never drop audio silently
        val trackSelector = DefaultTrackSelector(context).apply {
            parameters = buildUponParameters()
                .setAudioOffloadPreferences(
                    TrackSelectionParameters.AudioOffloadPreferences.Builder()
                        .setAudioOffloadMode(
                            TrackSelectionParameters.AudioOffloadPreferences.AUDIO_OFFLOAD_MODE_DISABLED
                        )
                        .build()
                )
                .setAllowAudioMixedMimeTypeAdaptiveness(true)
                .setAllowAudioMixedChannelCountAdaptiveness(true)
                .setAllowAudioMixedSampleRateAdaptiveness(true)
                .setAllowAudioMixedDecoderSupportAdaptiveness(true)
                .setExceedAudioConstraintsIfNecessary(true)
                .setMaxAudioChannelCount(2)
                .setPreferredAudioMimeTypes(
                    MimeTypes.AUDIO_AAC,
                    MimeTypes.AUDIO_MPEG,
                    MimeTypes.AUDIO_MPEG_L2,
                    MimeTypes.AUDIO_AC3,
                    MimeTypes.AUDIO_E_AC3
                )
                .build()
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        val isLiveContent = request.contentType == ContentType.LIVE
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                if (isLiveContent) 12_000 else 20_000,
                if (isLiveContent) 30_000 else 60_000,
                if (isLiveContent) 1_000 else 1_500,
                if (isLiveContent) 2_000 else 3_000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        ExoPlayer.Builder(context, renderersFactory)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory, extractorsFactory))
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true)
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                volume = 1f
            }
    }

    DisposableEffect(context, hostView) {
        val window = context.findActivityOrNull()?.window
        val controller = if (window != null) WindowCompat.getInsetsController(window, hostView) else null
        controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    val latestRequest by rememberUpdatedState(request)

    LaunchedEffect(exoPlayer, effectiveStreamUrl, retryToken) {
        playerError = null
        isBuffering = true
        isPlayerReady = false
        audioTracks = emptyList()
        hasVideoTrack = false
        val mediaItem = MediaItem.fromUri(effectiveStreamUrl)
        exoPlayer.volume = 1f
        exoPlayer.setMediaItem(mediaItem)
        if (request.startPositionMs > 0L && request.contentType != ContentType.LIVE) {
            exoPlayer.seekTo(request.startPositionMs)
        }
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.volume = 1f
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    isPlayerReady = true
                    exoPlayer.volume = 1f
                    totalDurationMs = exoPlayer.duration.coerceAtLeast(0L)
                    onSaveProgress(
                        latestRequest,
                        exoPlayer.currentPosition.coerceAtLeast(0L),
                        totalDurationMs
                    )
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (playing) {
                    exoPlayer.volume = 1f
                }
            }

            override fun onTracksChanged(tracks: Tracks) {
                hasVideoTrack = tracks.groups.any { it.type == C.TRACK_TYPE_VIDEO && it.isSelected }
                val parsedAudio = extractAudioTrackOptions(tracks)
                audioTracks = parsedAudio

                // If audio tracks exist but none is currently selected, force-select the first supported track
                if (parsedAudio.isNotEmpty() && parsedAudio.none { it.isSelected }) {
                    val fallbackTrack = parsedAudio.firstOrNull { it.isSupported }
                    if (fallbackTrack != null) {
                        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                            .buildUpon()
                            .setOverrideForType(
                                TrackSelectionOverride(
                                    fallbackTrack.mediaTrackGroup,
                                    fallbackTrack.trackIndex
                                )
                            )
                            .build()
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                if (latestRequest.contentType == ContentType.LIVE && !triedAlternateLiveExt) {
                    val alternateUrl = toggleLiveExtension(effectiveStreamUrl)
                    if (alternateUrl != null) {
                        triedAlternateLiveExt = true
                        effectiveStreamUrl = alternateUrl
                        return
                    }
                }
                if (!preferFfmpegDecoder) {
                    preferFfmpegDecoder = true
                    return
                }
                isBuffering = false
                playerError = "Yayın oynatılamadı (${error.errorCodeName}). Panel formatını (.m3u8/.ts) veya bağlantıyı kontrol edin."
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
            val dur = exoPlayer.duration.coerceAtLeast(0L)
            onSaveProgress(latestRequest, pos, dur)
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Auto-recover silent live channels: if video plays for 2.5s with no selected audio track, switch .m3u8 <-> .ts
    LaunchedEffect(effectiveStreamUrl, isPlayerReady, hasVideoTrack, audioTracks, triedAlternateLiveExt) {
        if (latestRequest.contentType == ContentType.LIVE &&
            isPlayerReady &&
            hasVideoTrack &&
            audioTracks.none { it.isSelected }
        ) {
            delay(2500)
            if (!triedAlternateLiveExt && audioTracks.none { it.isSelected }) {
                val alternateUrl = toggleLiveExtension(effectiveStreamUrl)
                if (alternateUrl != null) {
                    triedAlternateLiveExt = true
                    effectiveStreamUrl = alternateUrl
                } else if (!preferFfmpegDecoder) {
                    preferFfmpegDecoder = true
                }
            } else if (!preferFfmpegDecoder && audioTracks.none { it.isSelected }) {
                preferFfmpegDecoder = true
            }
        }
    }

    // Periodically update position & auto-hide controls
    LaunchedEffect(controlsVisible, isPlaying, audioMenuExpanded) {
        if (controlsVisible && isPlaying && !audioMenuExpanded) {
            delay(4500)
            controlsVisible = false
        }
    }

    LaunchedEffect(exoPlayer, request.streamUrl) {
        var tick = 0
        while (true) {
            currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L)
            totalDurationMs = exoPlayer.duration.coerceAtLeast(0L)
            tick++
            if (tick % 8 == 0 && totalDurationMs > 0L) {
                onSaveProgress(request, currentPositionMs, totalDurationMs)
            }
            delay(1000)
        }
    }

    fun exitPlayer() {
        val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
        val dur = exoPlayer.duration.coerceAtLeast(0L)
        onSaveProgress(latestRequest, pos, dur)
        onClose()
    }

    BackHandler {
        exitPlayer()
    }

    val selectedAudio = audioTracks.firstOrNull { it.isSelected }
    val liveFormatBadge = when {
        effectiveStreamUrl.endsWith(".ts", ignoreCase = true) -> "TS"
        effectiveStreamUrl.endsWith(".m3u8", ignoreCase = true) -> "HLS"
        else -> null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .onPreviewKeyEvent { event ->
                if (event.nativeKeyEvent.action == AndroidKeyEvent.ACTION_DOWN) {
                    val wasVisible = controlsVisible
                    controlsVisible = true
                    when (event.nativeKeyEvent.keyCode) {
                        AndroidKeyEvent.KEYCODE_CHANNEL_UP -> {
                            if (request.contentType == ContentType.LIVE) {
                                onZapChannel(1)
                                true
                            } else false
                        }
                        AndroidKeyEvent.KEYCODE_CHANNEL_DOWN -> {
                            if (request.contentType == ContentType.LIVE) {
                                onZapChannel(-1)
                                true
                            } else false
                        }
                        AndroidKeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                        AndroidKeyEvent.KEYCODE_SPACE -> {
                            if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                            true
                        }
                        AndroidKeyEvent.KEYCODE_MEDIA_PLAY -> {
                            exoPlayer.play()
                            true
                        }
                        AndroidKeyEvent.KEYCODE_MEDIA_PAUSE -> {
                            exoPlayer.pause()
                            true
                        }
                        AndroidKeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                            if (request.contentType != ContentType.LIVE) {
                                val target = exoPlayer.currentPosition + 30_000L
                                exoPlayer.seekTo(if (totalDurationMs > 0) target.coerceAtMost(totalDurationMs) else target)
                                true
                            } else false
                        }
                        AndroidKeyEvent.KEYCODE_MEDIA_REWIND -> {
                            if (request.contentType != ContentType.LIVE) {
                                exoPlayer.seekTo((exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L))
                                true
                            } else false
                        }
                        AndroidKeyEvent.KEYCODE_DPAD_RIGHT -> {
                            if (!wasVisible && request.contentType != ContentType.LIVE) {
                                val target = exoPlayer.currentPosition + 30_000L
                                exoPlayer.seekTo(if (totalDurationMs > 0) target.coerceAtMost(totalDurationMs) else target)
                                true
                            } else false
                        }
                        AndroidKeyEvent.KEYCODE_DPAD_LEFT -> {
                            if (!wasVisible && request.contentType != ContentType.LIVE) {
                                exoPlayer.seekTo((exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L))
                                true
                            } else false
                        }
                        AndroidKeyEvent.KEYCODE_DPAD_CENTER,
                        AndroidKeyEvent.KEYCODE_ENTER -> {
                            if (!wasVisible) true else false
                        }
                        else -> false
                    }
                } else false
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                controlsVisible = !controlsVisible
            }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    keepScreenOn = true
                    player = exoPlayer
                    resizeMode = resizeModes[resizeModeIndex].first
                }
            },
            update = { view ->
                view.keepScreenOn = true
                if (view.player !== exoPlayer) {
                    view.player = exoPlayer
                }
                view.resizeMode = resizeModes[resizeModeIndex].first
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isBuffering && playerError == null) {
            CircularProgressIndicator(
                color = ElectricCyan,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(52.dp)
            )
        }

        if (playerError != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                shape = RoundedCornerShape(20.dp),
                color = MidnightNavy.copy(alpha = 0.94f),
                border = BorderStroke(1.dp, BroadcastCoral)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = strings.playerErrorTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = BroadcastCoral
                    )
                    Text(
                        text = playerError!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { retryToken++ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = DeepObsidian
                            )
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.aiRetryBtn, fontWeight = FontWeight.Bold)
                        }
                        Button(onClick = { exitPlayer() }) {
                            Text(strings.playerBackBtn)
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.78f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp)
            ) {
                // Responsive Top Bar (wraps cleanly on portrait phones, single-row on TV/tablets)
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                ) {
                    val isCompactTop = maxWidth < 540.dp
                    val titleHeader: @Composable (Modifier) -> Unit = { m ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = m
                        ) {
                            IconButton(
                                onClick = { exitPlayer() },
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                    .testTag("player_back_button")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = strings.playerBackBtn,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (request.contentType == ContentType.LIVE) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = BroadcastCoral
                                        ) {
                                            Text(
                                                text = strings.liveBadge,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = request.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = request.subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ElectricCyan,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    val actionPills: @Composable () -> Unit = {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (request.contentType == ContentType.LIVE && liveFormatBadge != null) {
                                var fmtFocused by remember { mutableStateOf(false) }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MidnightNavy.copy(alpha = 0.85f),
                                    border = BorderStroke(
                                        if (fmtFocused) 2.dp else 1.dp,
                                        if (fmtFocused) ElectricCyan else GlassBorder
                                    ),
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .onFocusChanged { fmtFocused = it.isFocused }
                                        .clickable {
                                            val alt = toggleLiveExtension(effectiveStreamUrl)
                                            if (alt != null) {
                                                triedAlternateLiveExt = true
                                                effectiveStreamUrl = alt
                                            }
                                        }
                                        .focusable()
                                        .testTag("player_format_toggle")
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = liveFormatBadge,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = ElectricCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Audio Track & Decoder Menu
                            Box {
                                val hasAudioIssue = isPlayerReady && selectedAudio == null
                                var audioFocused by remember { mutableStateOf(false) }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MidnightNavy.copy(alpha = 0.85f),
                                    border = BorderStroke(
                                        if (audioFocused) 2.dp else 1.dp,
                                        when {
                                            audioFocused -> ElectricCyan
                                            hasAudioIssue -> BroadcastCoral
                                            else -> GlassBorder
                                        }
                                    ),
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .onFocusChanged { audioFocused = it.isFocused }
                                        .clickable { audioMenuExpanded = true }
                                        .focusable()
                                        .testTag("player_audio_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (hasAudioIssue) {
                                                Icons.AutoMirrored.Rounded.VolumeOff
                                            } else {
                                                Icons.AutoMirrored.Rounded.VolumeUp
                                            },
                                            contentDescription = strings.playerAudioBtn,
                                            tint = if (hasAudioIssue) BroadcastCoral else ElectricCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = selectedAudio?.codecSummary ?: strings.playerAudioBtn,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = audioMenuExpanded,
                                    onDismissRequest = { audioMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = strings.playerAudioTracksTitle,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = ElectricCyan,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        onClick = {},
                                        enabled = false
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(Icons.Rounded.GraphicEq, contentDescription = null)
                                        },
                                        text = {
                                            Text(
                                                text = if (preferFfmpegDecoder) {
                                                    strings.playerAudioDecoderFfmpeg
                                                } else {
                                                    strings.playerAudioDecoderAuto
                                                },
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        },
                                        trailingIcon = {
                                            if (preferFfmpegDecoder) {
                                                Icon(Icons.Rounded.Check, contentDescription = null)
                                            }
                                        },
                                        onClick = {
                                            preferFfmpegDecoder = !preferFfmpegDecoder
                                            audioMenuExpanded = false
                                        }
                                    )
                                    HorizontalDivider()

                                    DropdownMenuItem(
                                        text = { Text(strings.playerAudioAutoOption) },
                                        onClick = {
                                            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                .buildUpon()
                                                .clearOverridesOfType(C.TRACK_TYPE_AUDIO)
                                                .build()
                                            exoPlayer.volume = 1f
                                            audioMenuExpanded = false
                                        }
                                    )

                                    if (audioTracks.isEmpty()) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = strings.playerAudioNoneLabel,
                                                    color = BroadcastCoral
                                                )
                                            },
                                            onClick = {},
                                            enabled = false
                                        )
                                    } else {
                                        audioTracks.forEach { option ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = "${option.label} · ${option.codecSummary}",
                                                        fontWeight = if (option.isSelected) {
                                                            FontWeight.Bold
                                                        } else {
                                                            FontWeight.Normal
                                                        }
                                                    )
                                                },
                                                trailingIcon = {
                                                    if (option.isSelected) {
                                                        Icon(Icons.Rounded.Check, contentDescription = null)
                                                    }
                                                },
                                                onClick = {
                                                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                        .buildUpon()
                                                        .setOverrideForType(
                                                            TrackSelectionOverride(
                                                                option.mediaTrackGroup,
                                                                option.trackIndex
                                                            )
                                                        )
                                                        .build()
                                                    exoPlayer.volume = 1f
                                                    audioMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            var resizeFocused by remember { mutableStateOf(false) }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MidnightNavy.copy(alpha = 0.85f),
                                border = BorderStroke(
                                    if (resizeFocused) 2.dp else 1.dp,
                                    if (resizeFocused) ElectricCyan else GlassBorder
                                ),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .onFocusChanged { resizeFocused = it.isFocused }
                                    .clickable {
                                        resizeModeIndex = (resizeModeIndex + 1) % resizeModes.size
                                    }
                                    .focusable()
                                    .testTag("player_resize_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.AspectRatio,
                                        contentDescription = resizeModes[resizeModeIndex].second,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = resizeModes[resizeModeIndex].second,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    if (isCompactTop) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            titleHeader(Modifier.fillMaxWidth())
                            actionPills()
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            titleHeader(Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(12.dp))
                            actionPills()
                        }
                    }
                }

                // Bottom Controls + EPG
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (request.contentType == ContentType.LIVE && epgItems.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MidnightNavy.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, GlassBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                val current = epgItems.first()
                                Text(
                                    text = "${strings.epgNowPrefix}: ${current.title}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = ElectricCyan,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (epgItems.size > 1) {
                                    Text(
                                        text = "${strings.epgNextPrefix}: ${epgItems[1].title}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    if (request.contentType != ContentType.LIVE && totalDurationMs > 0L) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = formatDuration(currentPositionMs),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                            Slider(
                                value = currentPositionMs.toFloat(),
                                onValueChange = { exoPlayer.seekTo(it.toLong()) },
                                valueRange = 0f..totalDurationMs.toFloat().coerceAtLeast(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = ElectricCyan,
                                    activeTrackColor = ElectricCyan
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = formatDuration(totalDurationMs),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (request.contentType == ContentType.LIVE) {
                            IconButton(
                                onClick = { onZapChannel(-1) },
                                modifier = Modifier.testTag("player_prev_channel")
                            ) {
                                Icon(
                                    Icons.Rounded.SkipPrevious,
                                    contentDescription = "Önceki Kanal",
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    exoPlayer.seekTo((exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L))
                                }
                            ) {
                                Icon(
                                    Icons.Rounded.Replay10,
                                    contentDescription = "10 sn geri",
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        var playFocused by remember { mutableStateOf(false) }
                        Surface(
                            shape = CircleShape,
                            color = ElectricCyan,
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .then(
                                    if (playFocused) Modifier.border(3.dp, Color.White, CircleShape) else Modifier
                                )
                                .onFocusChanged { playFocused = it.isFocused }
                                .clickable {
                                    if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                                }
                                .focusable()
                                .testTag("player_play_pause")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    contentDescription = if (isPlaying) "Duraklat" else "Oynat",
                                    tint = DeepObsidian,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        if (request.contentType == ContentType.LIVE) {
                            IconButton(
                                onClick = { onZapChannel(1) },
                                modifier = Modifier.testTag("player_next_channel")
                            ) {
                                Icon(
                                    Icons.Rounded.SkipNext,
                                    contentDescription = "Sonraki Kanal",
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    val target = exoPlayer.currentPosition + 30_000L
                                    exoPlayer.seekTo(
                                        if (totalDurationMs > 0) target.coerceAtMost(totalDurationMs) else target
                                    )
                                }
                            ) {
                                Icon(
                                    Icons.Rounded.Forward30,
                                    contentDescription = "30 sn ileri",
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun toggleLiveExtension(url: String): String? {
    return when {
        url.endsWith(".m3u8", ignoreCase = true) -> url.dropLast(5) + ".ts"
        url.endsWith(".ts", ignoreCase = true) -> url.dropLast(3) + ".m3u8"
        else -> null
    }
}

private fun extractAudioTrackOptions(tracks: Tracks): List<AudioTrackOption> {
    val result = mutableListOf<AudioTrackOption>()
    var counter = 1
    tracks.groups.forEachIndexed { groupIdx, group ->
        if (group.type == C.TRACK_TYPE_AUDIO) {
            for (trackIdx in 0 until group.length) {
                val format = group.getTrackFormat(trackIdx)
                val langName = format.language
                    ?.takeIf { it.isNotBlank() && it != "und" }
                    ?.let { code ->
                        Locale.forLanguageTag(code).getDisplayLanguage(Locale.getDefault())
                            .replaceFirstChar { ch -> ch.uppercase() }
                            .ifBlank { code.uppercase() }
                    }
                val label = format.label?.takeIf { it.isNotBlank() }
                    ?: langName
                    ?: "Ses #$counter"
                val codec = shortAudioCodec(format.sampleMimeType)
                val chLabel = when (format.channelCount) {
                    1 -> "Mono"
                    2 -> "Stereo"
                    6 -> "5.1"
                    8 -> "7.1"
                    in 3..12 -> "${format.channelCount}ch"
                    else -> ""
                }
                val summary = listOf(codec, chLabel).filter { it.isNotBlank() }.joinToString(" ")
                result.add(
                    AudioTrackOption(
                        groupIndex = groupIdx,
                        trackIndex = trackIdx,
                        label = label,
                        codecSummary = summary.ifBlank { "Audio" },
                        isSelected = group.isTrackSelected(trackIdx),
                        isSupported = group.isTrackSupported(trackIdx),
                        mediaTrackGroup = group.mediaTrackGroup
                    )
                )
                counter++
            }
        }
    }
    return result
}

private fun shortAudioCodec(mimeType: String?): String {
    return when (mimeType?.lowercase()) {
        MimeTypes.AUDIO_AAC -> "AAC"
        MimeTypes.AUDIO_AC3 -> "AC3"
        MimeTypes.AUDIO_E_AC3, MimeTypes.AUDIO_E_AC3_JOC -> "E-AC3"
        MimeTypes.AUDIO_MPEG -> "MP3"
        MimeTypes.AUDIO_MPEG_L2 -> "MP2"
        MimeTypes.AUDIO_MPEG_L1 -> "MP1"
        MimeTypes.AUDIO_DTS, MimeTypes.AUDIO_DTS_HD, MimeTypes.AUDIO_DTS_X -> "DTS"
        MimeTypes.AUDIO_TRUEHD -> "TrueHD"
        MimeTypes.AUDIO_OPUS -> "Opus"
        MimeTypes.AUDIO_VORBIS -> "Vorbis"
        MimeTypes.AUDIO_FLAC -> "FLAC"
        else -> mimeType?.substringAfter("/")?.uppercase().orEmpty()
    }
}

private fun formatDuration(ms: Long): String {
    val totalSec = (ms / 1000L).coerceAtLeast(0L)
    val hours = totalSec / 3600
    val mins = (totalSec % 3600) / 60
    val secs = totalSec % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}

private tailrec fun Context.findActivityOrNull(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivityOrNull()
    else -> null
}
