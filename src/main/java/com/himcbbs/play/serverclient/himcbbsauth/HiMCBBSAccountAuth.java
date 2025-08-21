package com.himcbbs.play.serverclient.himcbbsauth;

import com.himcbbs.play.serverclient.himcbbsauth.command.MainCommand;
import com.himcbbs.play.serverclient.himcbbsauth.listener.PlayerListener;
import com.himcbbs.play.serverclient.himcbbsauth.listener.RegisterListener;
import com.himcbbs.play.serverclient.himcbbsauth.storage.Storage;
import com.himcbbs.play.serverclient.himcbbsauth.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HiMCBBSAccountAuth extends JavaPlugin {
    public final Logger LOGGER = getLogger();
    private static HiMCBBSAccountAuth INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new RegisterListener(), this);
        saveDefaultConfig();
        reloadConfig();
        try {
            MainCommand command = new MainCommand();
            getCommand("himcbbsaccountauth").setExecutor(command);
            getCommand("himcbbsaccountauth").setTabCompleter(command);
        } catch (RuntimeException | IOException e) {
            error(e, "当初始化HiMCBBS接入配置时出现问题！");
            disable();
            return;
        }
        try {
            StorageManager.getInstance().init();
        } catch (Exception e) {
            error(e, "当初始化存储配置时出现问题！");
            disable();
            return;
        }
        info("HiMCBBS Account Auth 已启用");
    }

    @Override
    public void onDisable() {
        info("HiMCBBS Account Auth 已禁用");
    }

    public void enable() {
        Bukkit.getPluginManager().enablePlugin(this);
    }

    public void disable() {
        Bukkit.getPluginManager().disablePlugin(this);
    }

    @NotNull
    public static HiMCBBSAccountAuth getInstance() {
        return INSTANCE;
    }

    public Path getStoragePath(Storage storage) {
        Path res = getDataFolder().toPath().resolve("storage").resolve(storage.id());
        res.toFile().mkdirs();
        return res;
    }

    public void info(String log, Object... args) {
        LOGGER.info(String.format(log, args));
    }

    public void warn(String log, Object... args) {
        LOGGER.warning(String.format(log, args));
    }

    public void error(String log, Object... args) {
        LOGGER.severe(String.format(log, args));
    }

    public void error(Throwable throwable, String log, Object... args) {
        LOGGER.log(Level.SEVERE, String.format(log, args), throwable);
    }
}
