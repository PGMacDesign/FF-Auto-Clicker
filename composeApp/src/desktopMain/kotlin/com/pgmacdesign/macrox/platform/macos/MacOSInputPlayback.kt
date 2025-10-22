package com.pgmacdesign.macrox.platform.macos

import com.pgmacdesign.macrox.model.*
import com.pgmacdesign.macrox.platform.PlaybackProgress
import com.pgmacdesign.macrox.platform.PlaybackState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent

/**
 * macOS-specific implementation for input playback using Java AWT Robot
 * This provides a functional implementation for testing on macOS
 */
class MacOSInputPlayback {
    private var playbackJob: Job? = null
    private val progressChannel = Channel<PlaybackProgress>(Channel.UNLIMITED)
    private var currentState = PlaybackState.COMPLETED
    private val robot = Robot()
    
    var isPlaying: Boolean = false
        private set
    
    var isPaused: Boolean = false
        private set
    
    init {
        // Enable smooth mouse movements
        robot.autoDelay = 0
        robot.isAutoWaitForIdle = false
    }
    
    suspend fun executeEvent(event: Event): Boolean {
        return try {
            when (event) {
                is MouseMoveEvent -> executeMouseMove(event)
                is MouseDownEvent -> executeMouseDown(event)
                is MouseUpEvent -> executeMouseUp(event)
                is MouseWheelEvent -> executeMouseWheel(event)
                is KeyDownEvent -> executeKeyDown(event)
                is KeyUpEvent -> executeKeyUp(event)
                is DelayEvent -> executeDelay(event)
                else -> false
            }
        } catch (e: Exception) {
            println("Error executing event: ${e.message}")
            false
        }
    }
    
