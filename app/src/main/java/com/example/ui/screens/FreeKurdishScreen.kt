package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.data.repository.FreeKurdishChannel
import com.example.data.repository.FreeKurdishDialect
import com.example.data.repository.FreeKurdishUiState
import com.example.ui.components.TvEmptyStateCard
import com.example.ui.components.TvFocusableCard
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SlateSurface

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FreeKurdishScreen(
    uiState: FreeKurdishUiState,
    favoriteUrls: Set<String>,
    onEnsureLoaded: () -> Unit,
    onRefresh: () -> Unit,
    onPlayChannel: (FreeKurdishChannel) -> Unit,
    onToggleFavorite: (FreeKurdishChannel) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedDialect by remember { mutableStateOf(FreeKurdishDialect.ALL) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var favoritesOnly by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onEnsureLoaded()
    }

    val allChannels = (uiState as? FreeKurdishUiState.Success)?.channels.orEmpty()
    val categories = remember(allChannels) {
        allChannels.map { it.category }.distinct().sorted()
    }
    val filteredChannels = remember(
        allChannels,
        searchQuery,
        selectedDialect,
        selectedCategory,
        favoritesOnly,
        favoriteUrls
    ) {
        val q = searchQuery.trim().lowercase()
        allChannels.filter { ch ->
            val dialectMatch = when (selectedDialect) {
                FreeKurdishDialect.ALL -> true
                FreeKurdishDialect.KURMANJI ->
                    ch.dialect == FreeKurdishDialect.KURMANJI || ch.dialect == FreeKurdishDialect.ZAXO_BADINAN
                else -> ch.dialect == selectedDialect
            }
            (!favoritesOnly || ch.streamUrl in favoriteUrls) &&
                dialectMatch &&
                (selectedCategory == null || ch.category == selectedCategory) &&
                (q.isEmpty() || ch.name.lowercase().contains(q) || ch.category.lowercase().contains(q))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("free_kurdish_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MidnightNavy,
            border = BorderStroke(1.dp, EmeraldLive.copy(alpha = 0.5f))
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                val isCompact = maxWidth < 520.dp
                val titleContent: @Composable (Modifier) -> Unit = { m ->
                    Row(
                        modifier = m,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = EmeraldLive.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, EmeraldLive.copy(alpha = 0.45f)),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Public,
                                    contentDescription = null,
                                    tint = EmeraldLive,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FlowRow(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = strings.kurdishFreeTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldLive.copy(alpha = 0.22f),
                                    border = BorderStroke(1.dp, EmeraldLive.copy(alpha = 0.65f))
                                ) {
                                    Text(
                                        text = strings.kurdishFreeBadge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldLive,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = strings.kurdishFreeSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                val refreshBtn: @Composable () -> Unit = {
                    OutlinedButton(
                        onClick = onRefresh,
                        enabled = uiState !is FreeKurdishUiState.Loading,
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("kurdish_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = strings.kurdishFreeRefreshBtn,
                            tint = ElectricCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.kurdishFreeRefreshBtn,
                            color = ElectricCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (isCompact) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        titleContent(Modifier.fillMaxWidth())
                        refreshBtn()
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        titleContent(Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(10.dp))
                        refreshBtn()
                    }
                }
            }
        }

        // Search & Dialect Filter Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(strings.searchChannelHint) },
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = ElectricCyan)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Rounded.Close, contentDescription = strings.closeBtn)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SlateSurface,
                    unfocusedContainerColor = SlateSurface,
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = GlassBorder
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("kurdish_search_input")
            )

            FilterChip(
                selected = favoritesOnly,
                onClick = { favoritesOnly = !favoritesOnly },
                leadingIcon = {
                    Icon(
                        imageVector = if (favoritesOnly) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                        contentDescription = null,
                        tint = if (favoritesOnly) DeepObsidian else AmberGold
                    )
                },
                label = { Text(strings.favoritesLabel) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AmberGold,
                    selectedLabelColor = DeepObsidian
                ),
                modifier = Modifier.heightIn(min = 48.dp)
            )
        }

        // Dialect & Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FreeKurdishDialect.entries) { dialect ->
                val selected = selectedDialect == dialect
                FilterChip(
                    selected = selected,
                    onClick = { selectedDialect = dialect },
                    label = {
                        Text(
                            text = dialectLabel(dialect, strings),
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricCyan,
                        selectedLabelColor = DeepObsidian
                    )
                )
            }

            if (categories.size > 1) {
                items(categories) { category ->
                    val selected = selectedCategory == category
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedCategory = if (selected) null else category
                        },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldLive,
                            selectedLabelColor = DeepObsidian
                        )
                    )
                }
            }
        }

        when (uiState) {
            is FreeKurdishUiState.Idle,
            is FreeKurdishUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = ElectricCyan)
                        Text(
                            text = strings.kurdishFreeLoading,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is FreeKurdishUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    TvEmptyStateCard(
                        title = strings.kurdishFreeErrorTitle,
                        description = uiState.message,
                        icon = Icons.Rounded.LiveTv,
                        actionLabel = strings.kurdishFreeRetryBtn,
                        onAction = onRefresh
                    )
                }
            }

            is FreeKurdishUiState.Success -> {
                if (!uiState.noticeMessage.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SlateSurface,
                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.55f))
                    ) {
                        Text(
                            text = uiState.noticeMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = AmberGold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
                if (filteredChannels.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        TvEmptyStateCard(
                            title = strings.kurdishFreeEmptyTitle,
                            description = strings.kurdishFreeEmptyDesc,
                            icon = Icons.Rounded.LiveTv
                        )
                    }
                } else {
                    Text(
                        text = "${filteredChannels.size} ${strings.channelCountSuffix} • ${strings.kurdishFreeBadge}",
                        style = MaterialTheme.typography.labelLarge,
                        color = EmeraldLive,
                        fontWeight = FontWeight.SemiBold
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 190.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .testTag("kurdish_channels_grid"),
                        contentPadding = PaddingValues(bottom = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredChannels, key = { it.id }) { channel ->
                            FreeKurdishChannelCard(
                                channel = channel,
                                isFavorite = channel.streamUrl in favoriteUrls,
                                strings = strings,
                                onClick = { onPlayChannel(channel) },
                                onToggleFavorite = { onToggleFavorite(channel) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FreeKurdishChannelCard(
    channel: FreeKurdishChannel,
    isFavorite: Boolean,
    strings: AppStrings,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    TvFocusableCard(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        containerColor = SlateSurface,
        modifier = Modifier.testTag("kurdish_channel_card")
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
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MidnightNavy,
                    border = BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.size(54.dp)
                ) {
                    if (channel.logoUrl.isNotBlank()) {
                        SubcomposeAsyncImage(
                            model = channel.logoUrl,
                            contentDescription = channel.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            error = {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Tv,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Tv,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                        contentDescription = strings.favoritesLabel,
                        tint = if (isFavorite) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = channel.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EmeraldLive.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = strings.kurdishFreeBadge,
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldLive,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "${channel.category} • ${dialectLabel(channel.dialect, strings)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun dialectLabel(dialect: FreeKurdishDialect, strings: AppStrings): String = when (dialect) {
    FreeKurdishDialect.ALL -> strings.kurdishDialectAll
    FreeKurdishDialect.ZAXO_BADINAN -> strings.kurdishDialectZaxo
    FreeKurdishDialect.SORANI -> strings.kurdishDialectSorani
    FreeKurdishDialect.KURMANJI -> strings.kurdishDialectKurmanji
    FreeKurdishDialect.GENERAL -> strings.kurdishDialectGeneral
}
