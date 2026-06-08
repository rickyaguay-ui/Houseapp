package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_prayers")
data class DailyPrayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val title: String,
    val churchNameAtTime: String,
    val timestamp: Long = System.currentTimeMillis()
)
