package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soap_reflections")
data class SoapEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scripture: String,
    val observation: String,
    val application: String,
    val prayer: String,
    val timestamp: Long = System.currentTimeMillis()
)
