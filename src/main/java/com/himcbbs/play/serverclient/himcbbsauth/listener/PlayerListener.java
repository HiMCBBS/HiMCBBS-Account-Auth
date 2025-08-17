package com.himcbbs.play.serverclient.himcbbsauth.listener;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
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

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(HiMCBBSAccountAuth.getInstance(), ()->{
            Storage storage = StorageManager.getInstance().getRunningStorage();
            try {
                if(storage.getUserId(player.getUniqueId())!=null) {
                    //TODO: force register/login player in the login plugin
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
            component1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hiauth bind"));
            component.addExtra(component1);
            component.addExtra("来绑定你的HiMCBBS账号以登录");
            player.spigot().sendMessage(component);
        }, 10);
    }
}
