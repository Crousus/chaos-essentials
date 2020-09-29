package de.chaosolymp.chaosessentials;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseProvider {

    private final HikariConfig config;
    private final HikariDataSource dataSource;
    private static Connection connection;
    private static DatabaseProvider instance;

    private DatabaseProvider(String jdbcUrl, String username, String password) {
        this.config = new HikariConfig();
        this.config.setJdbcUrl(jdbcUrl);
        this.config.setUsername(username);
        this.config.setPassword(password);
        this.config.addDataSourceProperty("cachePrepStmts", "true");
        this.config.addDataSourceProperty("prepStmtCacheSize", "250");
        this.config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.dataSource = new HikariDataSource(this.config);
    }

    public HikariDataSource getDataSource() {
        return instance.dataSource;
    }

    public HikariConfig getConfig() {
        return this.config;
    }


    static Connection getConnection(JavaPlugin plugin) {
        if (instance == null)
            instance = new DatabaseProvider(plugin.getConfig().getString("jdbc") + plugin.getConfig().getString("database"), plugin.getConfig().getString("username"), plugin.getConfig().getString("password"));
        try {
            connection = instance.dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    static boolean isConnected() {
        return instance.dataSource.isRunning();
    }


}