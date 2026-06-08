package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "burdens")
data class BurdenEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val analyzedState: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAnswered: Boolean = false,
    val testimony: String? = null
)
