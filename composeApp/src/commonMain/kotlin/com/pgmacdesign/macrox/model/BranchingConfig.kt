package com.pgmacdesign.macrox.model

import kotlinx.serialization.Serializable

/**
 * Configuration for branching behavior during macro playback
 */
@Serializable
data class BranchingConfig(
    /**
     * Whether branching is enabled for this macro
     */
    val enabled: Boolean = false,
    
    /**
     * List of branch rules to evaluate during playback
     */
    val branches: List<BranchRule> = emptyList(),
    
    /**
     * Behavior when a branch is triggered
     */
    val branchBehavior: BranchBehavior = BranchBehavior.PAUSE_PARENT
)

/**
 * A single branching rule that can trigger during macro execution
 */
@Serializable
data class BranchRule(
    /**
     * Unique identifier for this branch rule
     */
    val id: String,
    
    /**
     * Name/description of this branch
     */
    val name: String,
    
    /**
     * Trigger condition for this branch
     */
    val trigger: BranchTrigger,
    
    /**
     * Action to take when triggered
     */
    val action: BranchAction,
    
    /**
     * Whether this branch rule is enabled
     */
    val enabled: Boolean = true
)

/**
 * Conditions that can trigger a branch
 */
@Serializable
sealed class BranchTrigger {
    /**
     * Trigger after every N iterations of the main macro
     */
    @Serializable
    data class EveryNIterations(val n: Int) : BranchTrigger()
    
    /**
     * Trigger with a specific probability on each iteration
     */
    @Serializable
    data class RandomProbability(val probability: Double) : BranchTrigger()
    
    /**
     * Trigger after a specific number of events have been executed
     */
    @Serializable
    data class AfterNEvents(val n: Int) : BranchTrigger()
    
    /**
     * Trigger at a specific time offset from macro start
     */
    @Serializable
    data class AtTimeOffset(val offsetMs: Long) : BranchTrigger()
    
    /**
     * Trigger when a specific event type is encountered
     */
    @Serializable
    data class OnEventType(val eventType: String) : BranchTrigger()
}

/**
 * Actions that can be taken when a branch is triggered
 */
@Serializable
sealed class BranchAction {
    /**
     * Execute another macro by ID
     */
    @Serializable
    data class ExecuteMacro(
        val macroId: String,
        val iterations: Int = 1
    ) : BranchAction()
    
    /**
     * Insert a sequence of events
     */
    @Serializable
    data class InsertEvents(val events: List<Event>) : BranchAction()
    
    /**
     * Pause execution for a specified duration
     */
    @Serializable
    data class PauseExecution(val durationMs: Long) : BranchAction()
    
    /**
     * Stop the current macro execution
     */
    @Serializable
    object StopExecution : BranchAction()
    
    /**
     * Skip the next N events in the current macro
     */
    @Serializable
    data class SkipEvents(val count: Int) : BranchAction()
}

/**
 * How the parent macro should behave when a branch is executing
 */
@Serializable
enum class BranchBehavior {
    /**
     * Pause the parent macro until branch completes
     */
    PAUSE_PARENT,
    
    /**
     * Continue parent macro execution in parallel with branch
     */
    CONTINUE_PARALLEL,
    
    /**
     * Stop parent macro when branch starts
     */
    STOP_PARENT
}
