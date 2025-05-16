package com.example.darijadict.components

import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.launch

@Composable
fun AudioPlayer(audioUrl: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(audioUrl)))
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(
            onClick = {
                exoPlayer.play()
                coroutineScope.launch {
                    scale.animateTo(
                        targetValue = 1.2f,
                        animationSpec = tween(200)
                    )
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(800)
                    )
                }
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                modifier = Modifier.scale(scale.value)
            )
        }
    }
}
