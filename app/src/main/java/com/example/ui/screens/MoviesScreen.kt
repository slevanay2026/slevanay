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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.local.CategoryEntity
import com.example.data.local.VodEntity
import com.example.data.model.VodSortOption
import com.example.data.model.XtreamVodInfo
import com.example.ui.components.TvCategoryRowItem
import com.example.ui.components.TvEmptyStateCard
import com.example.ui.components.TvPosterCard
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SlateSurface

@Composable
fun MoviesScreen(
    hasActiveProfile: Boolean,
    categories: List<CategoryEntity>,
    allVods: List<VodEntity>,
    filteredVods: List<VodEntity>,
    selectedCategoryId: String?,
    searchQuery: String,
    sortOption: VodSortOption,
    favoritesOnly: Boolean,
    selectedVod: VodEntity?,
    selectedVodInfo: XtreamVodInfo?,
    isDetailLoading: Boolean,
    onSelectCategory: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    onSortChange: (VodSortOption) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onOpenVod: (VodEntity) -> Unit,
    onCloseVod: () -> Unit,
    onPlayVod: (VodEntity) -> Unit,
    onToggleFavorite: (VodEntity) -> Unit,
    onOpenManager: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    if (!hasActiveProfile) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TvEmptyStateCard(
                title = strings.moviesPanelRequiredTitle,
                description = strings.moviesPanelRequiredDesc,
                icon = Icons.Rounded.Movie,
                actionLabel = strings.addPanelBtn,
                onAction = onOpenManager
            )
        }
        return
    }

    val countsByCat = remember(allVods) {
        allVods.groupingBy { it.categoryId }.eachCount()
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 680.dp

        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier
                        .width(250.dp)
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
                            text = strings.movieCategoriesTitle,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            item {
                                TvCategoryRowItem(
                                    name = strings.allMoviesTitle,
                                    count = allVods.size,
                                    isSelected = selectedCategoryId == null,
                                    onClick = { onSelectCategory(null) }
                                )
                            }
                            items(categories, key = { it.id }) { cat ->
                                TvCategoryRowItem(
                                    name = cat.name,
                                    count = countsByCat[cat.categoryId] ?: 0,
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
                    MoviesFilterHeader(
                        searchQuery = searchQuery,
                        sortOption = sortOption,
                        favoritesOnly = favoritesOnly,
                        count = filteredVods.size,
                        onSearchChange = onSearchChange,
                        onSortChange = onSortChange,
                        onToggleFavoritesOnly = onToggleFavoritesOnly
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    MoviesPosterGrid(vods = filteredVods, onOpenVod = onOpenVod)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                MoviesFilterHeader(
                    searchQuery = searchQuery,
                    sortOption = sortOption,
                    favoritesOnly = favoritesOnly,
                    count = filteredVods.size,
                    onSearchChange = onSearchChange,
                    onSortChange = onSortChange,
                    onToggleFavoritesOnly = onToggleFavoritesOnly
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { onSelectCategory(null) },
                            label = { Text("${strings.allMoviesTitle} (${allVods.size})") }
                        )
                    }
                    items(categories, key = { it.id }) { cat ->
                        FilterChip(
                            selected = selectedCategoryId == cat.categoryId,
                            onClick = { onSelectCategory(cat.categoryId) },
                            label = { Text("${cat.name} (${countsByCat[cat.categoryId] ?: 0})") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                MoviesPosterGrid(vods = filteredVods, onOpenVod = onOpenVod)
            }
        }
    }

    if (selectedVod != null) {
        VodDetailDialog(
            vod = selectedVod,
            info = selectedVodInfo,
            isLoading = isDetailLoading,
            onDismiss = onCloseVod,
            onPlay = {
                onCloseVod()
                onPlayVod(selectedVod)
            },
            onToggleFavorite = { onToggleFavorite(selectedVod) }
        )
    }
}

private fun sortLabel(option: VodSortOption, strings: AppStrings): String = when (option) {
    VodSortOption.DEFAULT -> strings.sortDefault
    VodSortOption.NAME_ASC -> strings.sortNameAsc
    VodSortOption.RATING_DESC -> strings.sortRatingDesc
    VodSortOption.RECENT_DESC -> strings.sortRecentDesc
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MoviesFilterHeader(
    searchQuery: String,
    sortOption: VodSortOption,
    favoritesOnly: Boolean,
    count: Int,
    onSearchChange: (String) -> Unit,
    onSortChange: (VodSortOption) -> Unit,
    onToggleFavoritesOnly: () -> Unit
) {
    val strings = LocalAppStrings.current
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val searchField: @Composable (Modifier) -> Unit = { fieldModifier ->
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text(strings.searchMovieHint, maxLines = 1) },
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
            modifier = fieldModifier.testTag("movies_search_input")
        )
    }

    val sortBox: @Composable () -> Unit = {
        Box {
            OutlinedButton(
                onClick = { sortMenuExpanded = true },
                border = BorderStroke(1.dp, GlassBorder),
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("movies_sort_button")
            ) {
                Icon(Icons.AutoMirrored.Rounded.Sort, contentDescription = null, tint = ElectricCyan)
                Spacer(modifier = Modifier.width(6.dp))
                Text(sortLabel(sortOption, strings), maxLines = 1)
            }
            DropdownMenu(
                expanded = sortMenuExpanded,
                onDismissRequest = { sortMenuExpanded = false }
            ) {
                VodSortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(sortLabel(option, strings)) },
                        onClick = {
                            onSortChange(option)
                            sortMenuExpanded = false
                        }
                    )
                }
            }
        }
    }

    val favChip: @Composable () -> Unit = {
        FilterChip(
            selected = favoritesOnly,
            onClick = onToggleFavoritesOnly,
            label = { Text("${strings.favoritesLabel} ($count)", maxLines = 1) },
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
            modifier = Modifier.defaultMinSize(minHeight = 48.dp)
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 600.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                searchField(Modifier.fillMaxWidth())
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sortBox()
                    favChip()
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                searchField(Modifier.weight(1f))
                sortBox()
                favChip()
            }
        }
    }
}

