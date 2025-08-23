package com.himcbbs.play.serverclient.himcbbsauth.hook;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import com.himcbbs.play.serverclient.himcbbsauth.event.RegisterAuthPluginHookEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HookManager {
    private List<Hook> hooks;
    private static HookManager INSTANCE;
    private Hook runningHook;

    public void init() {
        hooks = new ArrayList<>();
        RegisterAuthPluginHookEvent event = new RegisterAuthPluginHookEvent(hooks::add);
        HiMCBBSAccountAuth plugin = HiMCBBSAccountAuth.getInstance();
        plugin.getServer().getPluginManager().callEvent(event);
    }

    public void initHookByName(String name) {
        for(Hook hook:hooks) {
            if(hook.name().equals(name)) {
                try {
                    hook.initializeHook();
                    runningHook = hook;
                } catch (Exception e) {
                    HiMCBBSAccountAuth.getInstance().error(e, "在接入登录插件“%s”时出现错误！", hook.name());
                }
                break;
            }
        }
    }

    public void forceLogin(Player player) throws Exception {
        runningHook.forceLogin(player);
    }

    public void checkPluginHook() {
        if(runningHook==null) {
            HiMCBBSAccountAuth.getInstance().error("未安装任意支持的登录插件，无法接入！");
            HiMCBBSAccountAuth.getInstance().disable();
        }
    }

    public static HookManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new HookManager();
        }
        return INSTANCE;
    }
}
