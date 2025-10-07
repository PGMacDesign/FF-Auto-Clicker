package com.pgmacdesign.macrox.platform

import com.pgmacdesign.macrox.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Desktop implementation of InputRecorder - stub implementation for now
 */
actual class InputRecorder {
    actual suspend fun startRecording(): Flow<Event> {
        // TODO: Implement actual recording
        return emptyFlow()
    }
    
    actual suspend fun stopRecording() {
        // TODO: Implement actual stop recording
    }
    
    actual val isRecording: Boolean = false
    
    actual suspend fun registerStopHotkey(keyCode: Int, callback: () -> Unit) {
        // TODO: Implement hotkey registration
    }
    
    actual suspend fun unregisterStopHotkey() {
        // TODO: Implement hotkey unregistration
    }
}
