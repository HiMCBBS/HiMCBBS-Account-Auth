package com.himcbbs.play.serverclient.himcbbsauth.hook;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import com.himcbbs.play.serverclient.himcbbsauth.storage.StorageManager;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.RestoreSessionEvent;
import fr.xephi.authme.settings.Settings;
import fr.xephi.authme.settings.properties.RegistrationSettings;
import fr.xephi.authme.settings.properties.RestrictionSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.BroadcastMessageEvent;

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

    private boolean commandAdded = false, tipsDisabled = false;

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
            } catch (Throwable e) {
                HiMCBBSAccountAuth.getInstance().warn(e, "[AuthMe插件接入] 无法自动添加禁用命令例外，请手动在AuthMe插件配置中添加（在settings.restrictions.allowCommands项中添加/himcauth及/hiauth）！");
            }
        }
        if(!tipsDisabled) {
            tipsDisabled = true;
            try {
                Field field = api.getPlugin().getClass().getDeclaredField("settings");
                field.setAccessible(true);
                Settings settings = (Settings) field.get(api.getPlugin());
                settings.setProperty(RegistrationSettings.MESSAGE_INTERVAL, 0);
            } catch (Throwable e) {
                HiMCBBSAccountAuth.getInstance().warn(e, "[AuthMe插件接入] 无法自动禁用提示信息，请手动在AuthMe插件配置中禁用（将settings.registration.messageInterval项设为0）！");
            }
        }
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
