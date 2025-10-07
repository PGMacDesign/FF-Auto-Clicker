package com.pgmacdesign.macrox.platform

import com.pgmacdesign.macrox.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Desktop implementation of InputPlayback - stub implementation for now
 */
actual class InputPlayback {
    actual suspend fun executeEvent(event: Event): Boolean {
        // TODO: Implement actual event execution
        return true
    }
    
    actual suspend fun executeEvents(events: List<Event>, iterations: Int): Flow<PlaybackProgress> {
        // TODO: Implement actual events execution
        return emptyFlow()
    }
    
    actual suspend fun stopPlayback() {
        // TODO: Implement actual stop playback
    }
    
    actual suspend fun pausePlayback() {
        // TODO: Implement actual pause playback
    }
    
    actual suspend fun resumePlayback() {
        // TODO: Implement actual resume playback
    }
    
    actual val isPlaying: Boolean = false
    
    actual val isPaused: Boolean = false
    
    actual suspend fun registerPlaybackHotkeys(
        playPauseKey: Int,
        stopKey: Int,
        onPlayPause: () -> Unit,
        onStop: () -> Unit
    ) {
        // TODO: Implement hotkey registration
    }
    
    actual suspend fun unregisterPlaybackHotkeys() {
        // TODO: Implement hotkey unregistration
    }
}
