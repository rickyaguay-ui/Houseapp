package com.example.ui.data

data class PrayerRequest(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
