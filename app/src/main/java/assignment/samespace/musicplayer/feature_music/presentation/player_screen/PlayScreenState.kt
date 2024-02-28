package assignment.samespace.musicplayer.feature_music.presentation.player_screen

import androidx.media3.common.MediaItem

data class PlayScreenState(
    val mediaItems: List<MediaItem> = emptyList(),
    val currentIndex: Int = 0,
    val currentSongTitle: String? = null,
    val currentSongArtist: String? = null,
    val currentSongAccent: String? = null,
    val currentDuration: Long? = null,
    val contentDuration: Long? = null,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
    val isPlaying: Boolean = false,
    val tempPosition: Long? = null,
)