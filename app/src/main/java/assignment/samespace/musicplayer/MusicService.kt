package assignment.samespace.musicplayer

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture

class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null


//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        when(intent?.action){
//            "PLAY_ACTION" -> {
//                Log.d("PLAY", "Play")
//                if(mediaSession?.player?.isPlaying == true) {
//                    mediaSession?.player?.pause()
//
//                }
//                else{
//                    mediaSession?.player?.play()
//                }
//            }
//            "STOP_ACTION" -> {
//                Log.d("PLAY", "Play")
//                mediaSession?.player?.stop()
//            }
//        }
//        return super.onStartCommand(intent, flags, startId)
//    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

//        setMediaNotificationProvider(object: MediaNotification.Provider{
//            override fun createNotification(
//                mediaSession: MediaSession,
//                customLayout: ImmutableList<CommandButton>,
//                actionFactory: MediaNotification.ActionFactory,
//                onNotificationChangedCallback: MediaNotification.Provider.Callback
//            ): MediaNotification {
//
//
//                val playIntent = Intent(this@MusicService, MusicService::class.java).apply {
//                    action = "PLAY_ACTION"
//                }
//                val playPendingIntent: PendingIntent = PendingIntent.getService(this@MusicService, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//                return MediaNotification(1, NotificationCompat.Builder(this@MusicService, "player_service_notification")
//                    // Show controls on lock screen even when user hides sensitive content.
//                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                    .setSmallIcon(R.drawable.baseline_music_note_24)
//                    // Add media control buttons that invoke intents in your media service
//                    .addAction(R.drawable.previous, "Previous", playPendingIntent) // #0
//                    .addAction(R.drawable.play_notification, "Pause", playPendingIntent) // #1
//                    .addAction(R.drawable.next, "Next", playPendingIntent) // #2
//                    // Apply the media style template
//                    .setStyle(
//                        MediaStyleNotificationHelper.MediaStyle(mediaSession)
//                            .setShowActionsInCompactView(0, 1, 2))
//                    .setContentTitle(mediaSession.player.mediaMetadata.title)
//                    .setContentText(mediaSession.player.mediaMetadata.artist)
//                    .build())
//            }
//
//            override fun handleCustomCommand(session: MediaSession, action: String, extras: Bundle): Boolean {
//                return true
//            }
//
//        })

        val player = ExoPlayer.Builder(this)
            .build()

        player.addListener(object: Player.Listener{
            override fun onPlayerError(error: PlaybackException) {
                player.prepare()
                player.seekToNext()
                Log.d("PlaybackException", error.message ?: "")

            }


        })
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 9, intent,  PendingIntent.FLAG_IMMUTABLE)
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(pendingIntent)
            .build()

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }


}