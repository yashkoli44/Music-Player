package assignment.samespace.musicplayer.feature_music.domain.model

import com.google.gson.annotations.SerializedName

data class Song(
    val id: Int,
    val status: String,
    @SerializedName("user_created")
    val userCreated: String,
    @SerializedName("date_created")
    val dateCreated: String,
    @SerializedName("user_updated")
    val userUpdated: String,
    @SerializedName("date_updated")
    val dateUpdated: String,
    val name: String,
    val artist: String,
    val accent: String,
    val cover: String,
    @SerializedName("top_track")
    val isTopTrack: Boolean,
    val url: String
)