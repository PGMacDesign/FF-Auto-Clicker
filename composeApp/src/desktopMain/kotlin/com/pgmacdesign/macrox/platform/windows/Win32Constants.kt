package com.pgmacdesign.macrox.platform.windows

/**
 * Win32 API constants for input handling
 */
object Win32Constants {
    // Input types
    const val INPUT_MOUSE = 0
    const val INPUT_KEYBOARD = 1
    const val INPUT_HARDWARE = 2
    
    // Mouse event flags
    const val MOUSEEVENTF_MOVE = 0x0001
    const val MOUSEEVENTF_LEFTDOWN = 0x0002
    const val MOUSEEVENTF_LEFTUP = 0x0004
    const val MOUSEEVENTF_RIGHTDOWN = 0x0008
    const val MOUSEEVENTF_RIGHTUP = 0x0010
    const val MOUSEEVENTF_MIDDLEDOWN = 0x0020
    const val MOUSEEVENTF_MIDDLEUP = 0x0040
    const val MOUSEEVENTF_XDOWN = 0x0080
    const val MOUSEEVENTF_XUP = 0x0100
    const val MOUSEEVENTF_WHEEL = 0x0800
    const val MOUSEEVENTF_HWHEEL = 0x01000
    const val MOUSEEVENTF_ABSOLUTE = 0x8000
    
    // Keyboard event flags
    const val KEYEVENTF_EXTENDEDKEY = 0x0001
    const val KEYEVENTF_KEYUP = 0x0002
    const val KEYEVENTF_UNICODE = 0x0004
    const val KEYEVENTF_SCANCODE = 0x0008
    
    // Mouse wheel delta
    const val WHEEL_DELTA = 120
    
    // Hook types
    const val WH_MOUSE_LL = 14
    const val WH_KEYBOARD_LL = 13
    
    // Hook messages
    const val WM_MOUSEMOVE = 0x0200
    const val WM_LBUTTONDOWN = 0x0201
    const val WM_LBUTTONUP = 0x0202
    const val WM_RBUTTONDOWN = 0x0204
    const val WM_RBUTTONUP = 0x0205
    const val WM_MBUTTONDOWN = 0x0207
    const val WM_MBUTTONUP = 0x0208
    const val WM_MOUSEWHEEL = 0x020A
    const val WM_XBUTTONDOWN = 0x020B
    const val WM_XBUTTONUP = 0x020C
    
    const val WM_KEYDOWN = 0x0100
    const val WM_KEYUP = 0x0101
    const val WM_SYSKEYDOWN = 0x0104
    const val WM_SYSKEYUP = 0x0105
}

