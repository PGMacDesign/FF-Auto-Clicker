package com.pgmacdesign.macrox.engine

import com.pgmacdesign.macrox.model.*
import com.pgmacdesign.macrox.platform.InputRecorder
import com.pgmacdesign.macrox.platform.RecordingConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.util.*

/**
 * Manages a recording session with high-precision timestamping and event capture
 */
class RecordingSession(
    private val inputRecorder: InputRecorder,
    private val config: RecordingConfig = RecordingConfig()
) {
    private var recordingJob: Job? = null
    private val _events = mutableListOf<Event>()
    private val _state = MutableStateFlow(RecordingState.IDLE)
    private val _recordingProgress = MutableStateFlow(RecordingProgress())
    
    private var startTime: Long = 0
    private var lastMouseMoveTime: Long = 0
    
    /**
     * Current recording state
     */
    val state: StateFlow<RecordingState> = _state.asStateFlow()
    
    /**
     * Current recording progress
     */
    val progress: StateFlow<RecordingProgress> = _recordingProgress.asStateFlow()
    
    /**
     * Events captured so far in this session
     */
    val events: List<Event> get() = _events.toList()
    
    /**
     * Start recording with an optional countdown
     */
    suspend fun startRecording(countdownSeconds: Int = 3): Flow<RecordingState> {
        if (_state.value != RecordingState.IDLE) {
            throw IllegalStateException("Recording session is already active")
        }
        
        return flow {
            try {
                // Countdown phase
                if (countdownSeconds > 0) {
                    _state.value = RecordingState.COUNTDOWN
                    emit(RecordingState.COUNTDOWN)
                    
                    for (i in countdownSeconds downTo 1) {
                        _recordingProgress.value = RecordingProgress(
                            countdownRemaining = i,
                            isCountingDown = true
                        )
                        delay(1000)
                    }
                }
                
                // Start actual recording
                _state.value = RecordingState.RECORDING
                _recordingProgress.value = RecordingProgress(
                    isRecording = true,
                    startTime = Instant.now().toString()
                )
                emit(RecordingState.RECORDING)
                
                startTime = System.currentTimeMillis()
                _events.clear()
                
                // Register stop hotkey (F12)
                inputRecorder.registerStopHotkey(123) { // F12 key code
                    CoroutineScope(Dispatchers.Default).launch {
                        stopRecording()
                    }
                }
                
                // Start recording input events
                recordingJob = CoroutineScope(Dispatchers.Default).launch {
                    inputRecorder.startRecording()
                        .collect { event ->
                            processEvent(event)
                        }
                }
                
            } catch (e: Exception) {
                _state.value = RecordingState.ERROR
                _recordingProgress.value = RecordingProgress(
                    errorMessage = e.message
                )
                emit(RecordingState.ERROR)
            }
        }
    }
    
    /**
     * Stop the current recording session
     */
    suspend fun stopRecording(): Macro? {
        if (_state.value != RecordingState.RECORDING) {
            return null
        }
        
        try {
            _state.value = RecordingState.STOPPING
            
            // Stop input recording
            inputRecorder.stopRecording()
            inputRecorder.unregisterStopHotkey()
            
            // Cancel recording job
            recordingJob?.cancel()
            recordingJob = null
            
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            
            _state.value = RecordingState.COMPLETED
            _recordingProgress.value = RecordingProgress(
                isCompleted = true,
                eventCount = _events.size,
                durationMs = duration,
                endTime = Instant.now().toString()
            )
            
            // Create macro from recorded events
            return createMacroFromEvents()
            
        } catch (e: Exception) {
            _state.value = RecordingState.ERROR
            _recordingProgress.value = RecordingProgress(
                errorMessage = e.message
            )
            return null
        }
    }
    
    /**
     * Pause the current recording (events will be ignored but recording continues)
     */
    suspend fun pauseRecording() {
        if (_state.value == RecordingState.RECORDING) {
            _state.value = RecordingState.PAUSED
            _recordingProgress.value = _recordingProgress.value.copy(
                isPaused = true
            )
        }
    }
    
    /**
     * Resume a paused recording
     */
    suspend fun resumeRecording() {
        if (_state.value == RecordingState.PAUSED) {
            _state.value = RecordingState.RECORDING
            _recordingProgress.value = _recordingProgress.value.copy(
                isPaused = false
            )
        }
    }
    
    /**
     * Cancel the current recording session without saving
     */
    suspend fun cancelRecording() {
        _state.value = RecordingState.CANCELLED
        
        inputRecorder.stopRecording()
        inputRecorder.unregisterStopHotkey()
        
        recordingJob?.cancel()
        recordingJob = null
        
        _events.clear()
        _recordingProgress.value = RecordingProgress()
        _state.value = RecordingState.IDLE
    }
    
    private suspend fun processEvent(event: Event) {
        // Only process events if we're actively recording (not paused)
        if (_state.value != RecordingState.RECORDING) {
            return
        }
        
        // Apply filtering based on configuration
        val shouldRecord = when (event) {
            is MouseMoveEvent -> {
                if (!config.recordMouseMovement) return
                
                // Throttle mouse movement events
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastMouseMoveTime < config.mouseMoveThrottleMs) {
                    return
                }
                lastMouseMoveTime = currentTime
                true
            }
            is MouseDownEvent, is MouseUpEvent -> config.recordMouseClicks
            is MouseWheelEvent -> config.recordMouseWheel
            is KeyDownEvent, is KeyUpEvent -> config.recordKeyboard
            else -> true
        }
        
        if (shouldRecord) {
            _events.add(event)
            
            // Update progress
            _recordingProgress.value = _recordingProgress.value.copy(
                eventCount = _events.size,
                durationMs = System.currentTimeMillis() - startTime,
                lastEventType = event::class.simpleName ?: "Unknown"
            )
        }
    }
    
    private fun createMacroFromEvents(): Macro {
        val macroId = UUID.randomUUID().toString()
        val now = Instant.now().toString()
        
        return Macro(
            id = macroId,
            name = "Recorded Macro ${Instant.now()}",
            description = "Macro recorded on ${Instant.now()}",
            events = _events.toList(),
            metadata = MacroMetadata(
                createdAt = now,
                modifiedAt = now,
                recordedPlatform = System.getProperty("os.name"),
                recordedResolution = "${System.getProperty("java.awt.headless", "unknown")}"
            )
        )
    }
    
    /**
     * Reset the session to idle state
     */
    fun reset() {
        if (_state.value == RecordingState.RECORDING) {
            throw IllegalStateException("Cannot reset while recording is active")
        }
        
        _events.clear()
        _recordingProgress.value = RecordingProgress()
        _state.value = RecordingState.IDLE
    }
}

/**
 * States of a recording session
 */
enum class RecordingState {
    IDLE,
    COUNTDOWN,
    RECORDING,
    PAUSED,
    STOPPING,
    COMPLETED,
    CANCELLED,
    ERROR
}

/**
 * Progress information for a recording session
 */
data class RecordingProgress(
    val isCountingDown: Boolean = false,
    val countdownRemaining: Int = 0,
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val eventCount: Int = 0,
    val durationMs: Long = 0,
    val startTime: String = "",
    val endTime: String = "",
    val lastEventType: String = "",
    val errorMessage: String? = null
)
