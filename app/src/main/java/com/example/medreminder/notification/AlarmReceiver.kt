package com.example.medreminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medreminder.data.preferences.SettingsRepository
import com.example.medreminder.data.repository.MedicationRepository
import com.example.medreminder.util.DayTimesUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: MedicationRepository
    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        val timeSlot = intent.getStringExtra(EXTRA_TIME_SLOT) ?: return
        val notificationId = timeSlot.filter { it.isDigit() }.toIntOrNull() ?: timeSlot.hashCode()

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cal = Calendar.getInstance()
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK).let { if (it == 1) 7 else it - 1 }

                val parts = timeSlot.split(":")
                val hour = parts[0].toIntOrNull() ?: 0
                val minute = parts[1].toIntOrNull() ?: 0
                val scheduledMillis = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val allMeds = repository.getAllActiveMedicationsOnce()
                val todayLogs = repository.getTodayLogsOnce()

                val medsToNotify = allMeds.filter { med ->
                    // Must include this time slot
                    if (!med.reminderTimes.split(",").map { it.trim() }.contains(timeSlot))
                        return@filter false

                    // Weekly: only notify on configured days
                    if (med.frequencyType == "weekly") {
                        val activeDays = med.daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }
                        if (!activeDays.contains(dayOfWeek)) return@filter false
                        // Per-day time check: if this med has per-day times, verify today includes this slot
                        if (med.dayReminderTimes.isNotEmpty()) {
                            val entries = DayTimesUtil.parseEntries(med.dayReminderTimes)
                            val dayTimes = DayTimesUtil.getTimesForDay(entries, dayOfWeek)
                            if (dayTimes.isNotEmpty() && !dayTimes.contains(timeSlot)) return@filter false
                        }
                    }

                    // Rolling: skip if already taken this week
                    if (med.frequencyType == "rolling") {
                        if (todayLogs.any { it.medicationId == med.id && it.status == "taken" })
                            return@filter false
                    }

                    // All types: skip if already taken at this exact scheduled time
                    if (todayLogs.any { it.medicationId == med.id && it.scheduledTime == scheduledMillis && it.status == "taken" })
                        return@filter false

                    // Skip if snoozed past this firing time (manual reschedule was applied)
                    val snoozeUntil = settingsRepository.getSnooze(med.id, scheduledMillis)
                    if (snoozeUntil > System.currentTimeMillis()) return@filter false

                    true
                }

                if (medsToNotify.isNotEmpty()) {
                    NotificationHelper.showGroupedNotification(context, notificationId, timeSlot, medsToNotify, settingsRepository)
                    scheduleRepeat(context, timeSlot, notificationId)
                }
                // Always reschedule next day's occurrence
                scheduleNextDay(context, timeSlot)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun scheduleExact(alarmManager: AlarmManager, triggerAt: Long, pi: PendingIntent) {
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    private fun scheduleRepeat(context: Context, timeSlot: String, notificationId: Int) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val repeatIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_TIME_SLOT, timeSlot)
        }
        val pi = PendingIntent.getBroadcast(
            context, notificationId + REPEAT_REQUEST_OFFSET, repeatIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        scheduleExact(alarmManager, System.currentTimeMillis() + NotificationHelper.REPEAT_INTERVAL_MS, pi)
    }

    private fun scheduleNextDay(context: Context, timeSlot: String) {
        val parts = timeSlot.split(":")
        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return
        val triggerAt = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val requestCode = timeSlot.filter { it.isDigit() }.toIntOrNull() ?: timeSlot.hashCode()
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_TIME_SLOT, timeSlot)
        }
        val pi = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        scheduleExact(alarmManager, triggerAt, pi)
    }

    companion object {
        const val EXTRA_TIME_SLOT = "time_slot"
        const val REPEAT_REQUEST_OFFSET = 50000
    }
}