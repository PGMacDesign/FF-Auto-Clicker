package com.pgmacdesign.macrox.platform

import com.pgmacdesign.macrox.model.Event
import com.pgmacdesign.macrox.model.MacroExecutionResult
import kotlinx.coroutines.flow.Flow

/**
 * Platform-specific interface for playing back input events
 */
expect class InputPlayback {
    /**
     * Execute a single event
     * @param event The event to execute
     * @return true if successful, false otherwise
     */
    suspend fun executeEvent(event: Event): Boolean
    
    /**
     * Execute a sequence of events
     * @param events The events to execute
     * @param iterations Number of times to repeat the sequence (0 = infinite)
     * @return Flow of execution progress and results
     */
    suspend fun executeEvents(
        events: List<Event>, 
        iterations: Int = 1
    ): Flow<PlaybackProgress>
    
    /**
     * Stop current playback immediately
     */
    suspend fun stopPlayback()
    
    /**
     * Pause current playback
     */
    suspend fun pausePlayback()
    
    /**
     * Resume paused playback
     */
    suspend fun resumePlayback()
    
    /**
     * Check if playback is currently active
     */
    val isPlaying: Boolean
    
    /**
     * Check if playback is currently paused
     */
    val isPaused: Boolean
    
    /**
     * Register global hotkeys for playback control
     */
    suspend fun registerPlaybackHotkeys(
        playPauseKey: Int,
        stopKey: Int,
        onPlayPause: () -> Unit,
        onStop: () -> Unit
    )
    
    /**
     * Unregister playback hotkeys
     */
    suspend fun unregisterPlaybackHotkeys()
}

/**
 * Progress information during playback
 */
data class PlaybackProgress(
    /**
     * Current iteration number (1-based)
     */
    val currentIteration: Int,
    
    /**
     * Total iterations to execute
     */
    val totalIterations: Int,
    
    /**
     * Current event index within the current iteration
     */
    val currentEventIndex: Int,
    
    /**
     * Total events in the sequence
     */
    val totalEvents: Int,
    
    /**
     * Elapsed time since playback started (ms)
     */
    val elapsedTimeMs: Long,
    
    /**
     * Current playback state
     */
    val state: PlaybackState,
    
    /**
     * Error message if an error occurred
     */
    val errorMessage: String? = null
)

/**
 * Playback state enumeration
 */
enum class PlaybackState {
    STARTING,
    PLAYING,
    PAUSED,
    STOPPING,
    COMPLETED,
    ERROR,
    ABORTED
}
