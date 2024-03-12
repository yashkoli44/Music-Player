package assignment.samespace.musicplayer.feature_music.presentation.songs_screen

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player.Listener
import androidx.media3.session.MediaController
import assignment.samespace.musicplayer.MediaControllerHelper
import assignment.samespace.musicplayer.feature_music.data.repository.SongRepositoryImpl
import assignment.samespace.musicplayer.feature_music.domain.use_cases.GetAllSongsUseCase
import assignment.samespace.musicplayer.feature_music.domain.use_cases.MusicUseCases
import assignment.samespace.musicplayer.feature_music.presentation.player_screen.PlayScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SongsScreenViewModel(
    private val musicUseCases: MusicUseCases,
    private val mediaControllerHelper: MediaControllerHelper,
) : ViewModel() {

    class SongsScreenViewModelFactory(private val musicUseCases: MusicUseCases, private val mediaControllerHelper: MediaControllerHelper) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SongsScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SongsScreenViewModel(musicUseCases, mediaControllerHelper) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val _songsScreenState = mutableStateOf(SongsScreenState())
    val songsScreenState: State<SongsScreenState> = _songsScreenState

    private var mediaController: MediaController? = null

    init {
        mediaControllerHelper.safeExecute {
            mediaController = it
            if (mediaController?.currentTimeline?.isEmpty == false) {
                val currentMediaItem = mediaController!!.currentMediaItem?.mediaMetadata
                val accent = currentMediaItem?.extras?.getString("ACCENT")
                _songsScreenState.value = _songsScreenState.value.copy(
                    playSongTitle = currentMediaItem?.title?.toString(),
                    playingSongImage = currentMediaItem?.artworkUri,
                    playingSongAccent = accent,
                    isPlaying = mediaController?.isPlaying ?: false
                )
            }
            mediaController?.addListener(object : Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    Log.d("Playback State (Play/Pause)", if (isPlaying) "Playing" else "Paused")
                    _songsScreenState.value = _songsScreenState.value.copy(
                        isPlaying = isPlaying
                    )
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    super.onMediaMetadataChanged(mediaMetadata)
                    _songsScreenState.value = _songsScreenState.value.copy(
                        playSongTitle = mediaMetadata.title?.toString(),
                        playingSongImage = mediaMetadata.artworkUri,
                        playingSongAccent = mediaMetadata.extras?.getString("ACCENT"),
                    )
                }
            })
        }
        onEvent(SongsScreenEvent.GetAllSongs)
    }


    fun onEvent(songsScreenEvent: SongsScreenEvent) {
        when (songsScreenEvent) {
            is SongsScreenEvent.GetAllSongs -> {
                viewModelScope.launch {
                    _songsScreenState.value = _songsScreenState.value.copy(loadingSongs = true)
                    try {
                        val songs = musicUseCases.getAllSongsUseCase()
                        _songsScreenState.value = _songsScreenState.value.copy(songs = songs, loadingSongs = false)
                    }
                    catch (e: Exception){
                        _songsScreenState.value = _songsScreenState.value.copy(isError = true, loadingSongs = false)
                    }
                }
            }
            is SongsScreenEvent.SetSongsAndStart -> {
                mediaController?.setMediaItems(
                    songsScreenEvent.songs.mapIndexed { index, song ->
                        MediaItem
                            .Builder()
                            .setUri(song.url.replace(" ", ""))
                            .setMediaId("for-you-$index")
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle(song.name)
                                    .setArtist(song.artist)
                                    .setExtras(Bundle().apply {
                                        putString("ACCENT", song.accent)
                                    })
                                    .setArtworkUri(Uri.parse("https://cms.samespace.com/assets/${song.cover}"))
                                    .build()
                            )
                            .build()
                    },
                    songsScreenEvent.currentIndex,
                    0
                )
                if (mediaController?.isPlaying == true) {
                    mediaController?.stop()
                }
                mediaController?.prepare()
                mediaController?.play()
                val currentMediaItem = mediaController?.currentMediaItem?.mediaMetadata
                val accent = currentMediaItem?.extras?.getString("ACCENT")
                _songsScreenState.value = _songsScreenState.value.copy(playSongTitle = currentMediaItem?.title?.toString(), playingSongImage = currentMediaItem?.artworkUri, playingSongAccent = accent, isPlaying = true)
            }

            is SongsScreenEvent.TogglePlayback -> {
                if(mediaController?.isPlaying == false) {
                    mediaController?.play()
                    _songsScreenState.value = _songsScreenState.value.copy(isPlaying = true)
                }
                else if (mediaController?.isPlaying == true){
                    mediaController?.pause()
                    _songsScreenState.value = _songsScreenState.value.copy(isPlaying = false)
                }

            }
        }

    }

}