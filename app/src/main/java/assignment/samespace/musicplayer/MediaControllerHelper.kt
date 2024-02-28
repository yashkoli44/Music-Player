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

    fun destroy(){
        mediaController?.release()
    }


}