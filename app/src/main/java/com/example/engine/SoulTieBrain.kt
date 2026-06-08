package com.example.engine

import java.util.Calendar

enum class SpiritualState(val icon: String, val label: String) {
    REST("🕊️", "Weariness"),
    PEACE("🛡️", "Anxiety"),
    FORGIVENESS("🩸", "Guilt"),
    JOY("🔥", "Gratitude"),
    NIGHT_PEACE("🌙", "Night Watch"),
    SILENT("🕯️", "Stillness")
}

data class BrainOutput(
    val state: SpiritualState,
    val counsel: String,
    val scripture: String,
    val scriptureRef: String,
    val recommendation: String
)

object SoulTieBrain {
    
    fun process(text: String): BrainOutput {
        val input = text.lowercase().trim()
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        return when {
            // Check for Weariness / Burnout
            input.containsAny("tired", "dragging", "weary", "exhausted", "burnout", "heavy", "worn out", "sleepy", "draining", "fatigued", "weak") -> {
                BrainOutput(
                    state = SpiritualState.REST,
                    counsel = "You were never designed to be the source of your own fuel. Rest is not a luxury; it is a declaration of trust in the hands that uphold the universe. Lean back.",
                    scripture = "Come to Me, all who are weary and heavy-laden, and I will give you rest. Take My yoke upon you and learn from Me, for I am gentle and humble in heart, and you will find rest for your souls.",
                    scriptureRef = "Matthew 11:28-29",
                    recommendation = "Go to the Wind-Down Room and breathe with the holy rhythm."
                )
            }
            // Check for Anxiety / Fear
            input.containsAny("anxious", "scared", "afraid", "fear", "overwhelmed", "worry", "panicking", "stress", "stressed", "terror", "uncertain") -> {
                BrainOutput(
                    state = SpiritualState.PEACE,
                    counsel = "The future does not belong to your fears. It belongs to the Father, who is already standing there waiting for you. Release your anxious calculations and breathe.",
                    scripture = "Do not be anxious about anything, but in everything by prayer and pleading with thanksgiving let your requests be made known to God. And the peace of God, which surpasses all comprehension, will guard your hearts and minds in Christ Jesus.",
                    scriptureRef = "Philippians 4:6-7",
                    recommendation = "Surrender this thought to the Mercy Seat. Watch it lift into the Sticky Notes Hall."
                )
            }
            // Check for Guilt / Shame
            input.containsAny("guilt", "shame", "failed", "sinned", "wrong", "screwed up", "condemned", "bad", "regret", "disappointed", "unclean") -> {
                BrainOutput(
                    state = SpiritualState.FORGIVENESS,
                    counsel = "The court is empty. No accusations remain. The blood was applied, justice is completely satisfied, and your standing is perfectly secure in His righteousness.",
                    scripture = "Therefore there is now no condemnation for those who are in Christ Jesus. For the law of the Spirit of life in Christ Jesus has set you free from the law of sin and of death.",
                    scriptureRef = "Romans 8:1-2",
                    recommendation = "Look upon the golden cross in the Worship sanctuary."
                )
            }
            // Check for Thanksgiving / Praise
            input.containsAny("thank", "grateful", "blessed", "goodness", "praise", "happy", "joy", "amazing", "victorious", "thankful", "worship") -> {
                BrainOutput(
                    state = SpiritualState.JOY,
                    counsel = "Your thanksgiving builds an altar in this room. Let this memory of His faithfulness be a monument you look back on in darker valleys.",
                    scripture = "Bless the Lord, my soul, and all that is within me, bless His holy name. Bless the Lord, my soul, and do not forget any of His benefits.",
                    scriptureRef = "Psalm 103:1-2",
                    recommendation = "Pin this memory to the Sticky Notes Hall as a legacy of praise."
                )
            }
            // Temporal context: Late Night (8 PM to 4 AM)
            (currentHour >= 20 || currentHour <= 4) -> {
                BrainOutput(
                    state = SpiritualState.NIGHT_PEACE,
                    counsel = "The daylight is gone, and so is its labor. He remains awake keeping watch over your spirit. Close your eyes and lean back into his finished work.",
                    scripture = "I will both lie down in peace and sleep, for You alone, Lord, make me dwell in safety.",
                    scriptureRef = "Psalm 4:8",
                    recommendation = "Initiate the deep-rest breathing sequence in the Wind-Down Room."
                )
            }
            // Default Stillness
            else -> {
                BrainOutput(
                    state = SpiritualState.SILENT,
                    counsel = "You do not need many words to be understood here. He understands your silence. Rest inside His deep, quiet, and satisfying presence.",
                    scripture = "Be still, and know that I am God; I will be exalted among the nations, I will be exalted in the earth.",
                    scriptureRef = "Psalm 46:10",
                    recommendation = "Open a S.O.A.P Reflection and silently map your thoughts."
                )
            }
        }
    }

    private fun String.containsAny(vararg keywords: String): Boolean {
        return keywords.any { this.contains(it) }
    }
}
