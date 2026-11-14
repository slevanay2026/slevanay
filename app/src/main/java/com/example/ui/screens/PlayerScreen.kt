package com.example.ui.screens

import android.view.KeyEvent as AndroidKeyEvent
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Forward30
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.data.model.ContentType
import com.example.data.model.PlaybackRequest
import com.example.data.model.XtreamEpgItem
import com.example.ui.theme.BroadcastCoral
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient

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
    var controlsVisible by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var playerError by remember { mutableStateOf<String?>(null) }
    var currentPositionMs by remember { mutableLongStateOf(request.startPositionMs) }
    var totalDurationMs by remember { mutableLongStateOf(0L) }
    var resizeModeIndex by remember { mutableIntStateOf(0) }
    var retryToken by remember { mutableIntStateOf(0) }

    val resizeModes = listOf(
        AspectRatioFrameLayout.RESIZE_MODE_FIT to "Sığdır",
        AspectRatioFrameLayout.RESIZE_MODE_ZOOM to "Yakınlaştır",
        AspectRatioFrameLayout.RESIZE_MODE_FILL to "Tam Ekran"
    )

    val exoPlayer = remember(context) {
        val dataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
            .setUserAgent("NovaTV/1.0 (Android TV)")
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()
    }

    LaunchedEffect(request.streamUrl, retryToken) {
        playerError = null
        isBuffering = true
        val mediaItem = MediaItem.fromUri(request.streamUrl)
        exoPlayer.setMediaItem(mediaItem)
        if (request.startPositionMs > 0L && request.contentType != ContentType.LIVE) {
            exoPlayer.seekTo(request.startPositionMs)
        }
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    totalDurationMs = exoPlayer.duration.coerceAtLeast(0L)
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayerError(error: PlaybackException) {
                isBuffering = false
                playerError = "Yayın oynatılamadı (${error.errorCodeName}). Panel formatını (.m3u8/.ts) veya bağlantıyı kontrol edin."
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
            val dur = exoPlayer.duration.coerceAtLeast(0L)
            onSaveProgress(request, pos, dur)
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Periodically update position & auto-hide controls
    LaunchedEffect(controlsVisible, isPlaying) {
        if (controlsVisible && isPlaying) {
            delay(4500)
            controlsVisible = false
        }
    }

    LaunchedEffect(exoPlayer, request.streamUrl) {
        while (true) {
            currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L)
            totalDurationMs = exoPlayer.duration.coerceAtLeast(0L)
            delay(1000)
        }
    }

    fun exitPlayer() {
        val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
        val dur = exoPlayer.duration.coerceAtLeast(0L)
        onSaveProgress(request, pos, dur)
        onClose()
    }

    BackHandler {
        exitPlayer()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .onPreviewKeyEvent { event ->
                if (event.nativeKeyEvent.action == AndroidKeyEvent.ACTION_DOWN) {
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
                        AndroidKeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                            if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                            true
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
                    player = exoPlayer
                    resizeMode = resizeModes[resizeModeIndex].first
                }
            },
            update = { view ->
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
                        text = "Yayın Hatası",
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
                            Text("Tekrar Dene", fontWeight = FontWeight.Bold)
                        }
                        Button(onClick = { exitPlayer() }) {
                            Text("Geri Dön")
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
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = { exitPlayer() },
                            modifier = Modifier.testTag("player_back_button")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Çıkış",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
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
                                            text = "CANLI",
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
                                color = ElectricCyan
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MidnightNavy.copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.clickable {
                            resizeModeIndex = (resizeModeIndex + 1) % resizeModes.size
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Rounded.AspectRatio,
                                contentDescription = "Ekran oranı",
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
                                    text = "Şu An: ${current.title}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = ElectricCyan,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (epgItems.size > 1) {
                                    Text(
                                        text = "Sonraki: ${epgItems[1].title}",
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

                        Surface(
                            shape = CircleShape,
                            color = ElectricCyan,
                            modifier = Modifier
                                .size(58.dp)
                                .clickable {
                                    if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                                }
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
