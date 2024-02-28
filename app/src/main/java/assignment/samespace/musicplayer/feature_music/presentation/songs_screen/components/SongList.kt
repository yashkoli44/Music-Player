package assignment.samespace.musicplayer.feature_music.presentation.songs_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import assignment.samespace.musicplayer.R
import assignment.samespace.musicplayer.feature_music.domain.model.Song
import coil.compose.AsyncImage

@Composable
fun SongList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    songs: List<Song>,
    onItemClick: (Int, Song) -> Unit = {index, song -> }
) {

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(songs.size) {
            val song = songs[it]
            Row(
                modifier = Modifier.run { fillMaxWidth().clickable {
                    onItemClick(it, songs[it])
                }.padding(10.dp) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    "https://cms.samespace.com/assets/${song.cover}",
                    contentDescription = "${song.name}'s album image",
                    placeholder = painterResource(R.drawable.place_holder),
                    modifier = Modifier.size(50.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(song.name)
                    Text(song.artist, style = TextStyle(color = Color.Gray, fontSize = 12.sp))
                }

            }
        }
    }
}