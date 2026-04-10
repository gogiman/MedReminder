package com.example.medreminder.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.data.database.entity.MedicationLog
import com.example.medreminder.data.repository.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class HistoryItem(
    val log: MedicationLog,
    val medication: Medication?,
    val dateLabel: String,
    val timeLabel: String
)

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repository: MedicationRepository) : ViewModel() {

    private val _medications = repository.getAllActiveMedications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _logs = repository.getRecentLogs(30)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyItems: StateFlow<List<HistoryItem>> = combine(_logs, _medications) { logs, meds ->
        val medMap = meds.associateBy { it.id }
        val dateFmt = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        logs.map { log ->
            HistoryItem(
                log = log,
                medication = medMap[log.medicationId],
                dateLabel = dateFmt.format(Date(log.scheduledTime)),
                timeLabel = timeFmt.format(Date(log.scheduledTime))
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
