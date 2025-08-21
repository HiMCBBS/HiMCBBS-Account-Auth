package com.himcbbs.play.serverclient.himcbbsauth.listener;

import com.himcbbs.play.serverclient.himcbbsauth.event.RegisterStorageModeEvent;
import com.himcbbs.play.serverclient.himcbbsauth.storage.JsonStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegisterListener implements Listener {
    @EventHandler
    public void onRegisterStorageMode(RegisterStorageModeEvent event) {
        event.registerStorageMode(new JsonStorage());
    }
}
