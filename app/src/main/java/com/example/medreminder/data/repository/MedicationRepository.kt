package com.example.medreminder.data.repository

import com.example.medreminder.data.database.dao.MedicationDao
import com.example.medreminder.data.database.dao.MedicationLogDao
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.data.database.entity.MedicationLog
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationRepository @Inject constructor(
    private val medicationDao: MedicationDao,
    private val logDao: MedicationLogDao
) {
    fun getAllActiveMedications(): Flow<List<Medication>> = medicationDao.getAllActive()

    fun getAllMedications(): Flow<List<Medication>> = medicationDao.getAll()

    suspend fun getAllActiveMedicationsOnce(): List<Medication> = medicationDao.getAllActiveOnce()

    suspend fun getTodayLogsOnce(): List<MedicationLog> {
        // Return the full current week (Mon–Sun) — needed for rolling medication taken-check
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dow == Calendar.SUNDAY) 6 else dow - Calendar.MONDAY
        cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val weekStart = cal.timeInMillis
        val weekEnd = weekStart + 7 * 24 * 60 * 60 * 1000L
        return logDao.getLogsForDayOnce(weekStart, weekEnd)
    }

    suspend fun getMedicationById(id: Long): Medication? = medicationDao.getById(id)

    suspend fun saveMedication(medication: Medication): Long = medicationDao.insert(medication)

    suspend fun updateMedication(medication: Medication) = medicationDao.update(medication)

    suspend fun deleteMedication(id: Long) {
        logDao.deleteByMedication(id)
        medicationDao.hardDelete(id)
    }

    suspend fun disableMedication(id: Long) = medicationDao.softDelete(id)

    suspend fun setMedicationEnabled(id: Long, enabled: Boolean) = medicationDao.setEnabled(id, enabled)

    fun getTodayLogs(): Flow<List<MedicationLog>> {
        // Return the full current week (Mon–Sun) so rolling medications
        // can see rescheduled entries from earlier in the week
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dow == Calendar.SUNDAY) 6 else dow - Calendar.MONDAY
        cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val weekStart = cal.timeInMillis
        val weekEnd = weekStart + 7 * 24 * 60 * 60 * 1000L
        return logDao.getLogsForDay(weekStart, weekEnd)
    }

    suspend fun getOldestLogTime(): Long? = logDao.getOldestLogTime()

    suspend fun deleteLogsForMedication(medId: Long) = logDao.deleteByMedication(medId)

    suspend fun purgeOldLogs() {
        val cutoff = System.currentTimeMillis() - 28 * 24 * 60 * 60 * 1000L
        logDao.deleteLogsBefore(cutoff)
    }

    fun getLogsForDateRange(from: Long, to: Long): Flow<List<MedicationLog>> =
        logDao.getLogsForRange(from, to)

    suspend fun markLog(medicationId: Long, scheduledTime: Long, status: String) {
        val existing = logDao.getByMedAndTime(medicationId, scheduledTime)
        if (existing != null) {
            logDao.update(existing.copy(status = status, takenTime = if (status == "taken") System.currentTimeMillis() else null))
        } else {
            logDao.insert(MedicationLog(
                medicationId = medicationId,
                scheduledTime = scheduledTime,
                takenTime = if (status == "taken") System.currentTimeMillis() else null,
                status = status
            ))
        }
    }
}
