package com.example.medreminder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.medreminder.MainActivity
import com.example.medreminder.R
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.data.preferences.SettingsRepository

object NotificationHelper {
    const val CHANNEL_NAME = "Medication Reminders"
    const val REPEAT_INTERVAL_MS = 5 * 60 * 1000L

    fun createNotificationChannel(context: Context, settingsRepository: SettingsRepository) {
        val soundUri = settingsRepository.getSoundUri()
        val channelId = settingsRepository.getChannelId()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val channel = NotificationChannel(
            channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders to take your medications"
            enableVibration(true)
            if (soundUri != Uri.EMPTY) setSound(soundUri, audioAttributes)
            else setSound(null, null)
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun showGroupedNotification(
        context: Context,
        notificationId: Int,
        timeSlot: String,
        medications: List<Medication>,
        settingsRepository: SettingsRepository
    ) {
        val channelId = settingsRepository.getChannelId()
        // Ensure channel exists with current settings
        createNotificationChannel(context, settingsRepository)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPi = PendingIntent.getActivity(
            context, notificationId, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val confirmIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_CONFIRM_ALL
            putExtra(NotificationActionReceiver.EXTRA_TIME_SLOT, timeSlot)
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(NotificationActionReceiver.EXTRA_MED_IDS, medications.map { it.id }.toLongArray())
        }
        val confirmPi = PendingIntent.getBroadcast(
            context, notificationId * 10 + 1, confirmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SNOOZE_30
            putExtra(NotificationActionReceiver.EXTRA_TIME_SLOT, timeSlot)
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(NotificationActionReceiver.EXTRA_MED_IDS, medications.map { it.id }.toLongArray())
        }
        val snoozePi = PendingIntent.getBroadcast(
            context, notificationId * 10 + 2, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val medCount = medications.size
        val title = if (medCount == 1) "Time to take ${medications[0].name}" else "Time to take $medCount medications"
        val bigText = medications.joinToString("\n") { "• ${it.name}  ${it.dosage} ${it.unit}" }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_medication)
            .setContentTitle(title)
            .setContentText(if (medCount == 1) "${medications[0].dosage} ${medications[0].unit}" else medications.joinToString(", ") { it.name })
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(openAppPi)
            .setAutoCancel(true)
            .setOngoing(false)
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .addAction(R.drawable.ic_medication, "✓ Confirm All", confirmPi)
            .addAction(R.drawable.ic_medication, "⏰ 30 min later", snoozePi)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(notificationId, notification)
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        context.getSystemService(NotificationManager::class.java).cancel(notificationId)
    }
}

