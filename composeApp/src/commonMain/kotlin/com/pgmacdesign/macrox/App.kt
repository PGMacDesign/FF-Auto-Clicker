package com.pgmacdesign.macrox

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Simple test app to verify the build works
 */
@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "MacroX",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Cross-platform macro recorder and player",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Status: Development Phase",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "✅ Project structure setup complete",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "✅ Core data models implemented",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "✅ Platform abstractions defined",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "✅ Basic UI framework ready",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "🚧 Windows platform implementation in progress",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
