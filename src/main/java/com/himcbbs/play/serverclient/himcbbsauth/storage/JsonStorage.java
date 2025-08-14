package com.himcbbs.play.serverclient.himcbbsauth.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonStorage implements Storage {
    private File storageFile;
    private Gson gson;
    private Map<String,String> map;

    @Nullable
    @Override
    public String getUserId(UUID uuid) {
        return map.get(uuid.toString());
    }

    @Override
    public void setUserId(UUID uuid, String userId) throws Exception {
        map.put(uuid.toString(), userId);
        try(FileOutputStream os = new FileOutputStream(storageFile)) {
            os.write(gson.toJson(map, new TypeToken<Map<String,String>>(){}.getType()).getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void init() throws Exception {
        storageFile = HiMCBBSAccountAuth.getInstance().getStoragePath(this).resolve("storage.json").toFile();
        if(storageFile.createNewFile()) {
            try(FileOutputStream os = new FileOutputStream(storageFile)) {
                os.write("{}".getBytes(StandardCharsets.UTF_8));
            }
        }
        gson = new GsonBuilder().setPrettyPrinting().create();
        String file;
        try(FileInputStream is = new FileInputStream(storageFile)) {
            file = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        map = gson.fromJson(file, new TypeToken<HashMap<String,String>>(){}.getType());
    }

    @Override
    public String id() {
        return "json";
    }
}
