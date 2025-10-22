package com.pgmacdesign.macrox.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Screen for recording new macros
 */
@Composable
fun RecordScreen(viewModel: com.pgmacdesign.macrox.viewmodel.MacroViewModel) {
    var recordingState by remember { mutableStateOf(RecordingState.IDLE) }
    var countdown by remember { mutableStateOf(3) }
    var eventsRecorded by remember { mutableStateOf(0) }
    var recordingDuration by remember { mutableStateOf(0L) }
    var macroName by remember { mutableStateOf("New Macro") }
    
    // Simulated recording progress
    LaunchedEffect(recordingState) {
        if (recordingState == RecordingState.COUNTDOWN && countdown > 0) {
            kotlinx.coroutines.delay(1000)
            countdown--
            if (countdown == 0) {
                recordingState = RecordingState.RECORDING
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.9f),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Title
                Text(
                    text = "Record New Macro",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Status Display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    when (recordingState) {
                        RecordingState.IDLE -> {
                            Icon(
                                Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                "Ready to Record",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Click 'Start Recording' to begin",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        RecordingState.COUNTDOWN -> {
                            val infiniteTransition = rememberInfiniteTransition()
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.2f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(500),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )
                            
                            Text(
                                text = countdown.toString(),
                                style = MaterialTheme.typography.displayLarge,
                                modifier = Modifier.scale(scale),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                "Get ready...",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        
                        RecordingState.RECORDING -> {
                            val infiniteTransition = rememberInfiniteTransition()
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0.3f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(800),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )
                            
                            Icon(
                                Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = MaterialTheme.colorScheme.error.copy(alpha = alpha)
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                "RECORDING",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(32.dp))
                            
                            // Stats during recording
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(32.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        eventsRecorded.toString(),
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Events Captured",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${recordingDuration}s",
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Duration",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Press F12 to stop recording",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        RecordingState.COMPLETED -> {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                "Recording Complete!",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Captured $eventsRecorded events in ${recordingDuration}s",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                
                // Configuration & Controls
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (recordingState == RecordingState.IDLE || recordingState == RecordingState.COMPLETED) {
                        OutlinedTextField(
                            value = macroName,
                            onValueChange = { macroName = it },
                            label = { Text("Macro Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Recording Options",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(12.dp))
                                
                                var recordMouse by remember { mutableStateOf(true) }
                                var recordKeyboard by remember { mutableStateOf(true) }
                                var countdownEnabled by remember { mutableStateOf(true) }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = recordMouse,
                                        onCheckedChange = { recordMouse = it }
                                    )
                                    Text("Record mouse events")
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = recordKeyboard,
                                        onCheckedChange = { recordKeyboard = it }
                                    )
                                    Text("Record keyboard events")
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = countdownEnabled,
                                        onCheckedChange = { countdownEnabled = it }
                                    )
                                    Text("3-second countdown before recording")
                                }
                            }
                        }
                    }
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (recordingState) {
                            RecordingState.IDLE -> {
                                Button(
                                    onClick = {
                                        countdown = 3
                                        recordingState = RecordingState.COUNTDOWN
                                        eventsRecorded = 0
                                        recordingDuration = 0
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Default.FiberManualRecord, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Start Recording")
                                }
                            }
                            
                            RecordingState.RECORDING -> {
                                Button(
                                    onClick = {
                                        recordingState = RecordingState.COMPLETED
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(Icons.Default.Stop, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Stop Recording (F12)")
                                }
                                OutlinedButton(
                                    onClick = {
                                        recordingState = RecordingState.IDLE
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Cancel, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Cancel")
                                }
                            }
                            
                            RecordingState.COMPLETED -> {
                                Button(
                                    onClick = {
                                        // Save macro
                                        recordingState = RecordingState.IDLE
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary
                                    )
                                ) {
                                    Icon(Icons.Default.Save, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Save Macro")
                                }
                                OutlinedButton(
                                    onClick = {
                                        recordingState = RecordingState.IDLE
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Discard")
                                }
                            }
                            
                            else -> { /* Countdown - no buttons */ }
                        }
                    }
                }
            }
        }
    }
}

enum class RecordingState {
    IDLE, COUNTDOWN, RECORDING, COMPLETED
}

