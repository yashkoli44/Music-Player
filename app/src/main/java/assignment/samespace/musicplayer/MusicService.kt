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
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture

class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        setMediaNotificationProvider(DefaultMediaNotificationProvider.Builder(this).setChannelId("player_service_notification").build().apply { setSmallIcon(R.drawable.baseline_music_note_24) })

        val player = ExoPlayer.Builder(this)
            .build()

        player.addListener(object: Player.Listener{
            override fun onPlayerError(error: PlaybackException) {
                player.prepare()
                player.seekToNext()
                Log.d("PlaybackException", error.message ?: "")

            }


        })

        mediaSession = MediaSession.Builder(this, player)
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