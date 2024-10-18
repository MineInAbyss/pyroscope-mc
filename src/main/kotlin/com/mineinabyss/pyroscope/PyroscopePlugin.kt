package com.mineinabyss.pyroscope

import com.mineinabyss.idofront.commands.brigadier.commands
import com.mineinabyss.idofront.config.config
import io.pyroscope.http.Format
import io.pyroscope.javaagent.EventType
import io.pyroscope.javaagent.PyroscopeAgent
import kotlinx.serialization.Serializable
import org.bukkit.plugin.java.JavaPlugin
import io.pyroscope.javaagent.config.Config as PyroscopeConfig

class PyroscopePlugin : JavaPlugin() {
    @Serializable
    data class Config(
        val applicationName: String = "minecraft",
        val serverAddress: String = "http://pyroscope-server:4040",
    )

    override fun onEnable() {
        start()
        commands {
            "pyroscope" {
                requiresPermission("pyroscope.admin")
                "reload" {
                    executes {
                        stop()
                        start()
                        sender.sendMessage("Pyroscope reloaded")
                    }
                }
            }
        }
    }

    private fun start() {
        val env = System.getenv()
        val labels = mapOf("server" to (env["SERVER_NAME"] ?: "unknown"))
        val config by config("config", dataPath, Config())
        logger.info("Starting Pyroscope with config: $config")
        PyroscopeAgent.start(
            PyroscopeConfig.Builder()
                .setLabels(labels)
                .setApplicationName(config.applicationName)
                .setProfilingEvent(EventType.ITIMER)
                .setProfilingAlloc("512k")
                .setFormat(Format.JFR)
                .setServerAddress(config.serverAddress)
                .build()
        )
    }

    override fun onDisable() {
        logger.info("Stopping Pyroscope")
        stop()
    }

    private fun stop() {
        PyroscopeAgent.stop()
    }
}
