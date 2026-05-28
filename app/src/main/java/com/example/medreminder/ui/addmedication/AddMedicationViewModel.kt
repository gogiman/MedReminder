package com.example.medreminder.ui.addmedication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.data.repository.MedicationRepository
import com.example.medreminder.notification.AlarmScheduler
import com.example.medreminder.util.DayTimesUtil
import com.example.medreminder.util.ScheduleEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddMedicationUiState(
    val name: String = "",
    val dosage: String = "",
    val unit: String = "mg",
    val frequencyType: String = "daily",
    // Shared times for daily / rolling
    val reminderTimes: MutableList<String> = mutableListOf("08:00"),
    // Rolling window days
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    // Weekly schedule entries (saved)
    val scheduleEntries: List<ScheduleEntry> = emptyList(),
    // Weekly pending (being built before "Add to schedule")
    val pendingDays: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    val pendingTimes: List<String> = listOf("08:00"),
    val colorIndex: Int = 0,
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddMedicationViewModel @Inject constructor(
    private val repository: MedicationRepository,
    private val alarmScheduler: AlarmScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val medicationId: Long = savedStateHandle["medicationId"] ?: -1L
    private val _uiState = MutableStateFlow(AddMedicationUiState())
    val uiState: StateFlow<AddMedicationUiState> = _uiState.asStateFlow()

    init {
        if (medicationId > 0) loadMedication(medicationId)
    }

    private fun loadMedication(id: Long) {
        viewModelScope.launch {
            val med = repository.getMedicationById(id) ?: return@launch
            val entries = DayTimesUtil.parseEntries(med.dayReminderTimes)
            _uiState.update {
                it.copy(
                    name = med.name, dosage = med.dosage, unit = med.unit,
                    frequencyType = med.frequencyType,
                    reminderTimes = med.reminderTimes.split(",").map { t -> t.trim() }.toMutableList(),
                    daysOfWeek = med.daysOfWeek.split(",").mapNotNull { d -> d.trim().toIntOrNull() },
                    scheduleEntries = entries,
                    colorIndex = med.color, notes = med.notes
                )
            }
        }
    }

    fun onNameChange(v: String) = _uiState.update { it.copy(name = v) }
    fun onDosageChange(v: String) = _uiState.update { it.copy(dosage = v) }
    fun onUnitChange(v: String) = _uiState.update { it.copy(unit = v) }

    fun onFrequencyChange(v: String) {
        _uiState.update {
            when (v) {
                "weekly" -> it.copy(
                    frequencyType = v,
                    pendingDays = listOf(1, 2, 3, 4, 5, 6, 7),
                    pendingTimes = listOf("08:00")
                )
                "rolling" -> {
                    val todayDow = java.util.Calendar.getInstance()
                        .get(java.util.Calendar.DAY_OF_WEEK).let { d -> if (d == 1) 7 else d - 1 }
                    it.copy(frequencyType = v, daysOfWeek = listOf(todayDow))
                }
                else -> it.copy(frequencyType = v)
            }
        }
    }

    fun onNotesChange(v: String) = _uiState.update { it.copy(notes = v) }
    fun onColorChange(v: Int) = _uiState.update { it.copy(colorIndex = v) }

    // --- Shared times (daily / rolling) ---
    fun addReminderTime(time: String) = _uiState.update {
        it.copy(reminderTimes = (it.reminderTimes + time).toMutableList())
    }
    fun removeReminderTime(index: Int) = _uiState.update {
        it.copy(reminderTimes = it.reminderTimes.toMutableList()
            .also { list -> if (list.size > 1) list.removeAt(index) })
    }
    fun updateReminderTime(index: Int, time: String) = _uiState.update {
        it.copy(reminderTimes = it.reminderTimes.toMutableList().also { list -> list[index] = time })
    }

    // --- Rolling day selector ---
    fun toggleDayOfWeek(day: Int) = _uiState.update {
        val days = it.daysOfWeek.toMutableList()
        if (days.contains(day)) { if (days.size > 1) days.remove(day) } else days.add(day)
        it.copy(daysOfWeek = days.sorted())
    }

    // --- Weekly pending ---
    fun togglePendingDay(day: Int) = _uiState.update {
        val days = it.pendingDays.toMutableList()
        if (days.contains(day)) { if (days.size > 1) days.remove(day) } else days.add(day)
        it.copy(pendingDays = days.sorted())
    }
    fun addPendingTime(time: String) = _uiState.update {
        it.copy(pendingTimes = it.pendingTimes + time)
    }
    fun removePendingTime(index: Int) = _uiState.update {
        it.copy(pendingTimes = it.pendingTimes.toMutableList()
            .also { list -> if (list.size > 1) list.removeAt(index) })
    }
    fun updatePendingTime(index: Int, time: String) = _uiState.update {
        it.copy(pendingTimes = it.pendingTimes.toMutableList().also { list -> list[index] = time })
    }

    // --- Weekly schedule entries ---
    fun addScheduleEntry() = _uiState.update {
        if (it.pendingDays.isEmpty() || it.pendingTimes.isEmpty()) return@update it
        val newEntry = ScheduleEntry(days = it.pendingDays.sorted(), times = it.pendingTimes)
        it.copy(
            scheduleEntries = it.scheduleEntries + newEntry,
            pendingDays = listOf(1, 2, 3, 4, 5, 6, 7),
            pendingTimes = listOf("08:00")
        )
    }
    fun removeScheduleEntry(index: Int) = _uiState.update {
        it.copy(scheduleEntries = it.scheduleEntries.toMutableList().also { list -> list.removeAt(index) })
    }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank() || state.dosage.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val (reminderTimesStr, daysOfWeekStr, dayReminderTimesStr) =
                if (state.frequencyType == "weekly" && state.scheduleEntries.isNotEmpty()) {
                    val union = DayTimesUtil.allUniqueTimes(state.scheduleEntries)
                    val days = DayTimesUtil.allDays(state.scheduleEntries)
                    Triple(
                        union.joinToString(","),
                        days.joinToString(","),
                        DayTimesUtil.serializeEntries(state.scheduleEntries)
                    )
                } else {
                    Triple(
                        state.reminderTimes.joinToString(","),
                        state.daysOfWeek.joinToString(","),
                        ""
                    )
                }

            val medication = Medication(
                id = if (medicationId > 0) medicationId else 0,
                name = state.name.trim(),
                dosage = state.dosage.trim(),
                unit = state.unit,
                frequencyType = state.frequencyType,
                reminderTimes = reminderTimesStr,
                daysOfWeek = daysOfWeekStr,
                dayReminderTimes = dayReminderTimesStr,
                notes = state.notes.trim(),
                color = state.colorIndex
            )
            // Cancel old alarms before saving (times/days may have changed)
            if (medicationId > 0) {
                val oldMed = repository.getMedicationById(medicationId)
                if (oldMed != null) {
                    alarmScheduler.cancelAlarms(oldMed)
                    // Clear all logs when frequency type changes so HomeScreen shows a fresh schedule
                    if (oldMed.frequencyType != state.frequencyType) {
                        repository.deleteLogsForMedication(medicationId)
                    }
                }
            }
            val savedId = repository.saveMedication(medication)
            alarmScheduler.scheduleAlarms(medication.copy(id = if (medicationId > 0) medicationId else savedId))
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}