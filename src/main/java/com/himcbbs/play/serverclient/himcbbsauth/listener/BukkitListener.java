package com.himcbbs.play.serverclient.himcbbsauth.listener;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import com.himcbbs.play.serverclient.himcbbsauth.hook.HookManager;
import com.himcbbs.play.serverclient.himcbbsauth.storage.Storage;
import com.himcbbs.play.serverclient.himcbbsauth.storage.StorageManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerLoadEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

public class BukkitListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HiMCBBSAccountAuth plugin = HiMCBBSAccountAuth.getInstance();
        Runnable runnable = ()->{
            Storage storage = StorageManager.getInstance().getRunningStorage();
            try {
                if(storage.getUserId(player.getUniqueId())!=null) {
                    HookManager.getInstance().forceLogin(player);
                    return;
                }
            } catch (Exception ignored) {
            }
            BaseComponent component = new TextComponent("你可以");
            BaseComponent component1 = new TextComponent("[点击这里]");
            component1.setBold(true);
            component1.setUnderlined(true);
            component1.setColor(ChatColor.GREEN);
            component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("点此绑定HiMCBBS账号")));
            component1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/himcauth bind"));
            component.addExtra(component1);
            component.addExtra("来绑定你的HiMCBBS账号以登录");
            player.spigot().sendMessage(component);
        };
        if(plugin.foliaSuppported) {
            try {
                Object scheduler = plugin.getGlobalRegionScheduler.invoke(player.getServer());
                Consumer<?> consumer = (Consumer<?>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Consumer.class}, ((proxy, method, args) -> {
                    if(method.getName().equals("accept")) {
                        runnable.run();
                        return null;
                    }
                    return method.invoke(proxy, args);
                }));
                plugin.runDelayed.invoke(scheduler, plugin, consumer, 20);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        player.getServer().getScheduler().runTaskLater(plugin, runnable, 20);
        // delay 20 ticks for auth plugins and FastLogin
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        HookManager.getInstance().initHookByName(event.getPlugin().getName());
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        HookManager.getInstance().initHookByName(event.getPlugin().getName());
    }

    @EventHandler
    public void onServerLoadFinish(ServerLoadEvent event) {
        HookManager.getInstance().checkPluginHook();
    }
}
