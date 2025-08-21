package com.himcbbs.play.serverclient.himcbbsauth;

import com.himcbbs.play.serverclient.himcbbsauth.command.MainCommand;
import com.himcbbs.play.serverclient.himcbbsauth.listener.PlayerListener;
import com.himcbbs.play.serverclient.himcbbsauth.listener.RegisterListener;
import com.himcbbs.play.serverclient.himcbbsauth.storage.Storage;
import com.himcbbs.play.serverclient.himcbbsauth.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HiMCBBSAccountAuth extends JavaPlugin {
    private static HiMCBBSAccountAuth INSTANCE;
    public final Logger LOGGER = getLogger();
    // folia start
    /** GlobalRegionScheduler getGlobalRegionScheduler(); */
    public Method getGlobalRegionScheduler = null;
    /** ScheduledTask runDelayed(Plugin plugin, Consumer<ScheduledTask> task, long delayTicks); */
    public Method runDelayed = null;
    public boolean foliaSuppported = false;
    //folia end

    @Override
    public void onEnable() {
        INSTANCE = this;
        try {
            Class<?> clazz = Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            getGlobalRegionScheduler = Server.class.getMethod("getGlobalRegionScheduler");
            runDelayed = clazz.getMethod("runDelayed", Plugin.class, Consumer.class, long.class);
            foliaSuppported = true;
        } catch (NoSuchMethodException | ClassNotFoundException ignored) {
        }
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new RegisterListener(), this);
        saveDefaultConfig();
        reloadConfig();
        try {
            MainCommand command = new MainCommand();
            getCommand("himcauth").setExecutor(command);
            getCommand("himcauth").setTabCompleter(command);
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
