package com.himcbbs.play.serverclient.himcbbsauth.storage;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;

import java.util.ServiceLoader;

public class StorageManager {
    private Storage runningStorage;
    private static StorageManager INSTANCE;
    public void init() {
        ServiceLoader<Storage> storages = ServiceLoader.load(Storage.class);
        String storageMode = HiMCBBSAccountAuth.getInstance().getConfig().getString("storage-mode");
        //TODO: make a way to migrate storage mode
        for(Storage storage:storages) {
            if(storage.id().equals(storageMode)) {
                runningStorage=storage;
                break;
            }
        }
        if(runningStorage==null) {
            HiMCBBSAccountAuth.getInstance().info("Invalid storage mode!");
            HiMCBBSAccountAuth.getInstance().disable();
        }
        try {
            runningStorage.init();
        } catch (Exception e) {
            HiMCBBSAccountAuth.getInstance().error(e, "An error occurred while initializing storage '{}'!", storageMode);
            HiMCBBSAccountAuth.getInstance().disable();
        }
    }
    public static StorageManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new StorageManager();
        }
        return INSTANCE;
    }
}
