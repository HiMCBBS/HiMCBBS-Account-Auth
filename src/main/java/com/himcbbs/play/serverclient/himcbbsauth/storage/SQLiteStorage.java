package com.himcbbs.play.serverclient.himcbbsauth.storage;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class SQLiteStorage extends DatabaseStorage {

    @Override
    public void init() throws Exception {
        super.init();
        ConfigurationSection config = HiMCBBSAccountAuth.getInstance().getConfig().getConfigurationSection("sqlite");
        if (config == null) {
            throw new RuntimeException("SQLite配置缺失！");
        }
        String fileName = config.getString("file", "sqlite.db");
        tableName = config.getString("table", "himcauth");
        HikariConfig hikariConfig = new HikariConfig();
        File dbFile = HiMCBBSAccountAuth.getInstance().getStoragePath(this).resolve(fileName).toFile();
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setMaximumPoolSize(5);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);
        dataSource = new HikariDataSource(hikariConfig);
        createTable();
    }

    @Override
    public String id() {
        return "sqlite";
    }
}