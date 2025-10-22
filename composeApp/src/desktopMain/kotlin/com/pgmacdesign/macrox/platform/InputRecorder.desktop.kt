package com.pgmacdesign.macrox.platform

import com.pgmacdesign.macrox.model.Event
import com.pgmacdesign.macrox.platform.macos.MacOSInputRecorder
import kotlinx.coroutines.flow.Flow

/**
 * Desktop implementation of InputRecorder using macOS-specific implementation
 * Works on macOS/Linux, can be replaced with Windows implementation when on Windows
 */
actual class InputRecorder {
    private val impl = MacOSInputRecorder()
    
    actual suspend fun startRecording(): Flow<Event> {
        return impl.startRecording()
    }
    
    actual suspend fun stopRecording() {
        impl.stopRecording()
    }
    
    actual val isRecording: Boolean
        get() = impl.isRecording
    
    actual suspend fun registerStopHotkey(keyCode: Int, callback: () -> Unit) {
        impl.registerStopHotkey(keyCode, callback)
    }
    
    actual suspend fun unregisterStopHotkey() {
        impl.unregisterStopHotkey()
    }
}
