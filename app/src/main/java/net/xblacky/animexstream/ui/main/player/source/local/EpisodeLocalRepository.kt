package net.xblacky.animexstream.ui.main.player.source.local

import io.realm.Realm
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.model.Content
import net.xblacky.animexstream.utils.model.WatchedEpisode
import net.xblacky.animexstream.utils.realm.InitalizeRealm
import timber.log.Timber
import javax.inject.Inject

class EpisodeLocalRepository @Inject constructor() {


     fun fetchWatchDuration(id: Int): WatchedEpisode? {
        val realm = Realm.getInstance(InitalizeRealm.getConfig())

            return realm.where(WatchedEpisode::class.java).equalTo("id", id).findFirst()


    }

    fun fetchContent(episodeUrl: String): Content? {
        val realm = Realm.getInstance(InitalizeRealm.getConfig())
        realm.use {
            var content: Content? = null
            val result =
                it.where(Content::class.java).equalTo("episodeUrl", episodeUrl).findFirst()
            result?.let { result ->
                content = realm.copyFromRealm(result)
            }
            Timber.e("ID : %s", content?.episodeUrl.hashCode())
            val watchedEpisode = fetchWatchDuration(content?.episodeUrl.hashCode())
            content?.watchedDuration = watchedEpisode?.watchedDuration ?: 0
            return content
        }
    }


    fun saveContent(content: Content) {
        val realm = Realm.getInstance(InitalizeRealm.getConfig())
        realm.use {
            content.insertionTime = System.currentTimeMillis()
            it.executeTransactionAsync { realmAsync ->
                realmAsync.insertOrUpdate(content)
            }

            val progressPercentage: Long =
                ((content.watchedDuration.toDouble() / (content.duration).toDouble()) * 100).toLong()
            val watchedEpisode = WatchedEpisode(
                id = content.episodeUrl.hashCode(),
                watchedDuration = content.watchedDuration,
                watchedPercentage = progressPercentage,
                animeName = content.animeName

            )
            realm.executeTransactionAsync { realmAsync ->
                realmAsync.insertOrUpdate(watchedEpisode)
            }
        }
    }


    fun clearContent() {
        val realm = Realm.getInstance(InitalizeRealm.getConfig())
        realm.use {
            it.executeTransactionAsync { realmAsync ->
                val results = realmAsync.where(Content::class.java).lessThanOrEqualTo(
                    "insertionTime",
                    System.currentTimeMillis() - C.MAX_TIME_M3U8_URL
                ).findAll()
                results.deleteAllFromRealm()
            }
        }

    }
}