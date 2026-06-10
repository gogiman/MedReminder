package com.example.medreminder.ui.medications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medreminder.data.database.entity.Medication
import com.example.medreminder.ui.theme.MedColors

import com.example.medreminder.util.DayTimesUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(
    onNavigateBack: () -> Unit,
    onAddMedication: () -> Unit,
    onEditMedication: (Long) -> Unit,
    viewModel: MedicationsViewModel = hiltViewModel()
) {
    val medications by viewModel.medications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Medications") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMedication) {
                Icon(Icons.Default.Add, contentDescription = "Add Medication")
            }
        }
    ) { padding ->
        if (medications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.LocalPharmacy, contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No medications added yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onAddMedication) { Text("Add your first medication") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medications, key = { it.id }) { medication ->
                    MedicationListItem(
                        medication = medication,
                        onEdit = { onEditMedication(medication.id) },
                        onToggleEnabled = { viewModel.toggleEnabled(medication) },
                        onDelete = { viewModel.deleteMedication(medication) }
                    )
                }
            }
        }
    }
}

@Composable
fun MedicationListItem(
    medication: Medication,
    onEdit: () -> Unit,
    onToggleEnabled: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val medColor = MedColors.getOrElse(medication.color) { MedColors[0] }
    val contentAlpha = if (medication.isActive) 1f else 0.45f

    Card(
        onClick = { if (medication.isActive) onEdit() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (medication.isActive) 2.dp else 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (medication.isActive) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(medColor.copy(alpha = 0.2f * contentAlpha)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Medication, contentDescription = null,
                    tint = medColor.copy(alpha = contentAlpha), modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        medication.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
                    )
                    if (!medication.isActive) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "Disabled",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                Text(
                    "${medication.dosage} ${medication.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule, contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = contentAlpha)
                    )
                    Spacer(Modifier.width(4.dp))
                    val timesLabel = medication.reminderTimes.replace(",", "  •  ")
                    Text(
                        timesLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = contentAlpha)
                    )
                }
                val frequencyLabel = when (medication.frequencyType) {
                    "weekly" -> {
                        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        val days = medication.daysOfWeek.split(",")
                            .mapNotNull { it.trim().toIntOrNull() }
                            .map { dayNames.getOrElse(it - 1) { "?" } }
                            .joinToString(", ")
                        "Weekly: $days"
                    }
                    else -> medication.frequencyType.replaceFirstChar { it.uppercase() }
                }
                Text(
                    frequencyLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha)
                )
                // Per-day schedule for weekly
                if (medication.frequencyType == "weekly" && medication.dayReminderTimes.isNotEmpty()) {
                    val entries = DayTimesUtil.parseEntries(medication.dayReminderTimes)
                    entries.forEach { entry ->
                        val dayStr = entry.days.joinToString(", ") { d ->
                            listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun").getOrElse(d - 1) { "?" }
                        }
                        Text(
                            "$dayStr: ${entry.times.joinToString(", ")}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                        )
                    }
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    if (medication.isActive) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = { showMenu = false; onEdit() },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(if (medication.isActive) "Disable" else "Enable") },
                        onClick = { showMenu = false; onToggleEnabled() },
                        leadingIcon = {
                            Icon(
                                if (medication.isActive) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = { showMenu = false; showDeleteDialog = true },
                        leadingIcon = { Icon(Icons.Default.Delete, null) }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Delete, null) },
            title = { Text("Delete Medication") },
            text = { Text("Delete \"${medication.name}\"? This will also remove all its reminders.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}
