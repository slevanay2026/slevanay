package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CategoryEntity
import com.example.data.local.ChannelEntity
import com.example.data.local.SeriesEntity
import com.example.data.local.VodEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.XtreamProfileEntity
import com.example.data.model.ContentType
import com.example.data.model.PlaybackRequest
import com.example.data.model.VodSortOption
import com.example.data.model.XtreamEpgItem
import com.example.data.model.XtreamEpisode
import com.example.data.model.XtreamSeriesDetail
import com.example.data.model.XtreamVodInfo
import com.example.data.remote.XtreamClient
import com.example.data.repository.XtreamRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppDestination(val route: String, val label: String) {
    HOME("home", "Ana Sayfa"),
    LIVE("live", "Canlı TV"),
    MOVIES("movies", "Filmler"),
    SERIES("series", "Diziler"),
    MANAGER("manager", "Yönetim")
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val client = XtreamClient()
    val repository = XtreamRepository(database.xtreamDao(), client)

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

    val watchHistory: StateFlow<List<WatchHistoryEntity>> = activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList())
            else repository.observeWatchHistory(profile.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun dismissMessages() {
        _statusBanner.value = null
        _errorBanner.value = null
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

    fun toggleCategoryVisibility(category: CategoryEntity) {
        viewModelScope.launch { repository.toggleCategoryVisibility(category) }
    }

    fun toggleCategoryPin(category: CategoryEntity) {
        viewModelScope.launch { repository.toggleCategoryPin(category) }
    }

    fun setAllCategoriesVisibility(type: ContentType, visible: Boolean) {
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
        viewModelScope.launch { repository.toggleChannelHidden(channel) }
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
        val profile = activeProfile.value ?: return
        val channels = filteredChannels.value
        val index = channels.indexOfFirst { it.streamId == channel.streamId }
        val url = repository.buildLiveStreamUrl(profile, channel.streamId)
        _activePlayback.value = PlaybackRequest(
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
        _activePlayback.value = PlaybackRequest(
            profileId = profile.id,
            contentType = ContentType.VOD,
            streamId = vod.streamId,
            title = vod.name,
            subtitle = "Film • ${vod.containerExtension.uppercase()}",
            imageUrl = vod.streamIcon,
            streamUrl = url,
            containerExtension = vod.containerExtension,
            startPositionMs = resumePositionMs
        )
    }

    fun playSeriesEpisode(series: SeriesEntity, episode: XtreamEpisode, resumePositionMs: Long = 0L) {
        val profile = activeProfile.value ?: return
        val epIdInt = episode.id.toIntOrNull() ?: episode.episodeNum
        val url = repository.buildSeriesStreamUrl(profile, episode.id, episode.containerExtension)
        _activePlayback.value = PlaybackRequest(
            profileId = profile.id,
            contentType = ContentType.SERIES,
            streamId = epIdInt,
            title = "${series.name} • S${episode.seasonNum} B${episode.episodeNum}",
            subtitle = episode.title,
            imageUrl = episode.coverUrl.ifBlank { series.cover },
            streamUrl = url,
            containerExtension = episode.containerExtension,
            startPositionMs = resumePositionMs
        )
    }

    fun resumeWatchHistory(entry: WatchHistoryEntity) {
        val type = ContentType.fromDb(entry.contentType)
        _activePlayback.value = PlaybackRequest(
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
        if (type == ContentType.LIVE) {
            loadEpgForChannel(entry.streamId)
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
}
