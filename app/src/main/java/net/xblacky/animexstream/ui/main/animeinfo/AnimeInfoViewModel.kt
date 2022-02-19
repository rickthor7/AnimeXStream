package net.xblacky.animexstream.ui.main.animeinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import kotlinx.coroutines.launch
import net.xblacky.animexstream.ui.main.animeinfo.source.AnimeInfoRepository
import net.xblacky.animexstream.utils.CommonViewModel
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.model.AnimeInfoModel
import net.xblacky.animexstream.utils.model.EpisodeModel
import net.xblacky.animexstream.utils.model.FavouriteModel
import net.xblacky.animexstream.utils.parser.HtmlParser
import okhttp3.Dispatcher
import okhttp3.ResponseBody
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class AnimeInfoViewModel(private val categoryUrl: String) : CommonViewModel() {

    private var _animeInfoModel: MutableLiveData<AnimeInfoModel> = MutableLiveData()
    var animeInfoModel: LiveData<AnimeInfoModel> = _animeInfoModel

    private var _episodeList: MutableLiveData<ArrayList<EpisodeModel>> = MutableLiveData()
    var episodeList: LiveData<ArrayList<EpisodeModel>> = _episodeList

    private val animeInfoRepository = AnimeInfoRepository()

    private var _isFavourite: MutableLiveData<Boolean> = MutableLiveData(false)
    var isFavourite: LiveData<Boolean> = _isFavourite

    init {
        fetchAnimeInfo()
    }

    fun fetchAnimeInfo() {
        viewModelScope.launch {
            try {
                updateLoading(loading = true)
                updateErrorModel(false, null, false)
                val data = animeInfoRepository.fetchAnimeInfo(categoryUrl)
                _animeInfoModel.value = data
                _isFavourite.value = animeInfoRepository.isFavourite(data.id)
                val episodeList =
                    animeInfoRepository.fetchEpisodeList(id = data.id, alias = data.alias)
                _episodeList.value = episodeList
                updateLoading(false)
            } catch (exc: Exception) {
                Timber.e(exc)
                updateErrorModel(show = true, e = exc, isListEmpty = false)
            }

        }
    }

    fun toggleFavourite() {
        if (_isFavourite.value!!) {
            animeInfoModel.value?.id?.let { animeInfoRepository.removeFromFavourite(it) }
            _isFavourite.value = false
        } else {
            saveFavourite()

        }
    }

    private fun saveFavourite() {
        val model = animeInfoModel.value
        animeInfoRepository.addToFavourite(
            FavouriteModel(
                ID = model?.id,
                categoryUrl = categoryUrl,
                animeName = model?.animeTitle,
                releasedDate = model?.releasedTime,
                imageUrl = model?.imageUrl
            )
        )
        _isFavourite.value = true
    }
}