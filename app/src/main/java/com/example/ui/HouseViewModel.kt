package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BurdenEntity
import com.example.data.SoapEntity
import com.example.data.DailyPrayerEntity
import com.example.data.StickyNoteEntity
import com.example.data.HouseDatabase
import com.example.data.HouseRepository
import com.example.engine.BrainOutput
import com.example.engine.SoulTieBrain
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ui.data.Scripture
import com.example.ui.data.ScriptureService
import com.example.ui.data.PrayerRequest
import com.example.ui.data.BattlePlan

enum class RoomView { FOYER, WORSHIP, MERCY, STICKY, REFLECTION, WINDDOWN, BATTLEPLAN }

data class ChurchConfig(
    val name: String,
    val location: String,
    val brandColorHex: String,
    val bgHex: String,
    val denomination: String,
    val logoEmoji: String,
    val welcomeMessage: String,
    val scriptureRef: String,
    val scriptureText: String
)

val preloadedChurches = listOf(
    ChurchConfig(
        name = "The House Sanctuary",
        location = "Digital Stillness",
        brandColorHex = "#F5C87A", // Gold
        bgHex = "#0F0A05", // Deep espresso
        denomination = "Sovereign Sanctuary (Local)",
        logoEmoji = "🏠",
        welcomeMessage = "This application is a self-sustained offline sanctuary. Every burden you release, prayer you whisper, or reflection you write down is held strictly on your device.",
        scriptureRef = "Psalm 122:1",
        scriptureText = "I was glad when they said to me, 'Let us go to the house of the Lord!'"
    ),
    ChurchConfig(
        name = "City Church Tallahassee",
        location = "Tallahassee, FL",
        brandColorHex = "#F2B84B", // Modern warm Gold
        bgHex = "#0A0908", // Matte Dark charcoal
        denomination = "Non-Denominational",
        logoEmoji = "⛪",
        welcomeMessage = "Welcome to the custom sanctuary mapped for City Church Tallahassee. We exist so that even the furthest away can connect to the heart of Jesus.",
        scriptureRef = "Matthew 6:33",
        scriptureText = "But seek first the kingdom of God and his righteousness, and all these things will be added to you."
    ),
    ChurchConfig(
        name = "The Church of Eleven22",
        location = "Jacksonville, FL",
        brandColorHex = "#00CED1", // Eleven22 Teal
        bgHex = "#050B10", // Sea Blue Depth
        denomination = "Non-Denominational Movement",
        logoEmoji = "🕊️",
        welcomeMessage = "Sustaining you at Eleven22. We are a movement of people who search together, discover His grace, and worship in raw truth. Walk in His path.",
        scriptureRef = "Psalm 27:4",
        scriptureText = "One thing have I asked of the Lord, that will I seek after: that I may dwell in the house of the Lord all the days of my life."
    ),
    ChurchConfig(
        name = "First Baptist Orlando",
        location = "Orlando, FL",
        brandColorHex = "#D11A2A", // Grace Crimson
        bgHex = "#0E0405", // Soft Burgundy darkness
        denomination = "Baptist / Evangelical",
        logoEmoji = "✙",
        welcomeMessage = "Connected to First Orlando. A faith-filled community dedicated to leading people into a growing relationship with Jesus. Gathered or scattered, we love.",
        scriptureRef = "Ephesians 2:8",
        scriptureText = "For by grace you have been saved through faith. And this is not your own doing; it is the gift of God."
    ),
    ChurchConfig(
        name = "St. James Catholic Cathedral",
        location = "Orlando, FL",
        brandColorHex = "#DAA520", // Cathedral Gold
        bgHex = "#070B14", // Sovereign Dark Blue
        denomination = "Liturgical / Catholic Roman",
        logoEmoji = "🕯️",
        welcomeMessage = "Within the local chapel of St. James. Serving the community in holy devotion, keeping the ancient liturgy alive to quiet and steady the modern spirit.",
        scriptureRef = "Psalm 119:105",
        scriptureText = "Your word is a lamp to my feet and a light to my path."
    ),
    ChurchConfig(
        name = "Calvary Orlando",
        location = "Winter Park, FL",
        brandColorHex = "#FF7F50", // Pentecostal Coral Fire
        bgHex = "#120B05", // Fiery Charcoal hue
        denomination = "Charismatic / Assemblies of God",
        logoEmoji = "🔥",
        welcomeMessage = "Tethered to Calvary Orlando. Where the fire of Pentecost burns raw, expecting the miraculous and praying for powerful individual breakthroughs.",
        scriptureRef = "Acts 1:8",
        scriptureText = "But you will receive power when the Holy Spirit has come upon you, and you will be my witnesses to the ends of the earth."
    )
)

