package net.xblacky.animexstream.ui.main.animeinfo.source

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.xblacky.animexstream.utils.Utils
import net.xblacky.animexstream.utils.di.DispatcherModule
import net.xblacky.animexstream.utils.model.AnimeInfoModel
import net.xblacky.animexstream.utils.model.EpisodeModel
import net.xblacky.animexstream.utils.parser.HtmlParser
import net.xblacky.animexstream.utils.rertofit.NetworkInterface
import net.xblacky.animexstream.utils.rertofit.RetrofitHelper
import okhttp3.ResponseBody
import javax.inject.Inject

class AnimeInfoRemoteRepo @Inject constructor(@DispatcherModule.IoDispatcher val dispatcher: CoroutineDispatcher) {
    private val retrofit = RetrofitHelper.getRetrofitInstance()


    suspend fun fetchAnimeInfo(categoryUrl: String): AnimeInfoModel {
        return withContext(dispatcher) {
            val animeInfoService = retrofit.create(NetworkInterface.FetchAnimeInfo::class.java)
            val data = animeInfoService.get(Utils.getHeader(), categoryUrl)
            HtmlParser.parseAnimeInfo(data.string())
        }

    }

    suspend fun fetchEpisodeList(
        id: String,
        alias: String
    ): ArrayList<EpisodeModel> {

        return withContext(dispatcher) {
            val animeEpisodeService = retrofit.create(NetworkInterface.FetchEpisodeList::class.java)
            val data = animeEpisodeService.get(
                id = id,
                alias = alias,
                header = Utils.getHeader()
            )
            HtmlParser.fetchEpisodeList(data.string())
        }
    }
}