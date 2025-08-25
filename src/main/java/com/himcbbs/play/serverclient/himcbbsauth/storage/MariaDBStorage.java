package com.himcbbs.play.serverclient.himcbbsauth.storage;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

public class MariaDBStorage extends DatabaseStorage {

    @Override
    public void init() throws Exception {
        super.init();
        ConfigurationSection config = HiMCBBSAccountAuth.getInstance().getConfig().getConfigurationSection("mariadb");
        if (config == null) {
            throw new RuntimeException("MariaDB配置缺失！");
        }
        String host = config.getString("host", "localhost");
        int port = config.getInt("port", 3306);
        String database = config.getString("database", "himcauth");
        String username = config.getString("username", "root");
        String password = config.getString("password", "");
        tableName = config.getString("table", "himcauth");
        int poolSize = config.getInt("pool-size", 10);
        int connectionTimeout = config.getInt("connection-timeout", 30000);
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        dataSource = new HikariDataSource(hikariConfig);
        // 好好好MariaDB也不能用
        try (java.sql.Statement statement = dataSource.getConnection().createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "user_id VARCHAR(255)" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            statement.execute(createTableSQL);
        }
    }

    @Override
    public String id() {
        return "mariadb";
    }
}