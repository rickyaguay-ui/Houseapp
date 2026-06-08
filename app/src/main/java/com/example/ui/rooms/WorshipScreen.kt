package com.example.ui.rooms

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HouseViewModel
import com.example.ui.RoomView
import com.example.ui.CommunalPrayerWall
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

data class SolfeggioFrequency(
    val hz: Int,
    val title: String,
    val benefit: String,
    val description: String
)

val frequencies = listOf(
    SolfeggioFrequency(396, "396 Hz - Deliverance", "Liberating Guilt & Fear", "Clears feelings of inadequacy and establishes a safe field of forgiveness."),
    SolfeggioFrequency(432, "432 Hz - Resonance", "Divine Harmony & Peace", "Aligns your heartbeat with the natural, steady pacing of creation."),
    SolfeggioFrequency(528, "528 Hz - Transformation", "Miracle Sound of Rebirth", "The ancient Solfeggio frequency of repair, deep restoration, and joy.")
)

enum class WorshipMode {
    TONE_ALTAR,
    SHORTS_FEED,
    ACCOUNT_LINK,
    PRAYER_WALL
}

data class WorshipShort(
    val id: String,
    val title: String,
    val preacher: String,
    val songTitle: String,
    val scripture: String,
    val textSnippet: String,
    val initialComments: List<String>,
    val colorStart: Color,
    val colorEnd: Color,
    val initialGlories: Int
)

