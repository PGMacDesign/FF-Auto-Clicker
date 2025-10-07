package com.pgmacdesign.macrox.model

import kotlinx.serialization.Serializable

/**
 * Base sealed class for all input events that can be recorded and played back
 */
@Serializable
sealed class Event {
    abstract val timestamp: Double // Time in milliseconds since recording start
    abstract val eventId: String // Unique identifier for this event
}

/**
 * Mouse movement event
 */
@Serializable
data class MouseMoveEvent(
    override val timestamp: Double,
    override val eventId: String,
    val x: Int,
    val y: Int,
    val isAbsolute: Boolean = true // true for absolute coordinates, false for relative
) : Event()

/**
 * Mouse button press event
 */
@Serializable
data class MouseDownEvent(
    override val timestamp: Double,
    override val eventId: String,
    val x: Int,
    val y: Int,
    val button: MouseButton
) : Event()

/**
 * Mouse button release event
 */
@Serializable
data class MouseUpEvent(
    override val timestamp: Double,
    override val eventId: String,
    val x: Int,
    val y: Int,
    val button: MouseButton
) : Event()

/**
 * Mouse wheel scroll event
 */
@Serializable
data class MouseWheelEvent(
    override val timestamp: Double,
    override val eventId: String,
    val x: Int,
    val y: Int,
    val deltaX: Int,
    val deltaY: Int
) : Event()

/**
 * Keyboard key press event
 */
@Serializable
data class KeyDownEvent(
    override val timestamp: Double,
    override val eventId: String,
    val keyCode: Int,
    val keyName: String,
    val modifiers: Set<KeyModifier> = emptySet()
) : Event()

/**
 * Keyboard key release event
 */
@Serializable
data class KeyUpEvent(
    override val timestamp: Double,
    override val eventId: String,
    val keyCode: Int,
    val keyName: String,
    val modifiers: Set<KeyModifier> = emptySet()
) : Event()

/**
 * Special delay event for explicit timing control
 */
@Serializable
data class DelayEvent(
    override val timestamp: Double,
    override val eventId: String,
    val delayMs: Long
) : Event()

/**
 * Mouse button enumeration
 */
@Serializable
enum class MouseButton {
    LEFT, RIGHT, MIDDLE, X1, X2
}

/**
 * Keyboard modifier keys
 */
@Serializable
enum class KeyModifier {
    CTRL, ALT, SHIFT, WIN, CMD, META
}
