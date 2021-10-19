package net.xblacky.animexstream.utils.model

import kotlin.collections.ArrayList

data class HomeScreenModel(
    val typeValue: Int,
    val type: String,
    val animeList: ArrayList<AnimeMetaModel>
)