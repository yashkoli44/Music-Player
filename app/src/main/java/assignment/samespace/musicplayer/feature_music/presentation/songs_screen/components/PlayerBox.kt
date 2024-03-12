package assignment.samespace.musicplayer.feature_music.presentation.songs_screen.components

import android.net.Uri
import android.os.Vibrator
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import assignment.samespace.musicplayer.R
import assignment.samespace.musicplayer.Route
import assignment.samespace.musicplayer.feature_music.presentation.songs_screen.SongsScreenEvent
import assignment.samespace.musicplayer.utils.hexStringToHexadecimal
import coil.compose.AsyncImage

@Composable
fun PlayerBox(
    songTitle: String?,
    songAccent: String?,
    songImage: Uri?,
    onClick: () -> Unit,
    onClickButton: () -> Unit,
    isPlaying: Boolean = false
) {
    val colorStops = arrayOf(
        0.05f to Color.Transparent,
        0.4f to Color(0x9D000000),
        1f to Color.Black,
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(colorStops = colorStops)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    if (songTitle != null) {
                        onClick()
                    }
                }
                .alpha(if (songTitle == null) 0f else 1f)
                .background(
                    color = Color(
                        songAccent
                            ?.substring(1)
                            .hexStringToHexadecimal()
                    )
                )
                .padding(vertical = 10.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = songImage,
                    contentDescription = "",
                    placeholder = painterResource(R.drawable.place_holder),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(50.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(songTitle ?: "unknown")
            }

            Image(
                if (isPlaying) painterResource(R.drawable.pause) else painterResource(R.drawable.play),
                contentDescription = "pause button",
                modifier = Modifier
                    .padding(5.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable {
                        onClickButton()
                    })

        }

    }
}