package com.example.medreminder

import android.app.Application
import android.app.NotificationManager
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.data.preferences.SettingsRepository
import com.example.medreminder.util.DayTimesUtil
import com.example.medreminder.data.repository.MedicationRepository
import com.example.medreminder.notification.AlarmScheduler
import com.example.medreminder.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltAndroidApp
class MedReminderApp : Application() {
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var repository: MedicationRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onCreate() {
        super.onCreate()
        ensureNotificationChannelHasSound()
        rescheduleAndNotifyMissed()
    }

    private fun ensureNotificationChannelHasSound() {
        val nm = getSystemService(NotificationManager::class.java)
        val channelId = settingsRepository.getChannelId()
        val existing = nm.getNotificationChannel(channelId)
        if (existing != null && existing.sound == null) {
            nm.deleteNotificationChannel(channelId)
            settingsRepository.bumpChannelVersion()
        }
        NotificationHelper.createNotificationChannel(this, settingsRepository)
    }

    private fun rescheduleAndNotifyMissed() {
        MainScope().launch {
            val medications = repository.getAllActiveMedicationsOnce()
            val weekLogs = repository.getTodayLogsOnce() // full Mon-Sun week
            val now = System.currentTimeMillis()
            val todayCal = Calendar.getInstance()
            val dayOfWeek = todayCal.get(Calendar.DAY_OF_WEEK).let { if (it == 1) 7 else it - 1 }

            val missedBySlot = mutableMapOf<String, MutableList<Medication>>()

            medications.forEach { med ->
                // Reschedule future alarms regardless
                alarmScheduler.scheduleAlarms(med)

                // Weekly: only check today's configured days
                if (med.frequencyType == "weekly") {
                    val activeDays = med.daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }
                    if (!activeDays.contains(dayOfWeek)) return@forEach
                }

                // Rolling: apply effective-day logic — skip if rescheduled past today
                if (med.frequencyType == "rolling") {
                    if (weekLogs.any { it.medicationId == med.id && it.status == "taken" })
                        return@forEach
                    val windowDays = med.daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }.sorted()
                    if (!windowDays.contains(dayOfWeek)) return@forEach
                    val rescheduledDays = weekLogs
                        .filter { it.medicationId == med.id && it.status == "rescheduled" }
                        .mapNotNull { log ->
                            val c = Calendar.getInstance().apply { timeInMillis = log.scheduledTime }
                            c.get(Calendar.DAY_OF_WEEK).let { if (it == 1) 7 else it - 1 }
                        }.toSet()
                    val effectiveDay = windowDays.firstOrNull { it !in rescheduledDays }
                    if (effectiveDay == null || effectiveDay > dayOfWeek) return@forEach
                }

                med.reminderTimes.split(",").map { it.trim() }.forEach { time ->
                    // Weekly with per-day times: skip this time slot if not valid for today
                    if (med.frequencyType == "weekly" && med.dayReminderTimes.isNotEmpty()) {
                        val entries = DayTimesUtil.parseEntries(med.dayReminderTimes)
                        val dayTimes = DayTimesUtil.getTimesForDay(entries, dayOfWeek)
                        if (dayTimes.isNotEmpty() && !dayTimes.contains(time)) return@forEach
                    }

                    val parts = time.split(":")
                    val hour = parts[0].toIntOrNull() ?: return@forEach
                    val minute = parts[1].toIntOrNull() ?: return@forEach
                    val scheduledMillis = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    // Only alert for slots that already passed today
                    if (scheduledMillis >= now) return@forEach

                    // Check if snoozed
                    val snoozeUntil = settingsRepository.getSnooze(med.id, scheduledMillis)
                    if (snoozeUntil > now) return@forEach

                    // For daily/weekly: check today's exact log entry
                    // For rolling: already handled above (week-level taken check)
                    val alreadyHandled = weekLogs.any { log ->
                        log.medicationId == med.id &&
                        log.scheduledTime == scheduledMillis &&
                        (log.status == "taken" || log.status == "skipped")
                    }
                    if (!alreadyHandled) {
                        missedBySlot.getOrPut(time) { mutableListOf() }.add(med)
                    }
                }
            }

            missedBySlot.forEach { (timeSlot, meds) ->
                val notificationId = timeSlot.filter { it.isDigit() }.toIntOrNull() ?: timeSlot.hashCode()
                NotificationHelper.showGroupedNotification(this@MedReminderApp, notificationId, timeSlot, meds, settingsRepository)
            }
        }
    }
}