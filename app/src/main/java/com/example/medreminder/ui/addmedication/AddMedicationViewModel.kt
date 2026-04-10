package com.example.medreminder.ui.addmedication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.data.repository.MedicationRepository
import com.example.medreminder.notification.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddMedicationUiState(
    val name: String = "",
    val dosage: String = "",
    val unit: String = "mg",
    val frequencyType: String = "daily",
    val reminderTimes: MutableList<String> = mutableListOf("08:00"),
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
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
            _uiState.update {
                it.copy(
                    name = med.name, dosage = med.dosage, unit = med.unit,
                    frequencyType = med.frequencyType,
                    reminderTimes = med.reminderTimes.split(",").map { t -> t.trim() }.toMutableList(),
                    daysOfWeek = med.daysOfWeek.split(",").mapNotNull { d -> d.trim().toIntOrNull() },
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
            val days = if ((v == "weekly" || v == "rolling") && it.frequencyType == "daily") {
                // When switching to weekly/rolling, default to today only
                val todayDow = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
                    .let { d -> if (d == 1) 7 else d - 1 }
                listOf(todayDow)
            } else it.daysOfWeek
            it.copy(frequencyType = v, daysOfWeek = days)
        }
    }
    fun onNotesChange(v: String) = _uiState.update { it.copy(notes = v) }
    fun onColorChange(v: Int) = _uiState.update { it.copy(colorIndex = v) }

    fun addReminderTime(time: String) = _uiState.update {
        it.copy(reminderTimes = (it.reminderTimes + time).toMutableList())
    }
    fun removeReminderTime(index: Int) = _uiState.update {
        it.copy(reminderTimes = it.reminderTimes.toMutableList().also { list -> if (list.size > 1) list.removeAt(index) })
    }
    fun updateReminderTime(index: Int, time: String) = _uiState.update {
        it.copy(reminderTimes = it.reminderTimes.toMutableList().also { list -> list[index] = time })
    }
    fun toggleDayOfWeek(day: Int) = _uiState.update {
        val days = it.daysOfWeek.toMutableList()
        if (days.contains(day)) { if (days.size > 1) days.remove(day) } else days.add(day)
        it.copy(daysOfWeek = days.sorted())
    }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank() || state.dosage.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val medication = Medication(
                id = if (medicationId > 0) medicationId else 0,
                name = state.name.trim(),
                dosage = state.dosage.trim(),
                unit = state.unit,
                frequencyType = state.frequencyType,
                reminderTimes = state.reminderTimes.joinToString(","),
                daysOfWeek = state.daysOfWeek.joinToString(","),
                notes = state.notes.trim(),
                color = state.colorIndex
            )
            val savedId = repository.saveMedication(medication)
            alarmScheduler.scheduleAlarms(medication.copy(id = if (medicationId > 0) medicationId else savedId))
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
