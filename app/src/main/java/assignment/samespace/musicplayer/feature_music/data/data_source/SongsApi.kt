package assignment.samespace.musicplayer.feature_music.data.data_source

import assignment.samespace.musicplayer.feature_music.domain.model.SongData
import retrofit2.http.GET


interface SongsApi {

    @GET("/items/songs")
    suspend fun getAllSongs(): SongData

}