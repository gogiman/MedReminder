package com.example.medreminder.ui.addmedication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medreminder.ui.theme.MedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddMedicationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.uiState.value.name.isEmpty()) "Add Medication" else "Edit Medication") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = viewModel::save, enabled = !uiState.isLoading) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Medication Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Medication, null) }
            )

            // Dosage + Unit
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.dosage,
                    onValueChange = viewModel::onDosageChange,
                    label = { Text("Dosage *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                val units = listOf("mg", "ml", "tablet(s)", "capsule(s)", "drop(s)", "unit(s)")
                var unitExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(unitExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = unitExpanded, onDismissRequest = { unitExpanded = false }) {
                        units.forEach { u ->
                            DropdownMenuItem(text = { Text(u) }, onClick = { viewModel.onUnitChange(u); unitExpanded = false })
                        }
                    }
                }
            }

            // Frequency
            Text("Frequency", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("daily", "weekly", "rolling").forEach { freq ->
                    FilterChip(
                        selected = uiState.frequencyType == freq,
                        onClick = { viewModel.onFrequencyChange(freq) },
                        label = { Text(freq.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // Days of week picker for weekly and rolling
            if (uiState.frequencyType == "weekly" || uiState.frequencyType == "rolling") {
                Text(
                    if (uiState.frequencyType == "rolling") "Allowed Days (window)" else "Days",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEachIndexed { i, label ->
                        val day = i + 1
                        val selected = uiState.daysOfWeek.contains(day)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.toggleDayOfWeek(day) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Reminder times
            Text("Reminder Times", style = MaterialTheme.typography.labelLarge)
            uiState.reminderTimes.forEachIndexed { index, time ->
                ReminderTimeRow(
                    time = time,
                    onTimeChange = { viewModel.updateReminderTime(index, it) },
                    onRemove = { viewModel.removeReminderTime(index) },
                    showRemove = uiState.reminderTimes.size > 1
                )
            }
            OutlinedButton(
                onClick = { viewModel.addReminderTime("12:00") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add Time")
            }

            // Color picker
            Text("Color", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(MedColors) { index, color ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (uiState.colorIndex == index)
                                    Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                else Modifier
                            )
                            .clickable { viewModel.onColorChange(index) }
                    )
                }
            }

            // Notes
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            if (uiState.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ReminderTimeRow(time: String, onTimeChange: (String) -> Unit, onRemove: () -> Unit, showRemove: Boolean) {
    var showPicker by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = { showPicker = true }, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.Schedule, null)
            Spacer(Modifier.width(8.dp))
            Text(time)
        }
        if (showRemove) {
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Remove, contentDescription = "Remove time", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
    if (showPicker) {
        TimePickerDialog(initialTime = time, onConfirm = { onTimeChange(it); showPicker = false }, onDismiss = { showPicker = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(initialTime: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    val parts = initialTime.split(":")
    val timePickerState = rememberTimePickerState(
        initialHour = parts[0].toIntOrNull() ?: 8,
        initialMinute = parts[1].toIntOrNull() ?: 0,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(String.format("%02d:%02d", timePickerState.hour, timePickerState.minute))
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}
