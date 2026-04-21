package com.university.srms.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database Connection Manager using HikariCP connection pool.
 * Singleton pattern — one pool shared across the application.
 */
public class DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    private static HikariDataSource dataSource;

    // Prevent instantiation
    private DBConnection() {}

    /**
     * Initialise the connection pool.
     * Call once at application startup (e.g., from a ServletContextListener).
     */
    public static synchronized void init() {
        if (dataSource != null && !dataSource.isClosed()) return;

        Properties props = loadProperties();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url",
                "jdbc:mysql://localhost:3306/university_srms?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true&characterEncoding=UTF-8"));
        config.setUsername(props.getProperty("db.username", "root"));
        config.setPassword(props.getProperty("db.password", "password"));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Pool settings
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300_000);           // 5 minutes
        config.setConnectionTimeout(30_000);      // 30 seconds
        config.setMaxLifetime(1_800_000);          // 30 minutes
        config.setLeakDetectionThreshold(60_000); // warn if connection held > 1 min
        config.setPoolName("SRMS-Pool");

        // MySQL optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        dataSource = new HikariDataSource(config);
        logger.info("Database connection pool initialized successfully");
    }

    /**
     * Get a connection from the pool.
     * Always use in a try-with-resources block!
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            init();
        }
        return dataSource.getConnection();
    }

    /**
     * Shutdown the pool gracefully.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool shut down");
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
                logger.info("Loaded db.properties");
            } else {
                logger.warn("db.properties not found — using defaults");
            }
        } catch (IOException e) {
            logger.error("Failed to load db.properties", e);
        }
        return props;
    }
}
