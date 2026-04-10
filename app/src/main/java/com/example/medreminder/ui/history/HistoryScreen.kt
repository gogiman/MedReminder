package com.example.medreminder.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medreminder.ui.theme.MedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyItems by viewModel.historyItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (historyItems.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(16.dp))
                    Text("No history yet", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            val grouped = historyItems.groupBy { it.dateLabel }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grouped.forEach { (date, items) ->
                    item {
                        Text(date, style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp))
                    }
                    items(items, key = { it.log.id }) { historyItem ->
                        HistoryCard(historyItem)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem) {
    val med = item.medication
    val medColor = med?.let { MedColors.getOrElse(it.color) { MedColors[0] } } ?: MedColors[0]
    val statusColor = when (item.log.status) {
        "taken" -> Color(0xFF4CAF50)
        "skipped" -> Color(0xFFFF9800)
        "rescheduled" -> Color(0xFF2196F3)
        else -> MaterialTheme.colorScheme.outline
    }
    val statusIcon = when (item.log.status) {
        "taken" -> Icons.Default.CheckCircle
        "skipped" -> Icons.Default.Cancel
        "rescheduled" -> Icons.Default.Schedule
        else -> Icons.Default.HourglassEmpty
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(med?.name ?: "Unknown", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text("${med?.dosage ?: ""} ${med?.unit ?: ""}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(item.timeLabel, style = MaterialTheme.typography.labelMedium)
                Text(item.log.status, style = MaterialTheme.typography.labelSmall, color = statusColor)
            }
        }
    }
}
