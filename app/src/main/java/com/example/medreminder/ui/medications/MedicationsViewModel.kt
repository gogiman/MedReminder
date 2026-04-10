package com.example.medreminder.ui.medications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.data.repository.MedicationRepository
import com.example.medreminder.notification.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicationsViewModel @Inject constructor(
    private val repository: MedicationRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    val medications: StateFlow<List<Medication>> = repository.getAllMedications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleEnabled(medication: Medication) {
        viewModelScope.launch {
            val enable = !medication.isActive
            if (enable) {
                repository.setMedicationEnabled(medication.id, true)
                alarmScheduler.scheduleAlarms(medication.copy(isActive = true))
            } else {
                repository.disableMedication(medication.id)
                alarmScheduler.cancelAlarms(medication)
            }
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            alarmScheduler.cancelAlarms(medication)
            repository.deleteMedication(medication.id)
        }
    }
}
