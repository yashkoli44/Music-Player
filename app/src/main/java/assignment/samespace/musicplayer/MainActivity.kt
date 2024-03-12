package assignment.samespace.musicplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import assignment.samespace.musicplayer.feature_music.domain.use_cases.MusicUseCases
import assignment.samespace.musicplayer.feature_music.presentation.songs_screen.SongsScreen
import assignment.samespace.musicplayer.feature_music.presentation.player_screen.PlayerScreen
import assignment.samespace.musicplayer.ui.theme.MusicPlayerTheme
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    @Inject
    lateinit var mediaControllerHelper: MediaControllerHelper

    @Inject
    lateinit var musicUseCases: MusicUseCases

    @Inject
    lateinit var vibrator: Vibrator


    private fun createMusicPlayerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Music Player"
            val descriptionText = "Helps music player run in background"

            val importance = NotificationManager.IMPORTANCE_LOW

            val channel = NotificationChannel("player_service_notification", name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStart() {
        super.onStart()
        mediaControllerHelper = MediaControllerHelper(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createMusicPlayerNotificationChannel()


        setContent {
            MusicPlayerTheme {

                val navController = rememberNavController()

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController, startDestination = Route.SongsScreen.name,
                        enterTransition = {
                            EnterTransition.None
                        },
                        exitTransition = {
                            ExitTransition.None
                        }) {
                        composable(Route.SongsScreen.name) {
                            SongsScreen(navController, vibrator, musicUseCases, mediaControllerHelper)
                        }
                        composable(Route.PlayerScreen.name,
                            enterTransition = {
                                fadeIn(
                                    animationSpec = tween(
                                        300, easing = LinearEasing
                                    )
                                ) + slideIntoContainer(
                                    animationSpec = tween(300, easing = EaseIn),
                                    towards = AnimatedContentTransitionScope.SlideDirection.Up
                                )
                            },
                            exitTransition = {
                                fadeOut(
                                    animationSpec = tween(
                                        300, easing = LinearEasing
                                    )
                                ) + slideOutOfContainer(
                                    animationSpec = tween(300, easing = EaseOut),
                                    towards = AnimatedContentTransitionScope.SlideDirection.Down
                                )
                            }) {
                            PlayerScreen(navController, vibrator, mediaControllerHelper)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaControllerHelper.destroy()
    }

}