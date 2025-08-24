package com.himcbbs.play.serverclient.himcbbsauth.storage;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import com.himcbbs.play.serverclient.himcbbsauth.event.RegisterStorageModeEvent;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class StorageManager {
    private Storage runningStorage;
    private static StorageManager INSTANCE;

    public void init() throws Exception {
        String storageMode = HiMCBBSAccountAuth.getInstance().getConfig().getString("storage-mode");
        List<Storage> storages = new ArrayList<>();
        RegisterStorageModeEvent event = new RegisterStorageModeEvent(storages::add);
        Bukkit.getServer().getPluginManager().callEvent(event);
        for(Storage storage:storages) {
            if(storage.id().equals(storageMode)) {
                runningStorage=storage;
                break;
            }
        }
        if(runningStorage==null) {
            throw new RuntimeException("错误的存储配置：“"+storageMode+"”！");
        }
        runningStorage.init();
    }

    public void disable() throws Exception {
        if(runningStorage!=null) {
            runningStorage.close();
        }
    }

    public static StorageManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new StorageManager();
        }
        return INSTANCE;
    }

    public Storage getRunningStorage() {
        return runningStorage;
    }
}
