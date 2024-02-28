package assignment.samespace.musicplayer

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class MediaControllerHelper(context: Context) {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>

    val isPlaying: Boolean
        get() {
            return mediaController?.isPlaying ?: false || mediaController?.isLoading ?: false
        }

    init {
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            mediaController = controllerFuture.get()

        }, MoreExecutors.directExecutor())
    }

    private fun isMediaControllerPresent(): Boolean{
        return mediaController != null
    }


    fun safeExecute(block: (MediaController) -> Unit){
        if(isMediaControllerPresent()) {
            block(mediaController!!)
        }
        else{
            controllerFuture.addListener({
                mediaController = controllerFuture.get()
                block(mediaController!!)
            }, MoreExecutors.directExecutor())
        }
    }


    /* Use this method only when you are certain that the media controller will be initialized at this point */
    fun getMediaController(): MediaController{
        return mediaController!!
    }

    fun play(){
        if(isMediaControllerPresent()) {
            mediaController!!.play()
        }
        else{
            controllerFuture.addListener({
                mediaController = controllerFuture.get()
                mediaController!!.play()
            }, MoreExecutors.directExecutor())
        }
    }

    fun pause(){
        if(isMediaControllerPresent()) {
            mediaController!!.pause()
        }
        else{
            controllerFuture.addListener({
                mediaController = controllerFuture.get()
                mediaController!!.pause()
            }, MoreExecutors.directExecutor())
        }
    }

    fun stop(){
        if(isMediaControllerPresent()) {
            mediaController!!.stop()
        }
        else{
            controllerFuture.addListener({
                mediaController = controllerFuture.get()
                mediaController!!.stop()
            }, MoreExecutors.directExecutor())
        }
    }

    fun prepare(){
        if(isMediaControllerPresent()) {
            mediaController!!.prepare()
        }
        else{
            controllerFuture.addListener({
                mediaController = controllerFuture.get()
                mediaController!!.prepare()
            }, MoreExecutors.directExecutor())
        }
    }

    fun release(){
        if(isMediaControllerPresent()) {
            mediaController!!.prepare()
        }
        else{
            controllerFuture.addListener({
                mediaController = controllerFuture.get()
                mediaController!!.prepare()
            }, MoreExecutors.directExecutor())
        }
    }

    fun destroy(){
        mediaController?.release()
    }


}