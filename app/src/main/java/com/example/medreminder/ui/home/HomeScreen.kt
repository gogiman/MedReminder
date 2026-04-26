package com.example.medreminder.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.medreminder.ui.theme.MedColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select new time") },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = state)
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddMedication: () -> Unit,
    onEditMedication: (Long) -> Unit,
    onViewMedications: () -> Unit,
    onViewSettings: () -> Unit,
    onViewWeekly: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
){
    val scheduleItems by viewModel.scheduleItems.collectAsState()
    val dayOffset by viewModel.dayOffset.collectAsState()
    val canGoPrevious by viewModel.canGoPrevious.collectAsState()
    val dateLabel = remember(dayOffset) {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, dayOffset) }
        val shortFmt = SimpleDateFormat("MMM d", Locale.getDefault())
        when (dayOffset) {
            0    -> "Today, ${shortFmt.format(cal.time)}"
            -1   -> "Yesterday, ${shortFmt.format(cal.time)}"
            1    -> "Tomorrow, ${shortFmt.format(cal.time)}"
            else -> SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(cal.time)
        }
    }
    var hideTaken by remember { mutableStateOf(true) }
    var swipeAccum by remember { mutableFloatStateOf(0f) }

    // Refresh snooze data every time the screen resumes (e.g. after a notification action)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshSnoozeTimes()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MedReminder") },
                actions = {
                    IconButton(onClick = onViewMedications) {
                        Icon(Icons.Default.MedicalServices, contentDescription = "All Medications")
                    }
                    IconButton(onClick = onViewWeekly) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Weekly Schedule")
                    }
                    IconButton(onClick = onViewSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMedication) {
                Icon(Icons.Default.Add, contentDescription = "Add Medication")
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { swipeAccum = 0f },
                    onDragCancel = { swipeAccum = 0f },
                    onDragEnd = {
                        val threshold = 80.dp.toPx()
                        if (swipeAccum < -threshold) viewModel.goToNextDay()
                        else if (swipeAccum > threshold) viewModel.goToPreviousDay()
                        swipeAccum = 0f
                    },
                    onHorizontalDrag = { _, delta -> swipeAccum += delta }
                )
            }
        ) {
            // Date header
            Surface(color = MaterialTheme.colorScheme.primaryContainer) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = viewModel::goToPreviousDay,
                        enabled = canGoPrevious,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous day", modifier = Modifier.size(20.dp))
                    }
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        dateLabel,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                            .then(if (dayOffset != 0) Modifier.clickable { viewModel.goToToday() } else Modifier),
                        color = if (dayOffset != 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                    )
                    val taken = scheduleItems.count { it.status == "taken" }
                    val total = scheduleItems.size
                    Text("$taken/$total taken", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.width(4.dp))
                    if (dayOffset == 0) {
                        IconButton(onClick = { hideTaken = !hideTaken }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                if (hideTaken) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (hideTaken) "Show taken" else "Hide taken",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = viewModel::goToNextDay,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next day", modifier = Modifier.size(20.dp))
                    }
                }
            }

            if (scheduleItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocalPharmacy, contentDescription = null,
                            modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(16.dp))
                        Text("No medications scheduled for this day", style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(8.dp))
                        if (dayOffset == 0) TextButton(onClick = onAddMedication) { Text("Add your first medication") }
                    }
                }
            } else {
                val grouped = remember(scheduleItems, hideTaken, dayOffset) {
                    val visible = if (hideTaken && dayOffset == 0)
                        scheduleItems.filter { it.status == "pending" }
                    else
                        scheduleItems
                    visible.groupBy { it.timeLabel }.entries.sortedBy { it.key }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    grouped.forEach { (timeLabel, items) ->
                        val pendingItems = if (dayOffset == 0) items.filter { it.status == "pending" } else emptyList()
                        item(key = "header_$timeLabel") {
                            TimeSlotHeader(
                                timeLabel = timeLabel,
                                pendingCount = pendingItems.size,
                                onTakeAll = { viewModel.takeAllAtTime(items) },
                                onRescheduleAll = { h, m -> viewModel.snoozeAllAtTime(items, h, m) }
                            )
                        }
                        items(items, key = { "${it.medication.id}_${it.scheduledTime}" }) { item ->
                            if (dayOffset == 0) {
                                MedicationCard(
                                    item = item,
                                    onTake = { viewModel.takeMedication(item) },
                                    onSkip = { viewModel.skipMedication(item) },
                                    onReschedule = { viewModel.rescheduleToNextDay(item) },
                                    onSnoozeUntil = { h, m -> viewModel.snoozeUntil(item, h, m) },
                                    onEdit = { onEditMedication(item.medication.id) },
                                    onDisable = { viewModel.disableMedication(item.medication) }
                                )
                            } else {
                                ReadOnlyMedicationCard(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeSlotHeader(
    timeLabel: String,
    pendingCount: Int,
    onTakeAll: () -> Unit,
    onRescheduleAll: (hour: Int, minute: Int) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val (initHour, initMinute) = remember(timeLabel) {
        val p = timeLabel.split(":")
        Pair(p[0].toIntOrNull() ?: 0, p[1].toIntOrNull() ?: 0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Schedule,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(6.dp))
        Text(
            timeLabel,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.weight(1f))
        if (pendingCount > 0) {
            IconButton(
                onClick = { showTimePicker = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.EditCalendar,
                    contentDescription = "Reschedule all",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.width(4.dp))
            FilledTonalButton(
                onClick = onTakeAll,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Take All", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = initHour,
            initialMinute = initMinute,
            onDismiss = { showTimePicker = false },
            onConfirm = { h, m -> showTimePicker = false; onRescheduleAll(h, m) }
        )
    }
}

@Composable
fun ReadOnlyMedicationCard(item: ScheduleItem) {
    val medColor = MedColors.getOrElse(item.medication.color) { MedColors[0] }
    val displayStatus = when {
        item.log?.status != null -> item.log.status
        item.scheduledTime < System.currentTimeMillis() -> "missed"
        else -> "pending"
    }
    val statusColor = when (displayStatus) {
        "taken"       -> Color(0xFF4CAF50)
        "skipped"     -> Color(0xFFFF9800)
        "missed"      -> Color(0xFFF44336)
        "rescheduled" -> Color(0xFF2196F3)
        else          -> MaterialTheme.colorScheme.outline
    }
    val statusIcon = when (displayStatus) {
        "taken"       -> Icons.Default.CheckCircle
        "skipped"     -> Icons.Default.Cancel
        "missed"      -> Icons.Default.Warning
        "rescheduled" -> Icons.Default.Redo
        else          -> Icons.Default.AccessTime
    }
    val scheduledTimeStr = remember(item.scheduledTime) {
        val cal = Calendar.getInstance().apply { timeInMillis = item.scheduledTime }
        String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(38.dp).clip(CircleShape).background(medColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Medication, contentDescription = null, tint = medColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.medication.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "${item.medication.dosage} ${item.medication.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(scheduledTimeStr, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(2.dp))
                val takenTime = item.log?.takenTime
                if (displayStatus == "taken" && takenTime != null) {
                    val cal = Calendar.getInstance().apply { timeInMillis = takenTime }
                    val takenStr = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = statusColor, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(2.dp))
                        Text("taken $takenStr", style = MaterialTheme.typography.labelSmall, color = statusColor)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(2.dp))
                        Text(displayStatus, style = MaterialTheme.typography.labelSmall, color = statusColor)
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationCard(
    item: ScheduleItem,
    onTake: () -> Unit,
    onSkip: () -> Unit,
    onReschedule: () -> Unit,
    onSnoozeUntil: (hour: Int, minute: Int) -> Unit,
    onEdit: () -> Unit,
    onDisable: () -> Unit
){
    var showMenu by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val initMillis = item.snoozedUntil ?: item.scheduledTime
    val initCal = remember(initMillis) { Calendar.getInstance().apply { timeInMillis = initMillis } }
    val medColor = MedColors.getOrElse(item.medication.color) { MedColors[0] }
    val statusColor = when (item.status) {
        "taken" -> Color(0xFF4CAF50)
        "skipped" -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            // Color indicator
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(medColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Medication, contentDescription = null, tint = medColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(10.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(item.medication.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "${item.medication.dosage} ${item.medication.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.snoozedUntil != null && item.status == "pending") {
                    val cal = Calendar.getInstance().apply { timeInMillis = item.snoozedUntil }
                    val snoozeTime = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                    Text("⏰ Snoozed until $snoozeTime", style = MaterialTheme.typography.labelSmall, color = Color(0xFF1976D2))
                } else if (item.status != "pending") {
                    Text("• ${item.status}", style = MaterialTheme.typography.labelSmall, color = statusColor)
                }
            }

            // Actions
            if (item.status == "pending") {
                IconButton(onClick = onTake, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Take", tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                }
                if (item.canReschedule) {
                    IconButton(onClick = onReschedule, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Schedule, contentDescription = "Reschedule", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    }
                } else {
                    IconButton(onClick = onSkip, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Cancel, contentDescription = "Skip", tint = Color(0xFFFF9800), modifier = Modifier.size(24.dp))
                    }
                }
            } else {
                Icon(
                    if (item.status == "taken") Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(20.dp))
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = { Text("Edit") }, onClick = { showMenu = false; onEdit() },
                        leadingIcon = { Icon(Icons.Default.Edit, null) })
                    if (item.status == "pending") {
                        DropdownMenuItem(
                            text = { Text("Reschedule time…") },
                            onClick = { showMenu = false; showTimePicker = true },
                            leadingIcon = { Icon(Icons.Default.EditCalendar, null) }
                        )
                    }
                    DropdownMenuItem(text = { Text("Disable") }, onClick = { showMenu = false; onDisable() },
                        leadingIcon = { Icon(Icons.Default.PauseCircle, null) })
                }
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = initCal.get(Calendar.HOUR_OF_DAY),
            initialMinute = initCal.get(Calendar.MINUTE),
            onDismiss = { showTimePicker = false },
            onConfirm = { h, m -> showTimePicker = false; onSnoozeUntil(h, m) }
        )
    }
}
