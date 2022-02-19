package net.xblacky.animexstream.ui.main.animeinfo.source

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import net.xblacky.animexstream.utils.Utils
import net.xblacky.animexstream.utils.model.AnimeInfoModel
import net.xblacky.animexstream.utils.model.EpisodeModel
import net.xblacky.animexstream.utils.model.FavouriteModel
import net.xblacky.animexstream.utils.realm.InitalizeRealm
import net.xblacky.animexstream.utils.rertofit.NetworkInterface
import net.xblacky.animexstream.utils.rertofit.RetrofitHelper
import okhttp3.ResponseBody
import javax.inject.Inject

class AnimeInfoRepository @Inject constructor(
    private val localRepo: AnimeInfoLocalRepo,
    private val remoteRepo: AnimeInfoRemoteRepo
) {

    fun isFavourite(id: String): Boolean {
        return localRepo.isFavourite(id)
    }

    fun addToFavourite(favouriteModel: FavouriteModel) {
        localRepo.addToFavourite(favouriteModel)
    }

    fun removeFromFavourite(id: String) {
        localRepo.removeFromFavourite(id)
    }


    suspend fun fetchAnimeInfo(categoryUrl: String): AnimeInfoModel {
        return localRepo.fetchAnimeInfo(categoryUrl) ?: fetchAndSaveAnimeFromInternet(categoryUrl)

    }

    private suspend fun fetchAndSaveAnimeFromInternet(categoryUrl: String): AnimeInfoModel {
        val model = remoteRepo.fetchAnimeInfo(categoryUrl)
        model.categoryUrl = categoryUrl
        localRepo.saveAnimeInfo(model)
        return model
    }

    suspend fun fetchEpisodeList(id: String, alias: String): ArrayList<EpisodeModel> {
        return remoteRepo.fetchEpisodeList(id = id, alias = alias)
    }
}