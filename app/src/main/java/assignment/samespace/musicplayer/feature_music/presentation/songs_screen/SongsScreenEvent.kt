package assignment.samespace.musicplayer.feature_music.presentation.songs_screen

import androidx.media3.session.MediaController
import assignment.samespace.musicplayer.feature_music.domain.model.Song

sealed class SongsScreenEvent {
    data class SetSongsAndStart(val songs: List<Song>, val currentIndex: Int): SongsScreenEvent()
    data object GetAllSongs: SongsScreenEvent()
    data object TogglePlayback : SongsScreenEvent()
}