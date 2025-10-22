package com.pgmacdesign.macrox

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.pgmacdesign.macrox.ui.MacroXMainScreen
import com.pgmacdesign.macrox.viewmodel.MacroViewModel

/**
 * Main application entry point with Material3 theming
 * The ViewModel is expected to be passed in from the platform-specific main
 */
@Composable
fun App(viewModel: MacroViewModel) {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Surface {
            MacroXMainScreen(viewModel)
        }
    }
}
