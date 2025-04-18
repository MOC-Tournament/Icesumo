package top.andyshen2006.icesumo

import org.bukkit.Bukkit
import java.io.File
import java.io.FileWriter
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Level

object CSVManager {
    private val plugin = Bukkit.getPluginManager().getPlugin("Icesumo") as Icesumo
    private val dataDir = File(plugin.dataFolder, "results")
    private val lock = ReentrantLock()

    init {
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }
    }

    fun startWriteResult(resultFile: File) {
        try {
            lock.lock()
            try {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                    FileWriter(resultFile, true).use { writer ->
                        writer.appendLine("PlayerUUID,PlayerName,Result,FailTime")
                    }
                })
            } catch (_: Exception) {
                plugin.logger.log(Level.SEVERE, "无法写入result，请检查是否有写入权限")
            }

        } finally {
            lock.unlock()
        }

    }

    fun appendResult(resultFile: File, uuid: String, username: String, result: Boolean, failTime: Long) {
        try {
            lock.lock()
            try {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                    FileWriter(resultFile, true).use { writer ->
                        writer.appendLine("${uuid},${username},${if (result) "SUCCESS" else "FAIL"},${failTime}")
                    }
                })
            } catch (_: Exception) {
                plugin.logger.log(Level.SEVERE, "无法写入result，请检查是否有写入权限")
            }
        } finally {
            lock.unlock()
        }
    }
}