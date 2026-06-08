package com.example.ui.rooms

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.HouseViewModel
import com.example.ui.RoomView
import java.text.SimpleDateFormat
import java.util.*

val soapScriptureSuggestions = listOf(
    "Psalm 23:1 - The Lord is my shepherd, I shall not want.",
    "Matthew 11:28 - Come to Me, all who are weary and heavy-laden, and I will give you rest.",
    "Philippians 4:6 - Do not be anxious about anything, but through prayer let your requests be known to God.",
    "Romans 8:1 - There is now no condemnation for those who are in Christ Jesus.",
    "Psalm 46:10 - Be still, and know that I am God."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOAPScreen(viewModel: HouseViewModel) {
    val state by viewModel.uiState.collectAsState()
    
    // SOAP form inputs
    var step by remember { mutableStateOf(1) } // 4 steps: 1=Scripture, 2=Observation, 3=Application, 4=Prayer
    var scriptureText by remember { mutableStateOf("") }
    var observationText by remember { mutableStateOf("") }
    var applicationText by remember { mutableStateOf("") }
    var prayerText by remember { mutableStateOf("") }

    var expandedSoapId by remember { mutableStateOf<Long?>(null) }
    val entryFormScrollState = rememberScrollState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(
                text = "📜",
                fontSize = 44.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "REFLECTION CHAPEL",
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 3.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        }

        // SOAP Input Wizard Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Gold, RoundedCornerShape(16.dp))
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                ) {
                    Text(
                        text = "S.O.A.P. Journal",
                        color = Gold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Scripture - Observation - Application - Prayer",
                        color = SecondaryText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Step indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 1..4) {
                            val active = i <= step
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .background(if (active) Gold else Color(0xFF2A1F12))
                            )
                        }
                    }

                    // Interactive Step Fields
                    AnimatedContent(
                        targetState = step,
                        label = "SoapStepTransition"
                    ) { currentStep ->
                        when (currentStep) {
                            1 -> {
                                Column {
                                    Text(
                                        text = "1. SCRIPTURE",
                                        color = Gold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Write down or paste a passage that spoke to your spirit today:",
                                        color = TextColor,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                                    )

                                    TextField(
                                        value = scriptureText,
                                        onValueChange = { scriptureText = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Black,
                                            unfocusedContainerColor = Color.Black,
                                            focusedTextColor = TextColor,
                                            unfocusedTextColor = TextColor,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        placeholder = { Text("E.g., Psalm 23:1...", color = SecondaryText, fontSize = 12.sp) }
                                    )

                                    // Quick suggestions
                                    Text(
                                        text = "Tap a scripture to load suggestion:",
                                        color = SecondaryText,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        soapScriptureSuggestions.forEach { suggestion ->
                                            Card(
                                                onClick = { scriptureText = suggestion },
                                                colors = CardDefaults.cardColors(containerColor = Color.Black),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.fillMaxWidth().border(0.5.dp, Color(0xFF1E140B), RoundedCornerShape(6.dp))
                                            ) {
                                                Text(
                                                    text = suggestion,
                                                    color = TextColor.copy(alpha = 0.8f),
                                                    fontSize = 11.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            2 -> {
                                Column {
                                    Text(
                                        text = "2. OBSERVATION",
                                        color = Gold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "What is the Lord saying? What stands out in this text? Write your immediate thoughts:",
                                        color = TextColor,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                                    )

                                    TextField(
                                        value = observationText,
                                        onValueChange = { observationText = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Black,
                                            unfocusedContainerColor = Color.Black,
                                            focusedTextColor = TextColor,
                                            unfocusedTextColor = TextColor,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        placeholder = { Text("What did you see? Or notice in the context?", color = SecondaryText, fontSize = 12.sp) }
                                    )
                                }
                            }
                            3 -> {
                                Column {
                                    Text(
                                        text = "3. APPLICATION",
                                        color = Gold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "How does this apply to your life today? What is the practical step?",
                                        color = TextColor,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                                    )

                                    TextField(
                                        value = applicationText,
                                        onValueChange = { applicationText = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Black,
                                            unfocusedContainerColor = Color.Black,
                                            focusedTextColor = TextColor,
                                            unfocusedTextColor = TextColor,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        placeholder = { Text("How can you walk in this truth today?", color = SecondaryText, fontSize = 12.sp) }
                                    )
                                }
                            }
                            else -> {
                                Column {
                                    Text(
                                        text = "4. PRAYER",
                                        color = Gold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Write your raw, honest, and heartfelt prayer of surrender or thanksgiving:",
                                        color = TextColor,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                                    )

                                    TextField(
                                        value = prayerText,
                                        onValueChange = { prayerText = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Black,
                                            unfocusedContainerColor = Color.Black,
                                            focusedTextColor = TextColor,
                                            unfocusedTextColor = TextColor,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        placeholder = { Text("Lord, guide my feet, fill my heart, help me carry...", color = SecondaryText, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (step > 1) {
                            OutlinedButton(
                                onClick = { step -= 1 },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Gold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("PREV", fontSize = 11.sp)
                            }
                        } else {
                            Box(modifier = Modifier.size(1.dp))
                        }

                        Button(
                            onClick = {
                                if (step < 4) {
                                    step += 1
                                } else {
                                    // Save SOAP entries to database
                                    if (scriptureText.isNotBlank()) {
                                        viewModel.submitSoap(
                                            scripture = scriptureText,
                                            observation = observationText,
                                            application = applicationText,
                                            prayer = prayerText
                                        )
                                        // Reset
                                        scriptureText = ""
                                        observationText = ""
                                        applicationText = ""
                                        prayerText = ""
                                        step = 1
                                    }
                                }
                            },
                            enabled = when (step) {
                                1 -> scriptureText.isNotBlank()
                                2 -> observationText.isNotBlank()
                                3 -> applicationText.isNotBlank()
                                else -> prayerText.isNotBlank()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Gold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            val text = if (step < 4) "NEXT ➔" else "SAVE JOURNAL"
                            Text(
                                text = text,
                                fontSize = 11.sp,
                                color = CardColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Historic List of reflections
        item {
            Text(
                text = "Past Journeys",
                color = Gold,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }

        if (state.reflections.isEmpty()) {
            item {
                Text(
                    text = "No recorded journeys yet. Lay the foundation of your first S.O.A.P. entry above.",
                    color = SecondaryText,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(state.reflections) { reflection ->
                val isExpanded = reflection.id == expandedSoapId
                Card(
                    onClick = { expandedSoapId = if (isExpanded) null else reflection.id },
                    colors = CardDefaults.cardColors(containerColor = CardColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .border(0.5.dp, Color(0xFF2A1F12), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reflection.scripture,
                                    color = Gold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val dateStr = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(reflection.timestamp))
                                Text(
                                    text = dateStr,
                                    color = SecondaryText,
                                    fontSize = 11.sp
                                )
                            }
                            IconButton(onClick = { expandedSoapId = if (isExpanded) null else reflection.id }) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                    tint = Gold
                                )
                            }
                        }

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            // Observation block
                            Text(text = "OBSERVATION", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Text(text = reflection.observation, color = TextColor, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 2.dp, bottom = 12.dp))

                            // Application block
                            Text(text = "APPLICATION", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Text(text = reflection.application, color = TextColor, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 2.dp, bottom = 12.dp))

                            // Prayer block
                            Text(text = "PRAYER", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Text(text = reflection.prayer, color = TextColor, fontSize = 13.sp, fontFamily = FontFamily.Serif, lineHeight = 18.sp, modifier = Modifier.padding(top = 2.dp, bottom = 16.dp))

                            // Permanent deletion check
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { viewModel.deleteSoap(reflection.id) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                                ) {
                                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "ERASE REFLECTION", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        // Walking portals
        item {
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🚶 WALKWAY PORTAL (TRAVERSE CHURCH WINGS):",
                    color = Gold,
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
        }
    }
}
}
