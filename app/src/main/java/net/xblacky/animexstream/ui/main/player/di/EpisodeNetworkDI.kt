package net.xblacky.animexstream.ui.main.player.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import net.xblacky.animexstream.utils.rertofit.NetworkInterface
import retrofit2.Retrofit


@InstallIn(ViewModelComponent::class)
@Module
object EpisodeNetworkDI {

    @Provides
    @ViewModelScoped
    fun provideEpisodeNetworkService(retrofit: Retrofit): NetworkInterface.EpisodeDataService {
        return retrofit.create(NetworkInterface.EpisodeDataService::class.java)
    }

}