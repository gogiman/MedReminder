package com.example.medreminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.medreminder.data.database.entity.Medication
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(private val context: Context) {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    private fun scheduleExact(triggerAt: Long, pi: PendingIntent) {
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    fun scheduleAlarms(medication: Medication) {
        if (!medication.isActive) return
        val times = medication.reminderTimes.split(",").map { it.trim() }
        times.forEach { time ->
            val parts = time.split(":")
            if (parts.size != 2) return@forEach
            val hour = parts[0].toIntOrNull() ?: return@forEach
            val minute = parts[1].toIntOrNull() ?: return@forEach
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }
            val requestCode = time.filter { it.isDigit() }.toIntOrNull() ?: time.hashCode()
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra(AlarmReceiver.EXTRA_TIME_SLOT, time)
            }
            val pi = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            scheduleExact(cal.timeInMillis, pi)
        }
    }

    fun cancelAlarms(medication: Medication) {
        val times = medication.reminderTimes.split(",").map { it.trim() }
        times.forEach { time ->
            val notificationId = time.filter { it.isDigit() }.toIntOrNull() ?: time.hashCode()
            // Cancel primary daily alarm
            cancelPendingIntent(notificationId)
            // Cancel repeat alarm (id + 50000)
            cancelPendingIntent(notificationId + AlarmReceiver.REPEAT_REQUEST_OFFSET)
            // Cancel notification snooze alarm (id + 60000)
            cancelPendingIntent(notificationId + NOTIFICATION_SNOOZE_REQUEST_OFFSET)
            // Cancel manual snooze alarm (id + 70000)
            cancelPendingIntent(notificationId + MANUAL_SNOOZE_REQUEST_OFFSET)
        }
    }

    private fun cancelPendingIntent(requestCode: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pi != null) alarmManager.cancel(pi)
    }

    fun scheduleManualSnooze(timeSlot: String, triggerAtMillis: Long) {
        val notificationId = timeSlot.filter { it.isDigit() }.toIntOrNull() ?: timeSlot.hashCode()
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TIME_SLOT, timeSlot)
        }
        val pi = PendingIntent.getBroadcast(
            context, notificationId + MANUAL_SNOOZE_REQUEST_OFFSET, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        scheduleExact(triggerAtMillis, pi)
    }

    fun cancelRepeatAlarm(timeSlot: String) {
        val notificationId = timeSlot.filter { it.isDigit() }.toIntOrNull() ?: timeSlot.hashCode()
        val intent = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, notificationId + AlarmReceiver.REPEAT_REQUEST_OFFSET, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pi != null) alarmManager.cancel(pi)
    }

    /** Cancel the primary daily alarm for a time slot (e.g. before rescheduling manually). */
    fun cancelTimeSlotAlarm(timeSlot: String) {
        val requestCode = timeSlot.filter { it.isDigit() }.toIntOrNull() ?: timeSlot.hashCode()
        val intent = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pi != null) alarmManager.cancel(pi)
    }

    companion object {
        const val NOTIFICATION_SNOOZE_REQUEST_OFFSET = 60000
        const val MANUAL_SNOOZE_REQUEST_OFFSET = 70000
    }
}