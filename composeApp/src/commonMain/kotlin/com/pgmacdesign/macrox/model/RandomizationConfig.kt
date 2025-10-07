package com.pgmacdesign.macrox.model

import kotlinx.serialization.Serializable

/**
 * Configuration for randomization behavior during macro playback
 */
@Serializable
data class RandomizationConfig(
    /**
     * Delay randomization settings
     */
    val delayRandomization: DelayRandomization = DelayRandomization(),
    
    /**
     * Click position randomization settings
     */
    val clickRandomization: ClickRandomization = ClickRandomization(),
    
    /**
     * Overall randomization enabled flag
     */
    val enabled: Boolean = false
)

/**
 * Configuration for delay timing randomization
 */
@Serializable
data class DelayRandomization(
    /**
     * Minimum delay adjustment in milliseconds (can be negative)
     */
    val minDelayMs: Long = -50,
    
    /**
     * Maximum delay adjustment in milliseconds
     */
    val maxDelayMs: Long = 100,
    
    /**
     * Frequency configuration for when to apply delay randomization
     */
    val frequency: RandomizationFrequency = RandomizationFrequency(),
    
    /**
     * Distribution type for delay randomization
     */
    val distribution: RandomDistribution = RandomDistribution.UNIFORM,
    
    /**
     * Whether delay randomization is enabled
     */
    val enabled: Boolean = false
)

/**
 * Configuration for click position randomization
 */
@Serializable
data class ClickRandomization(
    /**
     * Maximum offset in pixels for X coordinate
     */
    val maxOffsetX: Int = 5,
    
    /**
     * Maximum offset in pixels for Y coordinate
     */
    val maxOffsetY: Int = 5,
    
    /**
     * Probability of applying click randomization (0.0 to 1.0)
     */
    val probability: Double = 0.5,
    
    /**
     * Distribution type for position randomization
     */
    val distribution: RandomDistribution = RandomDistribution.UNIFORM,
    
    /**
     * Whether click randomization is enabled
     */
    val enabled: Boolean = false
)

/**
 * Configuration for randomization frequency
 */
@Serializable
data class RandomizationFrequency(
    /**
     * Apply randomization every N events (0 = apply to all events)
     */
    val everyNthEvent: Int = 0,
    
    /**
     * Probability of applying randomization (0.0 to 1.0)
     */
    val probability: Double = 1.0
)

/**
 * Types of random distributions available
 */
@Serializable
enum class RandomDistribution {
    /**
     * Uniform distribution - equal probability across range
     */
    UNIFORM,
    
    /**
     * Gaussian/Normal distribution - bell curve centered on mean
     */
    GAUSSIAN,
    
    /**
     * Exponential distribution - higher probability for smaller values
     */
    EXPONENTIAL
}
