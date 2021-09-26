package net.xblacky.animexstream.ui.main.home.source

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.xblacky.animexstream.utils.Result
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.parser.NetworkResponse
import net.xblacky.animexstream.utils.rertofit.RetrofitHelper
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


class HomeRemoteRepositoryTest {


    @Test
    //This one Uses Mock WebServer to get 200 Response & check is list parsed successfully
    fun fetchRecentSubOrDub_mockResponse_response200() {

        //Given
        val mockServer = MockWebServer()
        mockServer.enqueue(MockResponse().setBody(NetworkResponse.recentSubResponse))
        mockServer.start()

        val retrofit = Retrofit.Builder()
            .client(OkHttpClient())
            .baseUrl(C.BASE_URL)
            .build()

        val repository =
            HomeRemoteRepository(
                header = NetworkResponse.networkHeader,
                retrofit = retrofit,
                ioDispatcher = Dispatchers.Main
            )

        runBlocking {
            val data = repository.fetchRecentSubOrDub(1, C.TYPE_RECENT_SUB)
            assertThat(data).isInstanceOf(Result.Success::class.java)
        }


    }
}