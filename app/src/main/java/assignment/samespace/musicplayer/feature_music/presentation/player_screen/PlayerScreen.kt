package assignment.samespace.musicplayer.feature_music.presentation.player_screen

import android.annotation.SuppressLint
import android.os.Vibrator
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import assignment.samespace.musicplayer.MediaControllerHelper
import assignment.samespace.musicplayer.R
import assignment.samespace.musicplayer.utils.durationString
import assignment.samespace.musicplayer.utils.hexStringToHexadecimal
import assignment.samespace.musicplayer.utils.toPositive
import assignment.samespace.musicplayer.utils.toPositiveFloat
import assignment.samespace.musicplayer.utils.vibrate
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlayerScreen(
    navController: NavController,
    vibrator: Vibrator,
    mediaControllerHelper: MediaControllerHelper
    /*playScreenViewModel: PlayScreenViewModel = hiltViewModel()*/
) {

    val playScreenViewModel: PlayScreenViewModel = viewModel(
        factory = PlayScreenViewModel.PlayScreenViewModelFactory(mediaControllerHelper)
    )

    val playScreenState = playScreenViewModel.playScreenState.value

    val pagerState = rememberPagerState(
        initialPage = playScreenState.currentIndex
    ) { playScreenState.mediaItems.size }

    LaunchedEffect(Unit) {
        playScreenViewModel.onEvent(PlayScreenEvent.InitializeMedia)
        playScreenViewModel.onEvent(PlayScreenEvent.StartTimer)
        playScreenViewModel.eventFlow.collectLatest {
            when (it) {
                is PlayScreenViewModel.UiEvent.ScrollToPage -> {
                    pagerState.animateScrollToPage(it.index)
                }
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            playScreenViewModel.onEvent(PlayScreenEvent.PlayMediaIndex(page))
        }
    }

    var offsetY by remember { mutableStateOf(0.dp) }


    Scaffold {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(
                                playScreenState.currentSongAccent
                                    ?.substring(1)
                                    .hexStringToHexadecimal()
                            ), Color.Black
                        )
                    )
                )
                .windowInsetsPadding(WindowInsets.safeContent),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(5.dp)
                    .offset(y = offsetY)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x45FFFFFF))
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragCancel = {
                                offsetY = 0.dp
                            },
                            onDragEnd = {
                                offsetY = 0.dp
                            }
                        ) { change, _ ->
                            if (offsetY > 50.dp) {
                                vibrate(vibrator)
                                navController.navigateUp()
                                change.consume()
                            } else if (offsetY < 50.dp) {
                                offsetY += change.position.y.toDp()
                            }
                        }
                    }
            )
            Spacer(modifier = Modifier.height(40.dp))
            HorizontalPager(
                state = pagerState,
                beyondBoundsPageCount = 2,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(.3f),
                contentPadding = PaddingValues(horizontal = 40.dp),
                pageSpacing = 20.dp
            ) { page ->
                AsyncImage(
                    model = playScreenState.mediaItems[page].mediaMetadata.artworkUri,
                    contentDescription = "${playScreenState.mediaItems[page].mediaMetadata}'s album image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.place_holder)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                playScreenState.currentSongTitle ?: "Loading..",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium)
            )
            Text(
                playScreenState.currentSongArtist ?: "Loading..",
                style = TextStyle(color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Slider(
                    value = playScreenState.tempPosition?.toPositiveFloat()
                        ?: playScreenState.currentDuration?.toPositiveFloat() ?: 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()

                                    if (event.type == PointerEventType.Release) {
                                        playScreenViewModel.onEvent(PlayScreenEvent.SeekStop)
                                    }
                                }
                            }
                        },
                    valueRange = 0f.rangeTo(
                        playScreenState.contentDuration?.toPositiveFloat() ?: 0f
                    ),
                    onValueChange = {
                        playScreenViewModel.onEvent(PlayScreenEvent.SeekTo(it.toLong()))
                    },
                    thumb = {
                        if (playScreenState.tempPosition != null) {
                            SliderDefaults.Thumb(
                                interactionSource = remember { MutableInteractionSource() },
                                colors = SliderDefaults.colors(),
                                enabled = true
                            )
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        playScreenState.currentDuration?.toPositive()?.durationString() ?: "00:00",
                        style = TextStyle(fontSize = 12.sp)
                    )
                    Text(
                        playScreenState.contentDuration?.toPositive()?.durationString() ?: "00:00",
                        style = TextStyle(fontSize = 12.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(painter = painterResource(R.drawable.previous),
                    contentDescription = "play",
                    modifier = Modifier
                        .size(30.dp)
                        .alpha(if (playScreenState.hasPrevious) 1f else 0f)
                        .clickable {
                            if (playScreenState.hasPrevious) {
                                playScreenViewModel.onEvent(PlayScreenEvent.PreviousSong)
                            }

                        })
                Image(
                    if (playScreenState.isPlaying) painterResource(R.drawable.pause) else painterResource(
                        R.drawable.play
                    ),
                    contentDescription = "pause button",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable {
                            playScreenViewModel.onEvent(PlayScreenEvent.TogglePlayback)

                            // haptic feedback
                            vibrate(vibrator)
                        })
                Image(painter = painterResource(R.drawable.next),
                    contentDescription = "play",
                    modifier = Modifier
                        .size(30.dp)
                        .alpha(if (playScreenState.hasNext) 1f else 0f)
                        .clickable {
                            if (playScreenState.hasNext) {
                                playScreenViewModel.onEvent(PlayScreenEvent.NextSong)
                            }
                        })

            }
            Spacer(modifier = Modifier.height(50.dp))
        }

    }
}