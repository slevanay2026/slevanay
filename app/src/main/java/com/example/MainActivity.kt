package com.example

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.ai.configureAppCheck
import com.example.ui.AppDestination
import com.example.ui.MainViewModel
import com.example.ui.components.LanguageFlagIcon
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.stringsForLanguage
import com.example.ui.i18n.themeName
import com.example.ui.screens.ActivationScreen
import com.example.ui.screens.AiGuideScreen
import com.example.ui.screens.ContentManagerScreen
import com.example.ui.screens.FreeKurdishScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveTvScreen
import com.example.ui.screens.MoviesScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.SeriesScreen
import com.example.ui.screens.YouTubeScreen
import com.example.ui.theme.AppThemeOption
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
        configureAppCheck(this, intent)
        Coil.setImageLoader {
            ImageLoader.Builder(applicationContext)
                .memoryCache {
                    MemoryCache.Builder(applicationContext)
                        .maxSizePercent(0.20)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(applicationContext.cacheDir.resolve("jhe_image_cache"))
                        .maxSizePercent(0.03)
                        .build()
                }
                .crossfade(true)
                .allowRgb565(true)
                .respectCacheHeaders(false)
                .build()
        }
        enableEdgeToEdge()
        setContent {
            NovaTvApp()
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
    val allChannels by viewModel.allChannels.collectAsStateWithLifecycle()
    val filteredChannels by viewModel.filteredChannels.collectAsStateWithLifecycle()
    val allVisibleVods by viewModel.allVisibleVods.collectAsStateWithLifecycle()
    val filteredVods by viewModel.filteredVods.collectAsStateWithLifecycle()
    val allVisibleSeries by viewModel.allVisibleSeries.collectAsStateWithLifecycle()
    val filteredSeries by viewModel.filteredSeries.collectAsStateWithLifecycle()
    val recentlyWatchedChannels by viewModel.recentlyWatchedChannels.collectAsStateWithLifecycle()
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
    val aiUiState by viewModel.aiUiState.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
    val isAppActivated by viewModel.isAppActivated.collectAsStateWithLifecycle()
    val isParentalEnabled by viewModel.isParentalEnabled.collectAsStateWithLifecycle()
    val hasParentalPin by viewModel.hasParentalPin.collectAsStateWithLifecycle()
    val isParentalUnlocked by viewModel.isParentalUnlocked.collectAsStateWithLifecycle()
    val youtubeBookmarks by viewModel.youtubeBookmarks.collectAsStateWithLifecycle()
    val freeKurdishState by viewModel.freeKurdishState.collectAsStateWithLifecycle()
    val freeKurdishFavorites by viewModel.freeKurdishFavorites.collectAsStateWithLifecycle()
    val localWebPanelUrl by viewModel.localWebPanelUrl.collectAsStateWithLifecycle()
    val remoteWebsiteUrl by viewModel.remoteWebsiteUrl.collectAsStateWithLifecycle()
    val remoteAnnouncement by viewModel.remoteAnnouncement.collectAsStateWithLifecycle()
    val remoteWebsiteLastSync by viewModel.remoteWebsiteLastSync.collectAsStateWithLifecycle()
    val isWebPanelSyncing by viewModel.isWebPanelSyncing.collectAsStateWithLifecycle()
    val strings = remember(appLanguage) { stringsForLanguage(appLanguage) }
    var langMenuExpanded by remember { mutableStateOf(false) }
    var themeMenuExpanded by remember { mutableStateOf(false) }

    if (isAppActivated == true && currentDestination != AppDestination.HOME && activePlayback == null) {
        BackHandler {
            currentDestination = AppDestination.HOME
        }
    }

    NovaTvTheme(themeOption = appTheme) {
    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalLayoutDirection provides appLanguage.layoutDirection
    ) {
    when (isAppActivated) {
        null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DeepObsidian),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ElectricCyan)
            }
        }
        false -> {
            ActivationScreen(
                currentLanguage = appLanguage,
                onSelectLanguage = viewModel::setAppLanguage,
                onActivate = viewModel::submitActivationCode
            )
        }
        true -> {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
    ) {
        val configuration = LocalConfiguration.current
        val isTvDevice = (configuration.uiMode and Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavigationRail = maxWidth >= 600.dp || isTvDevice

            Scaffold(
                containerColor = DeepObsidian,
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    if (!useNavigationRail && activePlayback == null) {
                        NavigationBar(
                            containerColor = MidnightNavy,
                            modifier = Modifier.navigationBarsPadding()
                        ) {
                            val bottomNavDestinations = remember {
                                listOf(
                                    AppDestination.HOME,
                                    AppDestination.LIVE,
                                    AppDestination.KURDISH_FREE,
                                    AppDestination.MOVIES,
                                    AppDestination.SERIES
                                )
                            }
                            bottomNavDestinations.forEach { dest ->
                                val selected = currentDestination == dest
                                val destLabel = destinationLabel(dest, strings)
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentDestination = dest },
                                    icon = {
                                        Icon(
                                            imageVector = destinationIcon(dest, selected),
                                            contentDescription = destLabel
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = destLabel,
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis,
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
                        .then(
                            if (isTvDevice) Modifier.padding(horizontal = 14.dp, vertical = 8.dp) else Modifier
                        )
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
                                    color = Color(0xFF101E39),
                                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.55f)),
                                    modifier = Modifier.size(52.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.app_icon_foreground),
                                        contentDescription = "JHE HAKAR Logo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(14.dp))
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        ) {
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AppDestination.entries.forEach { dest ->
                                    val selected = currentDestination == dest
                                    val destLabel = destinationLabel(dest, strings)
                                    NavigationRailItem(
                                        selected = selected,
                                        onClick = { currentDestination = dest },
                                        icon = {
                                            Icon(
                                                imageVector = destinationIcon(dest, selected),
                                                contentDescription = destLabel
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = destLabel,
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
                                            .padding(vertical = 2.dp)
                                            .testTag("nav_${dest.route}")
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        // Top Quick Navigation, Account & Language Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!useNavigationRail) {
                                OutlinedButton(
                                    onClick = { currentDestination = AppDestination.YOUTUBE },
                                    border = BorderStroke(
                                        1.dp,
                                        if (currentDestination == AppDestination.YOUTUBE) ElectricCyan else GlassBorder
                                    ),
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("top_youtube_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SmartDisplay,
                                        contentDescription = strings.navYoutube,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = strings.navYoutube,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }

                                OutlinedButton(
                                    onClick = { currentDestination = AppDestination.MANAGER },
                                    border = BorderStroke(
                                        1.dp,
                                        if (currentDestination == AppDestination.MANAGER) ElectricCyan else GlassBorder
                                    ),
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("top_manager_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Tune,
                                        contentDescription = strings.navManager,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = strings.navManager,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }

                                OutlinedButton(
                                    onClick = { currentDestination = AppDestination.AI },
                                    border = BorderStroke(
                                        1.dp,
                                        if (currentDestination == AppDestination.AI) ElectricCyan else GlassBorder
                                    ),
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("top_ai_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.AutoAwesome,
                                        contentDescription = strings.navAi,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = strings.navAi,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }

                            Box {
                                OutlinedButton(
                                    onClick = { themeMenuExpanded = true },
                                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.55f)),
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("top_theme_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Palette,
                                        contentDescription = strings.themeBtnLabel,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(appTheme.palette.electricCyan)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = strings.themeName(appTheme),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                DropdownMenu(
                                    expanded = themeMenuExpanded,
                                    onDismissRequest = { themeMenuExpanded = false }
                                ) {
                                    AppThemeOption.entries.forEach { theme ->
                                        val isSelected = theme == appTheme
                                        DropdownMenuItem(
                                            leadingIcon = {
                                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(14.dp)
                                                            .clip(CircleShape)
                                                            .background(theme.palette.midnightNavy)
                                                            .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .size(14.dp)
                                                            .clip(CircleShape)
                                                            .background(theme.palette.electricCyan)
                                                    )
                                                }
                                            },
                                            trailingIcon = if (isSelected) {
                                                {
                                                    Icon(
                                                        imageVector = Icons.Rounded.CheckCircle,
                                                        contentDescription = null,
                                                        tint = theme.palette.electricCyan,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            } else null,
                                            text = {
                                                Text(
                                                    text = strings.themeName(theme),
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                viewModel.setAppTheme(theme)
                                                themeMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Box {
                                OutlinedButton(
                                    onClick = { langMenuExpanded = true },
                                    border = BorderStroke(1.dp, GlassBorder),
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("top_language_button")
                                ) {
                                    LanguageFlagIcon(
                                        language = appLanguage,
                                        width = 24.dp,
                                        height = 16.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = appLanguage.nativeName,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                DropdownMenu(
                                    expanded = langMenuExpanded,
                                    onDismissRequest = { langMenuExpanded = false }
                                ) {
                                    AppLanguage.entries.forEach { lang ->
                                        DropdownMenuItem(
                                            leadingIcon = {
                                                LanguageFlagIcon(
                                                    language = lang,
                                                    width = 28.dp,
                                                    height = 18.dp
                                                )
                                            },
                                            text = {
                                                Text(
                                                    text = "${lang.nativeName} (${lang.displayName})",
                                                    fontWeight = if (lang == appLanguage) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                viewModel.setAppLanguage(lang)
                                                langMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

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
                                recentlyWatchedChannels = recentlyWatchedChannels,
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
                                onResumeHistory = { viewModel.resumeWatchHistory(it) },
                                onRemoveHistoryItem = { viewModel.removeWatchHistoryItem(it) },
                                onClearHistory = { viewModel.clearHistory() },
                                remoteAnnouncement = remoteAnnouncement
                            )

                            AppDestination.LIVE -> LiveTvScreen(
                                hasActiveProfile = activeProfile != null,
                                categories = visibleLiveCategories,
                                allChannels = allVisibleChannels,
                                filteredChannels = filteredChannels,
                                selectedCategoryId = selectedLiveCategoryId,
                                searchQuery = liveSearchQuery,
                                favoritesOnly = liveFavoritesOnly,
                                isParentalEnabled = isParentalEnabled,
                                hasParentalPin = hasParentalPin,
                                isParentalUnlocked = isParentalUnlocked,
                                hiddenChannelsCount = allChannels.count { it.isHidden },
                                onSelectCategory = viewModel::selectLiveCategory,
                                onSearchChange = viewModel::updateLiveSearch,
                                onToggleFavoritesOnly = viewModel::toggleLiveFavoritesOnly,
                                onPlayChannel = viewModel::playLiveChannel,
                                onToggleFavorite = viewModel::toggleChannelFavorite,
                                onToggleHideChannel = viewModel::toggleChannelHidden,
                                onSaveParentalPin = viewModel::setupOrChangeParentalPin,
                                onVerifyParentalPin = viewModel::unlockParentalControl,
                                onLockParental = viewModel::lockParentalControl,
                                onOpenManager = { currentDestination = AppDestination.MANAGER }
                            )

                            AppDestination.KURDISH_FREE -> FreeKurdishScreen(
                                uiState = freeKurdishState,
                                favoriteUrls = freeKurdishFavorites,
                                onEnsureLoaded = viewModel::ensureFreeKurdishChannelsLoaded,
                                onRefresh = viewModel::refreshFreeKurdishChannels,
                                onPlayChannel = viewModel::playFreeKurdishChannel,
                                onToggleFavorite = viewModel::toggleFreeKurdishFavorite
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

                            AppDestination.YOUTUBE -> YouTubeScreen(
                                bookmarks = youtubeBookmarks,
                                onAddBookmark = viewModel::addYoutubeBookmark,
                                onRemoveBookmark = viewModel::removeYoutubeBookmark,
                                onNotify = viewModel::showStatusBanner,
                                onExitToHome = { currentDestination = AppDestination.HOME }
                            )

                            AppDestination.AI -> AiGuideScreen(
                                uiState = aiUiState,
                                channelCount = allVisibleChannels.size,
                                vodCount = allVisibleVods.size,
                                seriesCount = allVisibleSeries.size,
                                onAskPrompt = viewModel::askGeminiAdvice,
                                onReset = viewModel::resetAiState
                            )

                            AppDestination.MANAGER -> ContentManagerScreen(
                                currentLanguage = appLanguage,
                                onSelectLanguage = viewModel::setAppLanguage,
                                currentTheme = appTheme,
                                onSelectTheme = viewModel::setAppTheme,
                                profiles = profiles,
                                activeProfile = activeProfile,
                                liveCategories = allLiveCategories,
                                vodCategories = allVodCategories,
                                seriesCategories = allSeriesCategories,
                                allChannels = allChannels,
                                isSyncing = isSyncing,
                                isParentalEnabled = isParentalEnabled,
                                hasParentalPin = hasParentalPin,
                                isParentalUnlocked = isParentalUnlocked,
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
                                onClearHistory = viewModel::clearHistory,
                                onSaveParentalPin = viewModel::setupOrChangeParentalPin,
                                onVerifyParentalPin = viewModel::unlockParentalControl,
                                onLockParental = viewModel::lockParentalControl,
                                onSetParentalEnabled = viewModel::setParentalControlEnabled,
                                onToggleChannelHidden = viewModel::toggleChannelHidden,
                                onUnhideAllChannels = viewModel::unhideAllChannels,
                                localWebPanelUrl = localWebPanelUrl,
                                remoteWebsiteUrl = remoteWebsiteUrl,
                                remoteWebsiteLastSync = remoteWebsiteLastSync,
                                isWebPanelSyncing = isWebPanelSyncing,
                                onToggleLocalWebPanel = viewModel::toggleLocalWebPanel,
                                onConnectRemoteWebsite = viewModel::connectRemoteWebsitePanel,
                                onDisconnectRemoteWebsite = viewModel::disconnectRemoteWebsitePanel,
                                onShowBanner = viewModel::showStatusBanner
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
    }
    }
    }
}

private fun destinationLabel(destination: AppDestination, strings: AppStrings): String {
    return when (destination) {
        AppDestination.HOME -> strings.navHome
        AppDestination.LIVE -> strings.navLive
        AppDestination.KURDISH_FREE -> strings.navKurdishFree
        AppDestination.MOVIES -> strings.navMovies
        AppDestination.SERIES -> strings.navSeries
        AppDestination.YOUTUBE -> strings.navYoutube
        AppDestination.AI -> strings.navAi
        AppDestination.MANAGER -> strings.navManager
    }
}

private fun destinationIcon(destination: AppDestination, selected: Boolean): ImageVector {
    return when (destination) {
        AppDestination.HOME -> if (selected) Icons.Rounded.Home else Icons.Outlined.Home
        AppDestination.LIVE -> if (selected) Icons.Rounded.LiveTv else Icons.Outlined.LiveTv
        AppDestination.KURDISH_FREE -> if (selected) Icons.Rounded.Public else Icons.Outlined.Public
        AppDestination.MOVIES -> if (selected) Icons.Rounded.Movie else Icons.Outlined.Movie
        AppDestination.SERIES -> if (selected) Icons.Rounded.VideoLibrary else Icons.Outlined.VideoLibrary
        AppDestination.YOUTUBE -> if (selected) Icons.Rounded.SmartDisplay else Icons.Outlined.SmartDisplay
        AppDestination.AI -> if (selected) Icons.Rounded.AutoAwesome else Icons.Outlined.AutoAwesome
        AppDestination.MANAGER -> if (selected) Icons.Rounded.Tune else Icons.Outlined.Tune
    }
}
