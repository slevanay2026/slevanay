package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLink
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.ChannelEntity
import com.example.data.local.SeriesEntity
import com.example.data.local.VodEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.XtreamProfileEntity
import com.example.ui.AppDestination
import com.example.ui.components.TvEmptyStateCard
import com.example.ui.components.TvFocusableCard
import com.example.ui.components.TvPosterCard
import com.example.ui.components.TvSectionHeader
import com.example.ui.components.TvStatChip
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.SlateSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    activeProfile: XtreamProfileEntity?,
    channels: List<ChannelEntity>,
    vods: List<VodEntity>,
    seriesList: List<SeriesEntity>,
    watchHistory: List<WatchHistoryEntity>,
    isSyncing: Boolean,
    onNavigate: (AppDestination) -> Unit,
    onSyncNow: () -> Unit,
    onPlayChannel: (ChannelEntity) -> Unit,
    onOpenVod: (VodEntity) -> Unit,
    onOpenSeries: (SeriesEntity) -> Unit,
    onResumeHistory: (WatchHistoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteChannels = channels.filter { it.isFavorite }.take(12)
    val recentVods = vods.take(12)
    val recentSeries = seriesList.take(12)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SlateSurface)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_tv_hero_backdrop),
                    contentDescription = "NovaTV Sinema Arka Planı",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    DeepObsidian.copy(alpha = 0.94f),
                                    DeepObsidian.copy(alpha = 0.72f),
                                    DeepObsidian.copy(alpha = 0.35f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElectricCyan.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f))
                        ) {
                            Text(
                                text = "SMART TV & TV BOX",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        if (activeProfile != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldLive.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = "PANEL: ${activeProfile.status.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldLive,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = activeProfile?.name ?: "NovaTV Xtream Merkezi",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (activeProfile != null) {
                            "Kategorilerle düzenlenmiş canlı kanallar, filmler ve diziler tek ekranda."
                        } else {
                            "Başlamak için Xtream IPTV panel bilgilerinizi veya M3U/Xtream bağlantınızı ekleyin."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (activeProfile == null) {
                            Button(
                                onClick = { onNavigate(AppDestination.MANAGER) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = DeepObsidian
                                ),
                                modifier = Modifier.testTag("hero_connect_panel_button")
                            ) {
                                Icon(Icons.Rounded.AddLink, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Xtream Panel Ekle", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { onNavigate(AppDestination.LIVE) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = DeepObsidian
                                ),
                                modifier = Modifier.testTag("hero_watch_live_button")
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Canlı TV İzle", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onSyncNow,
                                enabled = !isSyncing,
                                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
                                modifier = Modifier.testTag("hero_sync_button")
                            ) {
                                Icon(Icons.Rounded.Refresh, contentDescription = null, tint = ElectricCyan)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (isSyncing) "Güncelleniyor..." else "Kataloğu Yenile",
                                    color = ElectricCyan
                                )
                            }
                            OutlinedButton(
                                onClick = { onNavigate(AppDestination.MANAGER) },
                                border = BorderStroke(1.dp, GlassBorder),
                                modifier = Modifier.testTag("hero_manage_button")
                            ) {
                                Icon(Icons.Rounded.Settings, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("İçerik Yönetimi", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        if (activeProfile == null) {
            item {
                TvEmptyStateCard(
                    title = "Henüz Xtream Paneli Bağlanmadı",
                    description = "Yönetim sekmesinden sunucu adresi, kullanıcı adı ve şifrenizi girerek ya da doğrudan Xtream / M3U linkini yapıştırarak tüm kanalları, filmleri ve dizileri otomatik olarak senkronize edebilirsiniz.",
                    icon = Icons.Rounded.AddLink,
                    actionLabel = "Xtream Kodu Entegre Et",
                    onAction = { onNavigate(AppDestination.MANAGER) }
                )
            }
        } else {
            // Stats Row
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        TvStatChip(
                            label = "Canlı Kanallar",
                            value = channels.size.toString(),
                            accent = ElectricCyan
                        )
                    }
                    item {
                        TvStatChip(
                            label = "Filmler (VOD)",
                            value = vods.size.toString(),
                            accent = Color.White
                        )
                    }
                    item {
                        TvStatChip(
                            label = "Diziler",
                            value = seriesList.size.toString(),
                            accent = AmberGold
                        )
                    }
                    item {
                        TvStatChip(
                            label = "Bağlantı",
                            value = "${activeProfile.activeCons}/${activeProfile.maxConnections}",
                            accent = EmeraldLive
                        )
                    }
                    item {
                        TvStatChip(
                            label = "Bitiş Tarihi",
                            value = formatExpDate(activeProfile.expDate),
                            accent = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Continue Watching
            if (watchHistory.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TvSectionHeader(
                            title = "Kaldığın Yerden Devam Et",
                            subtitle = "Son izlenen kanallar, filmler ve bölümler"
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            items(watchHistory, key = { it.id }) { entry ->
                                WatchHistoryCard(
                                    entry = entry,
                                    onClick = { onResumeHistory(entry) }
                                )
                            }
                        }
                    }
                }
            }

            // Favorite Channels
            if (favoriteChannels.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TvSectionHeader(
                            title = "Favori Canlı Kanallar",
                            subtitle = "Tek dokunuşla hızlı yayın erişimi",
                            trailing = {
                                OutlinedButton(onClick = { onNavigate(AppDestination.LIVE) }) {
                                    Text("Tüm Kanallar")
                                }
                            }
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(favoriteChannels, key = { it.id }) { ch ->
                                TvFocusableCard(
                                    onClick = { onPlayChannel(ch) },
                                    modifier = Modifier.width(190.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(DeepObsidian),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (ch.streamIcon.isNotBlank()) {
                                                AsyncImage(
                                                    model = ch.streamIcon,
                                                    contentDescription = ch.name,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(4.dp),
                                                    contentScale = ContentScale.Fit
                                                )
                                            } else {
                                                Icon(
                                                    Icons.Rounded.LiveTv,
                                                    contentDescription = null,
                                                    tint = ElectricCyan
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = ch.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recent Movies
            if (recentVods.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TvSectionHeader(
                            title = "Öne Çıkan Filmler",
                            subtitle = "Kategorilerden seçilmiş güncel VOD arşivi",
                            trailing = {
                                OutlinedButton(onClick = { onNavigate(AppDestination.MOVIES) }) {
                                    Text("Tüm Filmler")
                                }
                            }
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            items(recentVods, key = { it.id }) { vod ->
                                TvPosterCard(
                                    title = vod.name,
                                    posterUrl = vod.streamIcon,
                                    rating = vod.rating,
                                    badge = vod.containerExtension,
                                    isFavorite = vod.isFavorite,
                                    onClick = { onOpenVod(vod) },
                                    modifier = Modifier.width(148.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Recent Series
            if (recentSeries.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TvSectionHeader(
                            title = "Popüler Diziler",
                            subtitle = "Sezon ve bölüm takibiyle kesintisiz izleyin",
                            trailing = {
                                OutlinedButton(onClick = { onNavigate(AppDestination.SERIES) }) {
                                    Text("Tüm Diziler")
                                }
                            }
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            items(recentSeries, key = { it.id }) { series ->
                                TvPosterCard(
                                    title = series.name,
                                    posterUrl = series.cover,
                                    rating = series.rating,
                                    badge = "DİZİ",
                                    isFavorite = series.isFavorite,
                                    fallbackIcon = Icons.Rounded.Star,
                                    onClick = { onOpenSeries(series) },
                                    modifier = Modifier.width(148.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchHistoryCard(
    entry: WatchHistoryEntity,
    onClick: () -> Unit
) {
    val progress = if (entry.durationMs > 0L) {
        (entry.positionMs.toFloat() / entry.durationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

    TvFocusableCard(
        onClick = onClick,
        modifier = Modifier.width(240.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DeepObsidian),
                    contentAlignment = Alignment.Center
                ) {
                    if (entry.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = entry.imageUrl,
                            contentDescription = entry.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = if (entry.contentType == "LIVE") Icons.Rounded.LiveTv else Icons.Rounded.Movie,
                            contentDescription = null,
                            tint = ElectricCyan
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = entry.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (progress > 0f) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = ElectricCyan,
                    trackColor = DeepObsidian
                )
            }
        }
    }
}

private fun formatExpDate(epochSeconds: Long?): String {
    if (epochSeconds == null || epochSeconds <= 0L) return "Sınırsız"
    return runCatching {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR"))
        sdf.format(Date(epochSeconds * 1000L))
    }.getOrDefault("Bilinmiyor")
}
