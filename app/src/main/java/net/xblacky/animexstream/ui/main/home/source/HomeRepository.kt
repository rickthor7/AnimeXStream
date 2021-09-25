package net.xblacky.animexstream.ui.main.home.source

import kotlinx.coroutines.CoroutineDispatcher
import net.xblacky.animexstream.utils.Result
import net.xblacky.animexstream.utils.di.DispatcherModule
import okhttp3.Dispatcher
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import net.xblacky.animexstream.utils.model.AnimeMetaModel
import net.xblacky.animexstream.utils.parser.HtmlParser
import okhttp3.ResponseBody
import java.lang.Exception
import kotlin.coroutines.coroutineContext

class HomeRepository @Inject constructor(
    private val localRepository: HomeLocalRepository,
    private val remoteRepository: HomeRemoteRepository,
    @DispatcherModule.DefaultDispatcher val dispatcher: CoroutineDispatcher
) {

    suspend fun fetchRecentSub(page: Int, type: Int): Flow<Result<ArrayList<AnimeMetaModel>>> {
        return flow {
            val result = localRepository.fetchFromRealm(type)
            if (result is Result.Success) {
                emit(result)
            }
            val networkResponse = remoteRepository.fetchRecentSubOrDub(page, type)
            if (networkResponse is Result.Success) {
                localRepository.addDataInRealm(networkResponse.data)
            }
            emit(networkResponse)
        }
    }

    suspend fun fetchRecentDub(page: Int, type: Int): Flow<Result<ArrayList<AnimeMetaModel>>> {
        return flow {
            val result = localRepository.fetchFromRealm(type)
            if (result is Result.Success) {
                emit(result)
            }
            val networkResponse = remoteRepository.fetchRecentSubOrDub(page, type)
            if (networkResponse is Result.Success) {
                localRepository.addDataInRealm(networkResponse.data)
            }
            emit(networkResponse)
        }
    }

    suspend fun fetchMovies(page: Int, type: Int): Flow<Result<ArrayList<AnimeMetaModel>>> {
        return flow {
            val result = localRepository.fetchFromRealm(type)
            if (result is Result.Success) {
                emit(result)
            }
            val networkResponse = remoteRepository.fetchMovies(page, type)
            if (networkResponse is Result.Success) {
                localRepository.addDataInRealm(networkResponse.data)
            }
            emit(networkResponse)
        }
    }

    suspend fun fetchNewSeasons(page: Int, type: Int): Flow<Result<ArrayList<AnimeMetaModel>>> {
        return flow {
            val result = localRepository.fetchFromRealm(type)
            if (result is Result.Success) {
                emit(result)
            }
            val networkResponse = remoteRepository.fetchNewestAnime(page, type)
            if (networkResponse is Result.Success) {
                localRepository.addDataInRealm(networkResponse.data)
            }
            emit(networkResponse)
        }
    }

    suspend fun fetchPopular(page: Int, type: Int): Flow<Result<ArrayList<AnimeMetaModel>>> {
        return flow {
            val result = localRepository.fetchFromRealm(type)
            if (result is Result.Success) {
                emit(result)
            }
            val networkResponse = remoteRepository.fetchPopularFromAjax(page, type)
            if (networkResponse is Result.Success) {
                localRepository.addDataInRealm(networkResponse.data)
            }
            emit(networkResponse)
        }
    }

    suspend fun removeOldData() {
        localRepository.removeFromRealm()
    }


}