package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "xtream_profiles")
data class XtreamProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val serverUrl: String,
    val username: String,
    val password: String,
    val isActive: Boolean = true,
    val status: String = "Bilinmiyor",
    val expDate: Long? = null,
    val activeCons: Int = 0,
    val maxConnections: Int = 1,
    val isTrial: Boolean = false,
    val liveFormat: String = "m3u8",
    val lastSyncedAt: Long = 0L
)

@Entity(
    tableName = "categories",
    indices = [Index("profileId"), Index("type")]
)
data class CategoryEntity(
    @PrimaryKey val id: String,
    val profileId: Long,
    val categoryId: String,
    val name: String,
    val type: String, // LIVE, VOD, SERIES
    val sortOrder: Int,
    val isVisible: Boolean = true,
    val isPinned: Boolean = false
)

@Entity(
    tableName = "channels",
    indices = [Index("profileId"), Index("categoryId")]
)
data class ChannelEntity(
    @PrimaryKey val id: String,
    val profileId: Long,
    val streamId: Int,
    val num: Int,
    val name: String,
    val streamIcon: String,
    val categoryId: String,
    val epgChannelId: String,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false
)

@Entity(
    tableName = "vod_streams",
    indices = [Index("profileId"), Index("categoryId")]
)
data class VodEntity(
    @PrimaryKey val id: String,
    val profileId: Long,
    val streamId: Int,
    val name: String,
    val streamIcon: String,
    val categoryId: String,
    val containerExtension: String,
    val rating: String,
    val added: Long,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false
)

@Entity(
    tableName = "series_items",
    indices = [Index("profileId"), Index("categoryId")]
)
data class SeriesEntity(
    @PrimaryKey val id: String,
    val profileId: Long,
    val seriesId: Int,
    val name: String,
    val cover: String,
    val plot: String,
    val cast: String,
    val genre: String,
    val releaseDate: String,
    val rating: String,
    val categoryId: String,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false
)

@Entity(
    tableName = "watch_history",
    indices = [Index("profileId"), Index("updatedAt")]
)
data class WatchHistoryEntity(
    @PrimaryKey val id: String,
    val profileId: Long,
    val contentType: String,
    val streamId: Int,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val streamUrl: String,
    val containerExtension: String,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long
)
