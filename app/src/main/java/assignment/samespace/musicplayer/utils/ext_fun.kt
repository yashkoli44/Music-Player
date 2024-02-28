package assignment.samespace.musicplayer.utils

import java.util.concurrent.TimeUnit

fun Long.durationString(): String{
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60

    return String.format("%02d:%02d", minutes, seconds)
}

fun Long.toPositiveFloat(): Float{
    return if(this < 0f) 0f else toFloat()
}

fun Long.toPositive(): Long{
    return if(this < 0) 0 else this
}

fun String?.hexStringToHexadecimal(): Long{
    return 0xFF000000 + "00${this?.substring(1) ?: "000000"}".toLong(radix = 16)
}