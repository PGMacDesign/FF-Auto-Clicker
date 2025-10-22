package com.pgmacdesign.macrox.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pgmacdesign.macrox.platform.MacroInfo
import com.pgmacdesign.macrox.viewmodel.MacroViewModel

/**
 * Screen showing list of available macros
 */
@Composable
fun MacroListScreen(viewModel: MacroViewModel) {
    var selectedMacroId by remember { mutableStateOf<String?>(null) }
    val macros by viewModel.macros.collectAsState()
    val selectedMacro by viewModel.selectedMacro.collectAsState()
    
    // Load selected macro when ID changes
    LaunchedEffect(selectedMacroId) {
        selectedMacroId?.let { viewModel.selectMacro(it) }
    }
    
    Row(modifier = Modifier.fillMaxSize()) {
        // Macro List (Left)
        Card(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Macros",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        IconButton(onClick = { /* Import */ }) {
                            Icon(Icons.Default.Download, "Import")
                        }
                        IconButton(onClick = { /* Add new */ }) {
                            Icon(Icons.Default.Add, "New Macro")
                        }
                    }
                }
                
                Divider()
                
                // Search bar
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search macros...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    singleLine = true
                )
                
                // Macro list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    items(macros) { macro ->
                        MacroInfoListItem(
                            macro = macro,
                            isSelected = selectedMacroId == macro.id,
                            onClick = { selectedMacroId = macro.id }
                        )
                    }
                }
            }
        }
        
        // Macro Details (Right)
        Card(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (selectedMacro != null) {
                MacroDetailsPanel(selectedMacro!!, viewModel)
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.TouchApp,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Select a macro to view details",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MacroInfoListItem(
    macro: MacroInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = macro.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = macro.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${macro.eventCount} events",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${macro.durationMs / 1000}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun MacroDetailsPanel(macro: com.pgmacdesign.macrox.model.Macro, viewModel: MacroViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header with actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = macro.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Created: ${macro.metadata.createdAt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = { /* TODO: Edit */ }) {
                    Icon(Icons.Default.Edit, "Edit")
                }
                IconButton(onClick = { /* TODO: Export - file picker */ }) {
                    Icon(Icons.Default.Upload, "Export")
                }
                IconButton(onClick = { viewModel.deleteMacro(macro.id) }) {
                    Icon(Icons.Default.Delete, "Delete")
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        // Description
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Description",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    macro.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatsCard(
                title = "Events",
                value = macro.eventCount.toString(),
                icon = Icons.Default.Event,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                title = "Duration",
                value = "${macro.durationMs / 1000}s",
                icon = Icons.Default.Timer,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                title = "Runs",
                value = macro.metadata.executionCount.toString(),
                icon = Icons.Default.Replay,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Quick Actions
        Text(
            "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.playMacro(macro, iterations = 1) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Play Once")
            }
            OutlinedButton(
                onClick = { /* TODO: Edit events screen */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Edit Events")
            }
        }
        
        Spacer(Modifier.height(8.dp))
        
        OutlinedButton(
            onClick = { viewModel.duplicateMacro(macro) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Duplicate Macro")
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

