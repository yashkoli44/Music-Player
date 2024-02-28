package assignment.samespace.musicplayer.feature_music.domain.model

import com.google.gson.annotations.SerializedName

data class SongData(@SerializedName("data") val songs: List<Song>)
