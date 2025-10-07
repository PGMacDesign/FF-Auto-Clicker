package com.pgmacdesign.macrox.platform

import com.pgmacdesign.macrox.model.Macro
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

/**
 * Desktop implementation of FileManager using standard file system operations
 */
actual class FileManager {
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    actual val macroDirectory: String = run {
        val userHome = System.getProperty("user.home")
        val macroDir = File(userHome, ".macrox/macros")
        if (!macroDir.exists()) {
            macroDir.mkdirs()
        }
        macroDir.absolutePath
    }
    
    actual suspend fun saveMacro(macro: Macro): Boolean {
        return try {
            val file = File(macroDirectory, "${macro.id}.json")
            val jsonString = json.encodeToString(macro)
            file.writeText(jsonString)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    actual suspend fun loadMacro(macroId: String): Macro? {
        return try {
            val file = File(macroDirectory, "$macroId.json")
            if (!file.exists()) return null
            
            val jsonString = file.readText()
            json.decodeFromString<Macro>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    actual suspend fun deleteMacro(macroId: String): Boolean {
        return try {
            val file = File(macroDirectory, "$macroId.json")
            file.delete()
        } catch (e: Exception) {
            false
        }
    }
    
    actual suspend fun listMacros(): List<MacroInfo> {
        return try {
            val macroDir = File(macroDirectory)
            if (!macroDir.exists()) return emptyList()
            
            macroDir.listFiles { _, name -> name.endsWith(".json") }
                ?.mapNotNull { file ->
                    try {
                        val jsonString = file.readText()
                        val macro = json.decodeFromString<Macro>(jsonString)
                        MacroInfo(
                            id = macro.id,
                            name = macro.name,
                            description = macro.description,
                            eventCount = macro.eventCount,
                            durationMs = macro.durationMs,
                            createdAt = macro.metadata.createdAt,
                            modifiedAt = macro.metadata.modifiedAt,
                            tags = macro.metadata.tags
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    actual suspend fun exportMacro(macro: Macro, filePath: String): Boolean {
        return try {
            val file = File(filePath)
            val jsonString = json.encodeToString(macro)
            file.writeText(jsonString)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    actual suspend fun importMacro(filePath: String): Macro? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null
            
            val jsonString = file.readText()
            json.decodeFromString<Macro>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    actual suspend fun checkStorageAccess(): Boolean {
        return try {
            val macroDir = File(macroDirectory)
            macroDir.exists() && macroDir.canRead() && macroDir.canWrite()
        } catch (e: Exception) {
            false
        }
    }
    
    actual suspend fun createBackup(backupPath: String): Boolean {
        return try {
            val macroDir = File(macroDirectory)
            val backupFile = File(backupPath)
            
            // Create a simple backup by copying all macro files to a zip-like structure
            // For now, just copy to a directory
            val backupDir = File(backupFile.parent, backupFile.nameWithoutExtension)
            backupDir.mkdirs()
            
            macroDir.listFiles()?.forEach { file ->
                if (file.isFile && file.name.endsWith(".json")) {
                    file.copyTo(File(backupDir, file.name), overwrite = true)
                }
            }
            
            // Create a metadata file
            val metadata = mapOf(
                "created" to Instant.now().toString(),
                "version" to "1.0",
                "macroCount" to (macroDir.listFiles()?.count { it.name.endsWith(".json") } ?: 0)
            )
            File(backupDir, "backup_metadata.json").writeText(json.encodeToString(metadata))
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    actual suspend fun restoreBackup(backupPath: String): Boolean {
        return try {
            val backupDir = File(backupPath)
            if (!backupDir.exists() || !backupDir.isDirectory) return false
            
            val macroDir = File(macroDirectory)
            
            backupDir.listFiles()?.forEach { file ->
                if (file.isFile && file.name.endsWith(".json") && file.name != "backup_metadata.json") {
                    file.copyTo(File(macroDir, file.name), overwrite = true)
                }
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
}
