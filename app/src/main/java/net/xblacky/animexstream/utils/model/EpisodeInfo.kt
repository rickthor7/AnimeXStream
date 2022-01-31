package net.xblacky.animexstream.utils.model

data class EpisodeInfo(
    var vidCdnUrl: String,
    var nextEpisodeUrl: String? = null,
    var previousEpisodeUrl: String? = null
)