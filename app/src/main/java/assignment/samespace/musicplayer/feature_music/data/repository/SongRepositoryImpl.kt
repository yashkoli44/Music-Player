package assignment.samespace.musicplayer.feature_music.data.repository

import assignment.samespace.musicplayer.feature_music.data.data_source.SongsApi
import assignment.samespace.musicplayer.feature_music.domain.model.Song
import assignment.samespace.musicplayer.feature_music.domain.repository.SongRepository

class SongRepositoryImpl(
    private val songsApi: SongsApi
) : SongRepository {

    override suspend fun getAllSongs(): List<Song> {
        return songsApi.getAllSongs().songs
    }

}