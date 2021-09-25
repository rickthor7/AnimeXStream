package net.xblacky.animexstream.ui.main.home.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.xblacky.animexstream.utils.Result
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.di.AppModules
import net.xblacky.animexstream.utils.di.DispatcherModule
import net.xblacky.animexstream.utils.model.AnimeMetaModel
import net.xblacky.animexstream.utils.parser.HtmlParser
import net.xblacky.animexstream.utils.rertofit.NetworkInterface
import okhttp3.ResponseBody
import retrofit2.Retrofit
import java.lang.Exception
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class HomeRemoteRepository
@Inject constructor(
    val retrofit: Retrofit,
    @AppModules.RequestHeader val header: Map<String, String>,
    @DispatcherModule.IoDispatcher val ioDispatcher: CoroutineDispatcher
) {


    suspend fun fetchRecentSubOrDub(page: Int, type: Int): Result<ArrayList<AnimeMetaModel>> {
        return withContext(ioDispatcher) {
            try {
                val fetchHomeListService =
                    retrofit.create(NetworkInterface.FetchRecentSubOrDub::class.java)
                val response = fetchHomeListService.get(header, page, type)
                val data = HtmlParser.parseRecentSubOrDub(
                    response = response.string(),
                    typeValue = type,
                )
                Result.Success(data)
            } catch (exc: Exception) {
                Result.Error(exc)
            }
        }
    }

    suspend fun fetchPopularFromAjax(page: Int, type: Int): Result<ArrayList<AnimeMetaModel>> {
        return withContext(ioDispatcher) {
            try {
                val fetchPopularListService =
                    retrofit.create(NetworkInterface.FetchPopularFromAjax::class.java)
                val response = fetchPopularListService.get(header, page)
                val data = HtmlParser.parsePopular(
                    response = response.string(),
                    typeValue = type,
                )
                Result.Success(data)
            } catch (exc: Exception) {
                Result.Error(exc)
            }
        }
    }

    suspend fun fetchMovies(page: Int, type: Int): Result<ArrayList<AnimeMetaModel>> {

        return withContext(ioDispatcher) {
            try {
                val fetchMoviesListService =
                    retrofit.create(NetworkInterface.FetchMovies::class.java)
                val response = fetchMoviesListService.get(header, page)
                val data = HtmlParser.parseMovie(
                    response = response.string(),
                    typeValue = type,
                )
                Result.Success(data)
            } catch (exc: Exception) {
                Result.Error(exc)
            }
        }

    }

    suspend fun fetchNewestAnime(page: Int, type: Int): Result<ArrayList<AnimeMetaModel>> {
        return withContext(ioDispatcher) {
            try {
                val fetchNewestSeasonService =
                    retrofit.create(NetworkInterface.FetchNewestSeason::class.java)
                val response = fetchNewestSeasonService.get(header, page)
                val data = HtmlParser.parseMovie(
                    response = response.string(),
                    typeValue = type,
                )
                Result.Success(data)
            } catch (exc: Exception) {
                Result.Error(exc)
            }
        }
    }

}