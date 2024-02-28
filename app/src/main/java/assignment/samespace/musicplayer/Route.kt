package assignment.samespace.musicplayer

sealed class Route(val name: String) {

    data object SongsScreen: Route("songs_screen")

    data object PlayerScreen: Route("player_screen")

}