package net.xblacky.animexstream.ui.main.animeinfo.di

import dagger.assisted.AssistedFactory
import net.xblacky.animexstream.ui.main.animeinfo.AnimeInfoViewModel

@AssistedFactory
interface AnimeInfoFactory {
    fun create(categoryUrl: String): AnimeInfoViewModel
}
