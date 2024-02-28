package assignment.samespace.musicplayer.feature_music.presentation.player_screen

sealed class PlayScreenEvent {
    data object InitializeMedia: PlayScreenEvent()
    data object StartTimer: PlayScreenEvent()
    data object TogglePlayback : PlayScreenEvent()
    data object NextSong: PlayScreenEvent()
    data object PreviousSong: PlayScreenEvent()
    data class PlayMediaIndex(val index: Int): PlayScreenEvent()
    data object SeekStop: PlayScreenEvent()
    data class SeekTo(val milli: Long): PlayScreenEvent()
}