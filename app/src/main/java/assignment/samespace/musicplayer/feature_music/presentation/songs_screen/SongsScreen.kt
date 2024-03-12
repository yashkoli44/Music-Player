package assignment.samespace.musicplayer.feature_music.presentation.songs_screen

import android.annotation.SuppressLint
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import assignment.samespace.musicplayer.MediaControllerHelper
import assignment.samespace.musicplayer.R
import assignment.samespace.musicplayer.Route
import assignment.samespace.musicplayer.feature_music.domain.use_cases.MusicUseCases
import assignment.samespace.musicplayer.feature_music.presentation.player_screen.PlayScreenEvent
import assignment.samespace.musicplayer.feature_music.presentation.songs_screen.components.PlayerBox
import assignment.samespace.musicplayer.feature_music.presentation.songs_screen.components.SongList
import assignment.samespace.musicplayer.utils.vibrate
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SongsScreen(
    navController: NavController,
    vibrator: Vibrator,
    musicUseCases: MusicUseCases,
    mediaControllerHelper: MediaControllerHelper,
//    songsScreenViewModel: SongsScreenViewModel = hiltViewModel()
) {

    val songsScreenViewModel: SongsScreenViewModel = viewModel(
        factory = SongsScreenViewModel.SongsScreenViewModelFactory(
            musicUseCases,
            mediaControllerHelper
        )
    )

    val songsScreenState = songsScreenViewModel.songsScreenState.value

    val pagerState = rememberPagerState(pageCount = {
        2
    })

    val coroutineScope = rememberCoroutineScope()

    val interactionSource = remember { MutableInteractionSource() }


    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { }
            ) {
                PlayerBox(
                    songTitle = songsScreenState.playSongTitle,
                    songImage = songsScreenState.playingSongImage,
                    songAccent = songsScreenState.playingSongAccent,
                    isPlaying = songsScreenState.isPlaying,
                    onClick = {
                        navController.navigate(Route.PlayerScreen.name)
                    },
                    onClickButton = {
                        songsScreenViewModel.onEvent(SongsScreenEvent.TogglePlayback)
                        // haptic feedback
                        vibrate(vibrator)
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Black),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        0,
                                        animationSpec = TweenSpec()
                                    )
                                }
                            }
                            .padding(horizontal = 30.dp, vertical = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "For You",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (pagerState.currentPage == 0) Color.White else Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        if (pagerState.currentPage == 0) {
                            Icon(
                                Icons.Default.Circle,
                                contentDescription = "selected",
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        1,
                                        animationSpec = TweenSpec()
                                    )
                                }

                            }
                            .padding(horizontal = 30.dp, vertical = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Top Tracks",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (pagerState.currentPage == 1) Color.White else Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        if (pagerState.currentPage == 1) {
                            Icon(
                                Icons.Default.Circle,
                                contentDescription = "selected",
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeContent)
    ) { pv ->
        if (songsScreenState.loadingSongs) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }

        } else if (songsScreenState.isError) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Something went wrong", style = TextStyle(color = Color.Gray))
                TextButton(onClick = {
                    songsScreenViewModel.onEvent(SongsScreenEvent.GetAllSongs)
                }, content = { Text("Try Again") })
            }
        } else if (songsScreenState.songs.isNotEmpty()) {
            HorizontalPager(state = pagerState) { page ->
                if (page == 0) {

                    SongList(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = pv,
                        songs = songsScreenState.songs,
                        onItemClick = { currentIndex, song ->
                            songsScreenViewModel.onEvent(
                                SongsScreenEvent.SetSongsAndStart(
                                    songsScreenState.songs,
                                    currentIndex
                                )
                            )
                        }
                    )
                } else {
                    SongList(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = pv,
                        songs = songsScreenState.songs.filter { it.isTopTrack },
                        onItemClick = { currentIndex, song ->
                            songsScreenViewModel.onEvent(
                                SongsScreenEvent.SetSongsAndStart(
                                    songsScreenState.songs.filter { it.isTopTrack },
                                    currentIndex
                                )
                            )
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Something unexpectedly broke", style = TextStyle(color = Color.Gray))
            }
        }
    }
}







