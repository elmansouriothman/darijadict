package com.example.darijadict.util

import android.content.Context
import android.media.MediaPlayer

fun playAssetAudio(context: Context, audioTag: String) {
    // ✅ Clean: remove [sound: ] from audio filename
    val fileName = audioTag.removePrefix("[sound:").removeSuffix("]")

    val assetPath = "sounds/$fileName"

    try {
        val assetFileDescriptor = context.assets.openFd(assetPath)
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )
        mediaPlayer.prepare()
        mediaPlayer.start()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
