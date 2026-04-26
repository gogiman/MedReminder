package com.example.medreminder.ui.weekly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.data.database.entity.MedicationLog
import com.example.medreminder.data.repository.MedicationRepository
import com.example.medreminder.util.DayTimesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class WeekDayItem(
    val medication: Medication,
    val scheduledTime: Long,
    val takenTime: Long?,
    val status: String  // "taken", "skipped", "rescheduled", "pending", "missed"
) {
    val scheduledTimeLabel: String get() {
        val cal = Calendar.getInstance().apply { timeInMillis = scheduledTime }
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }
    val takenTimeLabel: String? get() {
        if (takenTime == null) return null
        val cal = Calendar.getInstance().apply { timeInMillis = takenTime }
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }
}

@HiltViewModel
class WeeklyViewModel @Inject constructor(
    private val repository: MedicationRepository
) : ViewModel() {

    private val _weekOffset = MutableStateFlow(0)
    private val _selectedDayIndex = MutableStateFlow(todayDayIndex())
    private val _minWeekOffset = MutableStateFlow(Int.MIN_VALUE)

    val weekOffset: StateFlow<Int> = _weekOffset.asStateFlow()
    val selectedDayIndex: StateFlow<Int> = _selectedDayIndex.asStateFlow()
    val canGoPrevious: StateFlow<Boolean> = combine(_weekOffset, _minWeekOffset) { offset, min ->
        offset > min
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            val oldest = repository.getOldestLogTime()
            _minWeekOffset.value = if (oldest != null) weekOffsetForTime(oldest) else 0
        }
    }

    val weekDates: StateFlow<List<Calendar>> = _weekOffset.map { offset ->
        val (start, _) = computeWeekBounds(offset)
        (0..6).map { dayIdx ->
            Calendar.getInstance().apply { timeInMillis = start + dayIdx * 86400000L }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), computeCurrentWeekDates())

    private val weekLogs: StateFlow<List<MedicationLog>> = _weekOffset.flatMapLatest { offset ->
        val (start, end) = computeWeekBounds(offset)
        repository.getLogsForDateRange(start, end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val medications: StateFlow<List<Medication>> = repository.getAllActiveMedications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dayItems: StateFlow<List<WeekDayItem>> = combine(
        weekDates, weekLogs, medications, _selectedDayIndex
    ) { dates, logs, meds, selectedIdx ->
        val targetDate = dates.getOrNull(selectedIdx) ?: return@combine emptyList()
        buildDaySchedule(targetDate, meds, logs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDay(index: Int) {
        _selectedDayIndex.value = index
    }

    fun goToPreviousWeek() {
        if (_weekOffset.value > _minWeekOffset.value) {
            _weekOffset.value -= 1
            _selectedDayIndex.value = 0
        }
    }

    fun goToNextWeek() {
        if (_weekOffset.value < 0) {
            _weekOffset.value += 1
            _selectedDayIndex.value = 0
        }
    }

    private fun weekOffsetForTime(timeMillis: Long): Int {
        val (thisWeekStart, _) = computeWeekBounds(0)
        val diff = timeMillis - thisWeekStart
        return (diff / (7 * 86400000L)).toInt().coerceAtMost(0)
    }

    private fun todayDayIndex(): Int {
        val dow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return if (dow == Calendar.SUNDAY) 6 else dow - Calendar.MONDAY
    }

    private fun computeCurrentWeekDates(): List<Calendar> {
        val (start, _) = computeWeekBounds(0)
        return (0..6).map { dayIdx ->
            Calendar.getInstance().apply { timeInMillis = start + dayIdx * 86400000L }
        }
    }

    private fun computeWeekBounds(offset: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dow == Calendar.SUNDAY) 6 else dow - Calendar.MONDAY
        cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday + offset * 7)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val weekStart = cal.timeInMillis
        return Pair(weekStart, weekStart + 7 * 86400000L)
    }

    private fun buildDaySchedule(
        targetDate: Calendar,
        meds: List<Medication>,
        weekLogs: List<MedicationLog>
    ): List<WeekDayItem> {
        val dayOfWeek = targetDate.get(Calendar.DAY_OF_WEEK).let { if (it == 1) 7 else it - 1 }
        val dayStart = (targetDate.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val dayEnd = dayStart + 86400000L
        val dayLogs = weekLogs.filter { it.scheduledTime >= dayStart && it.scheduledTime < dayEnd }
        val now = System.currentTimeMillis()
        val items = mutableListOf<WeekDayItem>()

        med@ for (med in meds) {
            when (med.frequencyType) {
                "weekly" -> {
                    val activeDays = med.daysOfWeek.split(",").map { it.trim().toIntOrNull() ?: 0 }
                    if (!activeDays.contains(dayOfWeek)) continue@med
                }
                "rolling" -> {
                    val windowDays = med.daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }.sorted()
                    if (!windowDays.contains(dayOfWeek)) continue@med

                    // If there is already a log for this specific day, always show it regardless of
                    // anything else (taken, skipped, rescheduled — all should be visible).
                    val hasLogForThisDay = dayLogs.any { it.medicationId == med.id }
                    if (!hasLogForThisDay) {
                        // Only skip if taken on a day BEFORE this one (not on this day itself).
                        val takenBeforeThisDay = weekLogs.any { log ->
                            log.medicationId == med.id && log.status == "taken" && log.scheduledTime < dayStart
                        }
                        if (takenBeforeThisDay) continue@med

                        // Effective day = first window day not yet rescheduled *before* this day.
                        val rescheduledDays = weekLogs
                            .filter { it.medicationId == med.id && it.status == "rescheduled" && it.scheduledTime < dayStart }
                            .mapNotNull { log ->
                                val c = Calendar.getInstance().apply { timeInMillis = log.scheduledTime }
                                c.get(Calendar.DAY_OF_WEEK).let { if (it == 1) 7 else it - 1 }
                            }.toSet()
                        val effectiveDay = windowDays.firstOrNull { it !in rescheduledDays }
                        if (effectiveDay == null || effectiveDay > dayOfWeek) continue@med

                        // For a purely future day with no logs yet, only show on the first effective
                        // window day to avoid repeating "pending" on every remaining day of the window.
                        val isDayInFuture = dayStart > System.currentTimeMillis()
                        if (isDayInFuture && effectiveDay != dayOfWeek) continue@med
                    }
                }
                // "daily" and others: always scheduled, no day filtering
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
                val parts = time.split(":")
                if (parts.size != 2) continue
                val hour = parts[0].toIntOrNull() ?: continue
                val minute = parts[1].toIntOrNull() ?: continue
                val scheduledMillis = (targetDate.clone() as Calendar).apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val log = dayLogs.find { it.medicationId == med.id && it.scheduledTime == scheduledMillis }
                val status = when {
                    log != null -> log.status
                    scheduledMillis > now -> "pending"
                    else -> "missed"
                }

                items.add(WeekDayItem(
                    medication = med,
                    scheduledTime = scheduledMillis,
                    takenTime = log?.takenTime,
                    status = status
                ))
            }
        }

        return items.sortedBy { it.scheduledTime }
    }
}
