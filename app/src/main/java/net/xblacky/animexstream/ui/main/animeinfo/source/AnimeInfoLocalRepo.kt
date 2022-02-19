package net.xblacky.animexstream.ui.main.animeinfo.source

import io.realm.Realm
import net.xblacky.animexstream.utils.model.AnimeInfoModel
import net.xblacky.animexstream.utils.model.FavouriteModel
import net.xblacky.animexstream.utils.realm.InitalizeRealm

class AnimeInfoLocalRepo {
    private val realm = Realm.getInstance(InitalizeRealm.getConfig())
    fun isFavourite(id: String): Boolean {
        val result = realm.where(FavouriteModel::class.java).equalTo("ID", id).findFirst()
        result?.let {
            return true
        } ?: return false
    }

    fun addToFavourite(favouriteModel: FavouriteModel) {
        realm.executeTransactionAsync {
            it.insertOrUpdate(favouriteModel)
        }
    }

    fun removeFromFavourite(id: String) {
        realm.executeTransactionAsync {
            it.where(FavouriteModel::class.java).equalTo("ID", id).findAll().deleteAllFromRealm()
        }

    }

     fun saveAnimeInfo(model: AnimeInfoModel) {
        realm.executeTransactionAsync {
            it.insertOrUpdate(model)
        }
    }

     fun fetchAnimeInfo(categoryUrl: String): AnimeInfoModel? {
        return realm.where(AnimeInfoModel::class.java).equalTo("categoryUrl", categoryUrl)
            .findFirst()

    }
}