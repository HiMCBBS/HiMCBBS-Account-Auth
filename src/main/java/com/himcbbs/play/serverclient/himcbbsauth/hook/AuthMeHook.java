package com.himcbbs.play.serverclient.himcbbsauth.hook;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import com.himcbbs.play.serverclient.himcbbsauth.storage.StorageManager;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.RestoreSessionEvent;
import fr.xephi.authme.settings.Settings;
import fr.xephi.authme.settings.properties.RestrictionSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class AuthMeHook implements Hook, Listener {
    private AuthMeApi api;

    @Override
    public void forceLogin(Player player) {
        api.forceLogin(player);
    }

    @Override
    public void forceRegister(Player player, String password) {
        api.forceRegister(player, password);
    }

    @Override
    public boolean isRegistered(Player player) {
        return api.isRegistered(player.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSessionRestore(RestoreSessionEvent event) {
        try {
            if(StorageManager.getInstance().getRunningStorage().getUserId(event.getPlayer().getUniqueId())!=null) {
                event.setCancelled(true);
            }
        } catch (Exception ignored) {
        }
    }

    private boolean commandAdded = false;

    @Override
    public void initializeHook() {
        api = AuthMeApi.getInstance();
        if(!commandAdded) {
            commandAdded = true;
            try {
                Field field = api.getPlugin().getClass().getDeclaredField("settings");
                field.setAccessible(true);
                Settings settings = (Settings) field.get(api.getPlugin());
                Set<String> defaults = new HashSet<>(settings.getProperty(RestrictionSettings.ALLOW_COMMANDS));
                defaults.add("/himcauth");
                defaults.add("/hiauth");
                settings.setProperty(RestrictionSettings.ALLOW_COMMANDS, defaults);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                HiMCBBSAccountAuth.getInstance().warn("[AuthMe插件接入] 无法自动添加命令例外，请手动添加！");
            }
        }
        //TODO: no annoying tips
    }

    @Override
    public void removeHook() {
        api = null;
    }

    @Override
    public String name() {
        return "AuthMe";
    }
}
