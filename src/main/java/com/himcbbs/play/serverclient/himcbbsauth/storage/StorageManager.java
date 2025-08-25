package com.himcbbs.play.serverclient.himcbbsauth.storage;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import com.himcbbs.play.serverclient.himcbbsauth.event.RegisterStorageModeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StorageManager {
    private Storage runningStorage;
    private static StorageManager INSTANCE;

    public void init() throws Exception {
        HiMCBBSAccountAuth plugin = HiMCBBSAccountAuth.getInstance();
        String storageMode = plugin.getConfig().getString("storage-mode");
        String lastStorageMode = plugin.getConfig().getString("previous-storage-mode", storageMode);
        if(lastStorageMode.equals(storageMode)) {
            lastStorageMode = null;
        }
        List<Storage> storages = new ArrayList<>();
        RegisterStorageModeEvent event = new RegisterStorageModeEvent(storages::add);
        plugin.getServer().getPluginManager().callEvent(event);
        Storage lastStorage = null;
        for(Storage storage:storages) {
            if(storage.id().equals(storageMode)) {
                runningStorage=storage;
            }
            if(storage.id().equals(lastStorageMode)) {
                lastStorage=storage;
            }
        }
        if(runningStorage==null) {
            throw new RuntimeException("错误的存储配置：“"+storageMode+"”！");
        }
        runningStorage.init();
        if(lastStorage!=null) {
            lastStorage.init();
            plugin.info("检测到存储配置变更：“%s” -> “%s”", lastStorageMode, storageMode);
            plugin.info("开始数据迁移...");
            try {
                Map<UUID, String> oldData = lastStorage.getAllMappings();
                int cnt = 0, total = oldData.size(), progress = 0;
                plugin.info("从“%s”中读取了%d条记录",lastStorageMode,total);
                for(Map.Entry<UUID, String> entry:oldData.entrySet()) {
                    runningStorage.setUserId(entry.getKey(), entry.getValue());
                    cnt++;
                    int now = cnt / total;
                    if(now-progress>=5) {
                        progress = now;
                        plugin.info("迁移进度：%d%%", progress);
                    }
                }
                plugin.info("成功迁移%d条记录到“%s”！", total, storageMode);
                plugin.getConfig().set("previous-storage-mode", storageMode);
                plugin.saveConfig();
                lastStorage.close();
            } catch (Exception e) {
                plugin.error(e, "数据迁移失败！");
            }
        }
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
