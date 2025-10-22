package com.pgmacdesign.macrox.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Screen for playing back macros with controls
 */
@Composable
fun PlaybackScreen(viewModel: com.pgmacdesign.macrox.viewmodel.MacroViewModel) {
    var selectedMacro by remember { mutableStateOf("Fishing Macro") }
    var iterations by remember { mutableStateOf("1") }
    var speedMultiplier by remember { mutableStateOf(1.0f) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentIteration by remember { mutableStateOf(0) }
    var currentEvent by remember { mutableStateOf(0) }
    var totalEvents by remember { mutableStateOf(120) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight(0.85f),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Title
                Text(
                    text = "Macro Playback",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.height(24.dp))
                
                // Macro Selection
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Selected Macro",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                selectedMacro,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { /* Select different macro */ }) {
                            Icon(Icons.Default.MoreVert, "Select Macro")
                        }
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Playback Configuration
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Iterations
                    Column {
                        Text(
                            "Number of Iterations",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = iterations,
                                onValueChange = { iterations = it },
                                modifier = Modifier.width(120.dp),
                                label = { Text("Iterations") },
                                singleLine = true
                            )
                            OutlinedButton(
                                onClick = { iterations = "0" }
                            ) {
                                Icon(Icons.Default.AllInclusive, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Infinite")
                            }
                        }
                    }
                    
                    // Speed Multiplier
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Speed Multiplier",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "${String.format("%.1f", speedMultiplier)}x",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Slider(
                            value = speedMultiplier,
                            onValueChange = { speedMultiplier = it },
                            valueRange = 0.1f..3.0f,
                            steps = 28
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("0.1x", style = MaterialTheme.typography.labelSmall)
                            Text("3.0x", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    
                    // Randomization Toggle
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shuffle, "Randomization")
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Randomization",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        "Apply timing and click variations",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            var randomizationEnabled by remember { mutableStateOf(false) }
                            Switch(
                                checked = randomizationEnabled,
                                onCheckedChange = { randomizationEnabled = it }
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Playback Progress
                if (isPlaying) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Playing...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Iteration $currentIteration / ${iterations.toIntOrNull() ?: 1}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = currentEvent.toFloat() / totalEvents,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Event $currentEvent / $totalEvents",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
                
                // Control Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isPlaying) {
                        Button(
                            onClick = { isPlaying = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Start Playback")
                        }
                        OutlinedButton(
                            onClick = { /* Configure */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Settings, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Configure")
                        }
                    } else {
                        Button(
                            onClick = { isPlaying = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Stop, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Stop (F10)")
                        }
                        OutlinedButton(
                            onClick = { /* Pause */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Pause, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Pause (F9)")
                        }
                    }
                }
                
                // Hotkey Info
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Keyboard,
                                null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "F9: Play/Pause",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Keyboard,
                                null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "F10: Stop",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

