package assignment.samespace.musicplayer.feature_music.presentation.player_screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import assignment.samespace.musicplayer.MediaControllerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
class PlayScreenViewModel /*@Inject*/ constructor(
    mediaControllerHelper: MediaControllerHelper
) : ViewModel() {

    class PlayScreenViewModelFactory(private val mediaControllerHelper: MediaControllerHelper) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlayScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlayScreenViewModel(mediaControllerHelper) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


    private var _playScreenState = mutableStateOf(PlayScreenState())
    val playScreenState: State<PlayScreenState> = _playScreenState

    private var _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val mediaController: MediaController

    init {
        mediaController = mediaControllerHelper.getMediaController()
        var i = 0
        val mediaItems = arrayListOf<MediaItem>()
        while (i < mediaController.mediaItemCount) {
            mediaItems.add(mediaController.getMediaItemAt(i))
            i++
        }
        _playScreenState.value = _playScreenState.value.copy(
            mediaItems = mediaItems,
            currentIndex = mediaController.currentMediaItemIndex
        )
    }

    fun onEvent(playScreenEvent: PlayScreenEvent) {
        when (playScreenEvent) {
            is PlayScreenEvent.InitializeMedia -> {
                val startAccent = mediaController.mediaMetadata.extras?.getString("ACCENT")
                _playScreenState.value = _playScreenState.value.copy(
                    currentSongTitle = mediaController.mediaMetadata.title.toString(),
                    currentSongArtist = mediaController.mediaMetadata.artist.toString(),
                    currentSongAccent = startAccent,
                    currentDuration = mediaController.currentPosition,
                    contentDuration = mediaController.contentDuration,
                    hasNext = mediaController.hasNextMediaItem(),
                    hasPrevious = mediaController.hasPreviousMediaItem(),
                    isPlaying = mediaController.isPlaying
                )
                mediaController.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        Log.d("Playback State (Play/Pause)", if (isPlaying) "Playing" else "Paused")
                        _playScreenState.value = _playScreenState.value.copy(isPlaying = isPlaying)
                    }

                    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                        super.onTimelineChanged(timeline, reason)
                        var i = 0
                        val songs = arrayListOf<MediaItem>()
                        while (i < mediaController.mediaItemCount) {
                            songs.add(mediaController.getMediaItemAt(i))
                            i++
                        }
                        _playScreenState.value = _playScreenState.value.copy(
                            mediaItems = songs,
                        )

                    }

                    override fun onIsLoadingChanged(isLoading: Boolean) {
                        super.onIsLoadingChanged(isLoading)
                        if (!isLoading){
                            _playScreenState.value = _playScreenState.value.copy(
                                contentDuration = mediaController.contentDuration,
                            )
                        }
                    }

                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        super.onMediaMetadataChanged(mediaMetadata)
                        val accent = mediaController.mediaMetadata.extras?.getString("ACCENT")
                        _playScreenState.value = _playScreenState.value.copy(
                            currentSongTitle = mediaController.mediaMetadata.title.toString(),
                            currentSongArtist = mediaController.mediaMetadata.artist.toString(),
                            currentSongAccent = accent,
                            currentDuration = mediaController.currentPosition,
                            contentDuration = mediaController.contentDuration,
                            hasNext = mediaController.hasNextMediaItem(),
                            hasPrevious = mediaController.hasPreviousMediaItem(),
                            isPlaying = mediaController.isPlaying
                        )
                        viewModelScope.launch {
                            _eventFlow.emit(UiEvent.ScrollToPage(mediaController.currentMediaItemIndex))
                        }

                    }

                })

            }
            is PlayScreenEvent.StartTimer -> {
                viewModelScope.launch {
                    while (true){
                        delay(100)
                        _playScreenState.value = _playScreenState.value.copy(currentDuration = mediaController.currentPosition)
                    }
                }
            }
            is PlayScreenEvent.TogglePlayback -> {
                if (!mediaController.isPlaying) {
                    mediaController.play()
                    _playScreenState.value = _playScreenState.value.copy(isPlaying = true)
                } else {
                    mediaController.pause()
                    _playScreenState.value = _playScreenState.value.copy(isPlaying = false)
                }
            }
            is PlayScreenEvent.NextSong -> {
                mediaController.seekToNextMediaItem()
            }
            is PlayScreenEvent.PlayMediaIndex -> {
                if (mediaController.currentMediaItemIndex != playScreenEvent.index) {
                    mediaController.seekTo(playScreenEvent.index, 0)
                }
            }
            is PlayScreenEvent.PreviousSong -> {
                mediaController.seekToPreviousMediaItem()
            }
            is PlayScreenEvent.SeekStop -> {
                mediaController.seekTo(_playScreenState.value.tempPosition ?: 0)
                _playScreenState.value = _playScreenState.value.copy(tempPosition = null)
            }
            is PlayScreenEvent.SeekTo -> {
                _playScreenState.value = _playScreenState.value.copy(tempPosition = playScreenEvent.milli)
            }
        }
    }


    sealed class UiEvent {
        data class ScrollToPage(val index: Int) : UiEvent()
    }

}