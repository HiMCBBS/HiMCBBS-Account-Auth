package com.himcbbs.play.serverclient.himcbbsauth.storage;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

public class MySQLStorage extends DatabaseStorage {

    @Override
    public void init() throws Exception {
        super.init();
        ConfigurationSection config = HiMCBBSAccountAuth.getInstance().getConfig().getConfigurationSection("mysql");
        if (config == null) {
            throw new RuntimeException("MySQL配置缺失！");
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
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
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
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        dataSource = new HikariDataSource(hikariConfig);
        // 创建表（为啥MySQL不能用基类的createTable啊啊啊）
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
        return "mysql";
    }
}