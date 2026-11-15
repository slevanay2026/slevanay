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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.VideoLibrary
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.SeriesEntity
import com.example.data.model.XtreamEpisode
import com.example.data.model.XtreamSeriesDetail
import com.example.ui.components.TvCategoryRowItem
import com.example.ui.components.TvEmptyStateCard
import com.example.ui.components.TvFocusableCard
import com.example.ui.components.TvPosterCard
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SlateSurface

@Composable
fun SeriesScreen(
    hasActiveProfile: Boolean,
    categories: List<CategoryEntity>,
    allSeries: List<SeriesEntity>,
    filteredSeries: List<SeriesEntity>,
    selectedCategoryId: String?,
    searchQuery: String,
    favoritesOnly: Boolean,
    selectedSeries: SeriesEntity?,
    selectedSeriesDetail: XtreamSeriesDetail?,
    isDetailLoading: Boolean,
    onSelectCategory: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onOpenSeries: (SeriesEntity) -> Unit,
    onCloseSeries: () -> Unit,
    onPlayEpisode: (SeriesEntity, XtreamEpisode) -> Unit,
    onToggleFavorite: (SeriesEntity) -> Unit,
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
                title = strings.seriesPanelRequiredTitle,
                description = strings.seriesPanelRequiredDesc,
                icon = Icons.Rounded.VideoLibrary,
                actionLabel = strings.addPanelBtn,
                onAction = onOpenManager
            )
        }
        return
    }

    val countsByCat = remember(allSeries) {
        allSeries.groupingBy { it.categoryId }.eachCount()
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
                            text = strings.seriesCategoriesTitle,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            item {
                                TvCategoryRowItem(
                                    name = strings.allSeriesTitle,
                                    count = allSeries.size,
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
                    SeriesSearchHeader(
                        searchQuery = searchQuery,
                        favoritesOnly = favoritesOnly,
                        count = filteredSeries.size,
                        onSearchChange = onSearchChange,
                        onToggleFavoritesOnly = onToggleFavoritesOnly
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SeriesPosterGrid(series = filteredSeries, onOpenSeries = onOpenSeries)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                SeriesSearchHeader(
                    searchQuery = searchQuery,
                    favoritesOnly = favoritesOnly,
                    count = filteredSeries.size,
                    onSearchChange = onSearchChange,
                    onToggleFavoritesOnly = onToggleFavoritesOnly
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { onSelectCategory(null) },
                            label = { Text("${strings.allSeriesTitle} (${allSeries.size})") }
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
                SeriesPosterGrid(series = filteredSeries, onOpenSeries = onOpenSeries)
            }
        }
    }

    if (selectedSeries != null) {
        SeriesDetailDialog(
            series = selectedSeries,
            detail = selectedSeriesDetail,
            isLoading = isDetailLoading,
            onDismiss = onCloseSeries,
            onToggleFavorite = { onToggleFavorite(selectedSeries) },
            onPlayEpisode = { episode ->
                onCloseSeries()
                onPlayEpisode(selectedSeries, episode)
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SeriesSearchHeader(
    searchQuery: String,
    favoritesOnly: Boolean,
    count: Int,
    onSearchChange: (String) -> Unit,
    onToggleFavoritesOnly: () -> Unit
) {
    val strings = LocalAppStrings.current

    val searchField: @Composable (Modifier) -> Unit = { fieldModifier ->
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text(strings.searchSeriesHint, maxLines = 1) },
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
            modifier = fieldModifier.testTag("series_search_input")
        )
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
                favChip()
            }
        }
    }
}

@Composable
private fun SeriesPosterGrid(
    series: List<SeriesEntity>,
    onOpenSeries: (SeriesEntity) -> Unit
) {
    val strings = LocalAppStrings.current

    if (series.isEmpty()) {
        TvEmptyStateCard(
            title = strings.noSeriesFoundTitle,
            description = strings.noSeriesFoundDesc,
            icon = Icons.Rounded.VideoLibrary
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
        items(series, key = { it.id }) { item ->
            TvPosterCard(
                title = item.name,
                posterUrl = item.cover,
                rating = item.rating,
                badge = strings.seriesBadge,
                isFavorite = item.isFavorite,
                fallbackIcon = Icons.Rounded.LiveTv,
                onClick = { onOpenSeries(item) }
            )
        }
    }
}

@Composable
private fun SeriesDetailDialog(
    series: SeriesEntity,
    detail: XtreamSeriesDetail?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPlayEpisode: (XtreamEpisode) -> Unit
) {
    val strings = LocalAppStrings.current
    val seasons = detail?.seasons.orEmpty()
    val seasonNumbers = remember(seasons) { seasons.keys.toList().sorted() }
    var selectedSeason by remember(seasonNumbers) {
        mutableIntStateOf(seasonNumbers.firstOrNull() ?: 1)
    }

    LaunchedEffect(seasonNumbers) {
        if (seasonNumbers.isNotEmpty() && selectedSeason !in seasonNumbers) {
            selectedSeason = seasonNumbers.first()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(20.dp)
                .widthIn(max = 820.dp)
                .fillMaxWidth()
                .heightIn(max = 680.dp),
            shape = RoundedCornerShape(24.dp),
            color = MidnightNavy,
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = detail?.name?.ifBlank { series.name } ?: series.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    OutlinedButton(
                        onClick = onToggleFavorite,
                        border = BorderStroke(1.dp, if (series.isFavorite) AmberGold else GlassBorder)
                    ) {
                        Icon(
                            imageVector = if (series.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                            contentDescription = null,
                            tint = if (series.isFavorite) AmberGold else Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (series.isFavorite) strings.inFavoritesBtn else strings.favoriteShort,
                            color = if (series.isFavorite) AmberGold else Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = strings.closeBtn)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DeepObsidian),
                        contentAlignment = Alignment.Center
                    ) {
                        val cover = detail?.coverUrl?.ifBlank { series.cover } ?: series.cover
                        if (cover.isNotBlank()) {
                            AsyncImage(
                                model = cover,
                                contentDescription = series.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Rounded.VideoLibrary, contentDescription = null, tint = ElectricCyan)
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val genre = detail?.genre?.ifBlank { series.genre } ?: series.genre
                        if (genre.isNotBlank()) {
                            Text(
                                text = genre,
                                style = MaterialTheme.typography.labelLarge,
                                color = ElectricCyan
                            )
                        }
                        val plot = detail?.plot?.ifBlank { series.plot } ?: series.plot
                        Text(
                            text = plot.ifBlank { strings.noSeriesDescAvailable },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ElectricCyan)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(strings.loadingSeasons)
                        }
                    }
                } else if (seasonNumbers.isEmpty()) {
                    Text(
                        text = strings.noEpisodesAvailable,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(seasonNumbers) { seasonNum ->
                            FilterChip(
                                selected = selectedSeason == seasonNum,
                                onClick = { selectedSeason = seasonNum },
                                label = { Text("${strings.seasonSuffix} $seasonNum") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan,
                                    selectedLabelColor = DeepObsidian
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val episodes = seasons[selectedSeason].orEmpty()
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        items(episodes, key = { it.id }) { ep ->
                            TvFocusableCard(
                                onClick = { onPlayEpisode(ep) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "E${ep.episodeNum}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = ElectricCyan,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ep.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (ep.duration.isNotBlank()) {
                                            Text(
                                                text = "${strings.durationPrefix}: ${ep.duration}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Rounded.PlayArrow,
                                        contentDescription = strings.playMovieBtn,
                                        tint = ElectricCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
