package assignment.samespace.musicplayer.feature_music.domain.repository

import assignment.samespace.musicplayer.feature_music.domain.model.Song

interface SongRepository {

    suspend fun getAllSongs(): List<Song>

}