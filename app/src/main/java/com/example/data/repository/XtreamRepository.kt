package com.example.data.repository

import com.example.data.local.CategoryEntity
import com.example.data.local.ChannelEntity
import com.example.data.local.SeriesEntity
import com.example.data.local.VodEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.XtreamDao
import com.example.data.local.XtreamProfileEntity
import com.example.data.model.ContentType
import com.example.data.model.PlaybackRequest
import com.example.data.model.XtreamEpgItem
import com.example.data.model.XtreamLinkParser
import com.example.data.model.XtreamSeriesDetail
import com.example.data.model.XtreamVodInfo
import com.example.data.remote.XtreamClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class XtreamRepository(
    private val dao: XtreamDao,
    private val client: XtreamClient
) {
    val okHttpClient get() = client.okHttpClient

    fun observeProfiles(): Flow<List<XtreamProfileEntity>> = dao.observeProfiles()
    fun observeActiveProfile(): Flow<XtreamProfileEntity?> = dao.observeActiveProfile()

    fun observeAllCategories(profileId: Long, type: ContentType): Flow<List<CategoryEntity>> =
        dao.observeCategories(profileId, type.dbValue)

    fun observeVisibleCategories(profileId: Long, type: ContentType): Flow<List<CategoryEntity>> =
        dao.observeVisibleCategories(profileId, type.dbValue)

    fun observeVisibleChannels(profileId: Long): Flow<List<ChannelEntity>> =
        dao.observeVisibleChannels(profileId)

    fun observeAllChannels(profileId: Long): Flow<List<ChannelEntity>> =
        dao.observeAllChannels(profileId)

    fun observeVisibleVods(profileId: Long): Flow<List<VodEntity>> =
        dao.observeVisibleVods(profileId)

    fun observeVisibleSeries(profileId: Long): Flow<List<SeriesEntity>> =
        dao.observeVisibleSeries(profileId)

    fun observeWatchHistory(profileId: Long): Flow<List<WatchHistoryEntity>> =
        dao.observeWatchHistory(profileId)

    fun observeRecentlyWatchedChannels(profileId: Long): Flow<List<WatchHistoryEntity>> =
        dao.observeRecentlyWatchedChannels(profileId)

    suspend fun connectAndSyncProfile(
        existingProfileId: Long? = null,
        name: String,
        serverUrl: String,
        username: String,
        password: String
    ): XtreamProfileEntity {
        val normalizedUrl = XtreamLinkParser.normalizeServerUrl(serverUrl)
        val cleanUser = username.trim()
        val cleanPass = password.trim()
        val auth = client.authenticate(normalizedUrl, cleanUser, cleanPass)

        val displayName = name.trim().ifBlank {
            normalizedUrl.removePrefix("https://").removePrefix("http://")
        }

        val existing = existingProfileId?.let { dao.getProfileById(it) }
        val profileToSave = XtreamProfileEntity(
            id = existing?.id ?: 0L,
            name = displayName,
            serverUrl = normalizedUrl,
            username = cleanUser,
            password = cleanPass,
            isActive = true,
            status = auth.status,
            expDate = auth.expDate,
            activeCons = auth.activeCons,
            maxConnections = auth.maxConnections,
            isTrial = auth.isTrial,
            liveFormat = existing?.liveFormat ?: "m3u8",
            lastSyncedAt = System.currentTimeMillis()
        )

        val savedId = if (existing != null) {
            dao.updateProfile(profileToSave)
            existing.id
        } else {
            dao.insertProfile(profileToSave)
        }
        dao.setActiveProfile(savedId)

        val savedProfile = dao.getProfileById(savedId) ?: profileToSave.copy(id = savedId)
        syncCatalog(savedProfile)
        return dao.getProfileById(savedId) ?: savedProfile
    }

    suspend fun loginViaPrivatePortal(
        portalUrl: String,
        username: String,
        password: String
    ): XtreamProfileEntity {
        val resolved = client.resolvePrivateLoginUrl(
            portalUrl = portalUrl,
            username = username,
            password = password
        )
        val existingMatch = dao.getAllProfilesOnce().firstOrNull {
            it.serverUrl.equals(resolved.xtreamServerUrl, ignoreCase = true) &&
                it.username.equals(resolved.xtreamUsername, ignoreCase = true)
        }
        val profileName = resolved.profileName.ifBlank { username.trim() }
        return connectAndSyncProfile(
            existingProfileId = existingMatch?.id,
            name = profileName,
            serverUrl = resolved.xtreamServerUrl,
            username = resolved.xtreamUsername,
            password = resolved.xtreamPassword
        )
    }

    suspend fun verifyPortalReachable(portalUrl: String) {
        client.verifyPortalReachable(portalUrl)
    }

    suspend fun switchActiveProfile(profileId: Long) {
        dao.setActiveProfile(profileId)
    }

    suspend fun deleteProfile(profileId: Long) {
        val wasActive = dao.getProfileById(profileId)?.isActive == true
        dao.deleteCategoriesForProfile(profileId)
        dao.deleteChannelsForProfile(profileId)
        dao.deleteVodsForProfile(profileId)
        dao.deleteSeriesForProfile(profileId)
        dao.clearWatchHistory(profileId)
        dao.deleteProfileById(profileId)
        if (wasActive) {
            // Activate another remaining profile if any
            dao.observeProfiles()
        }
    }

    suspend fun getProfileCount(): Int = dao.getProfileCount()

    suspend fun getChannelCount(profileId: Long): Int = dao.getChannelCount(profileId)

    suspend fun findPresetProfile(serverUrl: String): XtreamProfileEntity? {
        val normalized = XtreamLinkParser.normalizeServerUrl(serverUrl)
        return dao.getAllProfilesOnce().firstOrNull {
            it.serverUrl.equals(normalized, ignoreCase = true)
        }
    }

    suspend fun saveOfflinePresetProfile(
        name: String,
        serverUrl: String,
        username: String,
        password: String
    ): XtreamProfileEntity {
        val normalizedUrl = XtreamLinkParser.normalizeServerUrl(serverUrl)
        val profile = XtreamProfileEntity(
            name = name.trim(),
            serverUrl = normalizedUrl,
            username = username.trim(),
            password = password.trim(),
            isActive = true,
            status = "Senkronizasyon Bekliyor",
            liveFormat = "m3u8"
        )
        val savedId = dao.insertProfile(profile)
        dao.setActiveProfile(savedId)
        return dao.getProfileById(savedId) ?: profile.copy(id = savedId)
    }

    suspend fun syncCatalog(profile: XtreamProfileEntity): XtreamProfileEntity = coroutineScope {
        val authDeferred = async {
            runCatching { client.authenticate(profile.serverUrl, profile.username, profile.password) }
        }
        val liveCatDeferred = async {
            runCatching { client.getLiveCategories(profile.serverUrl, profile.username, profile.password) }
        }
        val liveStreamsDeferred = async {
            runCatching { client.getLiveStreams(profile.serverUrl, profile.username, profile.password) }
        }
        val vodCatDeferred = async {
            runCatching { client.getVodCategories(profile.serverUrl, profile.username, profile.password) }
        }
        val vodStreamsDeferred = async {
            runCatching { client.getVodStreams(profile.serverUrl, profile.username, profile.password) }
        }
        val seriesCatDeferred = async {
            runCatching { client.getSeriesCategories(profile.serverUrl, profile.username, profile.password) }
        }
        val seriesListDeferred = async {
            runCatching { client.getSeries(profile.serverUrl, profile.username, profile.password) }
        }

        val authResult = authDeferred.await()
        val liveCatsResult = liveCatDeferred.await()
        val liveStreamsResult = liveStreamsDeferred.await()
        val vodCatsResult = vodCatDeferred.await()
        val vodStreamsResult = vodStreamsDeferred.await()
        val seriesCatsResult = seriesCatDeferred.await()
        val seriesListResult = seriesListDeferred.await()

        val firstError = liveStreamsResult.exceptionOrNull()
            ?: liveCatsResult.exceptionOrNull()
            ?: authResult.exceptionOrNull()
            ?: vodStreamsResult.exceptionOrNull()

        if (liveStreamsResult.isFailure && vodStreamsResult.isFailure && seriesListResult.isFailure && firstError != null) {
            throw firstError
        }

        val auth = authResult.getOrNull()
        val liveCats = liveCatsResult.getOrDefault(emptyList())
        val liveStreams = liveStreamsResult.getOrDefault(emptyList())
        val vodCats = vodCatsResult.getOrDefault(emptyList())
        val vodStreams = vodStreamsResult.getOrDefault(emptyList())
        val seriesCats = seriesCatsResult.getOrDefault(emptyList())
        val seriesList = seriesListResult.getOrDefault(emptyList())

        val pid = profile.id

        // Preserve existing category visibility/pin/order
        val existingLiveCats = dao.getCategoriesOnce(pid, ContentType.LIVE.dbValue).associateBy { it.categoryId }
        val existingVodCats = dao.getCategoriesOnce(pid, ContentType.VOD.dbValue).associateBy { it.categoryId }
        val existingSeriesCats = dao.getCategoriesOnce(pid, ContentType.SERIES.dbValue).associateBy { it.categoryId }

        val mappedCategories = buildList {
            liveCats.forEachIndexed { idx, dto ->
                val prev = existingLiveCats[dto.categoryId]
                add(
                    CategoryEntity(
                        id = "${pid}_LIVE_${dto.categoryId}",
                        profileId = pid,
                        categoryId = dto.categoryId,
                        name = dto.categoryName,
                        type = ContentType.LIVE.dbValue,
                        sortOrder = prev?.sortOrder ?: idx,
                        isVisible = prev?.isVisible ?: true,
                        isPinned = prev?.isPinned ?: false
                    )
                )
            }
            vodCats.forEachIndexed { idx, dto ->
                val prev = existingVodCats[dto.categoryId]
                add(
                    CategoryEntity(
                        id = "${pid}_VOD_${dto.categoryId}",
                        profileId = pid,
                        categoryId = dto.categoryId,
                        name = dto.categoryName,
                        type = ContentType.VOD.dbValue,
                        sortOrder = prev?.sortOrder ?: idx,
                        isVisible = prev?.isVisible ?: true,
                        isPinned = prev?.isPinned ?: false
                    )
                )
            }
            seriesCats.forEachIndexed { idx, dto ->
                val prev = existingSeriesCats[dto.categoryId]
                add(
                    CategoryEntity(
                        id = "${pid}_SERIES_${dto.categoryId}",
                        profileId = pid,
                        categoryId = dto.categoryId,
                        name = dto.categoryName,
                        type = ContentType.SERIES.dbValue,
                        sortOrder = prev?.sortOrder ?: idx,
                        isVisible = prev?.isVisible ?: true,
                        isPinned = prev?.isPinned ?: false
                    )
                )
            }
        }

        // Preserve favorites & hidden flags
        val prevChannels = dao.getChannelsOnce(pid).associateBy { it.streamId }
        val mappedChannels = liveStreams.map { dto ->
            val prev = prevChannels[dto.streamId]
            ChannelEntity(
                id = "${pid}_live_${dto.streamId}",
                profileId = pid,
                streamId = dto.streamId,
                num = dto.num,
                name = dto.name,
                streamIcon = dto.streamIcon,
                categoryId = dto.categoryId,
                epgChannelId = dto.epgChannelId,
                isFavorite = prev?.isFavorite ?: false,
                isHidden = prev?.isHidden ?: false
            )
        }

        val prevVods = dao.getVodsOnce(pid).associateBy { it.streamId }
        val mappedVods = vodStreams.map { dto ->
            val prev = prevVods[dto.streamId]
            VodEntity(
                id = "${pid}_vod_${dto.streamId}",
                profileId = pid,
                streamId = dto.streamId,
                name = dto.name,
                streamIcon = dto.streamIcon,
                categoryId = dto.categoryId,
                containerExtension = dto.containerExtension,
                rating = dto.rating,
                added = dto.added,
                isFavorite = prev?.isFavorite ?: false,
                isHidden = prev?.isHidden ?: false
            )
        }

        val prevSeries = dao.getSeriesOnce(pid).associateBy { it.seriesId }
        val mappedSeries = seriesList.map { dto ->
            val prev = prevSeries[dto.seriesId]
            SeriesEntity(
                id = "${pid}_series_${dto.seriesId}",
                profileId = pid,
                seriesId = dto.seriesId,
                name = dto.name,
                cover = dto.cover,
                plot = dto.plot,
                cast = dto.cast,
                genre = dto.genre,
                releaseDate = dto.releaseDate,
                rating = dto.rating,
                categoryId = dto.categoryId,
                isFavorite = prev?.isFavorite ?: false,
                isHidden = prev?.isHidden ?: false
            )
        }

        if (liveCatsResult.isSuccess || vodCatsResult.isSuccess || seriesCatsResult.isSuccess) {
            dao.deleteCategoriesForProfile(pid)
            if (mappedCategories.isNotEmpty()) dao.insertCategories(mappedCategories)
        }
        if (liveStreamsResult.isSuccess) {
            dao.deleteChannelsForProfile(pid)
            mappedChannels.chunked(500).forEach { dao.insertChannels(it) }
        }
        if (vodStreamsResult.isSuccess) {
            dao.deleteVodsForProfile(pid)
            mappedVods.chunked(500).forEach { dao.insertVods(it) }
        }
        if (seriesListResult.isSuccess) {
            dao.deleteSeriesForProfile(pid)
            mappedSeries.chunked(500).forEach { dao.insertSeries(it) }
        }

        val updatedProfile = profile.copy(
            status = auth?.status ?: profile.status,
            expDate = auth?.expDate ?: profile.expDate,
            activeCons = auth?.activeCons ?: profile.activeCons,
            maxConnections = auth?.maxConnections ?: profile.maxConnections,
            isTrial = auth?.isTrial ?: profile.isTrial,
            lastSyncedAt = System.currentTimeMillis()
        )
        dao.updateProfile(updatedProfile)
        updatedProfile
    }

    suspend fun toggleCategoryVisibility(category: CategoryEntity) {
        dao.setCategoryVisibility(category.id, !category.isVisible)
    }

    suspend fun toggleCategoryPin(category: CategoryEntity) {
        dao.setCategoryPinned(category.id, !category.isPinned)
    }

    suspend fun setAllCategoriesVisibility(profileId: Long, type: ContentType, visible: Boolean) {
        dao.setAllCategoriesVisibility(profileId, type.dbValue, visible)
    }

    suspend fun swapCategoryOrder(first: CategoryEntity, second: CategoryEntity) {
        dao.updateCategorySortOrder(first.id, second.sortOrder)
        dao.updateCategorySortOrder(second.id, first.sortOrder)
    }

    suspend fun toggleChannelFavorite(channel: ChannelEntity) {
        dao.setChannelFavorite(channel.id, !channel.isFavorite)
    }

    suspend fun toggleChannelHidden(channel: ChannelEntity) {
        dao.setChannelHidden(channel.id, !channel.isHidden)
    }

    suspend fun unhideAllChannels(profileId: Long) {
        dao.setAllChannelsHidden(profileId, false)
    }

    suspend fun toggleVodFavorite(vod: VodEntity) {
        dao.setVodFavorite(vod.id, !vod.isFavorite)
    }

    suspend fun toggleSeriesFavorite(series: SeriesEntity) {
        dao.setSeriesFavorite(series.id, !series.isFavorite)
    }

    suspend fun updateLiveFormat(profileId: Long, format: String) {
        dao.updateLiveFormat(profileId, format)
    }

    suspend fun fetchShortEpg(profile: XtreamProfileEntity, streamId: Int): List<XtreamEpgItem> =
        client.getShortEpg(profile.serverUrl, profile.username, profile.password, streamId)

    suspend fun fetchVodInfo(profile: XtreamProfileEntity, vodId: Int): XtreamVodInfo? =
        client.getVodInfo(profile.serverUrl, profile.username, profile.password, vodId)

    suspend fun fetchSeriesInfo(profile: XtreamProfileEntity, seriesId: Int): XtreamSeriesDetail? =
        client.getSeriesInfo(profile.serverUrl, profile.username, profile.password, seriesId)

    suspend fun getSavedWatchPosition(
        profileId: Long,
        contentType: ContentType,
        streamId: Int
    ): Long {
        val id = "${profileId}_${contentType.dbValue}_${streamId}"
        return dao.getWatchHistoryItem(id)?.positionMs ?: 0L
    }

    suspend fun saveWatchProgress(request: PlaybackRequest, positionMs: Long, durationMs: Long) {
        val entryId = "${request.profileId}_${request.contentType.dbValue}_${request.streamId}"
        val existing = dao.getWatchHistoryItem(entryId)
        val effectivePos = if (positionMs <= 0L && existing != null && request.contentType != ContentType.LIVE) {
            existing.positionMs
        } else {
            positionMs.coerceAtLeast(0L)
        }
        val effectiveDur = if (durationMs <= 0L && existing != null) {
            existing.durationMs
        } else {
            durationMs.coerceAtLeast(0L)
        }
        val entry = WatchHistoryEntity(
            id = entryId,
            profileId = request.profileId,
            contentType = request.contentType.dbValue,
            streamId = request.streamId,
            title = request.title,
            subtitle = request.subtitle,
            imageUrl = request.imageUrl,
            streamUrl = request.streamUrl,
            containerExtension = request.containerExtension,
            positionMs = effectivePos,
            durationMs = effectiveDur,
            updatedAt = System.currentTimeMillis()
        )
        dao.upsertWatchHistory(entry)
    }

    suspend fun removeWatchHistoryItem(id: String) {
        dao.deleteWatchHistoryItem(id)
    }

    suspend fun clearWatchHistory(profileId: Long) {
        dao.clearWatchHistory(profileId)
    }

    fun buildLiveStreamUrl(profile: XtreamProfileEntity, streamId: Int): String {
        val base = XtreamLinkParser.normalizeServerUrl(profile.serverUrl)
        val ext = profile.liveFormat.ifBlank { "m3u8" }
        return "$base/live/${profile.username}/${profile.password}/$streamId.$ext"
    }

    fun buildVodStreamUrl(
        profile: XtreamProfileEntity,
        streamId: Int,
        containerExtension: String
    ): String {
        val base = XtreamLinkParser.normalizeServerUrl(profile.serverUrl)
        val ext = containerExtension.ifBlank { "mp4" }
        return "$base/movie/${profile.username}/${profile.password}/$streamId.$ext"
    }

    fun buildSeriesStreamUrl(
        profile: XtreamProfileEntity,
        episodeId: String,
        containerExtension: String
    ): String {
        val base = XtreamLinkParser.normalizeServerUrl(profile.serverUrl)
        val ext = containerExtension.ifBlank { "mp4" }
        return "$base/series/${profile.username}/${profile.password}/$episodeId.$ext"
    }
}
