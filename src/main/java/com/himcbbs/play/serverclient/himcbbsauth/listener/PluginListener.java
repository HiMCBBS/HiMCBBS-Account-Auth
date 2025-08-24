package com.himcbbs.play.serverclient.himcbbsauth.listener;

import com.himcbbs.play.serverclient.himcbbsauth.event.RegisterAuthPluginHookEvent;
import com.himcbbs.play.serverclient.himcbbsauth.event.RegisterStorageModeEvent;
import com.himcbbs.play.serverclient.himcbbsauth.hook.AuthMeHook;
import com.himcbbs.play.serverclient.himcbbsauth.storage.JsonStorage;
import com.himcbbs.play.serverclient.himcbbsauth.storage.MariaDBStorage;
import com.himcbbs.play.serverclient.himcbbsauth.storage.MySQLStorage;
import com.himcbbs.play.serverclient.himcbbsauth.storage.SQLiteStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PluginListener implements Listener {
    @EventHandler
    public void onRegisterStorageMode(RegisterStorageModeEvent event) {
        event.registerStorageMode(new JsonStorage());
        event.registerStorageMode(new SQLiteStorage());
        event.registerStorageMode(new MariaDBStorage());
        event.registerStorageMode(new MySQLStorage());
    }

    @EventHandler
    public void onRegisterAuthPluginHook(RegisterAuthPluginHookEvent event) {
        event.registerAuthPluginHook(new AuthMeHook());
    }
}
