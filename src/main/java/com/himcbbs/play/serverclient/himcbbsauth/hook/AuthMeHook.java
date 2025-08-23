package com.himcbbs.play.serverclient.himcbbsauth.hook;

import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.entity.Player;

public class AuthMeHook implements Hook {
    private Object api;

    @Override
    public void forceLogin(Player player) {
        ((AuthMeApi)api).forceLogin(player);
    }

    @Override
    public void initializeHook() {
        api = AuthMeApi.getInstance();
    }

    @Override
    public void removeHook() {
        api = null;
    }

    @Override
    public boolean isActive() {
        return api!=null;
    }

    @Override
    public String name() {
        return "AuthMe";
    }
}