@Composable
private fun MoviesPosterGrid(
    vods: List<VodEntity>,
    onOpenVod: (VodEntity) -> Unit
) {
    val strings = LocalAppStrings.current

    if (vods.isEmpty()) {
        TvEmptyStateCard(
            title = strings.noMovieFoundTitle,
            description = strings.noMovieFoundDesc,
            icon = Icons.Rounded.Movie
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 148.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(vods, key = { it.id }) { vod ->
            TvPosterCard(
                title = vod.name,
                posterUrl = vod.streamIcon,
                rating = vod.rating,
                badge = vod.containerExtension,
                isFavorite = vod.isFavorite,
                onClick = { onOpenVod(vod) }
            )
        }
    }
}

@Composable
private fun VodDetailDialog(
    vod: VodEntity,
    info: XtreamVodInfo?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val strings = LocalAppStrings.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(20.dp)
                .widthIn(max = 760.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MidnightNavy,
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = info?.name?.ifBlank { vod.name } ?: vod.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = strings.closeBtn)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(210.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DeepObsidian),
                        contentAlignment = Alignment.Center
                    ) {
                        val poster = info?.posterUrl?.ifBlank { vod.streamIcon } ?: vod.streamIcon
                        if (poster.isNotBlank()) {
                            AsyncImage(
                                model = poster,
                                contentDescription = vod.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Rounded.Movie,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = ElectricCyan
                                )
                                Text(
                                    text = strings.loadingMovieDetails,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        val genre = info?.genre.orEmpty()
                        val release = info?.releaseDate.orEmpty()
                        val duration = info?.duration.orEmpty()
                        val rating = info?.rating?.ifBlank { vod.rating } ?: vod.rating

                        if (genre.isNotBlank() || release.isNotBlank() || duration.isNotBlank()) {
                            Text(
                                text = listOfNotNull(
                                    genre.takeIf { it.isNotBlank() },
                                    release.takeIf { it.isNotBlank() },
                                    duration.takeIf { it.isNotBlank() }
                                ).joinToString(" • "),
                                style = MaterialTheme.typography.labelLarge,
                                color = ElectricCyan
                            )
                        }

                        if (rating.isNotBlank() && (rating.toDoubleOrNull() ?: 0.0) > 0.0) {
                            Text(
                                text = "IMDb: $rating",
                                style = MaterialTheme.typography.labelLarge,
                                color = AmberGold
                            )
                        }

                        val plot = info?.plot.orEmpty()
                        Text(
                            text = plot.ifBlank { strings.noPlotAvailable },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 6,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (!info?.director.isNullOrBlank()) {
                            Text(
                                text = "${strings.directorLabel}: ${info?.director}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (!info?.cast.isNullOrBlank()) {
                            Text(
                                text = "${strings.castLabel}: ${info?.cast}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onPlay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = DeepObsidian
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vod_play_button")
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.playMovieBtn, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onToggleFavorite,
                        border = BorderStroke(
                            1.dp,
                            if (vod.isFavorite) AmberGold else GlassBorder
                        ),
                        modifier = Modifier.testTag("vod_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (vod.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                            contentDescription = null,
                            tint = if (vod.isFavorite) AmberGold else Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (vod.isFavorite) strings.inFavoritesBtn else strings.addToFavoritesBtn,
                            color = if (vod.isFavorite) AmberGold else Color.White
                        )
                    }
                }
            }
        }
    }
}
