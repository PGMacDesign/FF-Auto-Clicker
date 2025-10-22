package com.pgmacdesign.macrox

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp
import com.pgmacdesign.macrox.platform.FileManager
import com.pgmacdesign.macrox.platform.InputPlayback
import com.pgmacdesign.macrox.platform.InputRecorder
import com.pgmacdesign.macrox.util.MockDataGenerator
import com.pgmacdesign.macrox.viewmodel.MacroViewModel
import kotlinx.coroutines.launch

fun main() = application {
    val windowState = rememberWindowState(
        width = 1200.dp,
        height = 800.dp
    )
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "MacroX - Macro Recorder & Player",
        state = windowState
    ) {
        // Create platform instances
        val fileManager = remember { FileManager() }
        val inputRecorder = remember { InputRecorder() }
        val inputPlayback = remember { InputPlayback() }
        
        // Create ViewModel
        val viewModel = remember {
            MacroViewModel(fileManager, inputRecorder, inputPlayback)
        }
        
        // Initialize with sample data for testing
        LaunchedEffect(Unit) {
            launch {
                // Generate and save sample macros
                val sampleMacros = MockDataGenerator.generateSampleMacros()
                sampleMacros.forEach { macro ->
                    fileManager.saveMacro(macro)
                }
                // Reload to show in UI
                viewModel.loadMacros()
            }
        }
        
        App(viewModel)
    }
}
