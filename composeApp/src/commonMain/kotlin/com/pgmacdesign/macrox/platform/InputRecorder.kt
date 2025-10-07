package com.pgmacdesign.macrox.platform

import com.pgmacdesign.macrox.model.Event
import kotlinx.coroutines.flow.Flow

/**
 * Platform-specific interface for recording input events
 */
expect class InputRecorder {
    /**
     * Start recording input events
     * @return Flow of events as they are captured
     */
    suspend fun startRecording(): Flow<Event>
    
    /**
     * Stop recording input events
     */
    suspend fun stopRecording()
    
    /**
     * Check if recording is currently active
     */
    val isRecording: Boolean
    
    /**
     * Register a global hotkey for stopping recording
     * @param keyCode The key code for the hotkey
     * @param callback Callback to invoke when hotkey is pressed
     */
    suspend fun registerStopHotkey(keyCode: Int, callback: () -> Unit)
    
    /**
     * Unregister the stop hotkey
     */
    suspend fun unregisterStopHotkey()
}

/**
 * Configuration for input recording
 */
data class RecordingConfig(
    /**
     * Whether to record mouse movements
     */
    val recordMouseMovement: Boolean = true,
    
    /**
     * Whether to record mouse clicks
     */
    val recordMouseClicks: Boolean = true,
    
    /**
     * Whether to record keyboard events
     */
    val recordKeyboard: Boolean = true,
    
    /**
     * Whether to record mouse wheel events
     */
    val recordMouseWheel: Boolean = true,
    
    /**
     * Minimum time between mouse movement events (ms) to reduce noise
     */
    val mouseMoveThrottleMs: Long = 10,
    
    /**
     * Whether to use absolute or relative coordinates
     */
    val useAbsoluteCoordinates: Boolean = true
)
