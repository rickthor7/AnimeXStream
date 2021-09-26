package net.xblacky.animexstream.ui.main.home.source

import net.xblacky.animexstream.utils.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.xblacky.animexstream.ui.main.home.di.HomeRepositoryModule
import net.xblacky.animexstream.utils.model.AnimeMetaModel

class HomeRepository @Inject constructor(
    @HomeRepositoryModule.LocalRepo private val localRepository: HomeDataSource,
    @HomeRepositoryModule.RemoteRepo private val remoteRepository: HomeDataSource,
) {

    suspend fun fetchRecentSub(page: Int, type: Int): Flow<Result<ArrayList<AnimeMetaModel>>> {
        return flow {
            val result = localRepository.getHomeData(page, type)
            if (result is Result.Success) {
                emit(result)
            }
            val networkResponse = remoteRepository.getHomeData(page, type)
            if (networkResponse is Result.Success) {
                localRepository.saveData(networkResponse.data)
            }
            emit(networkResponse)
        }
    }

    suspend fun fetchRecentDub(page: Int, type: Int): Flow<Result<ArrayList<AnimeMetaModel>>> {
        return flow {
            val result = localRepository.getHomeData(page, type)
            if (result is Result.Success) {
                emit(result)
            }
            val networkResponse = remoteRepository.getHomeData(page, type)
            if (networkResponse is Result.Success) {
                localRepository.saveData(networkResponse.data)
            }
            emit(networkResponse)
        }
    }

    suspend fun fetchMovies(page: Int, type: Int): Flow<Result<ArrayList<AnimeMetaModel>>> {
        return flow {
            val result = localRepository.getHomeData(page, type)
            if (result is Result.Success) {
                emit(result)
            }
            val networkResponse = remoteRepository.getHomeData(page, type)
            if (networkResponse is Result.Success) {
                localRepository.saveData(networkResponse.data)
            }
            emit(networkResponse)
        }
    }

    suspend fun fetchNewSeasons(page: Int, type: Int): Flow<Result<ArrayList<AnimeMetaModel>>> {
        return flow {
            val result = localRepository.getHomeData(page, type)
            if (result is Result.Success) {
                emit(result)
            }
            val networkResponse = remoteRepository.getHomeData(page, type)
            if (networkResponse is Result.Success) {
                localRepository.saveData(networkResponse.data)
            }
            emit(networkResponse)
        }
    }

    suspend fun fetchPopular(page: Int, type: Int): Flow<Result<ArrayList<AnimeMetaModel>>> {
        return flow {
            val result = localRepository.getHomeData(page, type)
            if (result is Result.Success) {
                emit(result)
            }
            val networkResponse = remoteRepository.getHomeData(page, type)
            if (networkResponse is Result.Success) {
                localRepository.saveData(networkResponse.data)
            }
            emit(networkResponse)
        }
    }

    suspend fun removeOldData() {
        localRepository.removeData()
    }


}