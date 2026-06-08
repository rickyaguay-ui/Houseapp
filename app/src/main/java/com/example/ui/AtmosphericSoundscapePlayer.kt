package com.example.ui

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AtmosphericSoundscapePlayer(enabled: Boolean) {
    DisposableEffect(enabled) {
        val audioTrack = if (enabled) {
            val sampleRate = 44100
            val numSamples = sampleRate
            val playSnd = ShortArray(numSamples)
            
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val wave60 = sin(2 * Math.PI * 60 * t)
                val wave80 = sin(2 * Math.PI * 80 * t) * 0.7
                val wave100 = sin(2 * Math.PI * 100 * t) * 0.5
                val noise = (Random.nextDouble() - 0.5) * 0.1
                
                val combined = (wave60 + wave80 + wave100 + noise) / 2.3
                playSnd[i] = (combined * Short.MAX_VALUE).toInt().toShort()
            }
            
            val track = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                numSamples * 2,
                AudioTrack.MODE_STATIC
            )
            track.write(playSnd, 0, numSamples)
            track.setLoopPoints(0, numSamples - 1, -1)
            track.play()
            track
        } else null
        
        onDispose {
            audioTrack?.apply {
                stop()
                release()
            }
        }
    }
}
