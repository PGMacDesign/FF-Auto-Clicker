package com.pgmacdesign.macrox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pgmacdesign.macrox.viewmodel.MacroViewModel

/**
 * Main screen for MacroX application
 */
@Composable
fun MacroXMainScreen(viewModel: MacroViewModel) {
    var selectedScreen by remember { mutableStateOf(Screen.MACROS) }
    
    Row(modifier = Modifier.fillMaxSize()) {
        // Left Sidebar Navigation
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Spacer(Modifier.height(16.dp))
            
            NavigationRailItem(
                icon = { Icon(Icons.Default.List, contentDescription = "Macros") },
                label = { Text("Macros") },
                selected = selectedScreen == Screen.MACROS,
                onClick = { selectedScreen = Screen.MACROS }
            )
            
            NavigationRailItem(
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Playback") },
                label = { Text("Playback") },
                selected = selectedScreen == Screen.PLAYBACK,
                onClick = { selectedScreen = Screen.PLAYBACK }
            )
            
            NavigationRailItem(
                icon = { Icon(Icons.Default.FiberManualRecord, contentDescription = "Record") },
                label = { Text("Record") },
                selected = selectedScreen == Screen.RECORD,
                onClick = { selectedScreen = Screen.RECORD }
            )
            
            NavigationRailItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                label = { Text("Settings") },
                selected = selectedScreen == Screen.SETTINGS,
                onClick = { selectedScreen = Screen.SETTINGS }
            )
        }
        
        // Main Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedScreen) {
                Screen.MACROS -> MacroListScreen(viewModel)
                Screen.PLAYBACK -> PlaybackScreen(viewModel)
                Screen.RECORD -> RecordScreen(viewModel)
                Screen.SETTINGS -> SettingsScreen(viewModel)
            }
        }
    }
}

enum class Screen {
    MACROS, PLAYBACK, RECORD, SETTINGS
}

