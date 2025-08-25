package com.himcbbs.play.serverclient.himcbbsauth.storage;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// SQL的抽象基类（不然太麻烦了）
public abstract class DatabaseStorage implements Storage {
    protected HikariDataSource dataSource;
    protected ExecutorService executorService;
    protected String tableName;

    @Nullable
    @Override
    public String getUserId(UUID uuid) throws Exception {
        CompletableFuture<String> future = new CompletableFuture<>();

        executorService.submit(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT user_id FROM " + tableName + " WHERE uuid = ?")) {

                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        future.complete(resultSet.getString("user_id"));
                    } else {
                        future.complete(null);
                    }
                }
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        });

        return future.get();
    }

    @Override
    public void setUserId(UUID uuid, @Nullable String userId) throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();

        executorService.submit(() -> {
            try (Connection connection = dataSource.getConnection()) {
                // 先检查是否存在该UUID的记录
                boolean exists;
                try (PreparedStatement checkStatement = connection.prepareStatement(
                        "SELECT 1 FROM " + tableName + " WHERE uuid = ?")) {

                    checkStatement.setString(1, uuid.toString());
                    try (ResultSet resultSet = checkStatement.executeQuery()) {
                        exists = resultSet.next();
                    }
                }

                if (userId == null) {
                    // 删除记录
                    try (PreparedStatement deleteStatement = connection.prepareStatement(
                            "DELETE FROM " + tableName + " WHERE uuid = ?")) {

                        deleteStatement.setString(1, uuid.toString());
                        deleteStatement.executeUpdate();
                    }
                } else if (exists) {
                    // 更新记录
                    try (PreparedStatement updateStatement = connection.prepareStatement(
                            "UPDATE " + tableName + " SET user_id = ? WHERE uuid = ?")) {

                        updateStatement.setString(1, userId);
                        updateStatement.setString(2, uuid.toString());
                        updateStatement.executeUpdate();
                    }
                } else {
                    // 插入新记录
                    try (PreparedStatement insertStatement = connection.prepareStatement(
                            "INSERT INTO " + tableName + " (uuid, user_id) VALUES (?, ?)")) {

                        insertStatement.setString(1, uuid.toString());
                        insertStatement.setString(2, userId);
                        insertStatement.executeUpdate();
                    }
                }

                future.complete(null);
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        });

        future.get();
    }

    protected void createTable() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             java.sql.Statement statement = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "user_id VARCHAR(255)" +
                    ")";

            statement.execute(createTableSQL);
        }
    }

    @Override
    public void init() throws Exception {
        // 创建线程池用于异步数据库操作
        executorService = Executors.newFixedThreadPool(5);
    }

    @Override
    public Map<UUID, String> getAllMappings() throws Exception {
        CompletableFuture<Map<UUID, String>> future = new CompletableFuture<>();

        executorService.submit(() -> {
            Map<UUID, String> result = new HashMap<>();
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT uuid, user_id FROM " + tableName);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String uuidStr = resultSet.getString("uuid");
                    String userId = resultSet.getString("user_id");
                    result.put(UUID.fromString(uuidStr), userId);
                }
                future.complete(Collections.unmodifiableMap(result));
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        });

        return future.get();
    }

    @Override
    public void close() {
        // 关闭线程池
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // 关闭数据源
        if (dataSource != null) {
            dataSource.close();
        }
    }
}