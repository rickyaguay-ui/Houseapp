package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.data.PrayerRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import android.graphics.Paint
import kotlin.random.Random


@Composable
fun CommunalPrayerWall(
    prayerRequests: List<PrayerRequest>,
    onAddPrayer: (String) -> Unit,
    primaryColor: Color
) {
    var text by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Prayer Input
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Share an anonymous prayer...", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.Gray
            )
        )
        Button(
            onClick = {
                onAddPrayer(text)
                text = ""
            },
            modifier = Modifier.padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("SUBMIT PRAYER", color = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Particle Prayer Wall
        PrayerParticleCanvas(prayerRequests)
    }
}

data class Particle(
    val id: String,
    val text: String,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float = 1.0f,
    var life: Float = 1.0f // 1.0 down to 0.0
)

@Composable
fun PrayerParticleCanvas(prayerRequests: List<PrayerRequest>) {
    val particles = remember { mutableStateListOf<Particle>() }
    
    // Manage particles based on prayerRequests
    LaunchedEffect(prayerRequests) {
        prayerRequests.forEach { prayer ->
            if (particles.none { it.id == prayer.id }) {
                particles.add(
                    Particle(
                        id = prayer.id,
                        text = prayer.text,
                        x = Random.nextFloat() * 800f, // Need actual width
                        y = 1000f, // Bottom
                        vx = (Random.nextFloat() - 0.5f) * 2f,
                        vy = -(Random.nextFloat() * 2f + 1f)
                    )
                )
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val paint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }

        particles.forEachIndexed { index, particle ->
            // Update
            particle.x += particle.vx
            particle.y += particle.vy
            particle.life -= 0.005f
            particle.alpha = particle.life
            
            // Draw
            paint.alpha = (particle.alpha * 255).toInt().coerceIn(0, 255)
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(particle.text, particle.x, particle.y, paint)
                // draw ember-like circle
                drawCircle(Color.Yellow.copy(alpha = particle.alpha), radius = 5f, center = androidx.compose.ui.geometry.Offset(particle.x, particle.y - 40f))
            }
        }
        
        // Remove dead particles
        particles.removeAll { it.life <= 0 }
    }
    
    // Animation loop
    LaunchedEffect(Unit) {
        while (isActive) {
            // Trigger recomposition for animation
            kotlinx.coroutines.delay(16)
        }
    }
}

