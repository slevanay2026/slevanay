package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface XtreamDao {

    // Profiles
    @Query("SELECT * FROM xtream_profiles ORDER BY isActive DESC, id DESC")
    fun observeProfiles(): Flow<List<XtreamProfileEntity>>

    @Query("SELECT * FROM xtream_profiles WHERE isActive = 1 LIMIT 1")
    fun observeActiveProfile(): Flow<XtreamProfileEntity?>

    @Query("SELECT * FROM xtream_profiles WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveProfile(): XtreamProfileEntity?

    @Query("SELECT * FROM xtream_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): XtreamProfileEntity?

    @Query("SELECT * FROM xtream_profiles ORDER BY isActive DESC, id DESC")
    suspend fun getAllProfilesOnce(): List<XtreamProfileEntity>

    @Query("SELECT COUNT(*) FROM xtream_profiles")
    suspend fun getProfileCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: XtreamProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: XtreamProfileEntity)

    @Query("UPDATE xtream_profiles SET isActive = CASE WHEN id = :activeId THEN 1 ELSE 0 END")
    suspend fun setActiveProfile(activeId: Long)

    @Query("UPDATE xtream_profiles SET liveFormat = :format WHERE id = :profileId")
    suspend fun updateLiveFormat(profileId: Long, format: String)

    @Query("DELETE FROM xtream_profiles WHERE id = :profileId")
    suspend fun deleteProfileById(profileId: Long)

    // Categories
    @Query(
        "SELECT * FROM categories WHERE profileId = :profileId AND type = :type " +
            "ORDER BY isPinned DESC, sortOrder ASC, name ASC"
    )
    fun observeCategories(profileId: Long, type: String): Flow<List<CategoryEntity>>

    @Query(
        "SELECT * FROM categories WHERE profileId = :profileId AND type = :type AND isVisible = 1 " +
            "ORDER BY isPinned DESC, sortOrder ASC, name ASC"
    )
    fun observeVisibleCategories(profileId: Long, type: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE profileId = :profileId AND type = :type")
    suspend fun getCategoriesOnce(profileId: Long, type: String): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("UPDATE categories SET isVisible = :isVisible WHERE id = :id")
    suspend fun setCategoryVisibility(id: String, isVisible: Boolean)

    @Query("UPDATE categories SET isPinned = :isPinned WHERE id = :id")
    suspend fun setCategoryPinned(id: String, isPinned: Boolean)

    @Query("UPDATE categories SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateCategorySortOrder(id: String, sortOrder: Int)

    @Query("UPDATE categories SET isVisible = :isVisible WHERE profileId = :profileId AND type = :type")
    suspend fun setAllCategoriesVisibility(profileId: Long, type: String, isVisible: Boolean)

    @Query("DELETE FROM categories WHERE profileId = :profileId")
    suspend fun deleteCategoriesForProfile(profileId: Long)

    // Live Channels
    @Query(
        "SELECT c.* FROM channels c " +
            "LEFT JOIN categories cat ON c.profileId = cat.profileId AND cat.type = 'LIVE' AND c.categoryId = cat.categoryId " +
            "WHERE c.profileId = :profileId AND c.isHidden = 0 AND (cat.isVisible IS NULL OR cat.isVisible = 1) " +
            "ORDER BY c.num ASC, c.name ASC"
    )
    fun observeVisibleChannels(profileId: Long): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE profileId = :profileId ORDER BY num ASC, name ASC")
    fun observeAllChannels(profileId: Long): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE profileId = :profileId")
    suspend fun getChannelsOnce(profileId: Long): List<ChannelEntity>

    @Query("SELECT COUNT(*) FROM channels WHERE profileId = :profileId")
    suspend fun getChannelCount(profileId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    @Query("UPDATE channels SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setChannelFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE channels SET isHidden = :isHidden WHERE id = :id")
    suspend fun setChannelHidden(id: String, isHidden: Boolean)

    @Query("UPDATE channels SET isHidden = :isHidden WHERE profileId = :profileId")
    suspend fun setAllChannelsHidden(profileId: Long, isHidden: Boolean)

    @Query("DELETE FROM channels WHERE profileId = :profileId")
    suspend fun deleteChannelsForProfile(profileId: Long)

    // VOD Movies
    @Query(
        "SELECT v.* FROM vod_streams v " +
            "LEFT JOIN categories cat ON v.profileId = cat.profileId AND cat.type = 'VOD' AND v.categoryId = cat.categoryId " +
            "WHERE v.profileId = :profileId AND v.isHidden = 0 AND (cat.isVisible IS NULL OR cat.isVisible = 1) " +
            "ORDER BY v.added DESC, v.name ASC"
    )
    fun observeVisibleVods(profileId: Long): Flow<List<VodEntity>>

    @Query("SELECT * FROM vod_streams WHERE profileId = :profileId")
    suspend fun getVodsOnce(profileId: Long): List<VodEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVods(vods: List<VodEntity>)

    @Query("UPDATE vod_streams SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setVodFavorite(id: String, isFavorite: Boolean)

    @Query("DELETE FROM vod_streams WHERE profileId = :profileId")
    suspend fun deleteVodsForProfile(profileId: Long)

    // Series
    @Query(
        "SELECT s.* FROM series_items s " +
            "LEFT JOIN categories cat ON s.profileId = cat.profileId AND cat.type = 'SERIES' AND s.categoryId = cat.categoryId " +
            "WHERE s.profileId = :profileId AND s.isHidden = 0 AND (cat.isVisible IS NULL OR cat.isVisible = 1) " +
            "ORDER BY s.name ASC"
    )
    fun observeVisibleSeries(profileId: Long): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series_items WHERE profileId = :profileId")
    suspend fun getSeriesOnce(profileId: Long): List<SeriesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(series: List<SeriesEntity>)

    @Query("UPDATE series_items SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setSeriesFavorite(id: String, isFavorite: Boolean)

    @Query("DELETE FROM series_items WHERE profileId = :profileId")
    suspend fun deleteSeriesForProfile(profileId: Long)

    // Watch History
    @Query("SELECT * FROM watch_history WHERE profileId = :profileId ORDER BY updatedAt DESC LIMIT 20")
    fun observeWatchHistory(profileId: Long): Flow<List<WatchHistoryEntity>>

    @Query(
        "SELECT * FROM watch_history " +
            "WHERE profileId = :profileId AND contentType = 'LIVE' " +
            "ORDER BY updatedAt DESC LIMIT 25"
    )
    fun observeRecentlyWatchedChannels(profileId: Long): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE id = :id LIMIT 1")
    suspend fun getWatchHistoryItem(id: String): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWatchHistory(entry: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE id = :id")
    suspend fun deleteWatchHistoryItem(id: String)

    @Query("DELETE FROM watch_history WHERE profileId = :profileId")
    suspend fun clearWatchHistory(profileId: Long)
}
