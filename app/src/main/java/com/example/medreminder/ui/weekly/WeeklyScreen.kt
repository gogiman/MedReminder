package com.example.medreminder.ui.weekly

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medreminder.ui.theme.MedColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeeklyViewModel = hiltViewModel()
) {
    val weekDates by viewModel.weekDates.collectAsState()
    val selectedDayIndex by viewModel.selectedDayIndex.collectAsState()
    val dayItems by viewModel.dayItems.collectAsState()
    val canGoPrevious by viewModel.canGoPrevious.collectAsState()
    val weekOffset by viewModel.weekOffset.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Schedule") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            WeekNavigationHeader(
                weekDates = weekDates,
                selectedDayIndex = selectedDayIndex,
                onSelectDay = viewModel::selectDay,
                onPreviousWeek = viewModel::goToPreviousWeek,
                onNextWeek = viewModel::goToNextWeek,
                canGoPrevious = canGoPrevious,
                canGoNext = weekOffset < 0
            )
            HorizontalDivider()

            if (dayItems.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.EventBusy, contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No medications scheduled for this day",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                val grouped = dayItems.groupBy { it.scheduledTimeLabel }.entries.sortedBy { it.key }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    grouped.forEach { (timeLabel, items) ->
                        item(key = "header_$timeLabel") {
                            WeekTimeSlotHeader(timeLabel = timeLabel)
                        }
                        items(items, key = { "${it.medication.id}_${it.scheduledTime}" }) { item ->
                            WeekDayCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeekNavigationHeader(
    weekDates: List<Calendar>,
    selectedDayIndex: Int,
    onSelectDay: (Int) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    canGoPrevious: Boolean,
    canGoNext: Boolean
) {
    val today = remember { Calendar.getInstance() }
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val monthFmt = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }
    val yearFmt = remember { SimpleDateFormat("yyyy", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Week range with prev/next navigation
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousWeek, enabled = canGoPrevious) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous week")
            }
            if (weekDates.isNotEmpty()) {
                val start = weekDates.first()
                val end = weekDates.last()
                Text(
                    text = "${monthFmt.format(start.time)} – ${monthFmt.format(end.time)}, ${yearFmt.format(end.time)}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onNextWeek, enabled = canGoNext) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next week")
            }
        }

        // Day selector chips
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDates.forEachIndexed { index, date ->
                val isToday = isSameDay(date, today)
                val isSelected = index == selectedDayIndex
                val dayNum = date.get(Calendar.DAY_OF_MONTH)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectDay(index) }
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Text(
                        dayNames[index],
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.primaryContainer
                                    else -> Color.Transparent
                                }
                            )
                    ) {
                        Text(
                            text = dayNum.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeekTimeSlotHeader(timeLabel: String) {
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
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
fun WeekDayCard(item: WeekDayItem) {
    val medColor = MedColors.getOrElse(item.medication.color) { MedColors[0] }
    val statusColor = when (item.status) {
        "taken"       -> Color(0xFF4CAF50)
        "skipped"     -> Color(0xFFFF9800)
        "missed"      -> Color(0xFFF44336)
        "rescheduled" -> Color(0xFF2196F3)
        else          -> MaterialTheme.colorScheme.outline  // pending/future
    }
    val statusIcon = when (item.status) {
        "taken"       -> Icons.Default.CheckCircle
        "skipped"     -> Icons.Default.Cancel
        "missed"      -> Icons.Default.Warning
        "rescheduled" -> Icons.AutoMirrored.Filled.Redo
        else          -> Icons.Default.AccessTime
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
            // Colored medication icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(medColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Medication, contentDescription = null, tint = medColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))

            // Name + dosage
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.medication.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${item.medication.dosage} ${item.medication.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Time + status column
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    item.scheduledTimeLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(2.dp))
                if (item.status == "taken" && item.takenTimeLabel != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = statusColor, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(2.dp))
                        Text(
                            "taken ${item.takenTimeLabel}",
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(2.dp))
                        Text(item.status, style = MaterialTheme.typography.labelSmall, color = statusColor)
                    }
                }
            }
        }
    }
}

private fun isSameDay(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
    a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
