package assignment.samespace.musicplayer.utils

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

fun vibrate(vibrator: Vibrator){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(40, 50))
    } else {
        vibrator.vibrate(40)
    }
}