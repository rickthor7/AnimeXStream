package net.xblacky.animexstream.ui.main.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableObserver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import net.xblacky.animexstream.ui.main.player.source.EpisodeRepository
import net.xblacky.animexstream.utils.CommonViewModel
import net.xblacky.animexstream.utils.di.DispatcherModule
import net.xblacky.animexstream.utils.model.Content
import net.xblacky.animexstream.utils.model.EpisodeInfo
import net.xblacky.animexstream.utils.model.M3U8FromAjaxModel
import net.xblacky.animexstream.utils.parser.HtmlParser
import okhttp3.ResponseBody
import timber.log.Timber
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    @DispatcherModule.MainDispatcher private val dispatcher: CoroutineDispatcher,
    private val episodeRepository: EpisodeRepository
) : CommonViewModel() {

    //Contains Episode info and Previous/Next Episode Urls
    private var _content = MutableLiveData(Content())
    val content: LiveData<Content> = _content

    //Contains all Player Urls ex Mp4/M3U8
    private val _mediaUrls = MutableLiveData<M3U8FromAjaxModel>()
    val mediaUrls: LiveData<M3U8FromAjaxModel> = _mediaUrls

    //Used to Dynamically create referer & Ajax Base Url
    private val _cdnServer: MutableLiveData<String> = MutableLiveData("")
    val cdnServer: LiveData<String> = _cdnServer

    fun updateEpisodeContent(content: Content) {
        _content.value = content
    }


    private fun handleM3U8Url(data: M3U8FromAjaxModel) {
        val content = _content.value
        content?.url = data.sourceMp4[2].url
        _content.value = content
        Timber.e(content?.url)
        saveContent(content!!)
        updateLoading(false)
    }


    //// ---------------------------New Methods------------------------------------------------------

     fun fetchEpisodeData() {
        viewModelScope.launch(dispatcher) {

            try {
                content.value?.episodeUrl?.let {

                    //Request 1st to scrape Media Server Urls
                    val gogoAnimeEpisodeData = episodeRepository.fetchEpisodeData(it)
                    handleEpisodeData(gogoAnimeEpisodeData)


                    //Request 2nd to VidCdn Server to decrypt AJAX URL
                    val encryptedAjaxParams =
                        episodeRepository.fetchAjaxUrl(gogoAnimeEpisodeData.vidCdnUrl)

                    //Generate Url based on Host and Params to request
                    val encryptedAjaxUrl = "${cdnServer.value}encrypt-ajax.php?$encryptedAjaxParams"
                    Timber.e(encryptedAjaxUrl)
                    val m3u8Data = episodeRepository.fetchM3U8DataFromAjax(encryptedAjaxUrl)
                    handleM3U8Url(m3u8Data)

                }
            } catch (exc: Exception) {
                Timber.e(exc)
                updateLoading(loading = false)
            }
        }
    }

    private suspend fun handleEpisodeData(episodeInfo: EpisodeInfo) {
        _cdnServer.value = "https://${URI(episodeInfo.vidCdnUrl).host}/"
        Timber.e(_cdnServer.value)

        _content.value?.previousEpisodeUrl = episodeInfo.previousEpisodeUrl
        _content.value?.nextEpisodeUrl = episodeInfo.nextEpisodeUrl
        updateWatchProgress()

    }

    private suspend fun updateWatchProgress() {
        val watchedEpisode =
            episodeRepository.getWatchDuration(_content.value?.episodeUrl.hashCode())
        _content.value?.watchedDuration = watchedEpisode?.watchedDuration ?: 0

    }

    fun saveContent(content: Content) {
        if (!content.url.isNullOrEmpty()) {
            episodeRepository.saveContent(content)
        }
    }

}