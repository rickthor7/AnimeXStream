package net.xblacky.animexstream.ui.main.home.di


import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import net.xblacky.animexstream.ui.main.home.source.HomeDataSource
import net.xblacky.animexstream.ui.main.home.source.HomeLocalRepository
import net.xblacky.animexstream.ui.main.home.source.HomeRemoteRepository
import net.xblacky.animexstream.utils.rertofit.NetworkInterface
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(ViewModelComponent::class)
@Module
object HomeNetworkDI {

    @Provides
    @ViewModelScoped
    fun provideHomeNetworkService(retrofit: Retrofit): NetworkInterface.HomeDataService {
        return retrofit.create(NetworkInterface.HomeDataService::class.java)
    }

}


@InstallIn(ViewModelComponent::class)
@Module
abstract class HomeRepositoryModule {

    @Qualifier
    annotation class LocalRepo

    @Qualifier
    annotation class RemoteRepo

    @LocalRepo
    @ViewModelScoped
    @Binds
    abstract fun bindLocalRepo(localRepo: HomeLocalRepository): HomeDataSource

    @RemoteRepo
    @ViewModelScoped
    @Binds
    abstract fun bindRemoteRepo(remoteRepo: HomeRemoteRepository): HomeDataSource
}