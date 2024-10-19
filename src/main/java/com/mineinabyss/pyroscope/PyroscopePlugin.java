package com.mineinabyss.pyroscope;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.pyroscope.http.Format;
import io.pyroscope.javaagent.EventType;
import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PyroscopePlugin extends JavaPlugin {
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onEnable() {
        saveDefaultConfig();
        start();
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(
                Commands.literal("pyroscope")
                        .requires(it -> it.getSender().hasPermission("pyroscope.admin"))
                        .then(Commands.literal("reload").executes(ctx -> {
                            stop();
                            start();
                            ctx.getSource().getSender().sendMessage("Pyroscope reloaded");
                            return Command.SINGLE_SUCCESS;
                        })).build()
        ));
    }

    private void start() {
        reloadConfig();
        var env = System.getenv();
        var config = getConfig();
        Map<String, String> labels = config.getObject("labels", Map.class, new HashMap());
        labels.putIfAbsent("server", env.getOrDefault("SERVER_NAME", "unknown"));
        getLogger().info("Starting Pyroscope");
        PyroscopeAgent.start(new Config.Builder()
                .setLabels(labels)
                .setApplicationName(config.getString("applicationName"))
                .setProfilingEvent(EventType.ITIMER)
                .setProfilingAlloc(config.getString("allocSize"))
                .setFormat(Format.JFR)
                .setServerAddress(config.getString("serverAddress"))
                .build()
        );
    }

    @Override
    public void onDisable() {
        getLogger().info("Stopping Pyroscope");
        stop();
    }

    private void stop() {
        PyroscopeAgent.stop();
    }
}
