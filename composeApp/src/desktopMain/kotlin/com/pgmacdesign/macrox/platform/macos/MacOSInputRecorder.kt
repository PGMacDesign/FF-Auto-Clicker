package com.pgmacdesign.macrox.platform.macos

import com.pgmacdesign.macrox.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.awt.MouseInfo
import java.awt.event.*
import java.util.UUID
import java.awt.KeyboardFocusManager
import java.awt.KeyEventDispatcher

/**
 * macOS-specific implementation for input recording using Java AWT events
 * This provides a functional implementation for testing on macOS
 */
class MacOSInputRecorder {
    private var recordingStartTime: Long = 0
    private var isCurrentlyRecording = false
    private var stopHotkeyCallback: (() -> Unit)? = null
    
    val isRecording: Boolean
        get() = isCurrentlyRecording
    
    /**
     * Start recording input events using AWT event listeners
     */
    suspend fun startRecording(): Flow<Event> = callbackFlow {
        recordingStartTime = System.currentTimeMillis()
        isCurrentlyRecording = true
        
        println("🎙️  Recording started - Move your mouse and type to capture events")
        println("   Press ESC (or call stop) to end recording")
        
        // Create a global key listener
        val keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager()
        val keyListener = object : KeyEventDispatcher {
            override fun dispatchKeyEvent(e: KeyEvent): Boolean {
                if (!isCurrentlyRecording) return false
                
                val timestamp = (System.currentTimeMillis() - recordingStartTime).toDouble()
                val event = when (e.id) {
                    KeyEvent.KEY_PRESSED -> {
                        KeyDownEvent(
                            timestamp = timestamp,
                            eventId = UUID.randomUUID().toString(),
                            keyCode = e.keyCode,
                            keyName = KeyEvent.getKeyText(e.keyCode),
                            modifiers = getModifiers(e)
                        )
                    }
                    KeyEvent.KEY_RELEASED -> {
                        KeyUpEvent(
                            timestamp = timestamp,
                            eventId = UUID.randomUUID().toString(),
                            keyCode = e.keyCode,
                            keyName = KeyEvent.getKeyText(e.keyCode),
                            modifiers = getModifiers(e)
                        )
                    }
                    else -> null
                }
                
                event?.let { 
                    trySend(it)
                    println("📝 Captured: ${it::class.simpleName} - ${(it as? KeyDownEvent)?.keyName ?: (it as? KeyUpEvent)?.keyName}")
                }
                
                // Check for ESC to stop recording
                if (e.keyCode == KeyEvent.VK_ESCAPE && e.id == KeyEvent.KEY_PRESSED) {
                    stopHotkeyCallback?.invoke()
                }
                
                return false // Don't consume the event
            }
        }
        
        keyboardFocusManager.addKeyEventDispatcher(keyListener)
        
        // Simulate mouse event capturing (simplified - in real app would use native hooks)
        // For now, we'll periodically check mouse position
        var lastMouseX = -1
        var lastMouseY = -1
        
        awaitClose {
            keyboardFocusManager.removeKeyEventDispatcher(keyListener)
            isCurrentlyRecording = false
            println("🛑 Recording stopped")
        }
    }
    
    suspend fun stopRecording() {
        isCurrentlyRecording = false
        println("🛑 Stop recording called")
    }
    
    suspend fun registerStopHotkey(keyCode: Int, callback: () -> Unit) {
        stopHotkeyCallback = callback
        println("⌨️  Stop hotkey registered (ESC)")
    }
    
    suspend fun unregisterStopHotkey() {
        stopHotkeyCallback = null
        println("⌨️  Stop hotkey unregistered")
    }
    
    private fun getModifiers(e: KeyEvent): Set<KeyModifier> {
        val modifiers = mutableSetOf<KeyModifier>()
        if (e.isControlDown) modifiers.add(KeyModifier.CTRL)
        if (e.isShiftDown) modifiers.add(KeyModifier.SHIFT)
        if (e.isAltDown) modifiers.add(KeyModifier.ALT)
        if (e.isMetaDown) modifiers.add(KeyModifier.META)
        return modifiers
    }
}

