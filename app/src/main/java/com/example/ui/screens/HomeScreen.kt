package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BroadcastCoral
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SlateSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    activeProfile: XtreamProfileEntity?,
    channels: List<ChannelEntity>,
    vods: List<VodEntity>,
    seriesList: List<SeriesEntity>,
    recentlyWatchedChannels: List<WatchHistoryEntity>,
    watchHistory: List<WatchHistoryEntity>,
    isSyncing: Boolean,
    onNavigate: (AppDestination) -> Unit,
    onSyncNow: () -> Unit,
    onPlayChannel: (ChannelEntity) -> Unit,
    onOpenVod: (VodEntity) -> Unit,
    onOpenSeries: (SeriesEntity) -> Unit,
    onResumeHistory: (WatchHistoryEntity) -> Unit,
    onRemoveHistoryItem: (String) -> Unit = {},
    onClearHistory: () -> Unit = {},
    remoteAnnouncement: String? = null,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val favoriteChannels = channels.filter { it.isFavorite }.take(12)
    val recentVods = vods.take(12)
    val recentSeries = seriesList.take(12)
    val channelsByStreamId = channels.associateBy { it.streamId }

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
                    contentDescription = "JHE HAKAR Backdrop",
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
                                text = strings.heroBadge,
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
                                    text = "${strings.panelStatusPrefix}: ${activeProfile.status.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldLive,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF101E39),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f)),
                            modifier = Modifier.size(60.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_icon_foreground),
                                contentDescription = "JHE HAKAR Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeProfile?.name ?: "JHE HAKAR",
                                style = MaterialTheme.typography.displaySmall,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (activeProfile != null) {
                                    strings.heroSubtitleConnected
                                } else {
                                    strings.heroSubtitleEmpty
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (activeProfile == null) {
                            Button(
                                onClick = { onNavigate(AppDestination.MANAGER) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = DeepObsidian
                                ),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("hero_connect_panel_button")
                            ) {
                                Icon(Icons.Rounded.AddLink, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.btnAddXtreamPanel, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { onNavigate(AppDestination.LIVE) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = DeepObsidian
                                ),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("hero_watch_live_button")
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.btnWatchLive, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onSyncNow,
                                enabled = !isSyncing,
                                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("hero_sync_button")
                            ) {
                                Icon(Icons.Rounded.Refresh, contentDescription = null, tint = ElectricCyan)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (isSyncing) strings.btnSyncing else strings.btnRefreshCatalog,
                                    color = ElectricCyan
                                )
                            }
                            OutlinedButton(
                                onClick = { onNavigate(AppDestination.MANAGER) },
                                border = BorderStroke(1.dp, GlassBorder),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("hero_manage_button")
                            ) {
                                Icon(Icons.Rounded.Settings, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.btnContentManager, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        if (!remoteAnnouncement.isNullOrBlank()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ElectricCyan.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_remote_announcement_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Campaign,
                            contentDescription = null,
                            tint = ElectricCyan
                        )
                        Text(
                            text = remoteAnnouncement,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Smart TV Quick App Shortcuts (Free Kurdish TV, YouTube, AI Guide)
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    OutlinedButton(
                        onClick = { onNavigate(AppDestination.KURDISH_FREE) },
                        border = BorderStroke(1.dp, EmeraldLive.copy(alpha = 0.75f)),
                        modifier = Modifier.testTag("home_quick_kurdish_button")
                    ) {
                        Icon(Icons.Rounded.Public, contentDescription = null, tint = EmeraldLive)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.navKurdishFree, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldLive.copy(alpha = 0.22f)
                        ) {
                            Text(
                                text = strings.kurdishFreeBadge,
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldLive,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onNavigate(AppDestination.YOUTUBE) },
                        border = BorderStroke(1.dp, BroadcastCoral.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("home_quick_youtube_button")
                    ) {
                        Icon(Icons.Rounded.SmartDisplay, contentDescription = null, tint = BroadcastCoral)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.navYoutube, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onNavigate(AppDestination.AI) },
                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("home_quick_ai_button")
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = AmberGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.navAi, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (activeProfile == null) {
            item {
                TvEmptyStateCard(
                    title = strings.emptyNoPanelTitle,
                    description = strings.emptyNoPanelDesc,
                    icon = Icons.Rounded.AddLink,
                    actionLabel = strings.emptyNoPanelCta,
                    onAction = { onNavigate(AppDestination.MANAGER) }
                )
            }
        } else {
            // Stats Row
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        TvStatChip(
                            label = strings.statLiveChannels,
                            value = channels.size.toString(),
                            accent = ElectricCyan
                        )
                    }
                    item {
                        TvStatChip(
                            label = strings.statMoviesVod,
                            value = vods.size.toString(),
                            accent = Color.White
                        )
                    }
                    item {
                        TvStatChip(
                            label = strings.statSeries,
                            value = seriesList.size.toString(),
                            accent = AmberGold
                        )
                    }
                    item {
                        TvStatChip(
                            label = strings.statConnections,
                            value = "${activeProfile.activeCons}/${activeProfile.maxConnections}",
                            accent = EmeraldLive
                        )
                    }
                    item {
                        TvStatChip(
                            label = strings.statExpiryDate,
                            value = formatExpDate(activeProfile.expDate, strings),
                            accent = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Recently Watched Channels (last 5 played channels)
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.testTag("recently_watched_rail")
                ) {
                    TvSectionHeader(
                        title = if (recentlyWatchedChannels.isNotEmpty()) {
                            "${strings.recentlyWatchedChannels} (${recentlyWatchedChannels.size}/5)"
                        } else {
                            strings.recentlyWatchedChannels
                        },
                        subtitle = strings.recentlyWatchedChannelsSub,
                        trailing = {
                            OutlinedButton(
                                onClick = { onNavigate(AppDestination.LIVE) },
                                modifier = Modifier.testTag("recently_watched_all_channels_button")
                            ) {
                                Text(strings.allChannelsBtn)
                            }
                        }
                    )

                    if (recentlyWatchedChannels.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MidnightNavy,
                            border = BorderStroke(1.dp, GlassBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recently_watched_empty")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = BroadcastCoral.copy(alpha = 0.16f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.LiveTv,
                                            contentDescription = null,
                                            tint = BroadcastCoral
                                        )
                                    }
                                }
                                Text(
                                    text = strings.recentlyWatchedEmpty,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedButton(onClick = { onNavigate(AppDestination.LIVE) }) {
                                    Text(strings.navLive)
                                }
                            }
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.testTag("recently_watched_list")
                        ) {
                            items(recentlyWatchedChannels, key = { "recent_ch_${it.streamId}" }) { entry ->
                                val matchedChannel = channelsByStreamId[entry.streamId]
                                RecentlyWatchedChannelCard(
                                    entry = entry,
                                    matchedChannel = matchedChannel,
                                    strings = strings,
                                    onClick = {
                                        if (matchedChannel != null) {
                                            onPlayChannel(matchedChannel)
                                        } else {
                                            onResumeHistory(entry)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Continue Watching (always visible when logged in)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TvSectionHeader(
                        title = if (watchHistory.isNotEmpty()) {
                            "${strings.continueWatching} (${watchHistory.size})"
                        } else {
                            strings.continueWatching
                        },
                        subtitle = strings.continueWatchingSub,
                        trailing = {
                            if (watchHistory.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = onClearHistory,
                                    modifier = Modifier.testTag("home_clear_history_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.DeleteSweep,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(strings.clearHistoryBtn)
                                }
                            }
                        }
                    )

                    if (watchHistory.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MidnightNavy,
                            border = BorderStroke(1.dp, GlassBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("continue_watching_empty")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = ElectricCyan.copy(alpha = 0.14f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.History,
                                            contentDescription = null,
                                            tint = ElectricCyan
                                        )
                                    }
                                }
                                Text(
                                    text = strings.continueWatchingEmpty,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedButton(onClick = { onNavigate(AppDestination.LIVE) }) {
                                    Text(strings.navLive)
                                }
                                OutlinedButton(onClick = { onNavigate(AppDestination.MOVIES) }) {
                                    Text(strings.navMovies)
                                }
                            }
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.testTag("continue_watching_list")
                        ) {
                            items(watchHistory, key = { it.id }) { entry ->
                                WatchHistoryCard(
                                    entry = entry,
                                    strings = strings,
                                    onClick = { onResumeHistory(entry) },
                                    onRemove = { onRemoveHistoryItem(entry.id) }
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
                            title = strings.favoriteChannels,
                            subtitle = strings.favoriteChannelsSub,
                            trailing = {
                                OutlinedButton(onClick = { onNavigate(AppDestination.LIVE) }) {
                                    Text(strings.allChannelsBtn)
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

            // Portal Live Channels (directly visible from synced Xtream portal)
            if (channels.isNotEmpty()) {
                item {
                    val homeChannels = channels.take(18)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TvSectionHeader(
                            title = "${strings.navLive} (${channels.size})",
                            subtitle = strings.favoriteChannelsSub,
                            trailing = {
                                OutlinedButton(onClick = { onNavigate(AppDestination.LIVE) }) {
                                    Text(strings.allChannelsBtn)
                                }
                            }
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(homeChannels, key = { "home_${it.id}" }) { ch ->
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
                            title = strings.featuredMovies,
                            subtitle = strings.featuredMoviesSub,
                            trailing = {
                                OutlinedButton(onClick = { onNavigate(AppDestination.MOVIES) }) {
                                    Text(strings.allMoviesBtn)
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
                            title = strings.popularSeries,
                            subtitle = strings.popularSeriesSub,
                            trailing = {
                                OutlinedButton(onClick = { onNavigate(AppDestination.SERIES) }) {
                                    Text(strings.allSeriesBtn)
                                }
                            }
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            items(recentSeries, key = { it.id }) { series ->
                                TvPosterCard(
                                    title = series.name,
                                    posterUrl = series.cover,
                                    rating = series.rating,
                                    badge = strings.seriesBadge,
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
private fun RecentlyWatchedChannelCard(
    entry: WatchHistoryEntity,
    matchedChannel: ChannelEntity?,
    strings: AppStrings,
    onClick: () -> Unit
) {
    val channelTitle = matchedChannel?.name?.ifBlank { entry.title } ?: entry.title
    val logoUrl = matchedChannel?.streamIcon?.ifBlank { entry.imageUrl } ?: entry.imageUrl
    val channelNum = matchedChannel?.num

    TvFocusableCard(
        onClick = onClick,
        modifier = Modifier
            .width(220.dp)
            .testTag("recently_watched_card_${entry.streamId}")
    ) { isFocused ->
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
                if (logoUrl.isNotBlank()) {
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = channelTitle,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.LiveTv,
                        contentDescription = null,
                        tint = ElectricCyan
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(5.dp),
                        color = BroadcastCoral.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = strings.liveBadge,
                            style = MaterialTheme.typography.labelSmall,
                            color = BroadcastCoral,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (channelNum != null && channelNum > 0) {
                        Text(
                            text = "#$channelNum",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    } else if (entry.updatedAt > 0L) {
                        Text(
                            text = formatPlayedClock(entry.updatedAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = channelTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isFocused) ElectricCyan else MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun formatPlayedClock(epochMs: Long): String {
    return runCatching {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(epochMs))
    }.getOrDefault("")
}

@Composable
private fun WatchHistoryCard(
    entry: WatchHistoryEntity,
    strings: AppStrings,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val isLive = entry.contentType == "LIVE"
    val progress = if (!isLive && entry.durationMs > 0L) {
        (entry.positionMs.toFloat() / entry.durationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val typeBadgeText = when (entry.contentType) {
        "LIVE" -> strings.liveBadge
        "SERIES" -> strings.seriesBadge
        else -> strings.navMovies.uppercase()
    }
    val typeBadgeColor = when (entry.contentType) {
        "LIVE" -> BroadcastCoral
        "SERIES" -> AmberGold
        else -> ElectricCyan
    }

    TvFocusableCard(
        onClick = onClick,
        modifier = Modifier
            .width(276.dp)
            .testTag("watch_history_card_${entry.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, end = 6.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
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
                            imageVector = if (isLive) Icons.Rounded.LiveTv else Icons.Rounded.Movie,
                            contentDescription = null,
                            tint = ElectricCyan
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(5.dp),
                            color = typeBadgeColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = typeBadgeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = typeBadgeColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = entry.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!isLive && entry.positionMs > 0L) {
                        Spacer(modifier = Modifier.height(2.dp))
                        val timeText = if (entry.durationMs > 0L) {
                            "${strings.resumeFromPrefix}: ${formatHistoryTime(entry.positionMs)} / ${formatHistoryTime(entry.durationMs)}"
                        } else {
                            "${strings.resumeFromPrefix}: ${formatHistoryTime(entry.positionMs)}"
                        }
                        Text(
                            text = timeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = strings.removeFromHistoryDesc,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
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

private fun formatHistoryTime(ms: Long): String {
    val totalSec = (ms / 1000L).coerceAtLeast(0L)
    val hours = totalSec / 3600
    val mins = (totalSec % 3600) / 60
    val secs = totalSec % 60
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, mins, secs)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }
}

private fun formatExpDate(epochSeconds: Long?, strings: AppStrings): String {
    if (epochSeconds == null || epochSeconds <= 0L) return strings.unlimited
    return runCatching {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        sdf.format(Date(epochSeconds * 1000L))
    }.getOrDefault(strings.unknown)
}
