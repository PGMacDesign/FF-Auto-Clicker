package com.pgmacdesign.macrox.platform

/**
 * Desktop implementation of PlatformPermissions for Windows
 */
actual class PlatformPermissions {
    actual suspend fun hasInputRecordingPermission(): Boolean {
        // On Windows, input recording typically requires no special permissions
        // but may require running as administrator for some low-level hooks
        return true
    }
    
    actual suspend fun requestInputRecordingPermission(): Boolean {
        // Windows doesn't have a runtime permission system like mobile platforms
        return hasInputRecordingPermission()
    }
    
    actual suspend fun hasInputSimulationPermission(): Boolean {
        // Input simulation on Windows typically works without special permissions
        return true
    }
    
    actual suspend fun requestInputSimulationPermission(): Boolean {
        return hasInputSimulationPermission()
    }
    
    actual suspend fun hasGlobalHotkeyPermission(): Boolean {
        // Global hotkeys may require elevated privileges in some cases
        return true
    }
    
    actual suspend fun requestGlobalHotkeyPermission(): Boolean {
        return hasGlobalHotkeyPermission()
    }
    
    actual suspend fun hasFileSystemPermission(): Boolean {
        // File system access is generally available on desktop platforms
        return true
    }
    
    actual suspend fun requestFileSystemPermission(): Boolean {
        return hasFileSystemPermission()
    }
    
    actual fun getPermissionDescription(permission: Permission): String {
        return when (permission) {
            Permission.INPUT_RECORDING -> 
                "MacroX needs permission to record mouse and keyboard input to create macros."
            Permission.INPUT_SIMULATION -> 
                "MacroX needs permission to simulate mouse and keyboard input to play back macros."
            Permission.GLOBAL_HOTKEYS -> 
                "MacroX needs permission to register global hotkeys for controlling recording and playback."
            Permission.FILE_SYSTEM -> 
                "MacroX needs permission to save and load macro files on your computer."
            Permission.ELEVATED_PRIVILEGES -> 
                "MacroX may need to run with administrator privileges for full functionality."
        }
    }
    
    actual val requiresElevatedPrivileges: Boolean = false
    
    actual fun getPermissionInstructions(): List<String> {
        return listOf(
            "On Windows, MacroX should work without special permissions.",
            "If you encounter issues with input recording or playback:",
            "1. Try running MacroX as Administrator",
            "2. Check Windows Security settings for input monitoring",
            "3. Ensure no antivirus software is blocking MacroX",
            "4. Add MacroX to Windows Defender exclusions if needed"
        )
    }
}
