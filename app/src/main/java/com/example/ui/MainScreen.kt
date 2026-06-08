package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.rooms.*
import com.example.ui.theme.TextColor
import com.example.ui.theme.parseColorHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: HouseViewModel) {
    val state by viewModel.uiState.collectAsState()
    val church = state.selectedChurch
    val primaryColor = parseColorHex(church.brandColorHex)
    val backgroundColor = parseColorHex(church.bgHex)

    Box(modifier = Modifier.fillMaxSize()) {
        // App Core Layout
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor,
                        titleContentColor = primaryColor
                    ),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(end = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "THE HOUSE",
                                fontSize = 20.sp,
                                letterSpacing = 3.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = church.logoEmoji,
                                fontSize = 26.sp
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleSoundscape() }) {
                            Icon(
                                imageVector = if (state.isSoundscapeEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "Toggle Audio",
                                tint = primaryColor
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.navigateTo(RoomView.STICKY) },
                    containerColor = primaryColor
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Sticky Note",
                        tint = Color.Black
                    )
                }
            },
            bottomBar = {
                RoomNavigationBar(
                    selectedRoom = state.currentRoom,
                    primaryColor = primaryColor,
                    onRoomSelected = { viewModel.navigateTo(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor)
            ) {
                Crossfade(
                    targetState = state.currentRoom,
                    animationSpec = tween(500),
                    label = "RoomCrossfade"
                ) { room ->
                    when (room) {
                        RoomView.FOYER -> FoyerScreen(viewModel)
                        RoomView.WORSHIP -> WorshipScreen(viewModel)
                        RoomView.MERCY -> MercySeatScreen(viewModel)
                        RoomView.STICKY -> StickyNotesScreen(viewModel)
                        RoomView.REFLECTION -> SOAPScreen(viewModel)
                        RoomView.WINDDOWN -> WindDownScreen(viewModel)
                        RoomView.BATTLEPLAN -> BattlePlanScreen(viewModel)
                    }
                }
            }
        }

        // Overlay Cinematic Entrance Sequence
        AnimatedVisibility(
            visible = state.cinematicActive,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(1000))
        ) {
            CinematicIntro(onFinished = { viewModel.endCinematic() })
        }
        
        // Weapons Box (Battle Plans)
        if (!state.cinematicActive) {
            var expanded by remember { mutableStateOf(false) }
            Surface(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
                    .widthIn(max = if (expanded) 240.dp else 48.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 8.dp,
                onClick = { expanded = !expanded }
            ) {
                Column(modifier = Modifier.padding(8.dp).clickable { viewModel.navigateTo(RoomView.BATTLEPLAN) }) {
                    Text("🛡️", fontSize = 24.sp)
                    if (expanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        state.battlePlans.forEach { plan ->
                            Text(plan.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(plan.verse, fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
        
        if (!state.cinematicActive) {
            state.currentScripture?.let { scripture ->
                var showScripture by remember { mutableStateOf(true) }
                if (showScripture) {
                    AlertDialog(
                        onDismissRequest = { showScripture = false },
                        title = { Text("Daily Scripture for You", color = MaterialTheme.colorScheme.primary) },
                        text = {
                            Column {
                                Text(text = scripture.verse, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = scripture.reference, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = scripture.reflection, style = MaterialTheme.typography.bodyMedium)
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showScripture = false }) { Text("Enter Sanctuary") }
                        }
                    )
                }
            }
        }
        
        AtmosphericSoundscapePlayer(state.isSoundscapeEnabled)
    }
}

@Composable
fun RoomNavigationBar(
    selectedRoom: RoomView,
    primaryColor: Color,
    onRoomSelected: (RoomView) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars) // Prevent system gestural navigation clipping
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoomView.values().forEach { room ->
                val isSelected = room == selectedRoom
                Surface(
                    onClick = { onRoomSelected(room) },
                    shape = RoundedCornerShape(99.dp),
                    color = if (isSelected) primaryColor else Color(0xFF1F1A15),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) primaryColor else Color(0xFF2E2214)
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    ) {
                        Text(
                            text = when (room) {
                                RoomView.FOYER -> "🏠 Foyer"
                                RoomView.WORSHIP -> "⛪ Worship"
                                RoomView.MERCY -> "🕊️ Mercy Seat"
                                RoomView.STICKY -> "📌 Sticky Notes"
                                RoomView.REFLECTION -> "📜 Reflection"
                                RoomView.WINDDOWN -> "🌙 Wind-Down"
                                RoomView.BATTLEPLAN -> "🛡️ Battle Plan"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.Black else TextColor
                        )
                    }
                }
            }
        }
    }
}
