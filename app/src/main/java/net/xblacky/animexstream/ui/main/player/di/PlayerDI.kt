package net.xblacky.animexstream.ui.main.player.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import net.xblacky.animexstream.utils.preference.Preference
import net.xblacky.animexstream.utils.rertofit.NetworkInterface
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Qualifier

@InstallIn(ActivityComponent::class)
@Module
object PlayerDI {

    @Qualifier
    annotation class ExoOkHttpClient

    @Provides
    @ActivityScoped
    @ExoOkHttpClient
    fun providePlayerOkHttpClient(preference: Preference): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Referer", preference.getReferrer())
                .addHeader(
                    "user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36"
                )
                .build()
            chain.proceed(newRequest)
        }
        return okHttpClient.build()
    }
}