data class HouseUiState(
    val currentRoom: RoomView = RoomView.FOYER,
    val burdens: List<BurdenEntity> = emptyList(),
    val reflections: List<SoapEntity> = emptyList(),
    val dailyPrayers: List<DailyPrayerEntity> = emptyList(),
    val activeCounsel: BrainOutput? = null,
    val cinematicActive: Boolean = true,
    val selectedChurch: ChurchConfig = preloadedChurches[0],
    val covenantStreak: Int = 0,
    val isSpotifyLinked: Boolean = false,
    val spotifyAccountName: String? = null,
    val isYoutubeLinked: Boolean = false,
    val youtubeAccountName: String? = null,
    val isSoundscapeEnabled: Boolean = true,
    val alarmTime: String = "07:00 AM",
    val currentScripture: Scripture? = null,
    val prayerRequests: List<PrayerRequest> = emptyList(),
    val battlePlans: List<BattlePlan> = emptyList(),
    val stickyNotes: List<StickyNoteEntity> = emptyList()
)

data class DatabaseSnapshot(
    val burdens: List<BurdenEntity>,
    val soap: List<SoapEntity>,
    val prayers: List<DailyPrayerEntity>,
    val stickyNotes: List<StickyNoteEntity>
)

class HouseViewModel(application: Application) : AndroidViewModel(application) {

    private val database = HouseDatabase.getDatabase(application)
    private val repository = HouseRepository(database.houseDao())
    private val prefs = application.getSharedPreferences("TheHousePrefs", Context.MODE_PRIVATE)
    
    private val _uiState = MutableStateFlow(HouseUiState())
    val uiState: StateFlow<HouseUiState> = _uiState.asStateFlow()

    init {
        // Collect database flows into UI state
        viewModelScope.launch {
            combine(
                repository.allBurdens,
                repository.allSoapReflections,
                repository.allDailyPrayers,
                repository.allStickyNotes
            ) { burdensList, soapList, prayersList, stickyNotesList ->
                DatabaseSnapshot(burdensList, soapList, prayersList, stickyNotesList)
            }.collect { snapshot ->
                _uiState.update { 
                    it.copy(
                        burdens = snapshot.burdens, 
                        reflections = snapshot.soap,
                        dailyPrayers = snapshot.prayers,
                        stickyNotes = snapshot.stickyNotes,
                        currentScripture = ScriptureService.getRandomScripture()
                    ) 
                }
            }
        }
        
        loadChurchConfig()
        evaluateQuietState()
        updateStreakUiState()
        loadLinkedAccounts()
        
        _uiState.update { 
             it.copy(
                 battlePlans = listOf(
                     BattlePlan("Spiritual Warfare", "Ephesians 6:11", "Put on the whole armor of God."),
                     BattlePlan("Daily Provision", "Philippians 4:19", "Trust in God's supply.")
                 )
             )
         }
    }

    private fun loadLinkedAccounts() {
        val isSpotify = prefs.getBoolean("is_spotify_linked", false)
        val spotifyName = prefs.getString("spotify_account_name", null)
        val isYoutube = prefs.getBoolean("is_youtube_linked", false)
        val youtubeName = prefs.getString("youtube_account_name", null)
        _uiState.update {
            it.copy(
                isSpotifyLinked = isSpotify,
                spotifyAccountName = spotifyName,
                isYoutubeLinked = isYoutube,
                youtubeAccountName = youtubeName
            )
        }
    }

    fun linkSpotify(accountName: String) {
        prefs.edit().apply {
            putBoolean("is_spotify_linked", true)
            putString("spotify_account_name", accountName)
            apply()
        }
        _uiState.update {
            it.copy(
                isSpotifyLinked = true,
                spotifyAccountName = accountName
            )
        }
    }

