package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.CategoryEntity
import com.example.data.local.ChannelEntity
import com.example.ui.components.TvCategoryRowItem
import com.example.ui.components.TvChannelCard
import com.example.ui.components.TvEmptyStateCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SlateSurface

@Composable
fun LiveTvScreen(
    hasActiveProfile: Boolean,
    categories: List<CategoryEntity>,
    allChannels: List<ChannelEntity>,
    filteredChannels: List<ChannelEntity>,
    selectedCategoryId: String?,
    searchQuery: String,
    favoritesOnly: Boolean,
    onSelectCategory: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onPlayChannel: (ChannelEntity) -> Unit,
    onToggleFavorite: (ChannelEntity) -> Unit,
    onOpenManager: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!hasActiveProfile) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TvEmptyStateCard(
                title = "Canlı TV İçin Panel Gerekli",
                description = "Canlı TV kanallarını ve kategorilerini listelemek için Yönetim sekmesinden Xtream IPTV panelinizi ekleyin.",
                icon = Icons.Rounded.LiveTv,
                actionLabel = "Panel Ekle",
                onAction = onOpenManager
            )
        }
        return
    }

    val channelCountsByCategory = remember(allChannels) {
        allChannels.groupingBy { it.categoryId }.eachCount()
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideLayout = maxWidth >= 680.dp

        if (isWideLayout) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Category Pane (TV-friendly sidebar)
                Surface(
                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight(),
                    color = MidnightNavy,
                    border = BorderStroke(1.dp, GlassBorder.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Kategoriler",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            item {
                                TvCategoryRowItem(
                                    name = "Tüm Kanallar",
                                    count = allChannels.size,
                                    isSelected = selectedCategoryId == null,
                                    onClick = { onSelectCategory(null) }
                                )
                            }
                            items(categories, key = { it.id }) { cat ->
                                TvCategoryRowItem(
                                    name = cat.name,
                                    count = channelCountsByCategory[cat.categoryId] ?: 0,
                                    isSelected = selectedCategoryId == cat.categoryId,
                                    isPinned = cat.isPinned,
                                    onClick = { onSelectCategory(cat.categoryId) }
                                )
                            }
                        }
                    }
                }

                // Right Channels Pane
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    LiveSearchAndFilterBar(
                        searchQuery = searchQuery,
                        favoritesOnly = favoritesOnly,
                        channelCount = filteredChannels.size,
                        onSearchChange = onSearchChange,
                        onToggleFavoritesOnly = onToggleFavoritesOnly
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LiveChannelsGrid(
                        channels = filteredChannels,
                        onPlayChannel = onPlayChannel,
                        onToggleFavorite = onToggleFavorite
                    )
                }
            }
        } else {
            // Compact portrait layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                LiveSearchAndFilterBar(
                    searchQuery = searchQuery,
                    favoritesOnly = favoritesOnly,
                    channelCount = filteredChannels.size,
                    onSearchChange = onSearchChange,
                    onToggleFavoritesOnly = onToggleFavoritesOnly
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { onSelectCategory(null) },
                            label = { Text("Tümü (${allChannels.size})") }
                        )
                    }
                    items(categories, key = { it.id }) { cat ->
                        FilterChip(
                            selected = selectedCategoryId == cat.categoryId,
                            onClick = { onSelectCategory(cat.categoryId) },
                            label = {
                                Text("${cat.name} (${channelCountsByCategory[cat.categoryId] ?: 0})")
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                LiveChannelsGrid(
                    channels = filteredChannels,
                    onPlayChannel = onPlayChannel,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
    }
}

@Composable
private fun LiveSearchAndFilterBar(
    searchQuery: String,
    favoritesOnly: Boolean,
    channelCount: Int,
    onSearchChange: (String) -> Unit,
    onToggleFavoritesOnly: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Kanal ara...") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Aramayı temizle")
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
                .testTag("live_search_input")
        )

        FilterChip(
            selected = favoritesOnly,
            onClick = onToggleFavoritesOnly,
            label = { Text("Favoriler") },
            leadingIcon = {
                Icon(
                    imageVector = if (favoritesOnly) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                    contentDescription = null,
                    tint = if (favoritesOnly) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = AmberGold.copy(alpha = 0.2f),
                selectedLabelColor = AmberGold
            ),
            modifier = Modifier.testTag("live_favorites_filter")
        )

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SlateSurface,
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Text(
                text = "$channelCount kanal",
                style = MaterialTheme.typography.labelLarge,
                color = ElectricCyan,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun LiveChannelsGrid(
    channels: List<ChannelEntity>,
    onPlayChannel: (ChannelEntity) -> Unit,
    onToggleFavorite: (ChannelEntity) -> Unit
) {
    if (channels.isEmpty()) {
        TvEmptyStateCard(
            title = "Kanal Bulunamadı",
            description = "Seçili kategori veya arama kriterine uygun canlı kanal yok.",
            icon = Icons.Rounded.LiveTv
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 270.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian.copy(alpha = 0.0f))
    ) {
        items(channels, key = { it.id }) { channel ->
            TvChannelCard(
                channelNumber = channel.num,
                name = channel.name,
                logoUrl = channel.streamIcon,
                isFavorite = channel.isFavorite,
                onPlay = { onPlayChannel(channel) },
                onToggleFavorite = { onToggleFavorite(channel) }
            )
        }
    }
}
