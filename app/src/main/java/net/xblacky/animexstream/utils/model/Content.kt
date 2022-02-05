package net.xblacky.animexstream.utils.model

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import java.time.Duration


open class Content(
    var urls: ArrayList<Source> = ArrayList(),
    var animeName: String = "",
    var episodeName: String? = "",
    var episodeUrl: String? = "",
    var nextEpisodeUrl: String? = null,
    var previousEpisodeUrl: String? = null,
    var watchedDuration: Long = 0,
    var duration: Long = 0,
)