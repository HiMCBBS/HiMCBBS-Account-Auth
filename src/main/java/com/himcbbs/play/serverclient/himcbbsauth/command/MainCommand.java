package com.himcbbs.play.serverclient.himcbbsauth.command;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import com.himcbbs.play.serverclient.himcbbsauth.network.JsonBaseResponse;
import com.himcbbs.play.serverclient.himcbbsauth.network.JsonState;
import com.himcbbs.play.serverclient.himcbbsauth.network.JsonUser;
import com.himcbbs.play.serverclient.himcbbsauth.network.NetworkManager;
import com.himcbbs.play.serverclient.himcbbsauth.storage.StorageManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor, TabCompleter {
    private final int serverId;
    private final String clientSecret;
    private final Map<UUID, Map.Entry<Long, JsonState>> stateMap;
    private static final String AUTHORIZE_FORMAT_URL = "https://www.himcbbs.com/oauth2/authorize?type=authorization_code&client_id=1435838936540453&redirect_uri=http://%s:%s/mcserver_client/processMCServerOauthLogin&state=%s&response_type=code&scope=user%%3Aread%%20profile_post:read";
    public MainCommand() throws RuntimeException, IOException {
        HiMCBBSAccountAuth plugin = HiMCBBSAccountAuth.getInstance();
        stateMap=new HashMap<>();
        serverId = plugin.getConfig().getInt("server-id", -1);
        clientSecret = plugin.getConfig().getString("client-secret");
        if(serverId==-1 || clientSecret==null || clientSecret.equals("null")) {
            throw new RuntimeException("server_id或client_secret未配置！请在配置文件中HiMCBBS接入模块中配置相应值！");
        }
        NetworkManager manager = NetworkManager.getInstance();
        try (Response response = manager.POST("mcserver_client/startMCServerOAuthLoginSession", getRequestBody(null))) {
            if(!response.isSuccessful()) {
                JsonBaseResponse<JsonState> state = getState(response);
                throw new RuntimeException("server_id或client_secret的配置值不正确！状态码："+state.status+" 消息："+state.message);
            }
        }
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        HiMCBBSAccountAuth plugin = HiMCBBSAccountAuth.getInstance();
        if(args.length==1) {
            if (args[0].equals("reload")) {
                if(withoutPermission(sender, "himcauth.reload")) return false;
                plugin.disable();
                plugin.enable();
                sender.sendMessage(ChatColor.GREEN + "插件已重载。");
                return true;
            }
            if (args[0].equals("bind")) {
                if(withoutPermission(sender, "himcauth.bind")) return false;
                if(!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED+"该命令无法在控制台中使用！");
                    return true;
                }
                Player player = (Player) sender;
                NetworkManager manager = NetworkManager.getInstance();
                try {
                    if(StorageManager.getInstance().getRunningStorage().getUserId(player.getUniqueId())!=null) {
                        sender.sendMessage("你已经绑定过HiMCBBS账号了！");
                        return true;
                    }
                } catch (Exception ignored) {
                }
                if(stateMap.get(player.getUniqueId())!=null) {
                    Map.Entry<Long, JsonState> entry = stateMap.get(player.getUniqueId());
                    if(entry.getKey()+entry.getValue().expires_in>=Instant.now().getEpochSecond()) {
                        try (Response response = manager.POST("mcserver_client/getMCServerOAuthResult", getRequestBody(entry.getValue().state))) {
                            JsonBaseResponse<JsonUser> user = manager.getObjectByResponse(response, new TypeToken<JsonBaseResponse<JsonUser>>(){});
                            if(!response.isSuccessful()) {
                                throw new RuntimeException("授权失败！状态码："+user.status+" 消息："+user.message);
                            }
                            StorageManager.getInstance().getRunningStorage().setUserId(player.getUniqueId(), String.valueOf(user.data.user_id));
                            player.sendMessage(ChatColor.GREEN+"绑定成功！");
                            stateMap.remove(player.getUniqueId());
                            //TODO: force register/login player in the login plugin
                            return true;
                        } catch (Exception e) {
                            player.sendMessage(ChatColor.RED+"绑定账号时出现错误，正在重新请求...");
                            plugin.error(e, "尝试给%s绑定玩家账号时出现错误！", player.getName());
                        }
                    }
                    else {
                        stateMap.remove(player.getUniqueId());
                        player.sendMessage(ChatColor.RED+"之前的会话已过期，正在重新请求...");
                    }
                }
                try (Response response = manager.POST("mcserver_client/startMCServerOAuthLoginSession", getRequestBody(null))) {
                    JsonBaseResponse<JsonState> state = getState(response);
                    if(!response.isSuccessful()) {
                        throw new RuntimeException("请求出错！状态码："+state.status+" 消息："+state.message);
                    }
                    stateMap.put(player.getUniqueId(), Map.entry(Instant.now().getEpochSecond(), state.data));
                    BaseComponent component = new TextComponent("请在"+state.data.expires_in+"秒内");
                    BaseComponent component1 = new TextComponent("[点击此处]");
                    component1.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                    component1.setBold(true);
                    component1.setUnderlined(true);
                    component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("点此打开浏览器授权")));
                    component1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, String.format(AUTHORIZE_FORMAT_URL, NetworkManager.API_TEST_HOST, NetworkManager.API_TEST_PORT, state.data.state)));
                    component.addExtra(component1);
                    component.addExtra(new TextComponent("打开浏览器授权登录HiMCBBS账号"));
                    player.spigot().sendMessage(component);
                    BaseComponent component2 = new TextComponent("[完成后请点击此处]");
                    component2.setBold(true);
                    component2.setUnderlined(true);
                    component2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("点此检测授权")));
                    component2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/himcauth bind"));
                    player.spigot().sendMessage(component2);
                } catch (IOException | RuntimeException e) {
                    player.sendMessage(ChatColor.RED+"绑定账号时出现错误，请手动重新输入/himcauth bind指令重新绑定！");
                    plugin.error(e, "尝试给%s绑定玩家账号时出现错误！", player.getName());
                }
                return true;
            }
        }
        if(args.length==2) {
            if(args[0].equals("unbind")) {
                if(withoutPermission(sender, "himcauth.unbind")) return true;
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                if(player.hasPlayedBefore()) {
                    try {
                        StorageManager.getInstance().getRunningStorage().setUserId(player.getUniqueId(), null);
                        sender.sendMessage(ChatColor.GREEN+"解绑成功！");
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED+"尝试给" + player.getName() + "解绑玩家账号时出现错误！");
                        plugin.error(e, "尝试给%s解绑玩家账号时出现错误！", player.getName());
                    }
                    return true;
                }
                sender.sendMessage(ChatColor.RED+"无法找到玩家"+args[1]+"！");
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED+"命令格式错误！");
        return false;
    }

    private boolean withoutPermission(CommandSender sender, String s) {
        if(!sender.hasPermission(s)) {
            sender.sendMessage(ChatColor.RED+"你没有使用此命令的权限！");
            return true;
        }
        return false;
    }

    private JsonBaseResponse<JsonState> getState(Response response) throws IOException, JsonParseException {
        return NetworkManager.getInstance().getObjectByResponse(response, new TypeToken<JsonBaseResponse<JsonState>>(){});
    }

    private Map<String, String> getRequestBody(String state) {
        Map<String,String> map = new HashMap<>();
        map.put("server_id",String.valueOf(serverId));
        map.put("client_secret",clientSecret);
        if(state!=null) {
            map.put("state", state);
        }
        return map;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> res = new ArrayList<>();
        if(args.length == 1) {
            if(sender.hasPermission("himcauth.reload")) {
                res.add("reload");
            }
            if(sender.hasPermission("himcauth.bind")) {
                res.add("bind");
            }
            if(sender.hasPermission("himcauth.unbind")) {
                res.add("unbind");
            }
        }
        if(args.length == 2 && sender.hasPermission("himcauth.unbind") && args[0].equals("unbind")) {
            return Arrays.stream(sender.getServer().getOfflinePlayers()).map((OfflinePlayer::getName)).collect(Collectors.toList());
        }
        return res;
    }
}
