package net.xblacky.animexstream.ui.main.home.source

import io.realm.Realm
import io.realm.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.di.DispatcherModule
import net.xblacky.animexstream.utils.model.AnimeMetaModel
import net.xblacky.animexstream.utils.realm.InitalizeRealm
import javax.inject.Inject
import net.xblacky.animexstream.utils.Result

class HomeLocalRepository @Inject constructor(@DispatcherModule.IoDispatcher val dispatcher: CoroutineDispatcher) {


    suspend fun addDataInRealm(animeList: ArrayList<AnimeMetaModel>) {

        withContext(dispatcher) {
            val realm: Realm = Realm.getInstance(InitalizeRealm.getConfig())
            try {
                realm.executeTransaction { realm1: Realm ->
                    realm1.insertOrUpdate(animeList)
                }
            } catch (ignored: Exception) {
            } finally {
                realm.close()
            }
        }

    }

    suspend fun removeFromRealm() {
        withContext(dispatcher) {
            val realm: Realm = Realm.getInstance(InitalizeRealm.getConfig())
            realm.executeTransaction {
                val results = it.where(AnimeMetaModel::class.java)
                    .lessThanOrEqualTo(
                        "timestamp",
                        System.currentTimeMillis() - C.MAX_TIME_FOR_ANIME
                    )
                    .findAll()
                results.deleteAllFromRealm()
            }
            realm.close()
        }

    }

    suspend fun fetchFromRealm(typeValue: Int): Result<ArrayList<AnimeMetaModel>> {
        return withContext(dispatcher) {
            val realm: Realm = Realm.getInstance(InitalizeRealm.getConfig())

            val list: ArrayList<AnimeMetaModel> = ArrayList()
            try {
                val results =
                    realm.where(AnimeMetaModel::class.java)?.equalTo("typeValue", typeValue)
                        ?.sort("insertionOrder", Sort.ASCENDING)?.findAll()
                results?.let {
                    list.addAll(realm.copyFromRealm(results))
                }
                Result.Success(list)

            } catch (exc: Exception) {
                Result.Error(exc)
            } finally {
                realm.close()
            }
        }
    }
}