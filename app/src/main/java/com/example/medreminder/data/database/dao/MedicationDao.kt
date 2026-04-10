package com.example.medreminder.data.database.dao

import androidx.room.*
import com.example.medreminder.data.database.entity.Medication
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<Medication>>

    @Query("SELECT * FROM medications ORDER BY name ASC")
    fun getAll(): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE isActive = 1 ORDER BY name ASC")
    suspend fun getAllActiveOnce(): List<Medication>

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getById(id: Long): Medication?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medication: Medication): Long

    @Update
    suspend fun update(medication: Medication)

    @Query("UPDATE medications SET isActive = 0 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("DELETE FROM medications WHERE id = :id")
    suspend fun hardDelete(id: Long)

    @Query("UPDATE medications SET isActive = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)
}
