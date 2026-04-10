package com.example.medreminder.data.preferences

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("med_reminder_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SOUND_URI = "notification_sound_uri"
        private const val KEY_CHANNEL_VERSION = "notification_channel_version"
        val DEFAULT_SOUND_URI: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }

    fun getSoundUri(): Uri {
        val stored = prefs.getString(KEY_SOUND_URI, null)
        return if (stored != null) Uri.parse(stored) else DEFAULT_SOUND_URI
    }

    fun setSoundUri(uri: Uri?) {
        val newVersion = getChannelVersion() + 1
        prefs.edit()
            .putString(KEY_SOUND_URI, uri?.toString())
            .putInt(KEY_CHANNEL_VERSION, newVersion)
            .apply()
    }

    fun getChannelVersion(): Int = prefs.getInt(KEY_CHANNEL_VERSION, 0)

    fun getChannelId(): String = "med_reminder_channel_v${getChannelVersion()}"

    fun setSnooze(medId: Long, scheduledTime: Long, snoozedUntil: Long) {
        prefs.edit().putLong("snooze_${medId}_$scheduledTime", snoozedUntil).apply()
    }

    fun getSnooze(medId: Long, scheduledTime: Long): Long =
        prefs.getLong("snooze_${medId}_$scheduledTime", 0L)

    fun clearSnooze(medId: Long, scheduledTime: Long) {
        prefs.edit().remove("snooze_${medId}_$scheduledTime").apply()
    }

    fun clearSnoozesForMeds(medIds: LongArray, scheduledTime: Long) {
        prefs.edit().also { ed -> medIds.forEach { ed.remove("snooze_${it}_$scheduledTime") } }.apply()
    }

    fun bumpChannelVersion() {
        prefs.edit().putInt(KEY_CHANNEL_VERSION, getChannelVersion() + 1).apply()
    }

    fun registerOnChangeListener(listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnChangeListener(listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
