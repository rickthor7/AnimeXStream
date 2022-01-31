package net.xblacky.animexstream.ui.main.player.source

import net.xblacky.animexstream.ui.main.player.source.local.EpisodeLocalRepository
import net.xblacky.animexstream.ui.main.player.source.remote.EpisodeRemoteRepository
import net.xblacky.animexstream.utils.Result
import net.xblacky.animexstream.utils.model.Content
import net.xblacky.animexstream.utils.model.EpisodeInfo
import net.xblacky.animexstream.utils.model.M3U8FromAjaxModel
import net.xblacky.animexstream.utils.model.WatchedEpisode
import javax.inject.Inject


class EpisodeRepository @Inject constructor(
    private val localRepo: EpisodeLocalRepository,
    private val remoteRepo: EpisodeRemoteRepository
) {


    suspend fun clearContent() {
        localRepo.clearContent()
    }

    fun saveContent(content: Content) {
        localRepo.saveContent(content = content)
    }

    suspend fun fetchEpisodeData(url: String): EpisodeInfo {
        return remoteRepo.fetchEpisodeData(url)
    }

    suspend fun fetchAjaxUrl(url: String): String {
        return remoteRepo.fetchAjaxUrl(url = url)
    }

    suspend fun fetchM3U8DataFromAjax(url: String): M3U8FromAjaxModel {
        return remoteRepo.fetchM3U8Url(url)

    }

    suspend fun getWatchDuration(id: Int): WatchedEpisode? {
        return localRepo.fetchWatchDuration(id)
    }


}