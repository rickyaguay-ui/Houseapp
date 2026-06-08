package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val Gold = Color(0xFFF5C87A)
val BackgroundColor = Color(0xFF0F0A05)
val CardColor = Color(0xFF1A120B)
val TextColor = Color(0xFFF5E8C7)
val SecondaryText = Color(0xFF8F8474)
val DarkBorder = Color(0xFF2A1F12)

// Premium Ambient Accent Colors mapped to SpiritualStates
val RestBlue = Color(0xFF1A2F3B)
val PeaceTeal = Color(0xFF143026)
val ForgivenessCrimson = Color(0xFF381515)
val JoyAmber = Color(0xFF3F2B10)

fun parseColorHex(hex: String, fallback: Color = Gold): Color {
    return try {
        val cleanHex = hex.trim().replace("#", "").replace("0x", "")
        if (cleanHex.length == 6) {
            Color(android.graphics.Color.parseColor("#$cleanHex"))
        } else if (cleanHex.length == 8) {
            Color(android.graphics.Color.parseColor("#$cleanHex"))
        } else {
            fallback
        }
    } catch (e: Exception) {
        fallback
    }
}
