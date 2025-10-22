package com.pgmacdesign.macrox.platform.windows

import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR
import com.sun.jna.platform.win32.WinDef.*

/**
 * Win32 API structures for input handling
 */

@Structure.FieldOrder("pt", "mouseData", "flags", "time", "dwExtraInfo")
class MSLLHOOKSTRUCT : Structure {
    @JvmField var pt = POINT()
    @JvmField var mouseData = DWORD(0)
    @JvmField var flags = DWORD(0)
    @JvmField var time = DWORD(0)
    @JvmField var dwExtraInfo = ULONG_PTR(0)
    
    constructor() : super()
    constructor(p: Pointer) : super(p)
}

@Structure.FieldOrder("vkCode", "scanCode", "flags", "time", "dwExtraInfo")
class KBDLLHOOKSTRUCT : Structure {
    @JvmField var vkCode = DWORD(0)
    @JvmField var scanCode = DWORD(0)
    @JvmField var flags = DWORD(0)
    @JvmField var time = DWORD(0)
    @JvmField var dwExtraInfo = ULONG_PTR(0)
    
    constructor() : super()
    constructor(p: Pointer) : super(p)
}

@Structure.FieldOrder("dx", "dy", "mouseData", "dwFlags", "time", "dwExtraInfo")
class MOUSEINPUT : Structure() {
    @JvmField var dx = LONG(0)
    @JvmField var dy = LONG(0)
    @JvmField var mouseData = DWORD(0)
    @JvmField var dwFlags = DWORD(0)
    @JvmField var time = DWORD(0)
    @JvmField var dwExtraInfo = ULONG_PTR(0)
}

@Structure.FieldOrder("wVk", "wScan", "dwFlags", "time", "dwExtraInfo")
class KEYBDINPUT : Structure() {
    @JvmField var wVk = WORD(0)
    @JvmField var wScan = WORD(0)
    @JvmField var dwFlags = DWORD(0)
    @JvmField var time = DWORD(0)
    @JvmField var dwExtraInfo = ULONG_PTR(0)
}

@Structure.FieldOrder("type", "input")
class INPUT : Structure() {
    @JvmField var type = DWORD(0)
    @JvmField var input: InputUnion = InputUnion()
    
    @Structure.FieldOrder("mi", "ki")
    class InputUnion : Structure(), Structure.ByReference {
        @JvmField var mi: MOUSEINPUT = MOUSEINPUT()
        @JvmField var ki: KEYBDINPUT = KEYBDINPUT()
    }
}