val preloadedShorts = listOf(
    WorshipShort(
        id = "1",
        title = "When God is Silent",
        preacher = "Pastor Jonathan Harris",
        songTitle = "Waymaker - Sinach Arrangement",
        scripture = "Psalm 46:10",
        textSnippet = "Sometimes His silence is not absence. God is not ignoring you; He is training your heart to listen to His heartbeat instead of the waves. Dwell with confidence.",
        initialComments = listOf("Deeply needed today.", "Praise the Lord for this rest!"),
        colorStart = Color(0xFF1E140A),
        colorEnd = Color(0xFF382512),
        initialGlories = 422
    ),
    WorshipShort(
        id = "2",
        title = "Release the Control",
        preacher = "Sister Sarah Jenkins",
        songTitle = "Gratitude - Brandon Lake",
        scripture = "Proverbs 3:5-6",
        textSnippet = "He is already standing in your tomorrow. Stop burning today's sacred energy worrying about what you cannot manage. Surrender it fully.",
        initialComments = listOf("I am releasing control now.", "Amen, Amen, Amen!"),
        colorStart = Color(0xFF0F1A1B),
        colorEnd = Color(0xFF1B3234),
        initialGlories = 589
    ),
    WorshipShort(
        id = "3",
        title = "The Beautiful Gate of Peace",
        preacher = "Cathedral Liturgical Devotional",
        songTitle = "Kyrie Eleison - Ancient Chorus",
        scripture = "Isaiah 26:3",
        textSnippet = "You meet God in the center of still resonance. Quiet your inner storms and remember His promises from age to age. Perfect peace is a covenant.",
        initialComments = listOf("Brings so much quiet.", "Beautiful!"),
        colorStart = Color(0xFF160A18),
        colorEnd = Color(0xFF2C1931),
        initialGlories = 312
    ),
    WorshipShort(
        id = "4",
        title = "A Covenant Kept Daily",
        preacher = "Apostle Marcus Paul",
        songTitle = "Fresh Wind - Hillsong Worship",
        scripture = "Philippians 4:8",
        textSnippet = "Our faith is not an occasional spark—it is a continuous daily walk. Kindle the Altar space daily, and watch His fire refine every anxiety.",
        initialComments = listOf("Fire in my heart 🔥", "Consistency breeds peace!"),
        colorStart = Color(0xFF1D0A0A),
        colorEnd = Color(0xFF3B1515),
        initialGlories = 771
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorshipScreen(viewModel: HouseViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    val church = state.selectedChurch
    val primaryColor = parseColorHex(church.brandColorHex)
    val backgroundColor = parseColorHex(church.bgHex)

    var currentTab by remember { mutableStateOf(WorshipMode.SHORTS_FEED) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High craft top sub-navigation tab header
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Subtab Selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    WorshipMode.values().forEach { mode ->
                        val isSelected = currentTab == mode
                        Surface(
                            onClick = { currentTab = mode },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) primaryColor else Color.Transparent,
                            border = if (isSelected) null else BorderStroke(1.dp, Color(0xFF2E2214)),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = when (mode) {
                                        WorshipMode.SHORTS_FEED -> "📱 shorts"
                                        WorshipMode.TONE_ALTAR -> "🎵 Drone Altar"
                                        WorshipMode.ACCOUNT_LINK -> "🔐 Accounts"
                                        WorshipMode.PRAYER_WALL -> "🙏 Prayer Wall"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TextColor
                                )
                            }
                        }
                    }
                }

                // Account linking quick badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (state.isSpotifyLinked || state.isYoutubeLinked) Color(0xFF0F2D1F) else Color(0xFF1E1D19))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (state.isSpotifyLinked || state.isYoutubeLinked) Color.Green else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (state.isSpotifyLinked || state.isYoutubeLinked) "CONNECTED" else "UNLINKED",
                        color = if (state.isSpotifyLinked || state.isYoutubeLinked) Color.Green else SecondaryText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Current view content injection
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentTab) {
                WorshipMode.SHORTS_FEED -> VerticalShortsFeed(viewModel, primaryColor)
                WorshipMode.TONE_ALTAR -> ToneAltarView(viewModel, primaryColor)
                WorshipMode.ACCOUNT_LINK -> AccountLinkingView(viewModel, primaryColor)
                WorshipMode.PRAYER_WALL -> CommunalPrayerWall(state.prayerRequests, { viewModel.addPrayerRequest(it) }, primaryColor)
            }
        }

        // ==== PHYSICAL WALK-AROUND CORRIDORS AND PATHWAYS ====
        Surface(
            color = Color.Black.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(color = primaryColor.copy(alpha = 0.2f), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(10.dp))
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
                    // Turn left to Mercy Seat
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(RoomView.MERCY) },
                        border = BorderStroke(1.dp, Color(0xFF2E2214)),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("🕊️ Walk to Mercy Seat", fontSize = 10.sp)
                    }

                    // Go back to the Foyer
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(RoomView.FOYER) },
                        border = BorderStroke(1.dp, Color(0xFF2E2214)),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("🏠 Return to Foyer", fontSize = 10.sp)
                    }

                    // Seek the prayer notes
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(RoomView.STICKY) },
                        border = BorderStroke(1.dp, Color(0xFF2E2214)),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("📌 Visit Sticky Wall", fontSize = 10.sp)
                    }

                    // Proceed to Scripture library
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(RoomView.REFLECTION) },
                        border = BorderStroke(1.dp, Color(0xFF2E2214)),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("📜 Go to Reflection Study", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

// Vertical Shorts swiper layout list
@Composable
fun VerticalShortsFeed(viewModel: HouseViewModel, primaryColor: Color) {
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { preloadedShorts.size })
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val localFeedComments = remember {
        mutableStateMapOf<String, List<String>>().apply {
            preloadedShorts.forEach { put(it.id, it.initialComments) }
        }
    }

    val localShortGlories = remember {
        mutableStateMapOf<String, Int>().apply {
            preloadedShorts.forEach { put(it.id, it.initialGlories) }
        }
    }

    var showGloryPopId by remember { mutableStateOf<String?>(null) }
    var scaleGloryPop by remember { mutableStateOf(false) }

    var currentPlayingPage by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var shortProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(currentPlayingPage, isPlaying) {
        shortProgress = 0f
        if (isPlaying) {
            while (isActive) {
                delay(150)
                shortProgress += 0.01f
                if (shortProgress >= 1f) {
                    shortProgress = 0f
                }
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        currentPlayingPage = pagerState.currentPage
        isPlaying = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val video = preloadedShorts[page]
            val glories = localShortGlories[video.id] ?: video.initialGlories

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { isPlaying = !isPlaying }
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(video.colorStart, video.colorEnd)))
                ) {
                    WorshipWaveVisualizer(isPlaying = isPlaying, hz = 432 + page * 20, primaryColor = primaryColor)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)))
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .align(Alignment.BottomCenter)
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))))
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                        .height(2.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(shortProgress)
                            .fillMaxHeight()
                            .background(primaryColor)
                    )
                }

                AnimatedVisibility(
                    visible = !isPlaying,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Paused Icon",
                            tint = primaryColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Information layout
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, end = 90.dp, bottom = 45.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(primaryColor.copy(alpha = 0.2f))
                            .border(BorderStroke(0.5.dp, primaryColor), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = video.preacher.uppercase(),
                            color = primaryColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = video.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 22.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .clickable {
                                Toast
                                    .makeText(
                                        context,
                                        "Scripture Sanctuary Focus: ${video.scripture}",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = video.scripture,
                            color = primaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "“${video.textSnippet}”",
                            color = TextColor.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Music indicator",
                            tint = if (state.isSpotifyLinked) Color(0xFF1DB954) else Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = video.songTitle,
                            color = if (state.isSpotifyLinked) Color(0xFF1DB954) else Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Vertical quick interactions sidebar panel
                Column(
                    modifier = Modifier
                        .width(76.dp)
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 45.dp, end = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Glory Tap
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                localShortGlories[video.id] = glories + 1
                                showGloryPopId = video.id
                                viewModel.submitDailyPrayer("glory_praise_tap", "Offered Glory Amen for short: ${video.title}")
                                coroutineScope.launch {
                                    scaleGloryPop = true
                                    delay(400)
                                    scaleGloryPop = false
                                    showGloryPopId = null
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .border(BorderStroke(1.dp, primaryColor.copy(alpha = 0.4f)), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Praise heart",
                                tint = if (showGloryPopId == video.id) Color.Red else primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$glories AMEN",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Comments notes sheet launcher
                    var viewCommentsSheet by remember { mutableStateOf(false) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { viewCommentsSheet = true },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Comment,
                                contentDescription = "Comments",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val commentsList = localFeedComments[video.id] ?: emptyList()
                        Text(
                            text = "${commentsList.size} NOTES",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Spotify verify status badge and actions
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                if (state.isSpotifyLinked) {
                                    Toast.makeText(
                                        context,
                                        "🎶 Account is linked! Deep-linking to Spotify for: ${video.songTitle}.. Playing pristine sanctuary track.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "🔒 Spotify Account not linked. Please visit the 'Accounts' tab to setup.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (state.isSpotifyLinked) Color(0xFF1DB954) else Color.Black.copy(alpha = 0.5f))
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (state.isSpotifyLinked) Color.White else Color.White.copy(alpha = 0.2f)
                                    ),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Spotify deep link",
                                tint = if (state.isSpotifyLinked) Color.Black else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (state.isSpotifyLinked) "SPOTIFY" else "LINK MUSIC",
                            color = if (state.isSpotifyLinked) Color(0xFF1DB954) else SecondaryText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // YouTube verification badge and actions
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                if (state.isYoutubeLinked) {
                                    Toast.makeText(
                                        context,
                                        "📺 YouTube verified! Preloaded sermon notes unlocked successfully.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "🔒 YouTube login unlinked. Pre-load details in 'Accounts' tab to watch uninterrupted.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (state.isYoutubeLinked) Color(0xFFFF0000) else Color.Black.copy(alpha = 0.5f))
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (state.isYoutubeLinked) Color.White else Color.White.copy(alpha = 0.2f)
                                    ),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = "YouTube link",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (state.isYoutubeLinked) "LIVE" else "LINK VIDEO",
                            color = if (state.isYoutubeLinked) Color(0xFFFF0000) else SecondaryText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Comments Modal Dialog inside item
                    if (viewCommentsSheet) {
                        val currentComments = localFeedComments[video.id] ?: emptyList()
                        var draftCommentText by remember { mutableStateOf("") }
                        
                        AlertDialog(
                            onDismissRequest = { viewCommentsSheet = false },
                            confirmButton = {
                                TextButton(onClick = { viewCommentsSheet = false }) {
                                    Text("Done", color = primaryColor)
                                }
                            },
                            title = {
                                Text(
                                    text = "Amen Notes (Testimonies)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                            },
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp)
                                ) {
                                    Text(
                                        text = "Add your local sanctuary Amen testimony to this feed:",
                                        fontSize = 11.sp,
                                        color = SecondaryText,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    OutlinedTextField(
                                        value = draftCommentText,
                                        onValueChange = { draftCommentText = it },
                                        placeholder = { Text("Write your amen reflection...", fontSize = 11.sp) },
                                        maxLines = 2,
                                        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryColor,
                                            unfocusedBorderColor = Color(0xFF2E2214)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp)
                                    )

                                    Button(
                                        onClick = {
                                            if (draftCommentText.isNotBlank()) {
                                                val accountTag = when {
                                                    state.isSpotifyLinked -> " [Spotify verified 🎧]"
                                                    state.isYoutubeLinked -> " [YouTube verified 📺]"
                                                    else -> ""
                                                }
                                                val newComment = "🤍 Guest: $draftCommentText$accountTag"
                                                val updatedList = currentComments + newComment
                                                localFeedComments[video.id] = updatedList
                                                
                                                viewModel.submitDailyPrayer(
                                                    "Amen reflection: ${video.title}",
                                                    "SHORT FEED PREAMBLE:\nScripture: ${video.scripture}\nReflection entry: $draftCommentText"
                                                )
                                                draftCommentText = ""
                                            }
                                        },
                                        enabled = draftCommentText.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp)
                                    ) {
                                        Text("Publish Amen Testimony", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text(
                                        text = "COMMUNITY REFLECTIONS ON-DEVICE (FEED):",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SecondaryText,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        currentComments.forEach { comment ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color.White.copy(alpha = 0.05f))
                                                    .padding(8.dp)
                                            ) {
                                                Text(
                                                    text = comment,
                                                    color = TextColor,
                                                    fontSize = 11.sp,
                                                    lineHeight = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            containerColor = Color(0xFF140E0A),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Heart blast celebration
                if (showGloryPopId == video.id) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .border(BorderStroke(1.dp, primaryColor), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔥", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "GLORY TO THE LORD",
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    "+1 Covenant Amen Recorded",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Tone player Subview
@Composable
fun ToneAltarView(viewModel: HouseViewModel, primaryColor: Color) {
    val state by viewModel.uiState.collectAsState()
    var selectedFreq by remember { mutableStateOf(frequencies[1]) }
    var isPlaying by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    var playJob by remember { mutableStateOf<Job?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            playJob?.cancel()
            isPlaying = false
        }
    }

    LaunchedEffect(selectedFreq) {
        if (isPlaying) {
            playJob?.cancel()
            playJob = coroutineScope.launch(Dispatchers.Default) {
                playTone(selectedFreq.hz)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✙",
            color = primaryColor,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "RESONANCE DRONE ALTAR",
            color = primaryColor,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        WorshipWaveVisualizer(isPlaying = isPlaying, hz = selectedFreq.hz, primaryColor = primaryColor)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(0.5.dp, primaryColor.copy(alpha = 0.5f)), RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedFreq.title,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = selectedFreq.benefit,
                    color = TextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = selectedFreq.description,
                    color = SecondaryText,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isPlaying) {
                            playJob?.cancel()
                            isPlaying = false
                        } else {
                            isPlaying = true
                            playJob = coroutineScope.launch(Dispatchers.Default) {
                                playTone(selectedFreq.hz)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) Color(0xFF331515) else primaryColor,
                        contentColor = if (isPlaying) Color.Red else Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Stop drone" else "Start drone",
                        modifier = Modifier.padding(end = 8.dp).size(18.dp)
                    )
                    Text(
                        text = if (isPlaying) "Silence Sanctuary Drone" else "Sustain Sanctuary Tone",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = if (isPlaying) Color.Red else Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Select Holy Frequency focus:",
            color = SecondaryText,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 6.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            frequencies.forEach { freq ->
                val isSelected = freq == selectedFreq
                Card(
                    onClick = { selectedFreq = freq },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) primaryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .border(
                            BorderStroke(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) primaryColor else Color(0xFF2E2214)
                            ),
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = freq.title,
                                color = if (isSelected) primaryColor else TextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = freq.benefit,
                                color = SecondaryText,
                                fontSize = 10.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = if (isSelected) primaryColor else Color(0xFF2E2214)
                        )
                    }
                }
            }
        }
    }
}

// Account verify/linking View
@Composable
fun AccountLinkingView(viewModel: HouseViewModel, primaryColor: Color) {
    val state by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var customHandleInput by remember { mutableStateOf("rickyaguayjr2026@gmail.com") }
    var linkingProgressState by remember { mutableStateOf("IDLE") }
    var linkingTargetType by remember { mutableStateOf("NONE") }
    val liveTerminalLogs = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Lock",
            tint = primaryColor,
            modifier = Modifier.size(36.dp)
        )
        Text(
            text = "ACCOUNT VERIFICATION SHACK",
            color = primaryColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 2.5.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )
        Text(
            text = "Establish verified links to deep-stream audio metrics or backup sermon note histories securely with direct account verification handshaking.",
            color = SecondaryText,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(
                        width = 0.5.dp,
                        color = if (state.isSpotifyLinked) Color(0xFF1DB954) else Color(0xFF2E2214)
                    ),
                    RoundedCornerShape(12.dp)
                )
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Spotify",
                            tint = Color(0xFF1DB954),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Spotify Music streaming", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = if (state.isSpotifyLinked) "🟢 Active: ${state.spotifyAccountName}" else "🔴 Unlinked offline mode",
                                color = if (state.isSpotifyLinked) Color(0xFF1DB954) else SecondaryText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (state.isSpotifyLinked) {
                        TextButton(onClick = { viewModel.unlinkSpotify() }) {
                            Text("Unlink", color = Color.Red, fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                linkingTargetType = "SPOTIFY"
                                linkingProgressState = "REQUESTING"
                                liveTerminalLogs.clear()
                                liveTerminalLogs.add("> HANDSHAKE REQUEST GRANTED")
                                liveTerminalLogs.add("> INITIAL SEARCH: spotify://auth?client_id=the_house")
                                
                                coroutineScope.launch {
                                    delay(800)
                                    linkingProgressState = "SHAKEHAND"
                                    liveTerminalLogs.add("> STATUS COMPILED: REDIRECT RECOVERY ACTIVE")
                                    liveTerminalLogs.add("> TOKEN INTERCHARGE EXCHANGED: SUCCESS-777122")
                                    delay(1000)
                                    liveTerminalLogs.add("> PARSING ACCOUNT SCHEMA: $customHandleInput")
                                    delay(800)
                                    viewModel.linkSpotify(customHandleInput)
                                    linkingProgressState = "COMPLETED"
                                    liveTerminalLogs.add("> SECURE CONNECTION SECURED!")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Verify Spotify", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(
                        width = 0.5.dp,
                        color = if (state.isYoutubeLinked) Color(0xFFFF0000) else Color(0xFF2E2214)
                    ),
                    RoundedCornerShape(12.dp)
                )
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "YouTube",
                            tint = Color(0xFFFF0000),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("YouTube Sermons feed", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = if (state.isYoutubeLinked) "🟢 Active: ${state.youtubeAccountName}" else "🔴 Unlinked offline mode",
                                color = if (state.isYoutubeLinked) Color(0xFFFF0000) else SecondaryText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (state.isYoutubeLinked) {
                        TextButton(onClick = { viewModel.unlinkYoutube() }) {
                            Text("Unlink", color = Color.Red, fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                linkingTargetType = "YOUTUBE"
                                linkingProgressState = "REQUESTING"
                                liveTerminalLogs.clear()
                                liveTerminalLogs.add("> HANDSHAKE REQUEST FOR YOUTUBE")
                                liveTerminalLogs.add("> LAUNCH OAUTH URL DISPATCHER")
                                
                                coroutineScope.launch {
                                    delay(800)
                                    linkingProgressState = "SHAKEHAND"
                                    liveTerminalLogs.add("> KEY EXCHANGE INITIATED: VERIFYING LOCKS")
                                    liveTerminalLogs.add("> SYSTEM SYNC SUCCESS: TOKEN STAGE ACQUIRED")
                                    delay(900)
                                    liveTerminalLogs.add("> ACCOUNT LOGGED: $customHandleInput")
                                    delay(700)
                                    viewModel.linkYoutube(customHandleInput)
                                    linkingProgressState = "COMPLETED"
                                    liveTerminalLogs.add("> SECURITY PORT HANDSHAKE SECURED!")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Verify Youtube", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (!state.isSpotifyLinked || !state.isYoutubeLinked) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(0.5.dp, Color(0xFF2E2214)), RoundedCornerShape(10.dp))
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "VERIFICATION CREDENTIALS CONFIGURATION",
                        color = primaryColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Enter your primary identity handle used to map deep linking handshakes. This is saved as your verified signature on-device.",
                        fontSize = 11.sp,
                        color = SecondaryText,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = customHandleInput,
                        onValueChange = { customHandleInput = it },
                        label = { Text("Account Handle / Email Address", fontSize = 11.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color(0xFF2E2214)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (linkingProgressState != "IDLE") {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, Color.Green.copy(alpha = 0.4f)), RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🌐 DEEP LINK HANDSHAKE CONSOLE: $linkingTargetType",
                            color = Color.Green,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (linkingProgressState != "COMPLETED") {
                            CircularProgressIndicator(
                                color = Color.Green,
                                strokeWidth = 1.5.dp,
                                modifier = Modifier.size(10.dp)
                            )
                        } else {
                            Text("DONE", color = Color.Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        liveTerminalLogs.forEach { logLine ->
                            Text(
                                text = logLine,
                                color = Color.Green.copy(alpha = 0.85f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        }
                    }

                    if (linkingProgressState == "COMPLETED") {
                        Button(
                            onClick = { linkingProgressState = "IDLE" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .align(Alignment.End)
                                .height(26.dp)
                        ) {
                            Text("Close Logs", color = Color.Green, fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorshipWaveVisualizer(isPlaying: Boolean, hz: Int, primaryColor: Color) {
    val baseDuration = 4000 - (hz - 300) * 4
    val infiniteTransition = rememberInfiniteTransition(label = "WorshipWaveVisualizer")
    
    val phaseShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(if (isPlaying) baseDuration else 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    val amplitudeMultiplier by animateFloatAsState(
        targetValue = if (isPlaying) 1.2f else 0.15f,
        animationSpec = tween(1500),
        label = "WaveAmplitude"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .border(BorderStroke(0.5.dp, Color(0xFF2E2214)), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val midY = height / 2f
            
            val paths = listOf(
                Pair(0.008f, 30f * amplitudeMultiplier),
                Pair(0.015f, 18f * amplitudeMultiplier),
                Pair(0.022f, 10f * amplitudeMultiplier)
            )

            paths.forEachIndexed { idx, (frequency, amp) ->
                val path = Path()
                path.moveTo(0f, midY)
                
                for (x in 0..width.toInt() step 4) {
                    val angle = x * frequency - phaseShift + (idx * 1.5f)
                    val y = midY + sin(angle) * amp
                    path.lineTo(x.toFloat(), y)
                }

                val brush = Brush.horizontalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.8f - (idx * 0.2f)),
                        primaryColor.copy(alpha = 0.5f - (idx * 0.15f)),
                        primaryColor.copy(alpha = 0.1f)
                    )
                )

                drawPath(
                    path = path,
                    brush = brush,
                    style = Stroke(width = (4f - idx).coerceAtLeast(1.5f).dp.toPx())
                )
            }
        }
        
        if (!isPlaying) {
            Text(
                text = "SANCTUARY ACOUSTICS DORMANT",
                color = SecondaryText,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
        }
    }
}

private fun playTone(hz: Int) {
    val sampleRate = 44100
    val numSamples = sampleRate
    val generatedSnd = DoubleArray(numSamples)
    val playSnd = ShortArray(numSamples)

    for (i in 0 until numSamples) {
        generatedSnd[i] = sin(2 * Math.PI * i / (sampleRate / hz.toDouble()))
        playSnd[i] = (generatedSnd[i] * Short.MAX_VALUE).toInt().toShort()
    }

    try {
        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            numSamples * 2,
            AudioTrack.MODE_STATIC
        )

        audioTrack.write(playSnd, 0, numSamples)
        audioTrack.setLoopPoints(0, numSamples - 1, -1)
        audioTrack.play()

        while (true) {
            Thread.sleep(1000)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
