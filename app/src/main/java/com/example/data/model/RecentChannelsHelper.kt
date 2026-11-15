package com.example.data.model

import com.example.data.local.ChannelEntity
import com.example.data.local.WatchHistoryEntity

object RecentChannelsHelper {
    const val MAX_RECENT_CHANNELS = 5

    fun filterRecentChannels(
        recentHistory: List<WatchHistoryEntity>,
        allChannels: List<ChannelEntity>,
        visibleChannels: List<ChannelEntity>,
        limit: Int = MAX_RECENT_CHANNELS
    ): List<WatchHistoryEntity> {
        val sortedLive = recentHistory
            .filter { ContentType.fromDb(it.contentType) == ContentType.LIVE }
            .sortedByDescending { it.updatedAt }
            .distinctBy { it.streamId }

        if (allChannels.isEmpty()) {
            return sortedLive.take(limit)
        }

        val visibleIds = visibleChannels.map { it.streamId }.toHashSet()
        return sortedLive
            .filter { it.streamId in visibleIds }
            .take(limit)
    }
}
