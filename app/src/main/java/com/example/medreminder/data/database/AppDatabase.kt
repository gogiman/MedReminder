package com.example.medreminder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.medreminder.data.database.dao.MedicationDao
import com.example.medreminder.data.database.dao.MedicationLogDao
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.data.database.entity.MedicationLog

@Database(
    entities = [Medication::class, MedicationLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun medicationLogDao(): MedicationLogDao
}
