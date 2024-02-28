package assignment.samespace.musicplayer.feature_music.presentation.songs_screen

import android.net.Uri
import assignment.samespace.musicplayer.feature_music.domain.model.Song

data class SongsScreenState(
    val loadingSongs: Boolean = true,
    val isError: Boolean = false,
    val songs: List<Song> = emptyList(),
    val playSongTitle: String? = null,
    val playingSongImage: Uri? = null,
    val playingSongAccent: String? = null,
    val isPlaying: Boolean = false,
)