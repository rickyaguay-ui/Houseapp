package com.example.ui.rooms

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyPrayerEntity
import com.example.ui.data.BattlePlan
import com.example.ui.ChurchConfig
import com.example.ui.HouseViewModel
import com.example.ui.RoomView
import com.example.ui.preloadedChurches
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class StudioTheme(
    val name: String,
    val textColor: Color,
    val colorStart: Color,
    val colorEnd: Color
)

@Composable
fun FoyerScreen(viewModel: HouseViewModel) {
    val state by viewModel.uiState.collectAsState()
    val church = state.selectedChurch
    val focusManager = LocalFocusManager.current
    
    // Dialog state for Church Selection
    var showChurchDialog by remember { mutableStateOf(false) }
    // Tab state in Church Selection dialog: 0 = Preloaded, 1 = Custom Church Builder
    var dialogTab by remember { mutableIntStateOf(0) }

    // Custom Church form state
    var customName by remember { mutableStateOf("") }
    var customLocation by remember { mutableStateOf("") }
    var customDenom by remember { mutableStateOf("Non-Denominational") }
    var customEmoji by remember { mutableStateOf("⛪") }
    var customWelcomeMsg by remember { mutableStateOf("") }
    var customScripRef by remember { mutableStateOf("Psalm 127:1") }
    var customScripText by remember { mutableStateOf("Unless the Lord builds the house, those who build it labor in vain.") }

    // Selected custom colors (Brand & Bg) presets list
    val colorPresets = remember {
        listOf(
            Triple("#F5C87A", "#0F0A05", "Gold / Espresso"),
            Triple("#00CED1", "#050B10", "Teal / Deep Blue"),
            Triple("#D11A2A", "#0E0405", "Crimson / Soft Wine"),
            Triple("#DAA520", "#070B14", "Bronze / Cathedral Navy"),
            Triple("#FF7F50", "#120B05", "Coral / Fiery Umber"),
            Triple("#9C27B0", "#0B040F", "Purple / Velvet Midnight"),
            Triple("#4CAF50", "#040F06", "Emerald / Forest Depth")
        )
    }
    var selectedColorPreset by remember { mutableStateOf(colorPresets[0]) }

    // Daily reflection/prayer input state
    var prayerTitle by remember { mutableStateOf("") }
    var prayerDraftText by remember { mutableStateOf(viewModel.getDraftPrayer()) }

    // Keep draft updated locally as they write (localStorage style caching)
    LaunchedEffect(prayerDraftText) {
        viewModel.saveDraftPrayer(prayerDraftText)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic Church Branding Logo and Transformation Title
        Text(
            text = church.logoEmoji,
            fontSize = 58.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = church.name.uppercase(),
            color = parseColorHex(church.brandColorHex),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Text(
            text = "${church.denomination} • ${church.location}",
            color = SecondaryText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Glowing Covenant Streak Indicator badge (YouVersion & Glorify Adapted)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(99.dp))
                .background(Color(0xFF251A0B))
                .border(0.5.dp, parseColorHex(church.brandColorHex).copy(alpha = 0.4f), RoundedCornerShape(99.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(text = if (state.covenantStreak > 0) "🔥" else "🕯️", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (state.covenantStreak > 0) "${state.covenantStreak}-DAY COVENANT STREAK" else "COVENANT TIMELINE UNKINDLED",
                color = if (state.covenantStreak > 0) parseColorHex(church.brandColorHex) else SecondaryText,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.2.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Daily Curated Battle Plans
        if (state.battlePlans.isNotEmpty()) {
            Text(
                text = "FASTPASS BATTLE PLANS",
                color = parseColorHex(church.brandColorHex),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 10.dp)
            )
            state.battlePlans.forEach { plan ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .border(1.dp, parseColorHex(church.brandColorHex).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = plan.title, fontWeight = FontWeight.Bold, color = parseColorHex(church.brandColorHex), fontSize = 16.sp)
                        Text(text = plan.verse, color = SecondaryText, fontSize = 12.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = plan.action, color = TextColor, fontSize = 14.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Main Greeting Transformative Welcome Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, parseColorHex(church.brandColorHex), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome Sanctuary",
                    color = parseColorHex(church.brandColorHex),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(10.dp))
                
                // Church Specific Welcome Message
                Text(
                    text = church.welcomeMessage,
                    color = TextColor,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                // Separator / Divider
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFF2A1F12), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Preloaded Scripture Focus Card
                Text(
                    text = "“${church.scriptureText}”",
                    color = TextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = "— ${church.scriptureRef}",
                    color = parseColorHex(church.brandColorHex),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))
                
                // Release Button
                Button(
                    onClick = { viewModel.navigateTo(RoomView.MERCY) },
                    colors = ButtonDefaults.buttonColors(containerColor = parseColorHex(church.brandColorHex)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Release Burden to Mercy Seat",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Switch Church Button
                OutlinedButton(
                    onClick = { showChurchDialog = true },
                    border = BorderStroke(0.5.dp, parseColorHex(church.brandColorHex).copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = parseColorHex(church.brandColorHex)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PinDrop,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Connect Local Church",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==== BIBLE SCRIPTURE & GRATITUDE DEVOTIONAL STUDIO (YouVersion & Glorify Inspired) ====
        Text(
            text = "BIBLE & GRATITUDE STUDIO",
            color = parseColorHex(church.brandColorHex),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 10.dp)
        )

        // Predefined beautiful verses
        val studioVerses = remember {
            listOf(
                "Psalm 46:10" to "Be still, and know that I am God.",
                "Isaiah 41:10" to "Fear not, for I am with you; be not dismayed, for I am your God.",
                "Philippians 4:13" to "I can do all things through Christ who strengthens me.",
                "Proverbs 3:5" to "Trust in the Lord with all your heart.",
                "Matthew 6:34" to "Therefore do not worry about tomorrow, for tomorrow will worry about itself."
            )
        }

        var selectedStudioVerseIdx by remember { mutableIntStateOf(0) }
        var isCustomVerseEnabled by remember { mutableStateOf(false) }
        var customVerseRef by remember { mutableStateOf("") }
        var customVerseText by remember { mutableStateOf("") }
        var gratitudeInput by remember { mutableStateOf("") }

        val gradientThemes = remember {
            listOf(
                StudioTheme("Sinai Gold", Color(0xFFF5C87A), Color(0xFF5F4522), Color(0xFF382305)),
                StudioTheme("Jordan Teal", Color(0xFF4DB6AC), Color(0xFF00796B), Color(0xFF004D40)),
                StudioTheme("Calvary Crimson", Color(0xFFE57373), Color(0xFF901B1B), Color(0xFF4E0606)),
                StudioTheme("Damascus Violet", Color(0xFFBA68C8), Color(0xFF7B1FA2), Color(0xFF4A148C))
            )
        }
        var selectedThemeIdx by remember { mutableIntStateOf(0) }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, parseColorHex(church.brandColorHex).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Craft a beautiful local Gratitude Devotional card. Select a Scripture focus, note your daily praise, select a background, and pin it to your sanctuary record.",
                    color = SecondaryText,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Select Scripture Verse Type: Preloaded or Custom
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isCustomVerseEnabled,
                        onClick = { isCustomVerseEnabled = false },
                        label = { Text("Preloaded Verses", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = parseColorHex(church.brandColorHex).copy(alpha = 0.15f),
                            selectedLabelColor = parseColorHex(church.brandColorHex)
                        )
                    )
                    FilterChip(
                        selected = isCustomVerseEnabled,
                        onClick = { isCustomVerseEnabled = true },
                        label = { Text("Custom Verse", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = parseColorHex(church.brandColorHex).copy(alpha = 0.15f),
                            selectedLabelColor = parseColorHex(church.brandColorHex)
                        )
                    )
                }

                if (!isCustomVerseEnabled) {
                    Text(
                        text = "Choose Inspiration Verse:",
                        color = SecondaryText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        studioVerses.forEachIndexed { idx, item ->
                            val isSelected = selectedStudioVerseIdx == idx && !isCustomVerseEnabled
                            SuggestionChip(
                                onClick = { selectedStudioVerseIdx = idx },
                                label = { Text(item.first, fontSize = 10.sp) },
                                border = BorderStroke(0.5.dp, if (isSelected) parseColorHex(church.brandColorHex) else Color(0xFF2A1F12))
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customVerseRef,
                            onValueChange = { customVerseRef = it },
                            placeholder = { Text("E.g. Romans 8:28") },
                            label = { Text("Reference", fontSize = 10.sp) },
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = parseColorHex(church.brandColorHex),
                                unfocusedBorderColor = Color(0xFF2A1F12)
                            ),
                            modifier = Modifier.weight(1.5f)
                        )
                        OutlinedTextField(
                            value = customVerseText,
                            onValueChange = { customVerseText = it },
                            placeholder = { Text("Scripture text...") },
                            label = { Text("Text", fontSize = 10.sp) },
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = parseColorHex(church.brandColorHex),
                                unfocusedBorderColor = Color(0xFF2A1F12)
                            ),
                            modifier = Modifier.weight(3f)
                        )
                    }
                }

                OutlinedTextField(
                    value = gratitudeInput,
                    onValueChange = { gratitudeInput = it },
                    label = { Text("What are you grateful for today?") },
                    placeholder = { Text("Today, I bless God for...", color = SecondaryText, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = parseColorHex(church.brandColorHex),
                        unfocusedBorderColor = Color(0xFF2A1F12)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                Text(
                    text = "Select Graphic Theme:",
                    color = SecondaryText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gradientThemes.forEachIndexed { idx, theme ->
                        val isSelected = selectedThemeIdx == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(26.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Brush.linearGradient(listOf(theme.colorStart, theme.colorEnd)))
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable { selectedThemeIdx = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = theme.name,
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                val currentTheme = gradientThemes[selectedThemeIdx]
                val currentRef = if (isCustomVerseEnabled) customVerseRef.ifBlank { "Unassigned Ref" } else studioVerses[selectedStudioVerseIdx].first
                val currentText = if (isCustomVerseEnabled) customVerseText.ifBlank { "Pour in your scripture focus." } else studioVerses[selectedStudioVerseIdx].second

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .border(0.5.dp, currentTheme.textColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(listOf(currentTheme.colorStart, currentTheme.colorEnd)))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "“$currentText”",
                            color = currentTheme.textColor,
                            fontFamily = FontFamily.Serif,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 17.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "— $currentRef",
                            color = currentTheme.textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.35f))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Gratitude Check-in: ${gratitudeInput.ifBlank { "Pour out praise..." }}",
                                color = Color.White,
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val finalNote = "📜 SCRIPTURE: $currentText ($currentRef)\n🤍 GRATITUDE: $gratitudeInput"
                        viewModel.submitDailyPrayer("Gratitude & Scripture Card: $currentRef", finalNote)
                        
                        gratitudeInput = ""
                        customVerseRef = ""
                        customVerseText = ""
                    },
                    enabled = gratitudeInput.isNotBlank() && (isCustomVerseEnabled.not() || (customVerseRef.isNotBlank() && customVerseText.isNotBlank())),
                    colors = ButtonDefaults.buttonColors(containerColor = parseColorHex(church.brandColorHex)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SAVE DEVOTION CARD", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Persistent Daily Reflections & Prayers Corner
        Text(
            text = "DAILY REFLEX & PRAYER CHAMBER",
            color = parseColorHex(church.brandColorHex),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 10.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, parseColorHex(church.brandColorHex).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Write your persistent prayer or reflection. It updates instantly to disk (localStorage equivalent) and can be committed to your permanent sanctuary record.",
                    color = SecondaryText,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Optional Reflection Title
                OutlinedTextField(
                    value = prayerTitle,
                    onValueChange = { prayerTitle = it },
                    label = { Text("Title or focus (e.g. Guidance, Rest, S.O.A.P Integration)") },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = parseColorHex(church.brandColorHex),
                        unfocusedBorderColor = Color(0xFF2A1F12),
                        focusedLabelColor = parseColorHex(church.brandColorHex),
                        unfocusedLabelColor = SecondaryText
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                )

                // Reflection Input
                OutlinedTextField(
                    value = prayerDraftText,
                    onValueChange = { prayerDraftText = it },
                    placeholder = { Text("Type quiet prayers, local announcements, group goals, or heart conditions here...") },
                    shape = RoundedCornerShape(12.dp),
                    minLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = parseColorHex(church.brandColorHex),
                        unfocusedBorderColor = Color(0xFF2A1F12),
                        focusedLabelColor = parseColorHex(church.brandColorHex),
                        unfocusedLabelColor = SecondaryText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (prayerDraftText.isNotBlank()) {
                            viewModel.submitDailyPrayer(prayerTitle, prayerDraftText)
                            prayerTitle = ""
                            prayerDraftText = ""
                            focusManager.clearFocus()
                        }
                    },
                    enabled = prayerDraftText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = parseColorHex(church.brandColorHex),
                        disabledContainerColor = Color(0xFF1E1C1A)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalActivity,
                        contentDescription = null,
                        tint = if (prayerDraftText.isNotBlank()) Color.Black else SecondaryText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pin to Sanctuary Record",
                        color = if (prayerDraftText.isNotBlank()) Color.Black else SecondaryText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Historical Ledger of Prayers
        if (state.dailyPrayers.isNotEmpty()) {
            Text(
                text = "COMMITTED DAILY JOURNALED RECORD",
                color = parseColorHex(church.brandColorHex).copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            state.dailyPrayers.forEach { prayer ->
                var isExpanded by remember { mutableStateOf(false) }
                Card(
                    onClick = { isExpanded = !isExpanded },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .border(0.5.dp, Color(0xFF2A1F12), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prayer.title,
                                    color = parseColorHex(church.brandColorHex),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Dated ${SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(prayer.timestamp))} • @ ${prayer.churchNameAtTime}",
                                    color = SecondaryText,
                                    fontSize = 10.sp
                                )
                            }
                            IconButton(onClick = { viewModel.deleteDailyPrayer(prayer.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete entry",
                                    tint = Color.Red.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = prayer.text,
                                    color = TextColor,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        if (!isExpanded) {
                            Text(
                                text = prayer.text,
                                color = TextColor.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                maxLines = 1,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stats Ledger Title
        Text(
            text = "SANCTUARY DIAGNOSTICS LEDGER",
            color = parseColorHex(church.brandColorHex),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stats Card 1: Burdens Pinned as sticky notes
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(0.5.dp, Color(0xFF2A1F12), RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📌", fontSize = 22.sp, modifier = Modifier.padding(bottom = 4.dp))
                    Text(
                        text = state.burdens.size.toString(),
                        color = parseColorHex(church.brandColorHex),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Burdens Released",
                        color = SecondaryText,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Stats Card 2: SOAP Reflections Completed
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(0.5.dp, Color(0xFF2A1F12), RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📜", fontSize = 22.sp, modifier = Modifier.padding(bottom = 4.dp))
                    Text(
                        text = state.reflections.size.toString(),
                        color = parseColorHex(church.brandColorHex),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "SOAP Daily Journals",
                        color = SecondaryText,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Immersive walkthrough gateways / doors
        Text(
            text = "🚪 WALK INTO THE CHURCH (PORTALS & DOORWAYS)",
            color = parseColorHex(church.brandColorHex),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 10.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, parseColorHex(church.brandColorHex).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "You are currently standing in the Foyer entrance hall. Select a doorway portal physically leading you deeper into the Church sanctuary wings.",
                    color = SecondaryText,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // List of doors to tap
                val corridorsList = listOf(
                    Triple(RoomView.WORSHIP, "⛪ Enter the Main Sanctuary (Worship Center)", "Walk inside the sanctuary with high arches, beautiful digital Shorts, sermon feeds & sustain tone players."),
                    Triple(RoomView.MERCY, "🕊️ Walk to the Mercy Seat Chapel", "Access the quiet private alcove of relief. Confess and burn mental weights/burdens securely on-device."),
                    Triple(RoomView.STICKY, "📌 Fellowship Hall (Sticky Prayers Wall)", "Visit the community sticky Wall board. Review persistent testimonies or leave an encouraging line."),
                    Triple(RoomView.REFLECTION, "📜 Climb to Cathedral Study (SOAP Library)", "Settle down at a timber desk. Draft, study, and commit real S.O.A.P scripture reflections."),
                    Triple(RoomView.WINDDOWN, "🌙 Go to the Sleep Wind-Down Vestry", "Wind down your system. Practice 4-7-8 holy breathing aligned to state cues to soothe raw nerves.")
                )

                corridorsList.forEach { (targetRoom, doorTitle, doorDesc) ->
                    Card(
                        onClick = { viewModel.navigateTo(targetRoom) },
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .border(0.5.dp, parseColorHex(church.brandColorHex).copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsWalk,
                                contentDescription = "Walk door path",
                                tint = parseColorHex(church.brandColorHex),
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 4.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = doorTitle,
                                    color = parseColorHex(church.brandColorHex),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = doorDesc,
                                    color = SecondaryText,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Step forward icon",
                                tint = parseColorHex(church.brandColorHex).copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Connect Local Church Dialog
    if (showChurchDialog) {
        AlertDialog(
            onDismissRequest = { showChurchDialog = false },
            title = {
                Text(
                    text = "Connect Local Church",
                    color = parseColorHex(church.brandColorHex),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    TabRow(
                        selectedTabIndex = dialogTab,
                        containerColor = Color.Transparent,
                        contentColor = parseColorHex(church.brandColorHex)
                    ) {
                        Tab(
                            selected = dialogTab == 0,
                            onClick = { dialogTab = 0 },
                            text = { Text("Preloaded Florida") }
                        )
                        Tab(
                            selected = dialogTab == 1,
                            onClick = { dialogTab = 1 },
                            text = { Text("Builder") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (dialogTab == 0) {
                        // Preloaded Churches List
                        preloadedChurches.forEachIndexed { idx, item ->
                            val isSelected = item.name == church.name
                            Card(
                                onClick = {
                                    viewModel.selectChurch(idx)
                                    showChurchDialog = false
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) parseColorHex(item.brandColorHex).copy(alpha = 0.15f) else Color(0xFF1E1812)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.5.dp,
                                        color = if (isSelected) parseColorHex(item.brandColorHex) else Color(0xFF2A1F12),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.logoEmoji,
                                        fontSize = 28.sp,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            fontWeight = FontWeight.Bold,
                                            color = parseColorHex(item.brandColorHex),
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${item.denomination} • ${item.location}",
                                            fontSize = 10.sp,
                                            color = SecondaryText
                                        )
                                        Text(
                                            text = "“${item.scriptureText}”",
                                            fontSize = 11.sp,
                                            color = TextColor,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Custom Church Builder Form
                        Text(
                            text = "TRANSFORM THE APP TO YOUR CHURCH",
                            color = SecondaryText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("Church Name (e.g. Life Church)") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = customLocation,
                            onValueChange = { customLocation = it },
                            label = { Text("Location (e.g. Tallahassee, FL)") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = customDenom,
                            onValueChange = { customDenom = it },
                            label = { Text("Denomination (e.g. Baptist, Assembly)") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = customEmoji,
                            onValueChange = { customEmoji = it },
                            label = { Text("Logo Emoji (e.g. ⛪, 🕊️, Flame: 🔥)") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = customWelcomeMsg,
                            onValueChange = { customWelcomeMsg = it },
                            label = { Text("Spiritual Banner Greeting Message") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = customScripRef,
                            onValueChange = { customScripRef = it },
                            label = { Text("Default Scripture Reference") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = customScripText,
                            onValueChange = { customScripText = it },
                            label = { Text("Default Scripture Text") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )

                        Text(
                            text = "Choose Sanctuary Branding Aesthetic Color:",
                            color = SecondaryText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Presets Grid Selector
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            colorPresets.forEach { preset ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(preset.first)))
                                        .border(
                                            width = if (selectedColorPreset == preset) 2.5.dp else 0.dp,
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColorPreset = preset }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (dialogTab == 1) {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = parseColorHex(church.brandColorHex)),
                        onClick = {
                            if (customName.isNotBlank()) {
                                viewModel.selectCustomChurch(
                                    name = customName,
                                    location = customLocation.ifBlank { "Local" },
                                    brandColorHex = selectedColorPreset.first,
                                    bgHex = selectedColorPreset.second,
                                    denomination = customDenom.ifBlank { "Community Church" },
                                    emoji = customEmoji.ifBlank { "⛪" },
                                    welcomeMessage = customWelcomeMsg.ifBlank { "Connected to custom local sanctuary. Gathered offline to worship." },
                                    scriptureRef = customScripRef.ifBlank { "Romans 1:16" },
                                    scriptureText = customScripText.ifBlank { "For I am not ashamed of the gospel, for it is the power of God for salvation." }
                                )
                                showChurchDialog = false
                            }
                        },
                        enabled = customName.isNotBlank()
                    ) {
                        Text("Transform Now", color = Color.Black)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showChurchDialog = false }) {
                    Text("Close", color = parseColorHex(church.brandColorHex))
                }
            },
            containerColor = Color(0xFF100D0B)
        )
    }
}
