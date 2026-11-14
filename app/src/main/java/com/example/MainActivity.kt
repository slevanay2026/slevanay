package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppDestination
import com.example.ui.MainViewModel
import com.example.ui.screens.ContentManagerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveTvScreen
import com.example.ui.screens.MoviesScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.SeriesScreen
import com.example.ui.theme.BroadcastCoral
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.NovaTvTheme
import com.example.ui.theme.SlateSurface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NovaTvTheme {
                NovaTvApp()
            }
        }
    }
}

@Composable
fun NovaTvApp(
    viewModel: MainViewModel = viewModel()
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.HOME) }

    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val activeProfile by viewModel.activeProfile.collectAsStateWithLifecycle()
    val visibleLiveCategories by viewModel.visibleLiveCategories.collectAsStateWithLifecycle()
    val visibleVodCategories by viewModel.visibleVodCategories.collectAsStateWithLifecycle()
    val visibleSeriesCategories by viewModel.visibleSeriesCategories.collectAsStateWithLifecycle()
    val allLiveCategories by viewModel.allLiveCategories.collectAsStateWithLifecycle()
    val allVodCategories by viewModel.allVodCategories.collectAsStateWithLifecycle()
    val allSeriesCategories by viewModel.allSeriesCategories.collectAsStateWithLifecycle()
    val allVisibleChannels by viewModel.allVisibleChannels.collectAsStateWithLifecycle()
    val filteredChannels by viewModel.filteredChannels.collectAsStateWithLifecycle()
    val allVisibleVods by viewModel.allVisibleVods.collectAsStateWithLifecycle()
    val filteredVods by viewModel.filteredVods.collectAsStateWithLifecycle()
    val allVisibleSeries by viewModel.allVisibleSeries.collectAsStateWithLifecycle()
    val filteredSeries by viewModel.filteredSeries.collectAsStateWithLifecycle()
    val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()

    val selectedLiveCategoryId by viewModel.selectedLiveCategoryId.collectAsStateWithLifecycle()
    val liveSearchQuery by viewModel.liveSearchQuery.collectAsStateWithLifecycle()
    val liveFavoritesOnly by viewModel.liveFavoritesOnly.collectAsStateWithLifecycle()

    val selectedVodCategoryId by viewModel.selectedVodCategoryId.collectAsStateWithLifecycle()
    val vodSearchQuery by viewModel.vodSearchQuery.collectAsStateWithLifecycle()
    val vodSortOption by viewModel.vodSortOption.collectAsStateWithLifecycle()
    val vodFavoritesOnly by viewModel.vodFavoritesOnly.collectAsStateWithLifecycle()

    val selectedSeriesCategoryId by viewModel.selectedSeriesCategoryId.collectAsStateWithLifecycle()
    val seriesSearchQuery by viewModel.seriesSearchQuery.collectAsStateWithLifecycle()
    val seriesFavoritesOnly by viewModel.seriesFavoritesOnly.collectAsStateWithLifecycle()

    val selectedVod by viewModel.selectedVod.collectAsStateWithLifecycle()
    val selectedVodInfo by viewModel.selectedVodInfo.collectAsStateWithLifecycle()
    val selectedSeries by viewModel.selectedSeries.collectAsStateWithLifecycle()
    val selectedSeriesDetail by viewModel.selectedSeriesDetail.collectAsStateWithLifecycle()
    val isDetailLoading by viewModel.isDetailLoading.collectAsStateWithLifecycle()

    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val errorBanner by viewModel.errorBanner.collectAsStateWithLifecycle()
    val activePlayback by viewModel.activePlayback.collectAsStateWithLifecycle()
    val activeEpg by viewModel.activeEpg.collectAsStateWithLifecycle()

    if (currentDestination != AppDestination.HOME && activePlayback == null) {
        BackHandler {
            currentDestination = AppDestination.HOME
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavigationRail = maxWidth >= 600.dp

            Scaffold(
                containerColor = DeepObsidian,
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    if (!useNavigationRail && activePlayback == null) {
                        NavigationBar(
                            containerColor = MidnightNavy,
                            modifier = Modifier.navigationBarsPadding()
                        ) {
                            AppDestination.entries.forEach { dest ->
                                val selected = currentDestination == dest
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentDestination = dest },
                                    icon = {
                                        Icon(
                                            imageVector = destinationIcon(dest, selected),
                                            contentDescription = dest.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = dest.label,
                                            maxLines = 1,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = DeepObsidian,
                                        selectedTextColor = ElectricCyan,
                                        indicatorColor = ElectricCyan,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("nav_${dest.route}")
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useNavigationRail) {
                        NavigationRail(
                            containerColor = MidnightNavy,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(88.dp),
                            header = {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = ElectricCyan.copy(alpha = 0.14f),
                                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f)),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.Tv,
                                            contentDescription = "NovaTV",
                                            tint = ElectricCyan
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        ) {
                            AppDestination.entries.forEach { dest ->
                                val selected = currentDestination == dest
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { currentDestination = dest },
                                    icon = {
                                        Icon(
                                            imageVector = destinationIcon(dest, selected),
                                            contentDescription = dest.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = dest.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = DeepObsidian,
                                        selectedTextColor = ElectricCyan,
                                        indicatorColor = ElectricCyan,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .testTag("nav_${dest.route}")
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        // Top Status / Error Banner
                        AnimatedVisibility(visible = errorBanner != null || statusBanner != null) {
                            val isError = errorBanner != null
                            val message = errorBanner ?: statusBanner.orEmpty()
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = if (isError) BroadcastCoral.copy(alpha = 0.16f) else SlateSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (isError) BroadcastCoral else ElectricCyan.copy(alpha = 0.6f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isError) BroadcastCoral else Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { viewModel.dismissMessages() }) {
                                        Icon(
                                            Icons.Rounded.Close,
                                            contentDescription = "Bildirimi kapat",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        when (currentDestination) {
                            AppDestination.HOME -> HomeScreen(
                                activeProfile = activeProfile,
                                channels = allVisibleChannels,
                                vods = allVisibleVods,
                                seriesList = allVisibleSeries,
                                watchHistory = watchHistory,
                                isSyncing = isSyncing,
                                onNavigate = { currentDestination = it },
                                onSyncNow = { viewModel.syncActiveProfile() },
                                onPlayChannel = { viewModel.playLiveChannel(it) },
                                onOpenVod = {
                                    currentDestination = AppDestination.MOVIES
                                    viewModel.openVodDetail(it)
                                },
                                onOpenSeries = {
                                    currentDestination = AppDestination.SERIES
                                    viewModel.openSeriesDetail(it)
                                },
                                onResumeHistory = { viewModel.resumeWatchHistory(it) }
                            )

                            AppDestination.LIVE -> LiveTvScreen(
                                hasActiveProfile = activeProfile != null,
                                categories = visibleLiveCategories,
                                allChannels = allVisibleChannels,
                                filteredChannels = filteredChannels,
                                selectedCategoryId = selectedLiveCategoryId,
                                searchQuery = liveSearchQuery,
                                favoritesOnly = liveFavoritesOnly,
                                onSelectCategory = viewModel::selectLiveCategory,
                                onSearchChange = viewModel::updateLiveSearch,
                                onToggleFavoritesOnly = viewModel::toggleLiveFavoritesOnly,
                                onPlayChannel = viewModel::playLiveChannel,
                                onToggleFavorite = viewModel::toggleChannelFavorite,
                                onOpenManager = { currentDestination = AppDestination.MANAGER }
                            )

                            AppDestination.MOVIES -> MoviesScreen(
                                hasActiveProfile = activeProfile != null,
                                categories = visibleVodCategories,
                                allVods = allVisibleVods,
                                filteredVods = filteredVods,
                                selectedCategoryId = selectedVodCategoryId,
                                searchQuery = vodSearchQuery,
                                sortOption = vodSortOption,
                                favoritesOnly = vodFavoritesOnly,
                                selectedVod = selectedVod,
                                selectedVodInfo = selectedVodInfo,
                                isDetailLoading = isDetailLoading,
                                onSelectCategory = viewModel::selectVodCategory,
                                onSearchChange = viewModel::updateVodSearch,
                                onSortChange = viewModel::updateVodSort,
                                onToggleFavoritesOnly = viewModel::toggleVodFavoritesOnly,
                                onOpenVod = viewModel::openVodDetail,
                                onCloseVod = viewModel::closeVodDetail,
                                onPlayVod = { viewModel.playVod(it) },
                                onToggleFavorite = viewModel::toggleVodFavorite,
                                onOpenManager = { currentDestination = AppDestination.MANAGER }
                            )

                            AppDestination.SERIES -> SeriesScreen(
                                hasActiveProfile = activeProfile != null,
                                categories = visibleSeriesCategories,
                                allSeries = allVisibleSeries,
                                filteredSeries = filteredSeries,
                                selectedCategoryId = selectedSeriesCategoryId,
                                searchQuery = seriesSearchQuery,
                                favoritesOnly = seriesFavoritesOnly,
                                selectedSeries = selectedSeries,
                                selectedSeriesDetail = selectedSeriesDetail,
                                isDetailLoading = isDetailLoading,
                                onSelectCategory = viewModel::selectSeriesCategory,
                                onSearchChange = viewModel::updateSeriesSearch,
                                onToggleFavoritesOnly = viewModel::toggleSeriesFavoritesOnly,
                                onOpenSeries = viewModel::openSeriesDetail,
                                onCloseSeries = viewModel::closeSeriesDetail,
                                onPlayEpisode = { s, ep -> viewModel.playSeriesEpisode(s, ep) },
                                onToggleFavorite = viewModel::toggleSeriesFavorite,
                                onOpenManager = { currentDestination = AppDestination.MANAGER }
                            )

                            AppDestination.MANAGER -> ContentManagerScreen(
                                profiles = profiles,
                                activeProfile = activeProfile,
                                liveCategories = allLiveCategories,
                                vodCategories = allVodCategories,
                                seriesCategories = allSeriesCategories,
                                isSyncing = isSyncing,
                                onConnectAndSync = { id, name, url, user, pass ->
                                    viewModel.connectAndSyncPanel(id, name, url, user, pass)
                                },
                                onSwitchProfile = viewModel::switchProfile,
                                onSyncActiveProfile = viewModel::syncActiveProfile,
                                onDeleteProfile = viewModel::deleteProfile,
                                onToggleCategoryVisibility = viewModel::toggleCategoryVisibility,
                                onToggleCategoryPin = viewModel::toggleCategoryPin,
                                onMoveCategory = viewModel::moveCategory,
                                onSetAllCategoriesVisibility = viewModel::setAllCategoriesVisibility,
                                onUpdateLiveFormat = viewModel::updateLiveFormat,
                                onClearHistory = viewModel::clearHistory
                            )
                        }
                    }
                }
            }
        }

        // Fullscreen Media3 Player Overlay
        val playback = activePlayback
        if (playback != null) {
            PlayerScreen(
                request = playback,
                epgItems = activeEpg,
                okHttpClient = viewModel.repository.okHttpClient,
                onZapChannel = viewModel::zapLiveChannel,
                onSaveProgress = viewModel::savePlaybackProgress,
                onClose = viewModel::stopPlayback
            )
        }
    }
}

private fun destinationIcon(destination: AppDestination, selected: Boolean): ImageVector {
    return when (destination) {
        AppDestination.HOME -> if (selected) Icons.Rounded.Home else Icons.Outlined.Home
        AppDestination.LIVE -> if (selected) Icons.Rounded.LiveTv else Icons.Outlined.LiveTv
        AppDestination.MOVIES -> if (selected) Icons.Rounded.Movie else Icons.Outlined.Movie
        AppDestination.SERIES -> if (selected) Icons.Rounded.VideoLibrary else Icons.Outlined.VideoLibrary
        AppDestination.MANAGER -> if (selected) Icons.Rounded.Tune else Icons.Outlined.Tune
    }
}
