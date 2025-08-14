package com.himcbbs.play.serverclient.himcbbsauth;

import com.himcbbs.play.serverclient.himcbbsauth.command.MainCommand;
import com.himcbbs.play.serverclient.himcbbsauth.storage.Storage;
import com.himcbbs.play.serverclient.himcbbsauth.storage.StorageManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HiMCBBSAccountAuth extends JavaPlugin {
    public final Logger LOGGER = getLogger();
    private static HiMCBBSAccountAuth INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.saveDefaultConfig();
        this.reloadConfig();
        this.getCommand("himcbbsaccountauth").setExecutor(new MainCommand());
        StorageManager.getInstance().init();
        info("HiMCBBS Account Auth enabled.");
    }

    @Override
    public void onDisable() {
        info("HiMCBBS Account Auth disabled.");
    }

    public void enable() {
        if(!HiMCBBSAccountAuth.getInstance().isEnabled()) {
            HiMCBBSAccountAuth.getInstance().getPluginLoader().enablePlugin(HiMCBBSAccountAuth.getInstance());
        }
    }

    public void disable() {
        if(!HiMCBBSAccountAuth.getInstance().isEnabled()) {
            HiMCBBSAccountAuth.getInstance().getPluginLoader().disablePlugin(HiMCBBSAccountAuth.getInstance());
        }
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
