package net.xblacky.animexstream.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.xblacky.animexstream.ui.main.home.source.HomeRepository
import net.xblacky.animexstream.utils.Result
import net.xblacky.animexstream.utils.Utils
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.di.DispatcherModule
import net.xblacky.animexstream.utils.model.AnimeMetaModel
import net.xblacky.animexstream.utils.model.HomeScreenModel
import net.xblacky.animexstream.utils.model.UpdateModel
import timber.log.Timber
import java.lang.IndexOutOfBoundsException
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    @DispatcherModule.MainDispatcher val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _animeList: MutableLiveData<ArrayList<HomeScreenModel>> =
        MutableLiveData(makeEmptyArrayList())
    var animeList: LiveData<ArrayList<HomeScreenModel>> = _animeList

    private var _updateModel: MutableLiveData<UpdateModel> = MutableLiveData()
    var updateModel: LiveData<UpdateModel> = _updateModel
    private lateinit var database: DatabaseReference

    init {
        fetchHomeList()
        queryDB()
    }

    private fun fetchHomeList() {
        viewModelScope.launch {

            val deferred = listOf(
                async { fetchRecentSub() },
                async { fetchRecentDub() },
                async { fetchPopular() },
                async { fetchNewSeason() },
                async { fetchMovies() }
            )
            deferred.awaitAll()
        }
    }

    private fun queryDB() {
        database = Firebase.database.reference
        val query: Query = database.child("appdata")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(ignored: DatabaseError) {
                Timber.e(ignored.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.e(snapshot.toString())
                _updateModel.value = UpdateModel(
                    versionCode = snapshot.child("versionCode").value as Long,
                    whatsNew = snapshot.child("whatsNew").value.toString()
                )
            }

        })
    }


    private fun updateError(e: Throwable) {
        var isListEmpty = true
        animeList.value?.forEach {
            if (!it.animeList.isNullOrEmpty()) {
                isListEmpty = false
            }
        }
//        super.updateErrorModel(true , e , isListEmpty)

    }


    private fun updateList(list: ArrayList<AnimeMetaModel>, typeValue: Int) {
        val homeScreenModel = HomeScreenModel(
            typeValue = typeValue,
            type = Utils.getTypeName(typeValue),
            animeList = list
        )
        Timber.e("Update List Called with type ${homeScreenModel.type}")
        Timber.e("List Called type value: $typeValue")
        val newList = animeList.value
        try {
            newList?.set(getPositionByType(typeValue), homeScreenModel)

        } catch (iobe: IndexOutOfBoundsException) {
        }
        _animeList.postValue(newList)
    }

    private fun getPositionByType(typeValue: Int): Int {
        return when (typeValue) {
            C.TYPE_RECENT_SUB -> C.RECENT_SUB_POSITION
            C.TYPE_RECENT_DUB -> C.RECENT_DUB_POSITION
            C.TYPE_MOVIE -> C.MOVIE_POSITION
            C.TYPE_NEW_SEASON -> C.NEWEST_SEASON_POSITION
            C.TYPE_POPULAR_ANIME -> C.POPULAR_POSITION
            else -> 0
        }
    }


    private fun makeEmptyArrayList(): ArrayList<HomeScreenModel> {
        var i = 1
        val arrayList: ArrayList<HomeScreenModel> = ArrayList()
        while (i <= 6) {
            arrayList.add(
                HomeScreenModel(
                    typeValue = i
                )
            )
            i++
        }
        return arrayList
    }

    private fun fetchRecentSub() {
        viewModelScope.launch(dispatcher) {
            repository.fetchRecentSub(1, C.RECENT_SUB).collect {
                if (it is Result.Success) {
                    updateList(it.data, C.TYPE_RECENT_SUB)
                }
            }
        }
    }

    private fun fetchRecentDub() {
        viewModelScope.launch(dispatcher) {
            repository.fetchRecentDub(1, C.TYPE_RECENT_DUB).collect {
                if (it is Result.Success) {
                    Timber.e("Recent Dub Called")
                    updateList(it.data, C.TYPE_RECENT_DUB)
                }
            }
        }
    }

    private fun fetchMovies() {

        viewModelScope.launch(dispatcher) {
            repository.fetchMovies(1, C.TYPE_MOVIE).collect {
                if (it is Result.Success) {
                    updateList(it.data, C.TYPE_MOVIE)
                }
            }
        }
    }

    private fun fetchPopular() {
        viewModelScope.launch(dispatcher) {
            repository.fetchPopular(1, C.TYPE_POPULAR_ANIME).collect {
                if (it is Result.Success) {
                    updateList(it.data, C.TYPE_POPULAR_ANIME)
                }
            }
        }
    }

    override fun onCleared() {
        viewModelScope.launch(dispatcher) {
            repository.removeOldData()
        }
        super.onCleared()
    }

    private fun fetchNewSeason() {
        viewModelScope.launch(dispatcher) {
            repository.fetchNewSeasons(1, C.TYPE_NEW_SEASON).collect {
                if (it is Result.Success) {
                    updateList(it.data, C.TYPE_NEW_SEASON)
                }
            }
        }
    }

}