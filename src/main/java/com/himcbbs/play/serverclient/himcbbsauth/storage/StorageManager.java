package com.himcbbs.play.serverclient.himcbbsauth.storage;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;

import java.util.ServiceLoader;

public class StorageManager {
    private Storage runningStorage;
    private static StorageManager INSTANCE;
    public void init() throws Exception {
        ServiceLoader<Storage> storages = ServiceLoader.load(Storage.class);
        String storageMode = HiMCBBSAccountAuth.getInstance().getConfig().getString("storage-mode");
        //TODO: make a way to migrate storage mode
        for(Storage storage:storages) {
            HiMCBBSAccountAuth.getInstance().info(storage.id());
            if(storage.id().equals(storageMode)) {
                runningStorage=storage;
                break;
            }
        }
        if(runningStorage==null) {
            throw new RuntimeException("Invalid storage mode '"+storageMode+"'");
        }
        runningStorage.init();
    }
    public static StorageManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new StorageManager();
        }
        return INSTANCE;
    }
}
