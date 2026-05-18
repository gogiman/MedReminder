package com.example.medreminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medreminder.data.preferences.SettingsRepository
import com.example.medreminder.data.repository.MedicationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: MedicationRepository
    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val timeSlot = intent.getStringExtra(EXTRA_TIME_SLOT) ?: return
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        val medIds = intent.getLongArrayExtra(EXTRA_MED_IDS) ?: return
        val scheduledTime = timeSlotToTodayMillis(timeSlot)

        val alarmManager = context.getSystemService(AlarmManager::class.java)

        // Cancel the 5-minute repeat alarm
        val repeatIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TIME_SLOT, timeSlot)
        }
        val repeatPi = PendingIntent.getBroadcast(
            context, notificationId + AlarmReceiver.REPEAT_REQUEST_OFFSET, repeatIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(repeatPi)

        NotificationHelper.cancelNotification(context, notificationId)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (action) {
                    ACTION_CONFIRM_ALL -> {
                        medIds.forEach { medId ->
                            repository.markLog(medId, scheduledTime, "taken")
                            settingsRepository.clearSnooze(medId, scheduledTime)
                        }
                    }
                    ACTION_SNOOZE_30 -> {
                        val triggerAt = System.currentTimeMillis() + 30 * 60 * 1000L
                        medIds.forEach { medId ->
                            settingsRepository.setSnooze(medId, scheduledTime, triggerAt)
                        }
                        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
                            putExtra(AlarmReceiver.EXTRA_TIME_SLOT, timeSlot)
                        }
                        val snoozePi = PendingIntent.getBroadcast(
                            context, notificationId + SNOOZE_REQUEST_OFFSET, snoozeIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, snoozePi)
                        } else {
                            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, snoozePi)
                        }
                    }
                    ACTION_RESCHEDULE_NEXT_DAY -> {
                        medIds.forEach { medId ->
                            repository.markLog(medId, scheduledTime, "rescheduled")
                            settingsRepository.clearSnooze(medId, scheduledTime)
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun timeSlotToTodayMillis(timeSlot: String): Long {
        val parts = timeSlot.split(":")
        val hour = parts[0].toIntOrNull() ?: 0
        val minute = parts[1].toIntOrNull() ?: 0
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    companion object {
        const val ACTION_CONFIRM_ALL = "com.example.medreminder.ACTION_CONFIRM_ALL"
        const val ACTION_SNOOZE_30 = "com.example.medreminder.ACTION_SNOOZE_30"
        const val ACTION_RESCHEDULE_NEXT_DAY = "com.example.medreminder.ACTION_RESCHEDULE_NEXT_DAY"
        const val EXTRA_TIME_SLOT = "time_slot"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_MED_IDS = "med_ids"
        const val SNOOZE_REQUEST_OFFSET = 60000
    }
}
