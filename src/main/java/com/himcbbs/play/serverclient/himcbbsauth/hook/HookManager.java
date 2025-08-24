package com.himcbbs.play.serverclient.himcbbsauth.hook;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import com.himcbbs.play.serverclient.himcbbsauth.event.RegisterAuthPluginHookEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class HookManager {
    private List<Hook> hooks;
    private static HookManager INSTANCE;
    private Hook runningHook;
    private static final String PASSWORD_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*";
    private static SecureRandom random;

    public void init() {
        hooks = new ArrayList<>();
        RegisterAuthPluginHookEvent event = new RegisterAuthPluginHookEvent(hooks::add);
        HiMCBBSAccountAuth plugin = HiMCBBSAccountAuth.getInstance();
        plugin.getServer().getPluginManager().callEvent(event);
        random = new SecureRandom();
    }

    public void initHookByName(String name) {
        for(Hook hook:hooks) {
            if(hook.name().equals(name)) {
                HiMCBBSAccountAuth plugin = HiMCBBSAccountAuth.getInstance();
                try {
                    hook.initializeHook();
                    runningHook = hook;
                    if(runningHook instanceof Listener) {
                        plugin.getServer().getPluginManager().registerEvents((Listener) runningHook, plugin);
                        plugin.info("已注册接入接口“%s”的监听器！", hook.name());
                    }
                    plugin.info("已接入登录插件“%s”！", hook.name());
                } catch (Exception e) {
                    plugin.error(e, "在接入登录插件“%s”时出现错误！", hook.name());
                }
                break;
            }
        }
    }

    public void disableHook() {
        if(runningHook!=null) {
            try {
                runningHook.removeHook();
                HiMCBBSAccountAuth.getInstance().info("已移除登录插件“%s”的接入接口！", runningHook.name());
            } catch (Exception e) {
                HiMCBBSAccountAuth.getInstance().error(e, "在移除登录插件“%s”的接入接口时出现错误！", runningHook.name());
            }
        }
    }

    public void forceLogin(Player player) throws Exception {
        if(runningHook.isRegistered(player)) {
            runningHook.forceLogin(player);
            return;
        }
        StringBuilder builder = new StringBuilder();
        for(int i=1;i<=16;i++) {
            builder.append(PASSWORD_CHARACTERS.charAt(random.nextInt(PASSWORD_CHARACTERS.length())));
        }
        runningHook.forceRegister(player, builder.toString());
    }

    public void checkPluginHook() {
        if(runningHook==null) {
            HiMCBBSAccountAuth.getInstance().error("未安装任意支持的登录插件，无法接入！");
            HiMCBBSAccountAuth.getInstance().disable();
        }
    }

    public String getRunningHookName() {
        return runningHook.name();
    }

    public static HookManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new HookManager();
        }
        return INSTANCE;
    }
}
