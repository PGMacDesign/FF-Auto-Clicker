package com.pgmacdesign.macrox.viewmodel

import com.pgmacdesign.macrox.engine.PlaybackController
import com.pgmacdesign.macrox.engine.RecordingSession
import com.pgmacdesign.macrox.engine.RecordingState
import com.pgmacdesign.macrox.engine.RecordingProgress
import com.pgmacdesign.macrox.model.*
import com.pgmacdesign.macrox.platform.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.util.UUID

/**
 * ViewModel for managing macro operations
 */
class MacroViewModel(
    private val fileManager: FileManager,
    private val inputRecorder: InputRecorder,
    private val inputPlayback: InputPlayback
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Recording Session
    private var recordingSession: RecordingSession? = null
    private val _recordingState = MutableStateFlow(RecordingState.IDLE)
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()
    
    private val _recordingProgress = MutableStateFlow(RecordingProgress())
    val recordingProgress: StateFlow<RecordingProgress> = _recordingProgress.asStateFlow()
    
    // Playback Controller
    private val playbackController = PlaybackController(inputPlayback)
    val playbackState: StateFlow<MacroState> = playbackController.state
    val playbackProgress = playbackController.progress
    
    // Macros List
    private val _macros = MutableStateFlow<List<MacroInfo>>(emptyList())
    val macros: StateFlow<List<MacroInfo>> = _macros.asStateFlow()
    
    private val _selectedMacro = MutableStateFlow<Macro?>(null)
    val selectedMacro: StateFlow<Macro?> = _selectedMacro.asStateFlow()
    
    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadMacros()
    }
    
    /**
     * Load all macros from storage
     */
    fun loadMacros() {
        scope.launch {
            _isLoading.value = true
            try {
                val macroList = fileManager.listMacros()
                _macros.value = macroList
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load macros: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Select a macro by ID
     */
    fun selectMacro(macroId: String) {
        scope.launch {
            try {
                val macro = fileManager.loadMacro(macroId)
                _selectedMacro.value = macro
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load macro: ${e.message}"
            }
        }
    }
    
    /**
     * Start recording a new macro
     */
    fun startRecording(config: RecordingConfig = RecordingConfig(), countdownSeconds: Int = 3) {
        scope.launch {
            try {
                recordingSession = RecordingSession(inputRecorder, config)
                
                recordingSession?.startRecording(countdownSeconds)?.collect { state ->
                    _recordingState.value = state
                }
                
                // Monitor recording progress
                recordingSession?.progress?.collect { progress ->
                    _recordingProgress.value = progress
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to start recording: ${e.message}"
                _recordingState.value = RecordingState.ERROR
            }
        }
    }
    
    /**
     * Stop recording and save the macro
     */
    suspend fun stopRecording(name: String, description: String = ""): Macro? {
        return try {
            val macro = recordingSession?.stopRecording()
            
            if (macro != null) {
                // Update name and description
                val updatedMacro = macro.copy(
                    name = name,
                    description = description,
                    metadata = macro.metadata.copy(
                        modifiedAt = Instant.now().toString()
                    )
                )
                
                // Save to storage
                val success = fileManager.saveMacro(updatedMacro)
                if (success) {
                    loadMacros() // Refresh the list
                    _recordingState.value = RecordingState.IDLE
                    updatedMacro
                } else {
                    _errorMessage.value = "Failed to save macro"
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            _errorMessage.value = "Failed to stop recording: ${e.message}"
            null
        }
    }
    
    /**
     * Cancel current recording
     */
    fun cancelRecording() {
        scope.launch {
            try {
                recordingSession?.cancelRecording()
                _recordingState.value = RecordingState.IDLE
            } catch (e: Exception) {
                _errorMessage.value = "Failed to cancel recording: ${e.message}"
            }
        }
    }
    
    /**
     * Play a macro
     */
    fun playMacro(macro: Macro, iterations: Int = 1, speedMultiplier: Double = 1.0) {
        scope.launch {
            try {
                playbackController.executeMacro(macro, iterations, speedMultiplier)
                    .collect { result ->
                        if (!result.success) {
                            _errorMessage.value = "Playback failed: ${result.errorMessage}"
                        }
                    }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to play macro: ${e.message}"
            }
        }
    }
    
    /**
     * Stop current playback
     */
    fun stopPlayback() {
        scope.launch {
            playbackController.stopPlayback()
        }
    }
    
    /**
     * Pause current playback
     */
    fun pausePlayback() {
        scope.launch {
            playbackController.pausePlayback()
        }
    }
    
    /**
     * Resume paused playback
     */
    fun resumePlayback() {
        scope.launch {
            playbackController.resumePlayback()
        }
    }
    
    /**
     * Delete a macro
     */
    fun deleteMacro(macroId: String) {
        scope.launch {
            try {
                val success = fileManager.deleteMacro(macroId)
                if (success) {
                    loadMacros()
                    if (_selectedMacro.value?.id == macroId) {
                        _selectedMacro.value = null
                    }
                } else {
                    _errorMessage.value = "Failed to delete macro"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete macro: ${e.message}"
            }
        }
    }
    
    /**
     * Export a macro
     */
    suspend fun exportMacro(macro: Macro, filePath: String): Boolean {
        return try {
            fileManager.exportMacro(macro, filePath)
        } catch (e: Exception) {
            _errorMessage.value = "Failed to export macro: ${e.message}"
            false
        }
    }
    
    /**
     * Import a macro
     */
    fun importMacro(filePath: String) {
        scope.launch {
            try {
                val macro = fileManager.importMacro(filePath)
                if (macro != null) {
                    val success = fileManager.saveMacro(macro)
                    if (success) {
                        loadMacros()
                    }
                } else {
                    _errorMessage.value = "Failed to import macro"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to import macro: ${e.message}"
            }
        }
    }
    
    /**
     * Duplicate a macro
     */
    fun duplicateMacro(macro: Macro) {
        scope.launch {
            try {
                val duplicated = macro.copy(
                    id = UUID.randomUUID().toString(),
                    name = "${macro.name} (Copy)",
                    metadata = macro.metadata.copy(
                        createdAt = Instant.now().toString(),
                        modifiedAt = Instant.now().toString(),
                        executionCount = 0
                    )
                )
                val success = fileManager.saveMacro(duplicated)
                if (success) {
                    loadMacros()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to duplicate macro: ${e.message}"
            }
        }
    }
    
    /**
     * Create backup
     */
    suspend fun createBackup(backupPath: String): Boolean {
        return try {
            fileManager.createBackup(backupPath)
        } catch (e: Exception) {
            _errorMessage.value = "Failed to create backup: ${e.message}"
            false
        }
    }
    
    /**
     * Restore backup
     */
    suspend fun restoreBackup(backupPath: String): Boolean {
        return try {
            val success = fileManager.restoreBackup(backupPath)
            if (success) {
                loadMacros()
            }
            success
        } catch (e: Exception) {
            _errorMessage.value = "Failed to restore backup: ${e.message}"
            false
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Clean up resources
     */
    fun dispose() {
        scope.cancel()
    }
}

