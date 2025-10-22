package com.pgmacdesign.macrox.platform

import com.pgmacdesign.macrox.model.Event
import com.pgmacdesign.macrox.platform.macos.MacOSInputPlayback
import kotlinx.coroutines.flow.Flow

/**
 * Desktop implementation of InputPlayback using macOS-specific implementation
 * Works on macOS/Linux, can be replaced with Windows implementation when on Windows
 */
actual class InputPlayback {
    private val impl = MacOSInputPlayback()
    
    actual suspend fun executeEvent(event: Event): Boolean {
        return impl.executeEvent(event)
    }
    
    actual suspend fun executeEvents(events: List<Event>, iterations: Int): Flow<PlaybackProgress> {
        return impl.executeEvents(events, iterations)
    }
    
    actual suspend fun stopPlayback() {
        impl.stopPlayback()
    }
    
    actual suspend fun pausePlayback() {
        impl.pausePlayback()
    }
    
    actual suspend fun resumePlayback() {
        impl.resumePlayback()
    }
    
    actual val isPlaying: Boolean
        get() = impl.isPlaying
    
    actual val isPaused: Boolean
        get() = impl.isPaused
    
    actual suspend fun registerPlaybackHotkeys(
        playPauseKey: Int,
        stopKey: Int,
        onPlayPause: () -> Unit,
        onStop: () -> Unit
    ) {
        // Hotkey registration for macOS would go here
        println("⌨️  Playback hotkeys registered (macOS)")
    }
    
    actual suspend fun unregisterPlaybackHotkeys() {
        println("⌨️  Playback hotkeys unregistered (macOS)")
    }
}
