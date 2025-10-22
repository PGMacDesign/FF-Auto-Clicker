package com.pgmacdesign.macrox.engine

import com.pgmacdesign.macrox.model.*
import com.pgmacdesign.macrox.platform.InputPlayback
import com.pgmacdesign.macrox.platform.PlaybackProgress
import com.pgmacdesign.macrox.platform.PlaybackState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

/**
 * Controls macro playback with timing precision, randomization, and branching support
 */
class PlaybackController(
    private val inputPlayback: InputPlayback
) {
    private var playbackJob: Job? = null
    private val _state = MutableStateFlow(MacroState.IDLE)
    private val _progress = MutableStateFlow(PlaybackControllerProgress())
    
    /**
     * Current playback state
     */
    val state: StateFlow<MacroState> = _state.asStateFlow()
    
    /**
     * Current playback progress
     */
    val progress: StateFlow<PlaybackControllerProgress> = _progress.asStateFlow()
    
    /**
     * Execute a macro with the specified configuration
     */
    suspend fun executeMacro(
        macro: Macro,
        iterations: Int = macro.playbackConfig.defaultIterations,
        speedMultiplier: Double = macro.playbackConfig.speedMultiplier
    ): Flow<MacroExecutionResult> {
        if (_state.value != MacroState.IDLE) {
            stopPlayback()
        }
        
        return channelFlow {
            try {
                _state.value = MacroState.PLAYING
                
                val processedEvents = preprocessEvents(macro.events, macro.randomization, speedMultiplier)
                val totalIterations = if (iterations <= 0) Int.MAX_VALUE else iterations
                
                _progress.value = PlaybackControllerProgress(
                    macroId = macro.id,
                    macroName = macro.name,
                    totalIterations = totalIterations,
                    totalEvents = processedEvents.size
                )
                
                playbackJob = CoroutineScope(Dispatchers.Default).launch {
                    var completedIterations = 0
                    var totalEventsExecuted = 0
                    val startTime = System.currentTimeMillis()
                    val executionLog = mutableListOf<String>()
                    
                    try {
                        for (iteration in 1..totalIterations) {
                            if (_state.value != MacroState.PLAYING) break
                            
                            _progress.value = _progress.value.copy(
                                currentIteration = iteration,
                                currentEventIndex = 0
                            )
                            
                            // Execute branching logic before iteration
                            val branchResult = executeBranchingLogic(macro, iteration)
                            if (branchResult != null) {
                                executionLog.add("Branch executed: ${branchResult.action}")
                                
                                when (branchResult.action) {
                                    is BranchAction.StopExecution -> break
                                    is BranchAction.ExecuteMacro -> {
                                        // TODO: Implement submacro execution
                                        executionLog.add("Submacro execution not yet implemented")
                                    }
                                    is BranchAction.PauseExecution -> {
                                        delay(branchResult.action.durationMs)
                                    }
                                    else -> { /* Handle other branch actions */ }
                                }
                            }
                            
                            // Execute events for this iteration
                            for ((index, event) in processedEvents.withIndex()) {
                                if (_state.value != MacroState.PLAYING) break
                                
                                _progress.value = _progress.value.copy(
                                    currentEventIndex = index + 1
                                )
                                
                                val success = inputPlayback.executeEvent(event)
                                if (success) {
                                    totalEventsExecuted++
                                    executionLog.add("Executed: ${event::class.simpleName} at ${event.timestamp}ms")
                                } else {
                                    executionLog.add("Failed: ${event::class.simpleName} at ${event.timestamp}ms")
                                }
                                
                                // Apply timing delay to next event
                                if (index < processedEvents.size - 1) {
                                    val nextEvent = processedEvents[index + 1]
                                    val baseDelay = ((nextEvent.timestamp - event.timestamp) / speedMultiplier).toLong()
                                    val randomizedDelay = applyDelayRandomization(baseDelay, macro.randomization)
                                    
                                    if (randomizedDelay > 0) {
                                        delay(randomizedDelay)
                                    }
                                }
                            }
                            
                            completedIterations++
                            
                            // Delay between iterations
                            if (iteration < totalIterations && macro.playbackConfig.iterationDelayMs > 0) {
                                delay(macro.playbackConfig.iterationDelayMs)
                            }
                        }
                        
                        val executionTime = System.currentTimeMillis() - startTime
                        val result = MacroExecutionResult(
                            success = true,
                            iterationsCompleted = completedIterations,
                            eventsExecuted = totalEventsExecuted,
                            executionTimeMs = executionTime,
                            executionLog = executionLog
                        )
                        
                        send(result)
                        
                    } catch (e: Exception) {
                        val executionTime = System.currentTimeMillis() - startTime
                        val result = MacroExecutionResult(
                            success = false,
                            iterationsCompleted = completedIterations,
                            eventsExecuted = totalEventsExecuted,
                            executionTimeMs = executionTime,
                            errorMessage = e.message,
                            executionLog = executionLog
                        )
                        
                        send(result)
                    }
                }
                
                // Monitor playback progress
                inputPlayback.executeEvents(processedEvents, totalIterations)
                    .collect { progress ->
                        _progress.value = _progress.value.copy(
                            currentIteration = progress.currentIteration,
                            currentEventIndex = progress.currentEventIndex,
                            elapsedTimeMs = progress.elapsedTimeMs
                        )
                        
                        when (progress.state) {
                            PlaybackState.COMPLETED -> _state.value = MacroState.STOPPED
                            PlaybackState.ERROR -> _state.value = MacroState.ERROR
                            PlaybackState.ABORTED -> _state.value = MacroState.STOPPED
                            else -> { /* Continue monitoring */ }
                        }
                    }
                
            } catch (e: Exception) {
                _state.value = MacroState.ERROR
                send(MacroExecutionResult(
                    success = false,
                    iterationsCompleted = 0,
                    eventsExecuted = 0,
                    executionTimeMs = 0,
                    errorMessage = e.message
                ))
            } finally {
                _state.value = MacroState.IDLE
                playbackJob = null
            }
        }
    }
    
    /**
     * Stop current playback immediately
     */
    suspend fun stopPlayback() {
        if (_state.value == MacroState.PLAYING || _state.value == MacroState.PAUSED) {
            _state.value = MacroState.STOPPED
            inputPlayback.stopPlayback()
            playbackJob?.cancel()
            playbackJob = null
        }
    }
    
    /**
     * Pause current playback
     */
    suspend fun pausePlayback() {
        if (_state.value == MacroState.PLAYING) {
            _state.value = MacroState.PAUSED
            inputPlayback.pausePlayback()
        }
    }
    
    /**
     * Resume paused playback
     */
    suspend fun resumePlayback() {
        if (_state.value == MacroState.PAUSED) {
            _state.value = MacroState.PLAYING
            inputPlayback.resumePlayback()
        }
    }
    
    private fun preprocessEvents(
        events: List<Event>,
        randomization: RandomizationConfig,
        speedMultiplier: Double
    ): List<Event> {
        return events.map { event ->
            when (event) {
                is MouseDownEvent, is MouseUpEvent -> {
                    if (randomization.enabled && randomization.clickRandomization.enabled) {
                        applyClickRandomization(event, randomization.clickRandomization)
                    } else event
                }
                else -> event
            }
        }
    }
    
    private fun applyClickRandomization(event: Event, config: ClickRandomization): Event {
        if (Random.nextDouble() > config.probability) {
            return event
        }
        
        val offsetX = Random.nextInt(-config.maxOffsetX, config.maxOffsetX + 1)
        val offsetY = Random.nextInt(-config.maxOffsetY, config.maxOffsetY + 1)
        
        return when (event) {
            is MouseDownEvent -> event.copy(
                x = (event.x + offsetX).coerceAtLeast(0),
                y = (event.y + offsetY).coerceAtLeast(0)
            )
            is MouseUpEvent -> event.copy(
                x = (event.x + offsetX).coerceAtLeast(0),
                y = (event.y + offsetY).coerceAtLeast(0)
            )
            else -> event
        }
    }
    
    private fun applyDelayRandomization(baseDelayMs: Long, config: RandomizationConfig): Long {
        if (!config.enabled || !config.delayRandomization.enabled) {
            return baseDelayMs
        }
        
        val delayConfig = config.delayRandomization
        
        // Check frequency
        if (delayConfig.frequency.everyNthEvent > 0) {
            // TODO: Implement nth event tracking
        }
        
        if (Random.nextDouble() > delayConfig.frequency.probability) {
            return baseDelayMs
        }
        
        val randomOffset = when (delayConfig.distribution) {
            RandomDistribution.UNIFORM -> 
                Random.nextLong(delayConfig.minDelayMs, delayConfig.maxDelayMs + 1)
            RandomDistribution.GAUSSIAN -> {
                val mean = (delayConfig.minDelayMs + delayConfig.maxDelayMs) / 2.0
                val stdDev = (delayConfig.maxDelayMs - delayConfig.minDelayMs) / 6.0
                // Simple Gaussian approximation using Box-Muller transform
                val u1 = Random.nextDouble()
                val u2 = Random.nextDouble()
                val z0 = kotlin.math.sqrt(-2.0 * kotlin.math.ln(u1)) * kotlin.math.cos(2.0 * kotlin.math.PI * u2)
                (z0 * stdDev + mean).toLong()
                    .coerceIn(delayConfig.minDelayMs, delayConfig.maxDelayMs)
            }
            RandomDistribution.EXPONENTIAL -> {
                // Simple exponential approximation
                val lambda = 1.0 / (delayConfig.maxDelayMs - delayConfig.minDelayMs)
                (-kotlin.math.ln(Random.nextDouble()) / lambda).toLong() + delayConfig.minDelayMs
            }
        }
        
        return (baseDelayMs + randomOffset).coerceAtLeast(0)
    }
    
    private suspend fun executeBranchingLogic(macro: Macro, currentIteration: Int): BranchRule? {
        if (!macro.branching.enabled) return null
        
        for (branch in macro.branching.branches) {
            if (!branch.enabled) continue
            
            val shouldTrigger = when (val trigger = branch.trigger) {
                is BranchTrigger.EveryNIterations -> 
                    currentIteration % trigger.n == 0
                is BranchTrigger.RandomProbability -> 
                    Random.nextDouble() < trigger.probability
                is BranchTrigger.AfterNEvents -> 
                    false // TODO: Implement event counting
                is BranchTrigger.AtTimeOffset -> 
                    false // TODO: Implement time-based triggers
                is BranchTrigger.OnEventType -> 
                    false // TODO: Implement event type triggers
            }
            
            if (shouldTrigger) {
                return branch
            }
        }
        
        return null
    }
}

/**
 * Progress information for playback controller
 */
data class PlaybackControllerProgress(
    val macroId: String = "",
    val macroName: String = "",
    val currentIteration: Int = 0,
    val totalIterations: Int = 0,
    val currentEventIndex: Int = 0,
    val totalEvents: Int = 0,
    val elapsedTimeMs: Long = 0,
    val estimatedRemainingMs: Long = 0
)
