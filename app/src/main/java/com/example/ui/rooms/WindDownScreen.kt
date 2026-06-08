package com.example.ui.rooms

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HouseViewModel
import com.example.ui.RoomView
import com.example.ui.theme.*
import kotlinx.coroutines.delay

val liturgies = listOf(
    "Psalm 4:8" to "I will both lie down in peace and sleep, for You alone, Lord, make me dwell in safety.",
    "Psalm 121:3-4" to "He will not let your foot slip—He who watches over you will not slumber. Behold, He who watches over Israel will neither slumber nor sleep.",
    "Proverbs 3:24" to "When you lie down, you will not be afraid; when you lie down, your sleep will be sweet.",
    "Matthew 11:28" to "Come to Me, all who are weary and heavy-laden, and I will give you rest.",
    "Psalm 91:4" to "He will cover you with His feathers, and under His wings you will find refuge; His faithfulness will be your shield and rampart."
)

enum class BreathState(val label: String, val durationSecs: Int, val scaleStart: Float, val scaleEnd: Float) {
    INHALE("Inhale (Divine Breath)", 4, 1.0f, 1.8f),
    HOLD("Hold (Stillness)", 7, 1.8f, 1.8f),
    EXHALE("Exhale (Release All)", 8, 1.8f, 1.0f)
}