    fun linkYoutube(accountName: String) {
        prefs.edit().apply {
            putBoolean("is_youtube_linked", true)
            putString("youtube_account_name", accountName)
            apply()
        }
        _uiState.update {
            it.copy(
                isYoutubeLinked = true,
                youtubeAccountName = accountName
            )
        }
    }

    fun unlinkSpotify() {
        prefs.edit().apply {
            putBoolean("is_spotify_linked", false)
            remove("spotify_account_name")
            apply()
        }
        _uiState.update {
            it.copy(
                isSpotifyLinked = false,
                spotifyAccountName = null
            )
        }
    }

    fun unlinkYoutube() {
        prefs.edit().apply {
            putBoolean("is_youtube_linked", false)
            remove("youtube_account_name")
            apply()
        }
        _uiState.update {
            it.copy(
                isYoutubeLinked = false,
                youtubeAccountName = null
            )
        }
    }

    fun toggleSoundscape() {
        _uiState.update { it.copy(isSoundscapeEnabled = !it.isSoundscapeEnabled) }
    }

    fun setAlarmTime(time: String) {
        _uiState.update { it.copy(alarmTime = time) }
    }

    fun addPrayerRequest(text: String) {
        if (text.isBlank()) return
        val newRequest = PrayerRequest(text = text)
        _uiState.update { it.copy(prayerRequests = it.prayerRequests + newRequest) }
        
        // Remove after a while? Or just let it be? The request says "fading text nodes"
        // I will implement fading in the UI component, not in ViewModel.
    }

    private fun loadChurchConfig() {
        val selectedIndex = prefs.getInt("selected_church_index", 0)
        val isCustom = prefs.getBoolean("is_custom_church", false)
        val church = if (isCustom) {
            ChurchConfig(
                name = prefs.getString("custom_church_name", "My Local Church") ?: "My Local Church",
                location = prefs.getString("custom_church_location", "Local Community") ?: "Local Community",
                brandColorHex = prefs.getString("custom_church_color", "#F5C87A") ?: "#F5C87A",
                bgHex = prefs.getString("custom_church_bg", "#0F0A05") ?: "#0F0A05",
                denomination = prefs.getString("custom_church_denom", "Local Sanctuary") ?: "Local Sanctuary",
                logoEmoji = prefs.getString("custom_church_emoji", "⛪") ?: "⛪",
                welcomeMessage = prefs.getString("custom_church_message", "Welcome to your local custom sanctuary.") ?: "Welcome to your local custom sanctuary.",
                scriptureRef = prefs.getString("custom_church_scrip_ref", "Proverbs 3:5") ?: "Proverbs 3:5",
                scriptureText = prefs.getString("custom_church_scrip_text", "Trust in the Lord with all your heart, and lean not on your own understanding.") ?: "Trust in the Lord with all your heart, and lean not on your own understanding."
            )
        } else {
            preloadedChurches.getOrElse(selectedIndex) { preloadedChurches[0] }
        }
        _uiState.update { it.copy(selectedChurch = church) }
    }

    fun selectChurch(index: Int) {
        prefs.edit().apply {
            putInt("selected_church_index", index)
            putBoolean("is_custom_church", false)
            apply()
        }
        _uiState.update { it.copy(selectedChurch = preloadedChurches[index]) }
    }

    fun selectCustomChurch(
        name: String,
        location: String,
        brandColorHex: String,
        bgHex: String,
        denomination: String,
        emoji: String,
        welcomeMessage: String,
        scriptureRef: String,
        scriptureText: String
    ) {
        val custom = ChurchConfig(
            name = name,
            location = location,
            brandColorHex = brandColorHex,
            bgHex = bgHex,
            denomination = denomination,
            logoEmoji = emoji,
            welcomeMessage = welcomeMessage,
            scriptureRef = scriptureRef,
            scriptureText = scriptureText
        )
        prefs.edit().apply {
            putBoolean("is_custom_church", true)
            putString("custom_church_name", name)
            putString("custom_church_location", location)
            putString("custom_church_color", brandColorHex)
            putString("custom_church_bg", bgHex)
            putString("custom_church_denom", denomination)
            putString("custom_church_emoji", emoji)
            putString("custom_church_message", welcomeMessage)
            putString("custom_church_scrip_ref", scriptureRef)
            putString("custom_church_scrip_text", scriptureText)
            apply()
        }
        _uiState.update { it.copy(selectedChurch = custom) }
    }

