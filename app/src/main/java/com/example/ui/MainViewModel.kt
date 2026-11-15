package com.example.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiAssistantRepository
import com.example.ai.AiUiState
import com.example.data.local.AppDatabase
import com.example.data.local.CategoryEntity
import com.example.data.local.ChannelEntity
import com.example.data.local.SeriesEntity
import com.example.data.local.VodEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.XtreamProfileEntity
import com.example.data.model.AppActivationHelper
import com.example.data.model.ContentType
import com.example.data.model.DefaultXtreamPreset
import com.example.data.model.OttM3uParser
import com.example.data.model.ParentalControlHelper
import com.example.data.model.PlaybackRequest
import com.example.data.model.RecentChannelsHelper
import com.example.data.model.VodSortOption
import com.example.data.model.XtreamEpgItem
import com.example.data.model.XtreamEpisode
import com.example.data.model.XtreamSeriesDetail
import com.example.data.model.XtreamVodInfo
import com.example.data.remote.RemoteWebPanelClient
import com.example.data.remote.RemoteWebPanelParser
import com.example.data.remote.WebPanelCommandHandler
import com.example.data.remote.WebPanelServer
import com.example.data.remote.WebPanelSnapshot
import com.example.data.remote.XtreamClient
import com.example.data.repository.FreeKurdishChannel
import com.example.data.repository.FreeKurdishDialect
import com.example.data.repository.FreeKurdishRepository
import com.example.data.repository.FreeKurdishUiState
import com.example.data.repository.XtreamRepository
import com.example.ui.i18n.AppLanguage
import com.example.ui.theme.AppThemeOption
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private val Context.slevanyDataStore by preferencesDataStore(name = "slevany_tv_prefs")
private val KEY_PRESET_SEEDED = booleanPreferencesKey("sanaz_preset_seeded")
private val KEY_APP_LANGUAGE = stringPreferencesKey("app_language_code")
private val KEY_APP_THEME = stringPreferencesKey("app_theme_id")
private val KEY_APP_ACTIVATED = booleanPreferencesKey("app_activated_v1")
private val KEY_FREE_KURDISH_FAVORITES = stringPreferencesKey("free_kurdish_favorite_urls")
private val KEY_REMOTE_WEBSITE_URL = stringPreferencesKey("remote_website_panel_url")
private val KEY_REMOTE_ANNOUNCEMENT = stringPreferencesKey("remote_website_announcement")
private val KEY_REMOTE_LAST_SYNC = longPreferencesKey("remote_website_last_sync")
private val KEY_CUSTOM_WEB_CHANNELS = stringPreferencesKey("custom_web_panel_channels")
private val KEY_YOUTUBE_BOOKMARKS = stringPreferencesKey("youtube_saved_bookmarks")
private val KEY_PARENTAL_ENABLED = booleanPreferencesKey("parental_control_enabled")
private val KEY_PARENTAL_PIN_HASH = stringPreferencesKey("parental_control_pin_hash")
private const val AI_TAG = "MainViewModelAI"

enum class AppDestination(val route: String, val label: String) {
    HOME("home", "Ana Sayfa"),
    LIVE("live", "Canlı TV"),
    KURDISH_FREE("kurdish_free", "Kürtçe TV"),
    MOVIES("movies", "Filmler"),
    SERIES("series", "Diziler"),
    YOUTUBE("youtube", "YouTube"),
    AI("ai", "AI Rehber"),
    MANAGER("manager", "Yönetim")
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val client = XtreamClient()
    val repository = XtreamRepository(database.xtreamDao(), client)
    private val freeKurdishRepository = FreeKurdishRepository(client.okHttpClient)
    private val remoteWebPanelClient = RemoteWebPanelClient(client.okHttpClient)
    private val aiRepository = AiAssistantRepository()

