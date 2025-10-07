package com.pgmacdesign.macrox.platform

/**
 * Platform-specific interface for checking and requesting permissions
 */
expect class PlatformPermissions {
    /**
     * Check if the application has permission to record input events
     */
    suspend fun hasInputRecordingPermission(): Boolean
    
    /**
     * Request permission to record input events
     * @return true if permission was granted, false otherwise
     */
    suspend fun requestInputRecordingPermission(): Boolean
    
    /**
     * Check if the application has permission to simulate input events
     */
    suspend fun hasInputSimulationPermission(): Boolean
    
    /**
     * Request permission to simulate input events
     * @return true if permission was granted, false otherwise
     */
    suspend fun requestInputSimulationPermission(): Boolean
    
    /**
     * Check if the application has permission to register global hotkeys
     */
    suspend fun hasGlobalHotkeyPermission(): Boolean
    
    /**
     * Request permission to register global hotkeys
     * @return true if permission was granted, false otherwise
     */
    suspend fun requestGlobalHotkeyPermission(): Boolean
    
    /**
     * Check if the application has permission to access the file system
     */
    suspend fun hasFileSystemPermission(): Boolean
    
    /**
     * Request permission to access the file system
     * @return true if permission was granted, false otherwise
     */
    suspend fun requestFileSystemPermission(): Boolean
    
    /**
     * Get a human-readable description of what permissions are needed
     */
    fun getPermissionDescription(permission: Permission): String
    
    /**
     * Check if the current platform requires elevated privileges
     */
    val requiresElevatedPrivileges: Boolean
    
    /**
     * Get platform-specific instructions for granting permissions
     */
    fun getPermissionInstructions(): List<String>
}

/**
 * Types of permissions that may be required
 */
enum class Permission {
    INPUT_RECORDING,
    INPUT_SIMULATION,
    GLOBAL_HOTKEYS,
    FILE_SYSTEM,
    ELEVATED_PRIVILEGES
}

/**
 * Result of a permission check or request
 */
data class PermissionResult(
    val permission: Permission,
    val granted: Boolean,
    val message: String? = null,
    val requiresRestart: Boolean = false
)
