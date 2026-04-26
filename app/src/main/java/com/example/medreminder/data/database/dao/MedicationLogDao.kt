package com.example.medreminder.data.database.dao

import androidx.room.*
import com.example.medreminder.data.database.entity.MedicationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationLogDao {
    @Query("""
        SELECT * FROM medication_logs 
        WHERE scheduledTime >= :startOfDay AND scheduledTime < :endOfDay
        ORDER BY scheduledTime ASC
    """)
    fun getLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<MedicationLog>>

    @Query("SELECT MIN(scheduledTime) FROM medication_logs")
    suspend fun getOldestLogTime(): Long?

    @Query("DELETE FROM medication_logs WHERE scheduledTime < :before")
    suspend fun deleteLogsBefore(before: Long)

    @Query("SELECT * FROM medication_logs WHERE medicationId = :medId AND scheduledTime = :scheduledTime LIMIT 1")
    suspend fun getByMedAndTime(medId: Long, scheduledTime: Long): MedicationLog?

    @Query("SELECT * FROM medication_logs WHERE scheduledTime >= :startOfDay AND scheduledTime < :endOfDay")
    suspend fun getLogsForDayOnce(startOfDay: Long, endOfDay: Long): List<MedicationLog>

    @Query("SELECT * FROM medication_logs WHERE scheduledTime >= :from AND scheduledTime < :to ORDER BY scheduledTime ASC")
    fun getLogsForRange(from: Long, to: Long): Flow<List<MedicationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: MedicationLog): Long

    @Update
    suspend fun update(log: MedicationLog)

    @Query("DELETE FROM medication_logs WHERE medicationId = :medId")
    suspend fun deleteByMedication(medId: Long)
}
