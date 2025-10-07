package com.pgmacdesign.macrox.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Represents a complete macro with all its events and configuration
 */
@Serializable
data class Macro(
    /**
     * Unique identifier for this macro
     */
    val id: String,
    
    /**
     * Human-readable name for this macro
     */
    val name: String,
    
    /**
     * Description of what this macro does
     */
    val description: String = "",
    
    /**
     * List of events that make up this macro
     */
    val events: List<Event>,
    
    /**
     * Randomization configuration for playback
     */
    val randomization: RandomizationConfig = RandomizationConfig(),
    
    /**
     * Branching configuration for complex behaviors
     */
    val branching: BranchingConfig = BranchingConfig(),
    
    /**
     * Playback configuration
     */
    val playbackConfig: PlaybackConfig = PlaybackConfig(),
    
    /**
     * Metadata about when this macro was created and modified
     */
    val metadata: MacroMetadata = MacroMetadata(),
    
    /**
     * Version of the macro format (for future compatibility)
     */
    val version: String = "1.0"
) {
    /**
     * Total duration of the macro in milliseconds
     */
    @Transient
    val durationMs: Long = if (events.isEmpty()) 0 else 
        (events.maxOfOrNull { it.timestamp }?.toLong() ?: 0)
    
    /**
     * Number of events in this macro
     */
    @Transient
    val eventCount: Int = events.size
    
    /**
     * Whether this macro has any randomization enabled
     */
    @Transient
    val hasRandomization: Boolean = randomization.enabled
    
    /**
     * Whether this macro has any branching enabled
     */
    @Transient
    val hasBranching: Boolean = branching.enabled
}

/**
 * Configuration for macro playback behavior
 */
@Serializable
data class PlaybackConfig(
    /**
     * Default number of iterations to play (0 = infinite)
     */
    val defaultIterations: Int = 1,
    
    /**
     * Speed multiplier for playback (1.0 = normal speed)
     */
    val speedMultiplier: Double = 1.0,
    
    /**
     * Whether to loop the macro indefinitely
     */
    val infiniteLoop: Boolean = false,
    
    /**
     * Delay between iterations in milliseconds
     */
    val iterationDelayMs: Long = 0,
    
    /**
     * Hotkey configuration for controlling playback
     */
    val hotkeys: PlaybackHotkeys = PlaybackHotkeys()
)

/**
 * Hotkey configuration for macro playback control
 */
@Serializable
data class PlaybackHotkeys(
    /**
     * Hotkey to start/stop playback
     */
    val playPause: String = "F9",
    
    /**
     * Hotkey to stop playback immediately
     */
    val stop: String = "F10",
    
    /**
     * Hotkey to abort recording
     */
    val abortRecording: String = "F12"
)

/**
 * Metadata about the macro
 */
@Serializable
data class MacroMetadata(
    /**
     * When this macro was created (ISO 8601 format)
     */
    val createdAt: String = "",
    
    /**
     * When this macro was last modified (ISO 8601 format)
     */
    val modifiedAt: String = "",
    
    /**
     * Who created this macro
     */
    val createdBy: String = "",
    
    /**
     * Tags for organizing macros
     */
    val tags: Set<String> = emptySet(),
    
    /**
     * Platform this macro was recorded on
     */
    val recordedPlatform: String = "",
    
    /**
     * Screen resolution when recorded
     */
    val recordedResolution: String = "",
    
    /**
     * Number of times this macro has been executed
     */
    val executionCount: Long = 0
)

/**
 * Current state of macro execution
 */
enum class MacroState {
    IDLE,
    RECORDING,
    PLAYING,
    PAUSED,
    STOPPED,
    ERROR
}

/**
 * Result of macro execution
 */
@Serializable
data class MacroExecutionResult(
    /**
     * Whether the execution completed successfully
     */
    val success: Boolean,
    
    /**
     * Number of iterations completed
     */
    val iterationsCompleted: Int,
    
    /**
     * Number of events executed
     */
    val eventsExecuted: Int,
    
    /**
     * Total execution time in milliseconds
     */
    val executionTimeMs: Long,
    
    /**
     * Error message if execution failed
     */
    val errorMessage: String? = null,
    
    /**
     * Detailed execution log
     */
    val executionLog: List<String> = emptyList()
)
