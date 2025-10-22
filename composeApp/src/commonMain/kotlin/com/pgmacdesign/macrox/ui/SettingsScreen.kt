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
 * Settings and preferences screen
 */
@Composable
fun SettingsScreen(viewModel: com.pgmacdesign.macrox.viewmodel.MacroViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(Modifier.height(24.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Recording Settings
            SettingsSection(
                title = "Recording",
                icon = Icons.Default.FiberManualRecord
            ) {
                var recordMouse by remember { mutableStateOf(true) }
                var recordKeyboard by remember { mutableStateOf(true) }
                var recordMouseWheel by remember { mutableStateOf(true) }
                var mouseThrottle by remember { mutableStateOf(10f) }
                
                SettingSwitch(
                    label = "Record mouse movements",
                    checked = recordMouse,
                    onCheckedChange = { recordMouse = it }
                )
                SettingSwitch(
                    label = "Record keyboard events",
                    checked = recordKeyboard,
                    onCheckedChange = { recordKeyboard = it }
                )
                SettingSwitch(
                    label = "Record mouse wheel",
                    checked = recordMouseWheel,
                    onCheckedChange = { recordMouseWheel = it }
                )
                
                Spacer(Modifier.height(8.dp))
                
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Mouse movement throttle",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${mouseThrottle.toInt()} ms",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Slider(
                        value = mouseThrottle,
                        onValueChange = { mouseThrottle = it },
                        valueRange = 1f..50f,
                        steps = 48
                    )
                }
            }
            
            // Hotkeys Settings
            SettingsSection(
                title = "Hotkeys",
                icon = Icons.Default.Keyboard
            ) {
                HotkeyRow("Start/Stop Recording", "F12")
                HotkeyRow("Play/Pause Playback", "F9")
                HotkeyRow("Stop Playback", "F10")
            }
            
            // Storage Settings
            SettingsSection(
                title = "Storage",
                icon = Icons.Default.Folder
            ) {
                Column {
                    Text(
                        "Macro storage location",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "~/.macrox/macros",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { /* Open folder */ }) {
                            Icon(Icons.Default.FolderOpen, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Open Folder")
                        }
                        OutlinedButton(onClick = { /* Change */ }) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Change")
                        }
                    }
                }
            }
            
            // Backup & Restore
            SettingsSection(
                title = "Backup & Restore",
                icon = Icons.Default.Backup
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { /* Backup */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Backup, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Create Backup")
                    }
                    OutlinedButton(
                        onClick = { /* Restore */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.RestorePage, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Restore Backup")
                    }
                }
            }
            
            // About
            SettingsSection(
                title = "About",
                icon = Icons.Default.Info
            ) {
                Column {
                    Text(
                        "MacroX",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Version 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Cross-platform macro recorder and player",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { /* Show licenses */ }) {
                        Icon(Icons.Default.Description, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("View Licenses")
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun SettingSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun HotkeyRow(
    label: String,
    hotkey: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium
        )
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                hotkey,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

