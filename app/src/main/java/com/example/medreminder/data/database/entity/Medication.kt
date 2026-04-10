package com.example.medreminder.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String,
    val unit: String,          // e.g. "mg", "ml", "tablet(s)"
    val frequencyType: String, // "daily", "weekly"
    val reminderTimes: String, // comma-separated HH:mm strings e.g. "08:00,20:00"
    val daysOfWeek: String = "1,2,3,4,5,6,7", // comma-separated 1=Mon..7=Sun (used for weekly)
    val isActive: Boolean = true,
    val notes: String = "",
    val color: Int = 0         // index into a palette
)
