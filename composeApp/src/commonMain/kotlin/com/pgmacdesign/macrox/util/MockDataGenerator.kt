package com.pgmacdesign.macrox.util

import com.pgmacdesign.macrox.model.*
import java.time.Instant
import java.util.UUID

/**
 * Generates mock data for testing and development
 */
object MockDataGenerator {
    
    /**
     * Generate sample macros for testing
     */
    fun generateSampleMacros(): List<Macro> {
        return listOf(
            createSampleMacro(
                name = "Click Loop",
                description = "Clicks at position every 2 seconds",
                eventCount = 45,
                durationMs = 5000
            ),
            createSampleMacro(
                name = "Fishing Macro",
                description = "Automated fishing with randomization",
                eventCount = 120,
                durationMs = 15000,
                withRandomization = true
            ),
            createSampleMacro(
                name = "Crafting Sequence",
                description = "Crafts items in sequence",
                eventCount = 89,
                durationMs = 12000
            ),
            createSampleMacro(
                name = "Quick Test",
                description = "Simple test macro",
                eventCount = 12,
                durationMs = 2000
            ),
            createSampleMacro(
                name = "Combat Rotation",
                description = "Performs combat rotation with skill delays",
                eventCount = 156,
                durationMs = 30000,
                withRandomization = true,
                withBranching = true
            )
        )
    }
    
    private fun createSampleMacro(
        name: String,
        description: String,
        eventCount: Int,
        durationMs: Long,
        withRandomization: Boolean = false,
        withBranching: Boolean = false
    ): Macro {
        val events = generateSampleEvents(eventCount, durationMs)
        val now = Instant.now().toString()
        
        return Macro(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            events = events,
            randomization = if (withRandomization) {
                RandomizationConfig(
                    enabled = true,
                    delayRandomization = DelayRandomization(
                        minDelayMs = -50,
                        maxDelayMs = 100,
                        enabled = true,
                        frequency = RandomizationFrequency(probability = 0.8)
                    ),
                    clickRandomization = ClickRandomization(
                        maxOffsetX = 10,
                        maxOffsetY = 10,
                        probability = 0.5,
                        enabled = true
                    )
                )
            } else {
                RandomizationConfig()
            },
            branching = if (withBranching) {
                BranchingConfig(
                    enabled = true,
                    branches = listOf(
                        BranchRule(
                            id = UUID.randomUUID().toString(),
                            name = "Every 5 iterations",
                            trigger = BranchTrigger.EveryNIterations(5),
                            action = BranchAction.PauseExecution(1000)
                        )
                    )
                )
            } else {
                BranchingConfig()
            },
            metadata = MacroMetadata(
                createdAt = now,
                modifiedAt = now,
                createdBy = "MockGenerator",
                recordedPlatform = System.getProperty("os.name"),
                executionCount = (0..500).random().toLong()
            )
        )
    }
    
    private fun generateSampleEvents(count: Int, totalDurationMs: Long): List<Event> {
        val events = mutableListOf<Event>()
        val timeStep = totalDurationMs.toDouble() / count
        
        for (i in 0 until count) {
            val timestamp = i * timeStep
            val eventId = UUID.randomUUID().toString()
            
            when (i % 4) {
                0 -> events.add(
                    MouseMoveEvent(
                        timestamp = timestamp,
                        eventId = eventId,
                        x = (100..800).random(),
                        y = (100..600).random()
                    )
                )
                1 -> events.add(
                    MouseDownEvent(
                        timestamp = timestamp,
                        eventId = eventId,
                        x = (100..800).random(),
                        y = (100..600).random(),
                        button = MouseButton.LEFT
                    )
                )
                2 -> events.add(
                    MouseUpEvent(
                        timestamp = timestamp,
                        eventId = eventId,
                        x = (100..800).random(),
                        y = (100..600).random(),
                        button = MouseButton.LEFT
                    )
                )
                3 -> events.add(
                    KeyDownEvent(
                        timestamp = timestamp,
                        eventId = eventId,
                        keyCode = (65..90).random(), // A-Z
                        keyName = ('A'..'Z').random().toString()
                    )
                )
            }
        }
        
        return events
    }
    
    /**
     * Generate a simple test macro for immediate playback testing
     */
    fun generateSimpleTestMacro(): Macro {
        val events = listOf(
            MouseMoveEvent(0.0, UUID.randomUUID().toString(), 500, 500),
            MouseDownEvent(100.0, UUID.randomUUID().toString(), 500, 500, MouseButton.LEFT),
            MouseUpEvent(150.0, UUID.randomUUID().toString(), 500, 500, MouseButton.LEFT),
            DelayEvent(200.0, UUID.randomUUID().toString(), 500),
            MouseMoveEvent(700.0, UUID.randomUUID().toString(), 600, 400),
            MouseDownEvent(800.0, UUID.randomUUID().toString(), 600, 400, MouseButton.LEFT),
            MouseUpEvent(850.0, UUID.randomUUID().toString(), 600, 400, MouseButton.LEFT)
        )
        
        return Macro(
            id = "test-macro-simple",
            name = "Simple Test Macro",
            description = "A simple macro for testing playback",
            events = events,
            metadata = MacroMetadata(
                createdAt = Instant.now().toString(),
                modifiedAt = Instant.now().toString(),
                recordedPlatform = System.getProperty("os.name")
            )
        )
    }
}

