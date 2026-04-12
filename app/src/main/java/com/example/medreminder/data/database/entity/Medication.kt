package com.example.medreminder.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String,
    val unit: String,
    val frequencyType: String,
    val reminderTimes: String,  // union of all times (comma-separated HH:mm), used for alarm scheduling
    val daysOfWeek: String = "1,2,3,4,5,6,7",
    val dayReminderTimes: String = "", // JSON {"1":"09:00","2":"10:00,21:00"} — weekly per-day times
    val isActive: Boolean = true,
    val notes: String = "",
    val color: Int = 0
)
