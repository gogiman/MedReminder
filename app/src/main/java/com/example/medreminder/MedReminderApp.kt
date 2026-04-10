package com.example.medreminder

import android.app.Application
import android.app.NotificationManager
import com.example.medreminder.data.preferences.SettingsRepository
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
            val todayLogs = repository.getTodayLogsOnce()
            val now = System.currentTimeMillis()

            val missedBySlot = mutableMapOf<String, MutableList<com.example.medreminder.data.database.entity.Medication>>()

            medications.forEach { med ->
                alarmScheduler.scheduleAlarms(med)

                med.reminderTimes.split(",").map { it.trim() }.forEach { time ->
                    val parts = time.split(":")
                    val hour = parts[0].toIntOrNull() ?: return@forEach
                    val minute = parts[1].toIntOrNull() ?: return@forEach
                    val scheduledMillis = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    if (scheduledMillis < now) {
                        val log = todayLogs.find { it.medicationId == med.id && it.scheduledTime == scheduledMillis }
                        if (log == null || log.status == "pending") {
                            missedBySlot.getOrPut(time) { mutableListOf() }.add(med)
                        }
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