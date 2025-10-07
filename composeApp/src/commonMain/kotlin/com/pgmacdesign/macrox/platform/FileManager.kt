package com.pgmacdesign.macrox.platform

import com.pgmacdesign.macrox.model.Macro

/**
 * Platform-specific interface for file operations
 */
expect class FileManager {
    /**
     * Save a macro to the file system
     * @param macro The macro to save
     * @return true if successful, false otherwise
     */
    suspend fun saveMacro(macro: Macro): Boolean
    
    /**
     * Load a macro from the file system
     * @param macroId The ID of the macro to load
     * @return The loaded macro, or null if not found
     */
    suspend fun loadMacro(macroId: String): Macro?
    
    /**
     * Delete a macro from the file system
     * @param macroId The ID of the macro to delete
     * @return true if successful, false otherwise
     */
    suspend fun deleteMacro(macroId: String): Boolean
    
    /**
     * List all available macros
     * @return List of macro metadata (ID, name, description)
     */
    suspend fun listMacros(): List<MacroInfo>
    
    /**
     * Export a macro to a specific file path
     * @param macro The macro to export
     * @param filePath The target file path
     * @return true if successful, false otherwise
     */
    suspend fun exportMacro(macro: Macro, filePath: String): Boolean
    
    /**
     * Import a macro from a file path
     * @param filePath The source file path
     * @return The imported macro, or null if import failed
     */
    suspend fun importMacro(filePath: String): Macro?
    
    /**
     * Get the default directory for storing macros
     */
    val macroDirectory: String
    
    /**
     * Check if the macro storage directory exists and is writable
     */
    suspend fun checkStorageAccess(): Boolean
    
    /**
     * Create a backup of all macros
     * @param backupPath The path for the backup file
     * @return true if successful, false otherwise
     */
    suspend fun createBackup(backupPath: String): Boolean
    
    /**
     * Restore macros from a backup
     * @param backupPath The path to the backup file
     * @return true if successful, false otherwise
     */
    suspend fun restoreBackup(backupPath: String): Boolean
}

/**
 * Basic information about a macro for listing purposes
 */
data class MacroInfo(
    val id: String,
    val name: String,
    val description: String,
    val eventCount: Int,
    val durationMs: Long,
    val createdAt: String,
    val modifiedAt: String,
    val tags: Set<String>
)
