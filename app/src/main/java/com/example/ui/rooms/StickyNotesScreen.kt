package com.example.ui.rooms

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HouseViewModel
import com.example.ui.RoomView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StickyNotesScreen(viewModel: HouseViewModel) {
    val state by viewModel.uiState.collectAsState()
    val church = state.selectedChurch
    val primaryColor = parseColorHex(church.brandColorHex)
    
    var noteInput by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "STICKY REFLECTIONS",
            color = primaryColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = noteInput,
            onValueChange = { noteInput = it },
            placeholder = { Text("Leave a reflection...", color = SecondaryText) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor)
        )
        
        Button(
            onClick = { 
                viewModel.submitStickyNote(noteInput)
                noteInput = ""
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("POST ANONYMOUSLY")
        }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.stickyNotes) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        Text(text = note.text, fontSize = 12.sp, overflow = TextOverflow.Ellipsis)
                        IconButton(
                            onClick = { viewModel.deleteStickyNote(note.id.toLong()) },
                            modifier = Modifier.align(Alignment.BottomEnd).size(24.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
        
        Button(
            onClick = { viewModel.navigateTo(RoomView.FOYER) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("RETURN TO FOYER")
        }
    }
}