    fun getDraftPrayer(): String {
        return prefs.getString("draft_prayer", "") ?: ""
    }

    fun saveDraftPrayer(text: String) {
        prefs.edit().putString("draft_prayer", text).apply()
    }

    fun submitDailyPrayer(title: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.insertDailyPrayer(
                DailyPrayerEntity(
                    title = if (title.isBlank()) "Daily Sanctuary Reflection" else title,
                    text = text,
                    churchNameAtTime = _uiState.value.selectedChurch.name
                )
            )
            saveDraftPrayer("") 
            incrementCovenantStreak()
            updateStreakUiState()
        }
    }

    fun deleteDailyPrayer(id: Long) {
        viewModelScope.launch {
            repository.deleteDailyPrayer(id)
        }
    }

    fun navigateTo(room: RoomView) {
        _uiState.update { it.copy(currentRoom = room) }
    }

    fun endCinematic() {
        _uiState.update { it.copy(cinematicActive = false) }
    }

    fun submitBurden(text: String) {
        if (text.isNotBlank()) {
            val brainOutput = SoulTieBrain.process(text)
            viewModelScope.launch {
                repository.insertBurden(
                    BurdenEntity(
                        text = text,
                        analyzedState = brainOutput.state.name
                    )
                )
                _uiState.update { it.copy(activeCounsel = brainOutput) }
            }
        }
    }

    fun clearCounsel() {
        _uiState.update { it.copy(activeCounsel = null) }
    }

    fun submitSoap(scripture: String, observation: String, application: String, prayer: String) {
        viewModelScope.launch {
            repository.insertSoap(
                SoapEntity(
                    scripture = scripture,
                    observation = observation,
                    application = application,
                    prayer = prayer
                )
            )
            incrementCovenantStreak()
            updateStreakUiState()
        }
    }

    fun deleteBurden(id: Long) {
        viewModelScope.launch {
            repository.deleteBurden(id)
        }
    }

    fun deleteSoap(id: Long) {
        viewModelScope.launch {
            repository.deleteSoap(id)
        }
    }

    private fun evaluateQuietState() {
        val quietBrainState = SoulTieBrain.process("")
        _uiState.update { it.copy(activeCounsel = quietBrainState) }
    }

    fun markBurdenAsAnswered(id: Long, testimony: String) {
        viewModelScope.launch {
            val burden = _uiState.value.burdens.find { it.id == id }
            if (burden != null) {
                repository.insertBurden(
                    burden.copy(
                        isAnswered = true,
                        testimony = testimony,
                        timestamp = System.currentTimeMillis()
                    )
                )
                incrementCovenantStreak()
                updateStreakUiState()
            }
        }
    }

    private fun incrementCovenantStreak() {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val todayStr = sdf.format(Date())
        val lastActiveStr = prefs.getString("last_active_date_str", "") ?: ""

        if (lastActiveStr == todayStr) {
            return
        }

        val streak = prefs.getInt("covenant_streak_count", 0)
        var newStreak = 1

        if (lastActiveStr.isNotEmpty()) {
            try {
                val lastDate = sdf.parse(lastActiveStr)
                val todayDate = sdf.parse(todayStr)
                if (lastDate != null && todayDate != null) {
                    val diffMs = todayDate.time - lastDate.time
                    val diffDays = diffMs / (1000 * 60 * 60 * 24)

                    if (diffDays == 1L) {
                        newStreak = streak + 1
                    } else if (diffDays > 1L) {
                        newStreak = 1
                    } else {
                        newStreak = streak
                    }
                }
            } catch (e: Exception) {
                newStreak = 1
            }
        } else {
            newStreak = 1
        }

        prefs.edit().apply {
            putInt("covenant_streak_count", newStreak)
            putString("last_active_date_str", todayStr)
            apply()
        }
    }

    private fun updateStreakUiState() {
        val streak = prefs.getInt("covenant_streak_count", 0)
        _uiState.update { it.copy(covenantStreak = streak) }
    }

    fun submitStickyNote(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.insertStickyNote(StickyNoteEntity(text = text))
        }
    }

    fun deleteStickyNote(id: Long) {
        viewModelScope.launch {
            repository.deleteStickyNote(id)
        }
    }
}
