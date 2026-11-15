package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shield
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.CategoryEntity
import com.example.data.local.ChannelEntity
import com.example.ui.components.ParentalPinDialog
import com.example.ui.components.ParentalPinDialogMode
import com.example.ui.components.TvCategoryRowItem
import com.example.ui.components.TvChannelCard
import com.example.ui.components.TvEmptyStateCard
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BroadcastCoral
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldLive
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
    isParentalEnabled: Boolean,
    hasParentalPin: Boolean,
    isParentalUnlocked: Boolean,
    hiddenChannelsCount: Int,
    onSelectCategory: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onPlayChannel: (ChannelEntity) -> Unit,
    onToggleFavorite: (ChannelEntity) -> Unit,
    onToggleHideChannel: (ChannelEntity) -> Unit,
    onSaveParentalPin: (String) -> Boolean,
    onVerifyParentalPin: suspend (String) -> Boolean,
    onLockParental: () -> Unit,
    onOpenManager: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var activePinDialog by remember { mutableStateOf<ParentalPinDialogMode?>(null) }

    if (!hasActiveProfile) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TvEmptyStateCard(
                title = strings.livePanelRequiredTitle,
                description = strings.livePanelRequiredDesc,
                icon = Icons.Rounded.LiveTv,
                actionLabel = strings.addPanelBtn,
                onAction = onOpenManager
            )
        }
        return
    }

    val channelCountsByCategory = remember(allChannels) {
        allChannels.groupingBy { it.categoryId }.eachCount()
    }
    val canHideChannelsHere = isParentalEnabled && isParentalUnlocked

    val handleParentalChipClick = {
        when {
            !hasParentalPin || !isParentalEnabled -> activePinDialog = ParentalPinDialogMode.CREATE
            !isParentalUnlocked -> activePinDialog = ParentalPinDialogMode.UNLOCK
            else -> onLockParental()
        }
    }

    if (activePinDialog != null) {
        ParentalPinDialog(
            mode = activePinDialog!!,
            onDismiss = { activePinDialog = null },
            onSavePin = onSaveParentalPin,
            onVerifyPin = onVerifyParentalPin
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideLayout = maxWidth >= 680.dp

        if (isWideLayout) {
            Row(modifier = Modifier.fillMaxSize()) {
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
                            text = strings.categoriesTitle,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            item {
                                TvCategoryRowItem(
                                    name = strings.allChannelsTitle,
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
                        isParentalEnabled = isParentalEnabled,
                        isParentalUnlocked = isParentalUnlocked,
                        hiddenChannelsCount = hiddenChannelsCount,
                        onSearchChange = onSearchChange,
                        onToggleFavoritesOnly = onToggleFavoritesOnly,
                        onParentalChipClick = handleParentalChipClick
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LiveChannelsGrid(
                        channels = filteredChannels,
                        canHideChannels = canHideChannelsHere,
                        onPlayChannel = onPlayChannel,
                        onToggleFavorite = onToggleFavorite,
                        onToggleHideChannel = onToggleHideChannel
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                LiveSearchAndFilterBar(
                    searchQuery = searchQuery,
                    favoritesOnly = favoritesOnly,
                    channelCount = filteredChannels.size,
                    isParentalEnabled = isParentalEnabled,
                    isParentalUnlocked = isParentalUnlocked,
                    hiddenChannelsCount = hiddenChannelsCount,
                    onSearchChange = onSearchChange,
                    onToggleFavoritesOnly = onToggleFavoritesOnly,
                    onParentalChipClick = handleParentalChipClick
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { onSelectCategory(null) },
                            label = { Text("${strings.allChannelsTitle} (${allChannels.size})") }
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
                    canHideChannels = canHideChannelsHere,
                    onPlayChannel = onPlayChannel,
                    onToggleFavorite = onToggleFavorite,
                    onToggleHideChannel = onToggleHideChannel
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LiveSearchAndFilterBar(
    searchQuery: String,
    favoritesOnly: Boolean,
    channelCount: Int,
    isParentalEnabled: Boolean,
    isParentalUnlocked: Boolean,
    hiddenChannelsCount: Int,
    onSearchChange: (String) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onParentalChipClick: () -> Unit
) {
    val strings = LocalAppStrings.current

    val searchField: @Composable (Modifier) -> Unit = { fieldModifier ->
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text(strings.searchChannelHint, maxLines = 1) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
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
            modifier = fieldModifier.testTag("live_search_input")
        )
    }

    val favChip: @Composable () -> Unit = {
        FilterChip(
            selected = favoritesOnly,
            onClick = onToggleFavoritesOnly,
            label = { Text(strings.favoritesLabel, maxLines = 1) },
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
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .testTag("live_favorites_filter")
        )
    }

    val parentalChip: @Composable () -> Unit = {
        FilterChip(
            selected = isParentalEnabled,
            onClick = onParentalChipClick,
            label = {
                Text(
                    when {
                        !isParentalEnabled -> "PIN"
                        isParentalUnlocked -> strings.parentalLockBtn
                        hiddenChannelsCount > 0 -> "$hiddenChannelsCount ${strings.parentalHiddenCountSuffix}"
                        else -> strings.parentalUnlockBtn
                    },
                    maxLines = 1
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = when {
                        !isParentalEnabled -> Icons.Rounded.Shield
                        isParentalUnlocked -> Icons.Rounded.LockOpen
                        else -> Icons.Rounded.Lock
                    },
                    contentDescription = strings.parentalSectionTitle,
                    tint = when {
                        !isParentalEnabled -> ElectricCyan
                        isParentalUnlocked -> EmeraldLive
                        else -> BroadcastCoral
                    }
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = if (isParentalUnlocked) {
                    EmeraldLive.copy(alpha = 0.16f)
                } else {
                    BroadcastCoral.copy(alpha = 0.16f)
                },
                selectedLabelColor = if (isParentalUnlocked) EmeraldLive else BroadcastCoral
            ),
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .testTag("live_parental_chip")
        )
    }

    val countBadge: @Composable () -> Unit = {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SlateSurface,
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Text(
                text = "$channelCount ${strings.channelCountSuffix}",
                style = MaterialTheme.typography.labelLarge,
                color = ElectricCyan,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 600.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                searchField(Modifier.fillMaxWidth())
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    favChip()
                    parentalChip()
                    countBadge()
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                searchField(Modifier.weight(1f))
                favChip()
                parentalChip()
                countBadge()
            }
        }
    }
}

@Composable
private fun LiveChannelsGrid(
    channels: List<ChannelEntity>,
    canHideChannels: Boolean,
    onPlayChannel: (ChannelEntity) -> Unit,
    onToggleFavorite: (ChannelEntity) -> Unit,
    onToggleHideChannel: (ChannelEntity) -> Unit
) {
    val strings = LocalAppStrings.current

    if (channels.isEmpty()) {
        TvEmptyStateCard(
            title = strings.noChannelFoundTitle,
            description = strings.noChannelFoundDesc,
            icon = Icons.Rounded.LiveTv
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 260.dp),
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
                onToggleFavorite = { onToggleFavorite(channel) },
                onToggleHidden = if (canHideChannels) {
                    { onToggleHideChannel(channel) }
                } else {
                    null
                }
            )
        }
    }
}
