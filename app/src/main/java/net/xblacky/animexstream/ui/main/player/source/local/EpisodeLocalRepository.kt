package net.xblacky.animexstream.ui.main.player.source.local

import android.net.Uri
import io.realm.Realm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.di.DispatcherModule
import net.xblacky.animexstream.utils.model.Content
import net.xblacky.animexstream.utils.model.EpisodeInfo
import net.xblacky.animexstream.utils.model.VidCdnModel
import net.xblacky.animexstream.utils.model.WatchedEpisode
import net.xblacky.animexstream.utils.realm.InitalizeRealm
import timber.log.Timber
import javax.inject.Inject

class EpisodeLocalRepository @Inject constructor(
    @DispatcherModule.IoDispatcher private val dispatcher: CoroutineDispatcher
) {


    fun fetchWatchDuration(id: Int): WatchedEpisode? {
        val realm = Realm.getInstance(InitalizeRealm.getConfig())
        return realm.where(WatchedEpisode::class.java).equalTo("id", id).findFirst()

    }

    suspend fun saveAjaxParams(vidCdnUrl: String, ajaxParams: String) {
        withContext(dispatcher) {
            val realm = Realm.getInstance(InitalizeRealm.getConfig())
            val cdnId = Uri.parse(vidCdnUrl).getQueryParameter("id").toString()
            val vidCdnModel = VidCdnModel(
                vidCdnId = cdnId,
                ajaxParams = ajaxParams
            )
            realm.use { instance ->
                instance.executeTransaction {
                    it.insertOrUpdate(vidCdnModel)
                }
            }
        }
    }


    suspend fun getEpisodeInfo(episodeUrl: String): EpisodeInfo? {
        return withContext(dispatcher) {
            val realm = Realm.getInstance(InitalizeRealm.getConfig())
            realm.use {
                val episodeInfo =
                    it.where(EpisodeInfo::class.java).equalTo("episodeUrl", episodeUrl).findFirst()
                if (episodeInfo != null) {
                    it.copyFromRealm(episodeInfo)
                } else {
                    null
                }
            }
        }
    }

    suspend fun saveEpisodeInfo(episodeInfo: EpisodeInfo) {
        withContext(dispatcher) {
            val realm = Realm.getInstance(InitalizeRealm.getConfig())
            realm.use { instance ->
                instance.executeTransaction {
                    it.insertOrUpdate(episodeInfo)
                }
            }
        }
    }

    suspend fun getAjaxParams(vidCdnUrl: String): String? {
        return withContext(dispatcher) {
            val cdnId = Uri.parse(vidCdnUrl).getQueryParameter("id").toString()
            val realm = Realm.getInstance(InitalizeRealm.getConfig())
            realm.use {
                val vidCdnModel =
                    it.where(VidCdnModel::class.java).equalTo("vidCdnId", cdnId).findFirst()
                vidCdnModel?.ajaxParams
            }
        }

    }


    fun saveWatchProgress(content: Content) {
        val realm = Realm.getInstance(InitalizeRealm.getConfig())
        realm.use {
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
}