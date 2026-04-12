package com.example.medreminder.ui.home

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.data.database.entity.MedicationLog
import com.example.medreminder.data.preferences.SettingsRepository
import com.example.medreminder.data.repository.MedicationRepository
import com.example.medreminder.notification.AlarmScheduler
import com.example.medreminder.notification.NotificationHelper
import com.example.medreminder.util.DayTimesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class ScheduleItem(
    val medication: Medication,
    val scheduledTime: Long,
    val log: MedicationLog?,
    val snoozedUntil: Long? = null
) {
    val timeLabel: String get() {
        val millis = snoozedUntil?.takeIf { it > System.currentTimeMillis() } ?: scheduledTime
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }
    val status: String get() = log?.status ?: "pending"

    val canReschedule: Boolean get() {
        if (medication.frequencyType != "rolling") return false
        val windowDays = medication.daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }.sorted()
        val cal = Calendar.getInstance().apply { timeInMillis = scheduledTime }
        val dow = cal.get(Calendar.DAY_OF_WEEK).let { if (it == 1) 7 else it - 1 }
        return windowDays.last() != dow
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MedicationRepository,
    private val alarmScheduler: AlarmScheduler,
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _medications = repository.getAllActiveMedications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _todayLogs = repository.getTodayLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _snoozeRefresh = MutableStateFlow(0L)

    // Auto-refresh when SharedPreferences snooze keys change (e.g. from notification action)
    private val snoozeChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key?.startsWith("snooze_") == true) {
            _snoozeRefresh.value = System.currentTimeMillis()
        }
    }

    init {
        settingsRepository.registerOnChangeListener(snoozeChangeListener)
    }

    override fun onCleared() {
        super.onCleared()
        settingsRepository.unregisterOnChangeListener(snoozeChangeListener)
    }

    val scheduleItems: StateFlow<List<ScheduleItem>> = combine(_medications, _todayLogs, _snoozeRefresh) { meds, logs, _ ->
        buildTodaySchedule(meds, logs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshSnoozeTimes() {
        _snoozeRefresh.value = System.currentTimeMillis()
    }

    private fun buildTodaySchedule(meds: List<Medication>, logs: List<MedicationLog>): List<ScheduleItem> {
        val today = Calendar.getInstance()
        val dayOfWeek = today.get(Calendar.DAY_OF_WEEK).let { if (it == 1) 7 else it - 1 }
        val items = mutableListOf<ScheduleItem>()

        med@ for (med in meds) {
            when (med.frequencyType) {
                "weekly" -> {
                    val activeDays = med.daysOfWeek.split(",").map { it.trim().toIntOrNull() ?: 0 }
                    if (!activeDays.contains(dayOfWeek)) continue@med
                }
                "rolling" -> {
                    val windowDays = med.daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }.sorted()
                    if (!windowDays.contains(dayOfWeek)) continue@med
                    if (logs.any { it.medicationId == med.id && it.status == "taken" }) continue@med
                    val rescheduledDays = logs
                        .filter { it.medicationId == med.id && it.status == "rescheduled" }
                        .mapNotNull { log ->
                            val c = Calendar.getInstance().apply { timeInMillis = log.scheduledTime }
                            c.get(Calendar.DAY_OF_WEEK).let { if (it == 1) 7 else it - 1 }
                        }.toSet()
                    val effectiveDay = windowDays.firstOrNull { it !in rescheduledDays }
                    if (effectiveDay == null || effectiveDay > dayOfWeek) continue@med
                }
            }

            val times = when {
                med.frequencyType == "weekly" && med.dayReminderTimes.isNotEmpty() -> {
                    val entries = DayTimesUtil.parseEntries(med.dayReminderTimes)
                    DayTimesUtil.getTimesForDay(entries, dayOfWeek)
                        .ifEmpty { med.reminderTimes.split(",").map { it.trim() } }
                }
                else -> med.reminderTimes.split(",").map { it.trim() }
            }
            for (time in times) {
                val (hour, minute) = time.split(":").map { it.toInt() }
                val scheduledMillis = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val log = logs.find { it.medicationId == med.id && it.scheduledTime == scheduledMillis }
                val snoozeUntil = settingsRepository.getSnooze(med.id, scheduledMillis)
                    .takeIf { it > System.currentTimeMillis() }
                items.add(ScheduleItem(med, scheduledMillis, log, snoozeUntil))
            }
        }
        return items.sortedBy { it.snoozedUntil ?: it.scheduledTime }
    }

    // --- Notification helpers ---

    private fun originalTimeSlot(scheduledMillis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = scheduledMillis }
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }

    private fun cancelNotificationIfAllDone(markedItem: ScheduleItem) {
        val slot = originalTimeSlot(markedItem.scheduledTime)
        val notificationId = slot.filter { it.isDigit() }.toIntOrNull() ?: 0
        val allAtSlot = scheduleItems.value.filter {
            originalTimeSlot(it.scheduledTime) == slot
        }
        // Consider the item we just marked as done
        val allDone = allAtSlot.all {
            it.medication.id == markedItem.medication.id || it.status != "pending"
        }
        if (allDone) {
            NotificationHelper.cancelNotification(context, notificationId)
            alarmScheduler.cancelRepeatAlarm(slot)
        }
    }

    private fun cancelNotificationForSlot(items: List<ScheduleItem>) {
        val first = items.firstOrNull() ?: return
        val slot = originalTimeSlot(first.scheduledTime)
        val notificationId = slot.filter { it.isDigit() }.toIntOrNull() ?: 0
        NotificationHelper.cancelNotification(context, notificationId)
        alarmScheduler.cancelRepeatAlarm(slot)
    }

    // --- Actions ---

    fun takeMedication(item: ScheduleItem) {
        viewModelScope.launch {
            settingsRepository.clearSnooze(item.medication.id, item.scheduledTime)
            repository.markLog(item.medication.id, item.scheduledTime, "taken")
            cancelNotificationIfAllDone(item)
        }
    }

    fun skipMedication(item: ScheduleItem) {
        viewModelScope.launch {
            settingsRepository.clearSnooze(item.medication.id, item.scheduledTime)
            repository.markLog(item.medication.id, item.scheduledTime, "skipped")
            cancelNotificationIfAllDone(item)
        }
    }

    fun rescheduleToNextDay(item: ScheduleItem) {
        viewModelScope.launch {
            repository.markLog(item.medication.id, item.scheduledTime, "rescheduled")
        }
    }

    fun snoozeUntil(item: ScheduleItem, hour: Int, minute: Int) {
        viewModelScope.launch {
            val targetMillis = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }.timeInMillis
            settingsRepository.setSnooze(item.medication.id, item.scheduledTime, targetMillis)
            val slot = originalTimeSlot(item.scheduledTime)
            alarmScheduler.cancelTimeSlotAlarm(slot)
            alarmScheduler.cancelRepeatAlarm(slot)
            alarmScheduler.scheduleManualSnooze(slot, targetMillis)
            refreshSnoozeTimes()
        }
    }

    fun snoozeAllAtTime(items: List<ScheduleItem>, hour: Int, minute: Int) {
        viewModelScope.launch {
            val targetMillis = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }.timeInMillis
            val pending = items.filter { it.status == "pending" }
            pending.forEach { item ->
                settingsRepository.setSnooze(item.medication.id, item.scheduledTime, targetMillis)
            }
            if (pending.isNotEmpty()) {
                val slot = originalTimeSlot(pending.first().scheduledTime)
                alarmScheduler.cancelTimeSlotAlarm(slot)
                alarmScheduler.cancelRepeatAlarm(slot)
                alarmScheduler.scheduleManualSnooze(slot, targetMillis)
            }
            refreshSnoozeTimes()
        }
    }

    fun takeAllAtTime(items: List<ScheduleItem>) {
        viewModelScope.launch {
            items.filter { it.status == "pending" }.forEach { item ->
                settingsRepository.clearSnooze(item.medication.id, item.scheduledTime)
                repository.markLog(item.medication.id, item.scheduledTime, "taken")
            }
            cancelNotificationForSlot(items)
        }
    }

    fun disableMedication(medication: Medication) {
        viewModelScope.launch {
            alarmScheduler.cancelAlarms(medication)
            repository.disableMedication(medication.id)
        }
    }
}