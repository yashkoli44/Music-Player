package assignment.samespace.musicplayer

import assignment.samespace.musicplayer.feature_music.data.data_source.SongsApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.gen5.api.AfterEach
import org.junit.gen5.api.BeforeEach
import org.junit.gen5.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SongApiUnitTest {
    private lateinit var server: MockWebServer
    private lateinit var api: SongsApi

    @BeforeEach
    fun beforeEach(){
        server = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SongsApi::class.java)
    }

    @Test
    fun `getSongs, status success`() = runTest {
//        val gson: Gson = GsonBuilder().create()
//        val json = gson.toJson(dto)!!
//        val res = MockResponse()
//        res.setBody(json)
//        server.enqueue(res)

        val data = api.getAllSongs()
        server.takeRequest()

        assert(data.songs.isNotEmpty())
    }

    @AfterEach
    fun afterEach() {
        server.shutdown()
    }

}