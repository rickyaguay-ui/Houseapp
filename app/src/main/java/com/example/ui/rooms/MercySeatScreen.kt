package com.example.ui.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HouseViewModel
import com.example.ui.RoomView
import com.example.ui.theme.*
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MercySeatScreen(viewModel: HouseViewModel) {
    val state by viewModel.uiState.collectAsState()
    var rawInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = "🕊️",
            fontSize = 44.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "THE MERCY SEAT",
            color = Gold,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )

        if (state.activeCounsel == null) {
            // Write Burden Phase
            Card(
                colors = CardDefaults.cardColors(containerColor = CardColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Gold, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Lay Down Your Load",
                        color = Gold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Whatever is weighing down your spirit right now—whether it is burnout, anxiety, guilt, or fear—write it here. It is safe, local, and completely released.",
                        color = TextColor,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TextField(
                        value = rawInput,
                        onValueChange = { rawInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .border(1.dp, Color(0xFF3E2D1A), RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Black,
                            unfocusedContainerColor = Color.Black,
                            disabledContainerColor = Color.Black,
                            cursorColor = Gold,
                            focusedTextColor = TextColor,
                            unfocusedTextColor = TextColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(
                                "I am exhausted...",
                                color = SecondaryText,
                                fontSize = 13.sp
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (rawInput.isNotBlank()) {
                                viewModel.submitBurden(rawInput)
                                rawInput = ""
                            }
                        },
                        enabled = rawInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            disabledContainerColor = Color(0xFF221A0F),
                            disabledContentColor = SecondaryText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Release to the Mercy Seat",
                            color = if (rawInput.isNotBlank()) CardColor else SecondaryText,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        } else {
            // Displays Counsel returned from Soul Tie Brain
            val output = state.activeCounsel!!
            Card(
                colors = CardDefaults.cardColors(containerColor = CardColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Gold, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Released & Forgiven",
                        color = Gold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // State Indicator Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(Color(0xFF2A1F12))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = output.state.icon, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Focus: ${output.state.label}",
                                color = Gold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "“${output.scripture}”",
                        color = TextColor,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = output.scriptureRef,
                        color = Gold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFF2A1F12), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = output.counsel,
                        color = TextColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Recommendation: ${output.recommendation}",
                        color = SecondaryText,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.clearCounsel() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Gold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Release New", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { 
                                viewModel.navigateTo(RoomView.STICKY) 
                                viewModel.clearCounsel()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Gold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Go To Sticky Hall", fontSize = 12.sp, color = CardColor, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
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

                        // Proceed to Scripture library
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(RoomView.REFLECTION) },
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2214)),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("📜 Reflection Study", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