@Composable
fun WindDownScreen(viewModel: HouseViewModel) {
    val state by viewModel.uiState.collectAsState()
    val church = state.selectedChurch
    val primaryColor = parseColorHex(church.brandColorHex)

    var isBreathingActive by remember { mutableStateOf(false) }
    var currentBreathState by remember { mutableStateOf(BreathState.INHALE) }
    var secondsRemaining by remember { mutableStateOf(4) }
    val scrollState = rememberScrollState()

    // Breathing counter mechanism
    LaunchedEffect(isBreathingActive, currentBreathState) {
        if (!isBreathingActive) return@LaunchedEffect
        
        secondsRemaining = currentBreathState.durationSecs
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining -= 1
        }
        
        // Transition state automatically
        currentBreathState = when (currentBreathState) {
            BreathState.INHALE -> BreathState.HOLD
            BreathState.HOLD -> BreathState.EXHALE
            BreathState.EXHALE -> BreathState.INHALE
        }
    }

    // Breathing ring animation linked to active timing state
    val breatheTransitionSpec = remember(currentBreathState) {
        val durationMs = currentBreathState.durationSecs * 1000
        tween<Float>(
            durationMillis = durationMs,
            easing = if (currentBreathState == BreathState.HOLD) LinearEasing else EaseInOutQuad
        )
    }

    val breatheScale by animateFloatAsState(
        targetValue = if (isBreathingActive) currentBreathState.scaleEnd else 1.0f,
        animationSpec = if (isBreathingActive) breatheTransitionSpec else tween(1000),
        label = "BreathingScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "🌙",
            fontSize = 44.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "WIND-DOWN SANCTUARY",
            color = primaryColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )

        // Interactive Breathing Circle Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, primaryColor, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                data class GuidedFocus(
                    val name: String,
                    val labelEmoji: String,
                    val inhaleCue: String,
                    val holdCue: String,
                    val exhaleCue: String
                )

                val guidedFocuses = remember {
                    listOf(
                        GuidedFocus("Silencing Anxiety", "🛡️", "Inhale Father's absolute safekeeping", "Rest in the stillness of His promise", "Let go of all heavy future thoughts"),
                        GuidedFocus("Calming Storms", "⛵", "Inhale His deep, sovereign peace", "Praise Him in the center of winds", "Cast away every turbulent wave"),
                        GuidedFocus("Covenant Grace", "🕊️", "Draw in the assurance of pure grace", "Sit with Christ in perfect security", "Release the weight of self-striving"),
                        GuidedFocus("Sleep in Psalm 23", "🐑", "Breathe in His goodness and mercy", "Lie down in green pastures of rest", "Fear no shadow, He is with you")
                    )
                }
                var selectedFocusIdx by remember { mutableIntStateOf(0) }
                val activeFocus = guidedFocuses[selectedFocusIdx]

                Text(
                    text = "Holy Sleep Breath (4-7-8)",
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "A guided breathing exercise to calm your autonomic nervous system and align your spirit in silent contemplation.",
                    color = SecondaryText,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Focus Chooser (Chips)
                Text(
                    text = "CHOOSE GUIDED HOLY FOCUS:",
                    color = SecondaryText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    guidedFocuses.forEachIndexed { idx, focus ->
                        val isSelected = selectedFocusIdx == idx
                        FilterChip(
                            selected = isSelected,
                            onClick = { 
                                selectedFocusIdx = idx
                                // Reset breathing state upon selection change
                                isBreathingActive = false
                                currentBreathState = BreathState.INHALE
                            },
                            label = { Text("${focus.labelEmoji} ${focus.name}", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryColor.copy(alpha = 0.15f),
                                selectedLabelColor = primaryColor
                            )
                        )
                    }
                }

                // The glowing breathing ring representation
                Box(
                    modifier = Modifier
                        .size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer pulsating canvas bloom ring
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(breatheScale)
                    ) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = if (isBreathingActive) 0.35f else 0.15f),
                                    Color.Transparent
                                )
                            ),
                            radius = size.minDimension / 2f
                        )
                        drawCircle(
                            color = primaryColor.copy(alpha = if (isBreathingActive) 0.8f else 0.4f),
                            radius = (size.minDimension / 2.5f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    // Inner timer readout
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isBreathingActive) {
                            Text(
                                text = secondsRemaining.toString(),
                                color = primaryColor,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Seconds",
                                color = SecondaryText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action instruction text and Abide state-synced cues
                val currentCue = when (currentBreathState) {
                    BreathState.INHALE -> activeFocus.inhaleCue
                    BreathState.HOLD -> activeFocus.holdCue
                    BreathState.EXHALE -> activeFocus.exhaleCue
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = if (isBreathingActive) currentBreathState.label.uppercase() else "PRACTICE QUIETUDE",
                        color = primaryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBreathingActive) "“$currentCue”" else "Select a Focus above and tap Synchronize to begin.",
                        color = TextColor,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }

                // Play / Pause breathing controller
                Button(
                    onClick = {
                        isBreathingActive = !isBreathingActive
                        if (!isBreathingActive) {
                            currentBreathState = BreathState.INHALE
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isBreathingActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isBreathingActive) "Pause" else "Start",
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = if (isBreathingActive) "Pause Breathing Pace" else "Synchronize Breath",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Tying Sticky Notes Altar/Ministry in Wind Down screen
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "🤝 SURRENDER PINNED PIECES BEFORE REST",
                color = primaryColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (state.burdens.isEmpty()) {
            Text(
                text = "Your heart and Sticky Notes board are clear. Rest in His sound.",
                color = SecondaryText,
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                state.burdens.forEach { burden ->
                    val cardBg = when (burden.analyzedState) {
                        "REST" -> RestBlue
                        "PEACE" -> PeaceTeal
                        "FORGIVENESS" -> ForgivenessCrimson
                        "JOY" -> JoyAmber
                        else -> MaterialTheme.colorScheme.surface
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .width(200.dp)
                            .height(105.dp)
                            .border(0.5.dp, primaryColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "“${burden.text}”",
                                color = TextColor,
                                fontSize = 11.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 14.sp
                            )
                            Text(
                                text = "Surrendered to the Father",
                                color = primaryColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bedtime liturgies
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(imageVector = Icons.Default.Bedtime, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Hatch Alarm (Wake Time: ${state.alarmTime})",
                color = primaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("06:00 AM", "07:00 AM", "08:00 AM").forEach { time ->
                FilterChip(
                    selected = state.alarmTime == time,
                    onClick = { viewModel.setAlarmTime(time) },
                    label = { Text(time, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = primaryColor.copy(alpha = 0.15f),
                        selectedLabelColor = primaryColor
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            liturgies.forEach { (ref, text) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, Color(0xFF2A1F12), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "“$text”",
                            color = TextColor,
                            fontFamily = FontFamily.Serif,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = ref,
                            color = primaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🚶 WALKWAY PORTAL (TRAVERSE CHURCH WINGS):",
                color = primaryColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Go back to the Foyer
                OutlinedButton(
                    onClick = { viewModel.navigateTo(RoomView.FOYER) },
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2214)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("🏠 Return to Foyer", fontSize = 10.sp)
                }

                // Go to Worship
                OutlinedButton(
                    onClick = { viewModel.navigateTo(RoomView.WORSHIP) },
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2214)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("⛪ Worship Center", fontSize = 10.sp)
                }
                
                // Go to Mercy Seat
                OutlinedButton(
                    onClick = { viewModel.navigateTo(RoomView.MERCY) },
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2214)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("🕊️ Mercy Seat", fontSize = 10.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