    suspend fun executeEvents(
        events: List<Event>,
        iterations: Int
    ): Flow<PlaybackProgress> {
        if (isPlaying) {
            stopPlayback()
        }
        
        playbackJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                isPlaying = true
                currentState = PlaybackState.STARTING
                
                val totalIterations = if (iterations <= 0) Int.MAX_VALUE else iterations
                val startTime = System.currentTimeMillis()
                
                progressChannel.send(
                    PlaybackProgress(
                        currentIteration = 0,
                        totalIterations = totalIterations,
                        currentEventIndex = 0,
                        totalEvents = events.size,
                        elapsedTimeMs = 0,
                        state = PlaybackState.STARTING
                    )
                )
                
                var currentIteration = 1
                
                while (currentIteration <= totalIterations && isPlaying) {
                    currentState = PlaybackState.PLAYING
                    
                    for ((index, event) in events.withIndex()) {
                        if (!isPlaying) break
                        
                        // Handle pause
                        while (isPaused && isPlaying) {
                            currentState = PlaybackState.PAUSED
                            delay(50)
                        }
                        
                        if (!isPlaying) break
                        
                        currentState = PlaybackState.PLAYING
                        
                        // Send progress update
                        progressChannel.send(
                            PlaybackProgress(
                                currentIteration = currentIteration,
                                totalIterations = totalIterations,
                                currentEventIndex = index,
                                totalEvents = events.size,
                                elapsedTimeMs = System.currentTimeMillis() - startTime,
                                state = currentState
                            )
                        )
                        
                        // Execute the event
                        val success = executeEvent(event)
                        if (!success) {
                            progressChannel.send(
                                PlaybackProgress(
                                    currentIteration = currentIteration,
                                    totalIterations = totalIterations,
                                    currentEventIndex = index,
                                    totalEvents = events.size,
                                    elapsedTimeMs = System.currentTimeMillis() - startTime,
                                    state = PlaybackState.ERROR,
                                    errorMessage = "Failed to execute event: ${event::class.simpleName}"
                                )
                            )
                            return@launch
                        }
                        
                        // Apply timing if this is not the last event
                        if (index < events.size - 1) {
                            val nextEvent = events[index + 1]
                            val delayMs = (nextEvent.timestamp - event.timestamp).toLong()
                            if (delayMs > 0) {
                                delay(delayMs)
                            }
                        }
                    }
                    
                    currentIteration++
                    
                    // Add delay between iterations if specified
                    if (currentIteration <= totalIterations) {
                        delay(100) // Default 100ms between iterations
                    }
                }
                
                currentState = PlaybackState.COMPLETED
                progressChannel.send(
                    PlaybackProgress(
                        currentIteration = currentIteration - 1,
                        totalIterations = totalIterations,
                        currentEventIndex = events.size,
                        totalEvents = events.size,
                        elapsedTimeMs = System.currentTimeMillis() - startTime,
                        state = PlaybackState.COMPLETED
                    )
                )
                
            } catch (e: Exception) {
                currentState = PlaybackState.ERROR
                progressChannel.send(
                    PlaybackProgress(
                        currentIteration = 0,
                        totalIterations = iterations,
                        currentEventIndex = 0,
                        totalEvents = events.size,
                        elapsedTimeMs = 0,
                        state = PlaybackState.ERROR,
                        errorMessage = e.message
                    )
                )
            } finally {
                isPlaying = false
                isPaused = false
            }
        }
        
        return progressChannel.receiveAsFlow()
    }
    
    suspend fun stopPlayback() {
        isPlaying = false
        isPaused = false
        currentState = PlaybackState.STOPPING
        playbackJob?.cancel()
        playbackJob = null
    }
    
    suspend fun pausePlayback() {
        if (isPlaying) {
            isPaused = true
        }
    }
    
    suspend fun resumePlayback() {
        if (isPlaying) {
            isPaused = false
        }
    }
    
    private fun executeMouseMove(event: MouseMoveEvent): Boolean {
        return try {
            robot.mouseMove(event.x, event.y)
            println("✓ Mouse moved to (${event.x}, ${event.y})")
            true
        } catch (e: Exception) {
            println("✗ Failed to move mouse: ${e.message}")
            false
        }
    }
    
    private fun executeMouseDown(event: MouseDownEvent): Boolean {
        return try {
            robot.mouseMove(event.x, event.y)
            val button = when (event.button) {
                MouseButton.LEFT -> InputEvent.BUTTON1_DOWN_MASK
                MouseButton.RIGHT -> InputEvent.BUTTON3_DOWN_MASK
                MouseButton.MIDDLE -> InputEvent.BUTTON2_DOWN_MASK
                else -> InputEvent.BUTTON1_DOWN_MASK
            }
            robot.mousePress(button)
            println("✓ Mouse ${event.button} pressed at (${event.x}, ${event.y})")
            true
        } catch (e: Exception) {
            println("✗ Failed to press mouse: ${e.message}")
            false
        }
    }
    
    private fun executeMouseUp(event: MouseUpEvent): Boolean {
        return try {
            val button = when (event.button) {
                MouseButton.LEFT -> InputEvent.BUTTON1_DOWN_MASK
                MouseButton.RIGHT -> InputEvent.BUTTON3_DOWN_MASK
                MouseButton.MIDDLE -> InputEvent.BUTTON2_DOWN_MASK
                else -> InputEvent.BUTTON1_DOWN_MASK
            }
            robot.mouseRelease(button)
            println("✓ Mouse ${event.button} released at (${event.x}, ${event.y})")
            true
        } catch (e: Exception) {
            println("✗ Failed to release mouse: ${e.message}")
            false
        }
    }
    
    private fun executeMouseWheel(event: MouseWheelEvent): Boolean {
        return try {
            robot.mouseWheel(event.deltaY)
            println("✓ Mouse wheel scrolled ${event.deltaY}")
            true
        } catch (e: Exception) {
            println("✗ Failed to scroll wheel: ${e.message}")
            false
        }
    }
    
    private fun executeKeyDown(event: KeyDownEvent): Boolean {
        return try {
            // Map key codes - this is simplified, real implementation would need full mapping
            val keyCode = mapKeyCode(event.keyCode, event.keyName)
            robot.keyPress(keyCode)
            println("✓ Key pressed: ${event.keyName}")
            true
        } catch (e: Exception) {
            println("✗ Failed to press key: ${e.message}")
            false
        }
    }
    
    private fun executeKeyUp(event: KeyUpEvent): Boolean {
        return try {
            val keyCode = mapKeyCode(event.keyCode, event.keyName)
            robot.keyRelease(keyCode)
            println("✓ Key released: ${event.keyName}")
            true
        } catch (e: Exception) {
            println("✗ Failed to release key: ${e.message}")
            false
        }
    }
    
    private suspend fun executeDelay(event: DelayEvent): Boolean {
        delay(event.delayMs)
        return true
    }
    
    /**
     * Map key codes to Java KeyEvent codes
     * This is a simplified mapping - full implementation would need comprehensive mapping
     */
    private fun mapKeyCode(code: Int, name: String): Int {
        // Try to match by name first
        return when (name.uppercase()) {
            "A" -> KeyEvent.VK_A
            "B" -> KeyEvent.VK_B
            "C" -> KeyEvent.VK_C
            "D" -> KeyEvent.VK_D
            "E" -> KeyEvent.VK_E
            "F" -> KeyEvent.VK_F
            "G" -> KeyEvent.VK_G
            "H" -> KeyEvent.VK_H
            "I" -> KeyEvent.VK_I
            "J" -> KeyEvent.VK_J
            "K" -> KeyEvent.VK_K
            "L" -> KeyEvent.VK_L
            "M" -> KeyEvent.VK_M
            "N" -> KeyEvent.VK_N
            "O" -> KeyEvent.VK_O
            "P" -> KeyEvent.VK_P
            "Q" -> KeyEvent.VK_Q
            "R" -> KeyEvent.VK_R
            "S" -> KeyEvent.VK_S
            "T" -> KeyEvent.VK_T
            "U" -> KeyEvent.VK_U
            "V" -> KeyEvent.VK_V
            "W" -> KeyEvent.VK_W
            "X" -> KeyEvent.VK_X
            "Y" -> KeyEvent.VK_Y
            "Z" -> KeyEvent.VK_Z
            "SPACE" -> KeyEvent.VK_SPACE
            "ENTER" -> KeyEvent.VK_ENTER
            "TAB" -> KeyEvent.VK_TAB
            "ESCAPE" -> KeyEvent.VK_ESCAPE
            "BACKSPACE" -> KeyEvent.VK_BACK_SPACE
            "DELETE" -> KeyEvent.VK_DELETE
            "SHIFT" -> KeyEvent.VK_SHIFT
            "CONTROL", "CTRL" -> KeyEvent.VK_CONTROL
            "ALT" -> KeyEvent.VK_ALT
            "META", "COMMAND", "CMD" -> KeyEvent.VK_META
            else -> code // Use the provided code as fallback
        }
    }
}

