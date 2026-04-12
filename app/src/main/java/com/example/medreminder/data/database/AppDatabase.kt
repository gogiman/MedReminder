package com.example.medreminder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.medreminder.data.database.dao.MedicationDao
import com.example.medreminder.data.database.dao.MedicationLogDao
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.data.database.entity.MedicationLog

@Database(
    entities = [Medication::class, MedicationLog::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun medicationLogDao(): MedicationLogDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE medications ADD COLUMN dayReminderTimes TEXT NOT NULL DEFAULT ''"
                )
            }
        }
    }
}