    val profiles: StateFlow<List<XtreamProfileEntity>> = repository.observeProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeProfile: StateFlow<XtreamProfileEntity?> = repository.observeActiveProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val visibleLiveCategories: StateFlow<List<CategoryEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeVisibleCategories(profile.id, ContentType.LIVE)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visibleVodCategories: StateFlow<List<CategoryEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeVisibleCategories(profile.id, ContentType.VOD)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visibleSeriesCategories: StateFlow<List<CategoryEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeVisibleCategories(profile.id, ContentType.SERIES)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLiveCategories: StateFlow<List<CategoryEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeAllCategories(profile.id, ContentType.LIVE)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVodCategories: StateFlow<List<CategoryEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeAllCategories(profile.id, ContentType.VOD)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSeriesCategories: StateFlow<List<CategoryEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeAllCategories(profile.id, ContentType.SERIES)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVisibleChannels: StateFlow<List<ChannelEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeVisibleChannels(profile.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChannels: StateFlow<List<ChannelEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeAllChannels(profile.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVisibleVods: StateFlow<List<VodEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeVisibleVods(profile.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVisibleSeries: StateFlow<List<SeriesEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeVisibleSeries(profile.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val rawWatchHistory: Flow<List<WatchHistoryEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeWatchHistory(profile.id)
        }

    val watchHistory: StateFlow<List<WatchHistoryEntity>> = combine(
        rawWatchHistory,
        allChannels,
        allVisibleChannels,
        allVisibleVods,
        allVisibleSeries
    ) { history, totalChannels, visibleChannels, visibleVods, visibleSeries ->
        if (totalChannels.isEmpty()) {
            history
        } else {
            val visibleLiveIds = visibleChannels.map { it.streamId }.toHashSet()
            val visibleVodIds = visibleVods.map { it.streamId }.toHashSet()
            val visibleSeriesIds = visibleSeries.map { it.seriesId }.toHashSet()
            history.filter { item ->
                when (ContentType.fromDb(item.contentType)) {
                    ContentType.LIVE -> item.streamId in visibleLiveIds
                    ContentType.VOD -> visibleVodIds.isEmpty() || item.streamId in visibleVodIds
                    ContentType.SERIES -> visibleSeriesIds.isEmpty() || item.streamId in visibleSeriesIds || item.streamId > 0
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val rawRecentChannels: Flow<List<WatchHistoryEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeRecentlyWatchedChannels(profile.id)
        }

    val recentlyWatchedChannels: StateFlow<List<WatchHistoryEntity>> = combine(
        rawRecentChannels,
        allChannels,
        allVisibleChannels
    ) { recent, totalChannels, visibleChannels ->
        RecentChannelsHelper.filterRecentChannels(
            recentHistory = recent,
            allChannels = totalChannels,
            visibleChannels = visibleChannels,
            limit = RecentChannelsHelper.MAX_RECENT_CHANNELS
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filters & Queries
    private val _selectedLiveCategoryId = MutableStateFlow<String?>(null)
    val selectedLiveCategoryId: StateFlow<String?> = _selectedLiveCategoryId.asStateFlow()

    private val _liveSearchQuery = MutableStateFlow("")
    val liveSearchQuery: StateFlow<String> = _liveSearchQuery.asStateFlow()

    private val _liveFavoritesOnly = MutableStateFlow(false)
    val liveFavoritesOnly: StateFlow<Boolean> = _liveFavoritesOnly.asStateFlow()

    val filteredChannels: StateFlow<List<ChannelEntity>> = combine(
        allVisibleChannels,
        _selectedLiveCategoryId,
        _liveSearchQuery,
        _liveFavoritesOnly
    ) { channels, catId, query, favOnly ->
        val q = query.trim().lowercase()
        channels.filter { ch ->
            (!favOnly || ch.isFavorite) &&
                (catId == null || ch.categoryId == catId) &&
                (q.isEmpty() || ch.name.lowercase().contains(q))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedVodCategoryId = MutableStateFlow<String?>(null)
    val selectedVodCategoryId: StateFlow<String?> = _selectedVodCategoryId.asStateFlow()

    private val _vodSearchQuery = MutableStateFlow("")
    val vodSearchQuery: StateFlow<String> = _vodSearchQuery.asStateFlow()

    private val _vodSortOption = MutableStateFlow(VodSortOption.DEFAULT)
    val vodSortOption: StateFlow<VodSortOption> = _vodSortOption.asStateFlow()

    private val _vodFavoritesOnly = MutableStateFlow(false)
    val vodFavoritesOnly: StateFlow<Boolean> = _vodFavoritesOnly.asStateFlow()

    val filteredVods: StateFlow<List<VodEntity>> = combine(
        allVisibleVods,
        _selectedVodCategoryId,
        _vodSearchQuery,
        _vodSortOption,
        _vodFavoritesOnly
    ) { vods, catId, query, sort, favOnly ->
        val q = query.trim().lowercase()
        val filtered = vods.filter { v ->
            (!favOnly || v.isFavorite) &&
                (catId == null || v.categoryId == catId) &&
                (q.isEmpty() || v.name.lowercase().contains(q))
        }
        when (sort) {
            VodSortOption.DEFAULT -> filtered
            VodSortOption.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
            VodSortOption.RATING_DESC -> filtered.sortedByDescending { it.rating.toDoubleOrNull() ?: 0.0 }
            VodSortOption.RECENT_DESC -> filtered.sortedByDescending { it.added }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSeriesCategoryId = MutableStateFlow<String?>(null)
    val selectedSeriesCategoryId: StateFlow<String?> = _selectedSeriesCategoryId.asStateFlow()

    private val _seriesSearchQuery = MutableStateFlow("")
    val seriesSearchQuery: StateFlow<String> = _seriesSearchQuery.asStateFlow()

    private val _seriesFavoritesOnly = MutableStateFlow(false)
    val seriesFavoritesOnly: StateFlow<Boolean> = _seriesFavoritesOnly.asStateFlow()

    val filteredSeries: StateFlow<List<SeriesEntity>> = combine(
        allVisibleSeries,
        _selectedSeriesCategoryId,
        _seriesSearchQuery,
        _seriesFavoritesOnly
    ) { list, catId, query, favOnly ->
        val q = query.trim().lowercase()
        list.filter { s ->
            (!favOnly || s.isFavorite) &&
                (catId == null || s.categoryId == catId) &&
                (q.isEmpty() || s.name.lowercase().contains(q))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Status & Feedback
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _errorBanner = MutableStateFlow<String?>(null)
    val errorBanner: StateFlow<String?> = _errorBanner.asStateFlow()

    // Details & EPG
    private val _selectedVod = MutableStateFlow<VodEntity?>(null)
    val selectedVod: StateFlow<VodEntity?> = _selectedVod.asStateFlow()

    private val _selectedVodInfo = MutableStateFlow<XtreamVodInfo?>(null)
    val selectedVodInfo: StateFlow<XtreamVodInfo?> = _selectedVodInfo.asStateFlow()

    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading: StateFlow<Boolean> = _isDetailLoading.asStateFlow()

    private val _selectedSeries = MutableStateFlow<SeriesEntity?>(null)
    val selectedSeries: StateFlow<SeriesEntity?> = _selectedSeries.asStateFlow()

    private val _selectedSeriesDetail = MutableStateFlow<XtreamSeriesDetail?>(null)
    val selectedSeriesDetail: StateFlow<XtreamSeriesDetail?> = _selectedSeriesDetail.asStateFlow()

    private val _activeEpg = MutableStateFlow<List<XtreamEpgItem>>(emptyList())
    val activeEpg: StateFlow<List<XtreamEpgItem>> = _activeEpg.asStateFlow()

    // Playback
    private val _activePlayback = MutableStateFlow<PlaybackRequest?>(null)
    val activePlayback: StateFlow<PlaybackRequest?> = _activePlayback.asStateFlow()

    // Gemini AI Guide State
    private val _aiUiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val aiUiState: StateFlow<AiUiState> = _aiUiState.asStateFlow()

    // Free Kurdish TV State + Custom Web Panel Channels merged
    private val _rawFreeKurdishState = MutableStateFlow<FreeKurdishUiState>(FreeKurdishUiState.Idle)

    val customWebChannels: StateFlow<List<FreeKurdishChannel>> = getApplication<Application>()
        .slevanyDataStore.data
        .map { prefs ->
            RemoteWebPanelParser.deserializeCustomChannels(prefs[KEY_CUSTOM_WEB_CHANNELS].orEmpty())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val freeKurdishState: StateFlow<FreeKurdishUiState> = combine(
        _rawFreeKurdishState,
        customWebChannels
    ) { state, custom ->
        when (state) {
            is FreeKurdishUiState.Success -> {
                val mergedByUrl = LinkedHashMap<String, FreeKurdishChannel>()
                custom.forEach { mergedByUrl[it.streamUrl.trim().lowercase()] = it }
                state.channels.forEach { ch ->
                    val key = ch.streamUrl.trim().lowercase()
                    if (!mergedByUrl.containsKey(key)) {
                        mergedByUrl[key] = ch
                    }
                }
                state.copy(channels = mergedByUrl.values.toList())
            }
            else -> state
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FreeKurdishUiState.Idle)

    // Web Control Panel & Remote Website Panel State
    private val _localWebPanelUrl = MutableStateFlow<String?>(null)
    val localWebPanelUrl: StateFlow<String?> = _localWebPanelUrl.asStateFlow()

    private val _isWebPanelSyncing = MutableStateFlow(false)
    val isWebPanelSyncing: StateFlow<Boolean> = _isWebPanelSyncing.asStateFlow()

    val remoteWebsiteUrl: StateFlow<String> = getApplication<Application>()
        .slevanyDataStore.data
        .map { prefs -> prefs[KEY_REMOTE_WEBSITE_URL].orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val remoteAnnouncement: StateFlow<String?> = getApplication<Application>()
        .slevanyDataStore.data
        .map { prefs -> prefs[KEY_REMOTE_ANNOUNCEMENT]?.takeIf { it.isNotBlank() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val remoteWebsiteLastSync: StateFlow<Long> = getApplication<Application>()
        .slevanyDataStore.data
        .map { prefs -> prefs[KEY_REMOTE_LAST_SYNC] ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val webPanelServer = WebPanelServer(
        scope = viewModelScope,
        handler = object : WebPanelCommandHandler {
            override fun getSnapshot(): WebPanelSnapshot = WebPanelSnapshot(
                activeProfile = activeProfile.value,
                profiles = profiles.value,
                liveCount = allVisibleChannels.value.size,
                vodCount = allVisibleVods.value.size,
                seriesCount = allVisibleSeries.value.size,
                appTheme = appTheme.value,
                appLanguage = appLanguage.value,
                remoteWebsiteUrl = remoteWebsiteUrl.value,
                remoteAnnouncement = remoteAnnouncement.value,
                customChannels = customWebChannels.value
            )

            override fun connectXtreamFromWeb(
                name: String,
                serverUrl: String,
                username: String,
                password: String
            ) {
                viewModelScope.launch {
                    connectAndSyncPanel(null, name, serverUrl, username, password)
                }
            }

            override fun switchProfileFromWeb(profileId: Long) {
                viewModelScope.launch {
                    val target = profiles.value.firstOrNull { it.id == profileId }
                    if (target != null) {
                        switchProfile(target)
                    }
                }
            }

            override fun syncActiveFromWeb() {
                viewModelScope.launch { syncActiveProfile() }
            }

            override fun playStreamUrlFromWeb(title: String, streamUrl: String) {
                viewModelScope.launch {
                    val syntheticId = -((streamUrl.hashCode() and 0x7FFFFFFF).coerceAtLeast(1))
                    _activePlayback.value = PlaybackRequest(
                        profileId = activeProfile.value?.id ?: 0L,
                        contentType = ContentType.LIVE,
                        streamId = syntheticId,
                        title = title,
                        subtitle = "Web Panel",
                        imageUrl = "",
                        streamUrl = streamUrl,
                        containerExtension = OttM3uParser.inferExtension(streamUrl),
                        startPositionMs = 0L
                    )
                }
            }

            override fun addCustomChannelFromWeb(
                name: String,
                streamUrl: String,
                category: String,
                dialect: String
            ) {
                addCustomWebChannel(name, streamUrl, category, dialect)
            }

            override fun removeCustomChannelFromWeb(streamUrl: String) {
                removeCustomWebChannel(streamUrl)
            }

            override fun setThemeFromWeb(themeId: String) {
                setAppTheme(AppThemeOption.fromId(themeId))
            }

            override fun setLanguageFromWeb(languageCode: String) {
                setAppLanguage(AppLanguage.fromCode(languageCode))
            }

            override fun syncRemoteWebsiteFromWeb(websiteUrl: String) {
                connectRemoteWebsitePanel(websiteUrl)
            }
        }
    )

    val freeKurdishFavorites: StateFlow<Set<String>> = getApplication<Application>()
        .slevanyDataStore.data
        .map { prefs ->
            prefs[KEY_FREE_KURDISH_FAVORITES]
                .orEmpty()
                .lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val appLanguage: StateFlow<AppLanguage> = getApplication<Application>()
        .slevanyDataStore.data
        .map { prefs -> AppLanguage.fromCode(prefs[KEY_APP_LANGUAGE]) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.TURKISH)

    val appTheme: StateFlow<AppThemeOption> = getApplication<Application>()
        .slevanyDataStore.data
        .map { prefs -> AppThemeOption.fromId(prefs[KEY_APP_THEME]) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeOption.CYBER_OBSIDIAN)

    val isAppActivated: StateFlow<Boolean?> = getApplication<Application>()
        .slevanyDataStore.data
        .map<androidx.datastore.preferences.core.Preferences, Boolean?> { prefs ->
            prefs[KEY_APP_ACTIVATED] ?: false
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isParentalEnabled: StateFlow<Boolean> = getApplication<Application>()
        .slevanyDataStore.data
        .map { prefs -> prefs[KEY_PARENTAL_ENABLED] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val hasParentalPin: StateFlow<Boolean> = getApplication<Application>()
        .slevanyDataStore.data
        .map { prefs -> !prefs[KEY_PARENTAL_PIN_HASH].isNullOrBlank() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isParentalUnlocked = MutableStateFlow(false)
    val isParentalUnlocked: StateFlow<Boolean> = _isParentalUnlocked.asStateFlow()

    val youtubeBookmarks: StateFlow<List<Pair<String, String>>> = getApplication<Application>()
        .slevanyDataStore.data
        .map { prefs ->
            val raw = prefs[KEY_YOUTUBE_BOOKMARKS].orEmpty()
            if (raw.isBlank()) emptyList()
            else raw.lines().mapNotNull { line ->
                val idx = line.indexOf('|')
                if (idx <= 0) null
                else line.substring(0, idx) to line.substring(idx + 1)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        seedIntegratedPresetIfNeeded()
        startLocalWebPanel()
        refreshSavedRemoteWebsiteIfSet()
    }

    override fun onCleared() {
        webPanelServer.stop()
        super.onCleared()
    }

    private fun seedIntegratedPresetIfNeeded() {
        viewModelScope.launch {

            val existingPreset = repository.findPresetProfile(DefaultXtreamPreset.SERVER_URL)
            val existingChannelCount = existingPreset?.let { repository.getChannelCount(it.id) } ?: 0
            val needsCredentialFix = existingPreset != null &&
                existingPreset.username != DefaultXtreamPreset.USERNAME &&
                existingPreset.username.equals(DefaultXtreamPreset.USERNAME, ignoreCase = true)
            val needsSync = existingPreset == null || needsCredentialFix || existingChannelCount == 0

            if (!needsSync && repository.getProfileCount() > 0) return@launch

            getApplication<Application>().slevanyDataStore.edit {
                it[KEY_PRESET_SEEDED] = true
            }
            _isSyncing.value = true
            _errorBanner.value = null
            _statusBanner.value = "\"${DefaultXtreamPreset.NAME}\" (${DefaultXtreamPreset.USERNAME}) Xtream portalından kanallar alınıyor..."
            try {
                val synced = repository.connectAndSyncProfile(
                    existingProfileId = existingPreset?.id,
                    name = DefaultXtreamPreset.NAME,
                    serverUrl = DefaultXtreamPreset.SERVER_URL,
                    username = DefaultXtreamPreset.USERNAME,
                    password = DefaultXtreamPreset.PASSWORD
                )
                val chCount = repository.getChannelCount(synced.id)
                _statusBanner.value = "\"${synced.name}\" portalı bağlandı ($chCount canlı kanal ve içerik yerleştirildi)."
            } catch (e: Exception) {
                if (existingPreset == null && repository.getProfileCount() == 0) {
                    repository.saveOfflinePresetProfile(
                        name = DefaultXtreamPreset.NAME,
                        serverUrl = DefaultXtreamPreset.SERVER_URL,
                        username = DefaultXtreamPreset.USERNAME,
                        password = DefaultXtreamPreset.PASSWORD
                    )
                }
                _statusBanner.value = null
                _errorBanner.value = "\"${DefaultXtreamPreset.NAME}\" kanalları alınamadı: ${e.message ?: "Bağlantı hatası"}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun dismissMessages() {
        _statusBanner.value = null
        _errorBanner.value = null
    }

    fun showStatusBanner(message: String) {
        _errorBanner.value = null
        _statusBanner.value = message
    }

    fun selectLiveCategory(categoryId: String?) {
        _selectedLiveCategoryId.value = categoryId
    }

    fun updateLiveSearch(query: String) {
        _liveSearchQuery.value = query
    }

    fun toggleLiveFavoritesOnly() {
        _liveFavoritesOnly.value = !_liveFavoritesOnly.value
    }

    fun selectVodCategory(categoryId: String?) {
        _selectedVodCategoryId.value = categoryId
    }

    fun updateVodSearch(query: String) {
        _vodSearchQuery.value = query
    }

    fun updateVodSort(sortOption: VodSortOption) {
        _vodSortOption.value = sortOption
    }

    fun toggleVodFavoritesOnly() {
        _vodFavoritesOnly.value = !_vodFavoritesOnly.value
    }

    fun selectSeriesCategory(categoryId: String?) {
        _selectedSeriesCategoryId.value = categoryId
    }

    fun updateSeriesSearch(query: String) {
        _seriesSearchQuery.value = query
    }

    fun toggleSeriesFavoritesOnly() {
        _seriesFavoritesOnly.value = !_seriesFavoritesOnly.value
    }

    fun connectAndSyncPanel(
        existingProfileId: Long? = null,
        name: String,
        serverUrl: String,
        username: String,
        password: String,
        onSuccess: () -> Unit = {}
    ) {
        if (serverUrl.isBlank() || username.isBlank() || password.isBlank()) {
            _errorBanner.value = "Lütfen sunucu adresi, kullanıcı adı ve şifre alanlarını doldurun."
            return
        }
        viewModelScope.launch {
            _isSyncing.value = true
            _errorBanner.value = null
            _statusBanner.value = "Xtream paneline bağlanılıyor ve katalog indiriliyor..."
            try {
                val synced = repository.connectAndSyncProfile(
                    existingProfileId = existingProfileId,
                    name = name,
                    serverUrl = serverUrl,
                    username = username,
                    password = password
                )
                resetCategorySelections()
                _statusBanner.value = "\"${synced.name}\" paneli başarıyla senkronize edildi."
                onSuccess()
            } catch (e: Exception) {
                _statusBanner.value = null
                _errorBanner.value = e.message ?: "Panel bağlantısı sırasında hata oluştu."
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun syncActiveProfile() {
        val profile = activeProfile.value ?: return
        viewModelScope.launch {
            _isSyncing.value = true
            _errorBanner.value = null
            _statusBanner.value = "\"${profile.name}\" güncelleniyor..."
            try {
                repository.syncCatalog(profile)
                _statusBanner.value = "Tüm kategoriler ve içerikler güncellendi."
            } catch (e: Exception) {
                _statusBanner.value = null
                _errorBanner.value = e.message ?: "Senkronizasyon hatası."
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun switchProfile(profile: XtreamProfileEntity) {
        viewModelScope.launch {
            repository.switchActiveProfile(profile.id)
            resetCategorySelections()
            _statusBanner.value = "Aktif panel değiştirildi: ${profile.name}"
        }
    }

    fun deleteProfile(profile: XtreamProfileEntity) {
        viewModelScope.launch {
            repository.deleteProfile(profile.id)
            val remaining = profiles.value.firstOrNull { it.id != profile.id }
            if (remaining != null) {
                repository.switchActiveProfile(remaining.id)
            }
            resetCategorySelections()
            _statusBanner.value = "\"${profile.name}\" paneli kaldırıldı."
        }
    }

    private fun resetCategorySelections() {
        _selectedLiveCategoryId.value = null
        _selectedVodCategoryId.value = null
        _selectedSeriesCategoryId.value = null
    }

    private fun canEditParentalVisibility(): Boolean {
        return !isParentalEnabled.value || _isParentalUnlocked.value
    }

    fun toggleCategoryVisibility(category: CategoryEntity) {
        if (!canEditParentalVisibility()) return
        viewModelScope.launch { repository.toggleCategoryVisibility(category) }
    }

    fun toggleCategoryPin(category: CategoryEntity) {
        viewModelScope.launch { repository.toggleCategoryPin(category) }
    }

    fun setAllCategoriesVisibility(type: ContentType, visible: Boolean) {
        if (!canEditParentalVisibility()) return
        val profileId = activeProfile.value?.id ?: return
        viewModelScope.launch {
            repository.setAllCategoriesVisibility(profileId, type, visible)
        }
    }

    fun moveCategory(list: List<CategoryEntity>, index: Int, direction: Int) {
        val targetIndex = index + direction
        if (index !in list.indices || targetIndex !in list.indices) return
        val current = list[index]
        val target = list[targetIndex]
        if (current.isPinned != target.isPinned) return
        viewModelScope.launch {
            repository.swapCategoryOrder(current, target)
        }
    }

    fun toggleChannelFavorite(channel: ChannelEntity) {
        viewModelScope.launch { repository.toggleChannelFavorite(channel) }
    }

    fun toggleChannelHidden(channel: ChannelEntity) {
        if (!canEditParentalVisibility()) return
        viewModelScope.launch { repository.toggleChannelHidden(channel) }
    }

    fun unhideAllChannels() {
        if (!canEditParentalVisibility()) return
        val pid = activeProfile.value?.id ?: return
        viewModelScope.launch {
            repository.unhideAllChannels(pid)
        }
    }

    fun toggleVodFavorite(vod: VodEntity) {
        viewModelScope.launch { repository.toggleVodFavorite(vod) }
    }

    fun toggleSeriesFavorite(series: SeriesEntity) {
        viewModelScope.launch { repository.toggleSeriesFavorite(series) }
    }

    fun updateLiveFormat(format: String) {
        val pid = activeProfile.value?.id ?: return
        viewModelScope.launch {
            repository.updateLiveFormat(pid, format)
            _statusBanner.value = "Canlı yayın formatı .$format olarak ayarlandı."
        }
    }

    fun clearHistory() {
        val pid = activeProfile.value?.id ?: return
        viewModelScope.launch {
            repository.clearWatchHistory(pid)
            _statusBanner.value = "İzleme geçmişi temizlendi."
        }
    }

    fun openVodDetail(vod: VodEntity) {
        _selectedVod.value = vod
        _selectedVodInfo.value = null
        val profile = activeProfile.value ?: return
        viewModelScope.launch {
            _isDetailLoading.value = true
            _selectedVodInfo.value = runCatching {
                repository.fetchVodInfo(profile, vod.streamId)
            }.getOrNull()
            _isDetailLoading.value = false
        }
    }

    fun closeVodDetail() {
        _selectedVod.value = null
        _selectedVodInfo.value = null
    }

    fun openSeriesDetail(series: SeriesEntity) {
        _selectedSeries.value = series
        _selectedSeriesDetail.value = null
        val profile = activeProfile.value ?: return
        viewModelScope.launch {
            _isDetailLoading.value = true
            _selectedSeriesDetail.value = runCatching {
                repository.fetchSeriesInfo(profile, series.seriesId)
            }.getOrNull()
            _isDetailLoading.value = false
        }
    }

    fun closeSeriesDetail() {
        _selectedSeries.value = null
        _selectedSeriesDetail.value = null
    }

    fun playLiveChannel(channel: ChannelEntity) {
        if (channel.isHidden) return
        val profile = activeProfile.value ?: return
        val channels = filteredChannels.value
        val index = channels.indexOfFirst { it.streamId == channel.streamId }
        val url = repository.buildLiveStreamUrl(profile, channel.streamId)
        val request = PlaybackRequest(
            profileId = profile.id,
            contentType = ContentType.LIVE,
            streamId = channel.streamId,
            title = channel.name,
            subtitle = "Canlı Yayın",
            imageUrl = channel.streamIcon,
            streamUrl = url,
            containerExtension = profile.liveFormat,
            liveChannelIndex = index
        )
        _activePlayback.value = request
        viewModelScope.launch {
            repository.saveWatchProgress(request, 0L, 0L)
        }
        loadEpgForChannel(channel.streamId)
    }

    fun zapLiveChannel(delta: Int) {
        val channels = filteredChannels.value
        val current = _activePlayback.value ?: return
        if (channels.isEmpty() || current.contentType != ContentType.LIVE) return
        val currentIdx = channels.indexOfFirst { it.streamId == current.streamId }
            .let { if (it == -1) 0 else it }
        val nextIdx = (currentIdx + delta + channels.size) % channels.size
        playLiveChannel(channels[nextIdx])
    }

    private fun loadEpgForChannel(streamId: Int) {
        val profile = activeProfile.value ?: return
        viewModelScope.launch {
            _activeEpg.value = repository.fetchShortEpg(profile, streamId)
        }
    }

    fun playVod(vod: VodEntity, resumePositionMs: Long = 0L) {
        val profile = activeProfile.value ?: return
        val url = repository.buildVodStreamUrl(profile, vod.streamId, vod.containerExtension)
        viewModelScope.launch {
            val savedPos = if (resumePositionMs > 0L) {
                resumePositionMs
            } else {
                repository.getSavedWatchPosition(profile.id, ContentType.VOD, vod.streamId)
            }
            val request = PlaybackRequest(
                profileId = profile.id,
                contentType = ContentType.VOD,
                streamId = vod.streamId,
                title = vod.name,
                subtitle = "Film • ${vod.containerExtension.uppercase()}",
                imageUrl = vod.streamIcon,
                streamUrl = url,
                containerExtension = vod.containerExtension,
                startPositionMs = savedPos
            )
            _activePlayback.value = request
            repository.saveWatchProgress(request, savedPos, 0L)
        }
    }

    fun playSeriesEpisode(series: SeriesEntity, episode: XtreamEpisode, resumePositionMs: Long = 0L) {
        val profile = activeProfile.value ?: return
        val epIdInt = episode.id.toIntOrNull() ?: episode.episodeNum
        val url = repository.buildSeriesStreamUrl(profile, episode.id, episode.containerExtension)
        viewModelScope.launch {
            val savedPos = if (resumePositionMs > 0L) {
                resumePositionMs
            } else {
                repository.getSavedWatchPosition(profile.id, ContentType.SERIES, epIdInt)
            }
            val request = PlaybackRequest(
                profileId = profile.id,
                contentType = ContentType.SERIES,
                streamId = epIdInt,
                title = "${series.name} • S${episode.seasonNum} B${episode.episodeNum}",
                subtitle = episode.title,
                imageUrl = episode.coverUrl.ifBlank { series.cover },
                streamUrl = url,
                containerExtension = episode.containerExtension,
                startPositionMs = savedPos
            )
            _activePlayback.value = request
            repository.saveWatchProgress(request, savedPos, 0L)
        }
    }

    fun resumeWatchHistory(entry: WatchHistoryEntity) {
        val type = ContentType.fromDb(entry.contentType)
        val request = PlaybackRequest(
            profileId = entry.profileId,
            contentType = type,
            streamId = entry.streamId,
            title = entry.title,
            subtitle = entry.subtitle,
            imageUrl = entry.imageUrl,
            streamUrl = entry.streamUrl,
            containerExtension = entry.containerExtension,
            startPositionMs = if (type == ContentType.LIVE) 0L else entry.positionMs
        )
        _activePlayback.value = request
        viewModelScope.launch {
            repository.saveWatchProgress(
                request,
                if (type == ContentType.LIVE) 0L else entry.positionMs,
                entry.durationMs
            )
        }
        if (type == ContentType.LIVE) {
            loadEpgForChannel(entry.streamId)
        }
    }

    fun removeWatchHistoryItem(id: String) {
        viewModelScope.launch {
            repository.removeWatchHistoryItem(id)
        }
    }

    fun savePlaybackProgress(request: PlaybackRequest, positionMs: Long, durationMs: Long) {
        viewModelScope.launch {
            repository.saveWatchProgress(request, positionMs, durationMs)
        }
    }

    fun stopPlayback() {
        _activePlayback.value = null
        _activeEpg.value = emptyList()
    }

    fun setAppLanguage(language: AppLanguage) {
        viewModelScope.launch {
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                prefs[KEY_APP_LANGUAGE] = language.code
            }
        }
    }

    fun setAppTheme(theme: AppThemeOption) {
        viewModelScope.launch {
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                prefs[KEY_APP_THEME] = theme.id
            }
        }
    }

    fun submitActivationCode(code: String): Boolean {
        if (!AppActivationHelper.verifyCode(code)) {
            return false
        }
        viewModelScope.launch {
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                prefs[KEY_APP_ACTIVATED] = true
            }
        }
        return true
    }

    fun ensureFreeKurdishChannelsLoaded() {
        val current = _rawFreeKurdishState.value
        if (current is FreeKurdishUiState.Idle || current is FreeKurdishUiState.Error) {
            refreshFreeKurdishChannels()
        }
    }

    fun refreshFreeKurdishChannels() {
        if (_rawFreeKurdishState.value is FreeKurdishUiState.Loading) return
        viewModelScope.launch {
            _rawFreeKurdishState.value = FreeKurdishUiState.Loading
            try {
                val (channels, notice) = freeKurdishRepository.fetchFreeKurdishChannels()
                _rawFreeKurdishState.value = FreeKurdishUiState.Success(channels, notice)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _rawFreeKurdishState.value = FreeKurdishUiState.Error(
                    e.message ?: "Ücretsiz Kürtçe kanallar yüklenemedi."
                )
            }
        }
    }

    fun startLocalWebPanel() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val url = runCatching { webPanelServer.start() }.getOrNull()
            _localWebPanelUrl.value = url
        }
    }

    fun stopLocalWebPanel() {
        webPanelServer.stop()
        _localWebPanelUrl.value = null
    }

    fun toggleLocalWebPanel() {
        if (_localWebPanelUrl.value != null) {
            stopLocalWebPanel()
        } else {
            startLocalWebPanel()
        }
    }

    private fun refreshSavedRemoteWebsiteIfSet() {
        viewModelScope.launch {
            val savedUrl = getApplication<Application>().slevanyDataStore.data.first()[KEY_REMOTE_WEBSITE_URL].orEmpty()
            if (savedUrl.isNotBlank()) {
                runCatching { applyRemoteWebsitePayload(savedUrl, silent = true) }
            }
        }
    }

    fun connectRemoteWebsitePanel(websiteUrl: String) {
        val trimmed = websiteUrl.trim()
        if (trimmed.isBlank()) {
            _errorBanner.value = "Lütfen web sitesi adresini girin."
            return
        }
        viewModelScope.launch {
            applyRemoteWebsitePayload(trimmed, silent = false)
        }
    }

    fun disconnectRemoteWebsitePanel() {
        viewModelScope.launch {
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                prefs.remove(KEY_REMOTE_WEBSITE_URL)
                prefs.remove(KEY_REMOTE_ANNOUNCEMENT)
                prefs.remove(KEY_REMOTE_LAST_SYNC)
            }
            _statusBanner.value = "Harici web sitesi bağlantısı kaldırıldı."
        }
    }

    private suspend fun applyRemoteWebsitePayload(websiteUrl: String, silent: Boolean) {
        if (!silent) {
            _isWebPanelSyncing.value = true
            _errorBanner.value = null
            _statusBanner.value = "Web sitesi paneline bağlanılıyor..."
        }
        try {
            val normalizedUrl = RemoteWebPanelClient.normalizeWebsiteUrl(websiteUrl)
            val payload = remoteWebPanelClient.fetchPanelFromWebsite(normalizedUrl)
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                prefs[KEY_REMOTE_WEBSITE_URL] = normalizedUrl
                prefs[KEY_REMOTE_LAST_SYNC] = System.currentTimeMillis()
                if (!payload.announcement.isNullOrBlank()) {
                    prefs[KEY_REMOTE_ANNOUNCEMENT] = payload.announcement
                } else {
                    prefs.remove(KEY_REMOTE_ANNOUNCEMENT)
                }
                if (payload.channels.isNotEmpty()) {
                    prefs[KEY_CUSTOM_WEB_CHANNELS] = RemoteWebPanelParser.serializeCustomChannels(payload.channels)
                }
                if (!payload.themeId.isNullOrBlank()) {
                    prefs[KEY_APP_THEME] = AppThemeOption.fromId(payload.themeId).id
                }
            }
            val xtream = payload.xtreamConfig
            if (xtream != null) {
                val existing = repository.findPresetProfile(xtream.serverUrl)
                repository.connectAndSyncProfile(
                    existingProfileId = existing?.id,
                    name = xtream.name,
                    serverUrl = xtream.serverUrl,
                    username = xtream.username,
                    password = xtream.password
                )
            }
            if (!silent) {
                _statusBanner.value = buildString {
                    append(payload.panelTitle ?: "Web sitesi")
                    append(" senkronize edildi")
                    if (payload.channels.isNotEmpty()) {
                        append(" (${payload.channels.size} kanal)")
                    }
                    if (xtream != null) {
                        append(" • Xtream panel güncellendi")
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (!silent) {
                _statusBanner.value = null
                _errorBanner.value = "Web sitesi paneli alınamadı: ${e.message ?: "Bağlantı hatası"}"
            }
        } finally {
            if (!silent) {
                _isWebPanelSyncing.value = false
            }
        }
    }

    fun addCustomWebChannel(
        name: String,
        streamUrl: String,
        category: String = "Web Panel",
        dialect: String = "general"
    ) {
        val cleanName = name.trim()
        val cleanUrl = streamUrl.trim()
        if (cleanName.isBlank() || cleanUrl.isBlank()) return
        val resolvedDialect = when (dialect.lowercase()) {
            "zaxo", "zakho", "badinan", "badînan" -> FreeKurdishDialect.ZAXO_BADINAN
            "sorani", "ckb" -> FreeKurdishDialect.SORANI
            "kurmanji", "badini", "kmr" -> FreeKurdishDialect.KURMANJI
            else -> FreeKurdishDialect.GENERAL
        }
        viewModelScope.launch {
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                val list = RemoteWebPanelParser.deserializeCustomChannels(prefs[KEY_CUSTOM_WEB_CHANNELS].orEmpty())
                    .filterNot { it.streamUrl.equals(cleanUrl, ignoreCase = true) }
                    .toMutableList()
                list.add(
                    0,
                    FreeKurdishChannel(
                        id = "web_${cleanUrl.lowercase()}",
                        name = cleanName,
                        category = category.ifBlank { "Web Panel" },
                        dialect = resolvedDialect,
                        logoUrl = "",
                        streamUrl = cleanUrl,
                        containerExtension = OttM3uParser.inferExtension(cleanUrl),
                        isDirectStream = true
                    )
                )
                prefs[KEY_CUSTOM_WEB_CHANNELS] = RemoteWebPanelParser.serializeCustomChannels(list)
            }
        }
    }

    fun removeCustomWebChannel(streamUrl: String) {
        viewModelScope.launch {
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                val updated = RemoteWebPanelParser.deserializeCustomChannels(prefs[KEY_CUSTOM_WEB_CHANNELS].orEmpty())
                    .filterNot { it.streamUrl.equals(streamUrl, ignoreCase = true) }
                prefs[KEY_CUSTOM_WEB_CHANNELS] = RemoteWebPanelParser.serializeCustomChannels(updated)
            }
        }
    }

    fun toggleFreeKurdishFavorite(channel: FreeKurdishChannel) {
        viewModelScope.launch {
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                val current = prefs[KEY_FREE_KURDISH_FAVORITES]
                    .orEmpty()
                    .lines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .toMutableSet()
                if (!current.add(channel.streamUrl)) {
                    current.remove(channel.streamUrl)
                }
                prefs[KEY_FREE_KURDISH_FAVORITES] = current.joinToString("\n")
            }
        }
    }

    fun playFreeKurdishChannel(channel: FreeKurdishChannel) {
        val syntheticId = -((channel.streamUrl.hashCode() and 0x7FFFFFFF).coerceAtLeast(1))
        _activePlayback.value = PlaybackRequest(
            profileId = activeProfile.value?.id ?: 0L,
            contentType = ContentType.LIVE,
            streamId = syntheticId,
            title = channel.name,
            subtitle = "FREE • ${channel.category}",
            imageUrl = channel.logoUrl,
            streamUrl = channel.streamUrl,
            containerExtension = channel.containerExtension,
            startPositionMs = 0L
        )
    }

    fun askGeminiAdvice(userPrompt: String) {
        val cleanPrompt = userPrompt.trim()
        if (cleanPrompt.isEmpty()) return

        val currentLang = appLanguage.value
        val liveCats = visibleLiveCategories.value.take(12).joinToString(", ") { it.name }
        val vodCats = visibleVodCategories.value.take(12).joinToString(", ") { it.name }
        val sampleMovies = allVisibleVods.value.take(15).joinToString(", ") { it.name }
        val sampleSeries = allVisibleSeries.value.take(15).joinToString(", ") { it.name }
        val sampleChannels = allVisibleChannels.value.take(15).joinToString(", ") { it.name }

        val fullPrompt = buildString {
            appendLine("Yanıt Dili (Zorunlu): ${currentLang.aiLanguagePromptName}")
            appendLine("Kullanıcı Sorusu: $cleanPrompt")
            appendLine()
            appendLine("Kullanıcının mevcut JHE HAKAR kataloğu:")
            appendLine("- Toplam Canlı Kanal: ${allVisibleChannels.value.size} (Kategoriler: ${liveCats.ifBlank { "Yok" }})")
            appendLine("- Örnek Kanallar: ${sampleChannels.ifBlank { "Henüz senkronize edilmedi" }}")
            appendLine("- Toplam Film: ${allVisibleVods.value.size} (Kategoriler: ${vodCats.ifBlank { "Yok" }})")
            appendLine("- Örnek Filmler: ${sampleMovies.ifBlank { "Henüz senkronize edilmedi" }}")
            appendLine("- Toplam Dizi: ${allVisibleSeries.value.size} (Örnekler: ${sampleSeries.ifBlank { "Henüz senkronize edilmedi" }})")
        }

        viewModelScope.launch {
            _aiUiState.value = AiUiState.Loading
            val accumulated = StringBuilder()
            try {
                withTimeout(60_000L) {
                    aiRepository.streamTvAdvice(fullPrompt).collect { chunk ->
                        accumulated.append(chunk)
                        _aiUiState.value = AiUiState.Streaming(accumulated.toString())
                    }
                }
                if (accumulated.isNotBlank()) {
                    _aiUiState.value = AiUiState.Success(accumulated.toString())
                } else {
                    _aiUiState.value = AiUiState.Error("Gemini boş yanıt döndürdü. Lütfen tekrar deneyin.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(AI_TAG, "Gemini request timed out", e)
                _aiUiState.value = AiUiState.Error("İstek 60 saniye içinde yanıt vermedi. Lütfen tekrar deneyin.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(AI_TAG, "Firebase AI generation failed", e)
                _aiUiState.value = AiUiState.Error(e.localizedMessage ?: "Gemini isteği başarısız oldu.")
            }
        }
    }

    fun resetAiState() {
        _aiUiState.value = AiUiState.Idle
    }

    fun setupOrChangeParentalPin(newPin: String): Boolean {
        if (!ParentalControlHelper.isValidPinFormat(newPin)) {
            return false
        }
        val hash = ParentalControlHelper.hashPin(newPin)
        viewModelScope.launch {
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                prefs[KEY_PARENTAL_PIN_HASH] = hash
                prefs[KEY_PARENTAL_ENABLED] = true
            }
            _isParentalUnlocked.value = true
            _errorBanner.value = null
            _statusBanner.value = "Ebeveyn kontrolü PIN kodu kaydedildi ve etkinleştirildi."
        }
        return true
    }

    suspend fun unlockParentalControl(enteredPin: String): Boolean {
        val prefs = getApplication<Application>().slevanyDataStore.data.first()
        val storedHash = prefs[KEY_PARENTAL_PIN_HASH]
        val valid = ParentalControlHelper.verifyPin(enteredPin, storedHash)
        if (valid) {
            _isParentalUnlocked.value = true
            _errorBanner.value = null
        }
        return valid
    }

    fun lockParentalControl() {
        _isParentalUnlocked.value = false
    }

    fun setParentalControlEnabled(enabled: Boolean) {
        if (isParentalEnabled.value && !_isParentalUnlocked.value) return
        viewModelScope.launch {
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                prefs[KEY_PARENTAL_ENABLED] = enabled
            }
            if (!enabled) {
                _isParentalUnlocked.value = false
            } else {
                _isParentalUnlocked.value = true
            }
        }
    }

    fun addYoutubeBookmark(title: String, url: String) {
        val cleanTitle = title.replace("|", " ").replace("\n", " ").trim()
            .ifBlank { "YouTube" }
            .take(36)
        val cleanUrl = url.replace("|", "").replace("\n", "").trim()
        if (cleanUrl.isBlank()) return
        viewModelScope.launch {
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                val current = prefs[KEY_YOUTUBE_BOOKMARKS].orEmpty()
                    .lines()
                    .filter { it.contains('|') }
                    .toMutableList()
                val entry = "$cleanTitle|$cleanUrl"
                current.removeAll { it.endsWith("|$cleanUrl") }
                current.add(0, entry)
                prefs[KEY_YOUTUBE_BOOKMARKS] = current.take(12).joinToString("\n")
            }
        }
    }

    fun removeYoutubeBookmark(url: String) {
        viewModelScope.launch {
            getApplication<Application>().slevanyDataStore.edit { prefs ->
                val updated = prefs[KEY_YOUTUBE_BOOKMARKS].orEmpty()
                    .lines()
                    .filter { it.contains('|') && !it.endsWith("|$url") }
                prefs[KEY_YOUTUBE_BOOKMARKS] = updated.joinToString("\n")
            }
        }
    }
}
