package com.wardiusz.Pandus.commands.DTO;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.wardiusz.Pandus.Handler.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MyDatabase {
    static final Logger LOGGER = LoggerFactory.getLogger(MyDatabase.class);
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource dataSource;

    static {
        try {
            final File dbFile = new File("database.db");

            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    LOGGER.info("Database file has been created.");
                } else {
                    LOGGER.error("Database file could not been created.");
                }
            }
        } catch (IOException e) {
            LOGGER.error("Could not create database file: {}", e.getMessage());
        }

        config.setJdbcUrl("jdbc:sqlite:database.db");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(30000); // 30 seconds
        config.setConnectionTimeout(30000); // 30 seconds
        config.setLeakDetectionThreshold(60000); // 60 seconds for leak detection
        dataSource = new HikariDataSource(config);


        try (Connection connection = getConnection();
             Statement statement = connection
                     .createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");

        //  Table "Guilds"
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Guilds (
                    id             INTEGER PRIMARY KEY AUTOINCREMENT,
                    server         BIGINT NOT NULL,
                    prefix         VARCHAR(10) NOT NULL DEFAULT '%s',
                    active_modules TEXT NOT NULL DEFAULT '{"welcome": false, "goodbye": false, "music": false, "leveling": false, "words_filtering": false, "media_channel_filtering": false}',
                    bot_role       BIGINT NOT NULL,
                    log_channel    BIGINT NOT NULL,
                    member_role    BIGINT NOT NULL,
                    music_channel  BIGINT NOT NULL,
                    joined_at      DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP,'localtime'))
                );
            """);

        //  Table "WelcomeConfig"
            statement.execute("""
                CREATE TABLE IF NOT EXISTS WelcomeConfig (
                    server_id        INTEGER PRIMARY KEY,
                    enabled          INTEGER NOT NULL DEFAULT 0,
                    channel_id       BIGINT,
                    message_template TEXT,
                    embed_config     TEXT,
                    FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE
                );
            """);

        //  Table "GoodbyeConfig"
            statement.execute("""
                CREATE TABLE IF NOT EXISTS GoodbyeConfig (
                    server_id        INTEGER PRIMARY KEY,
                    enabled          INTEGER NOT NULL DEFAULT 0,
                    channel_id       BIGINT,
                    message_template TEXT,
                    embed_config     TEXT,
                    FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE
                );
            """);

        // Table "AutomodRules"
            statement.execute("""
                CREATE TABLE IF NOT EXISTS AutomodRules (
                    id              INTEGER PRIMARY KEY AUTOINCREMENT,
                    server_id       INTEGER NOT NULL,
                    rule_type       VARCHAR(50) NOT NULL,
                    enabled         INTEGER NOT NULL DEFAULT 1,
                    'action'          VARCHAR(20) NOT NULL DEFAULT 'delete',
                    action_duration INTEGER,
                    'options'         TEXT,
                    exempt_roles    TEXT,
                    exempt_channels TEXT,
                    UNIQUE (server_id, rule_type),
                    FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE
                );
            """);

        //  Table "Mutes"
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Mutes (
                    id             INTEGER PRIMARY KEY AUTOINCREMENT,
                    server_id      INTEGER NOT NULL,
                    member         BIGINT NOT NULL,
                    'admin'          BIGINT NOT NULL,
                    reason         VARCHAR(255),
                    cmd_creation   DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP,'localtime')),
                    cmd_expiration DATETIME NOT NULL,
                    FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE
                    );
                """);

        //  Table "Bans"
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Bans (
                    id           INTEGER PRIMARY KEY AUTOINCREMENT,
                    server_id    INTEGER NOT NULL,
                    member       BIGINT NOT NULL,
                    'admin'      BIGINT NOT NULL,
                    reason       VARCHAR(255),
                    cmd_creation DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP,'localtime')),
                    FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE
                    );
                """);

        // Table "Ranks"
            statement.execute("""
                CREATE TABLE IF NOT EXISTS LevelingConfig (
                    server_id        INTEGER PRIMARY KEY,
                    enabled          INTEGER NOT NULL DEFAULT 0,
                    levelup_channel  INTEGER,
                    levelup_message  TEXT,
                    ignored_channels TEXT,
                    FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE
                );
            """);

        // Table "Ranks"
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Ranks (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    server_id   INTEGER NOT NULL UNIQUE,
                    member      BIGINT NOT NULL,
                    xp          BIGINT NOT NULL DEFAULT 0,
                    rank        INTEGER NOT NULL DEFAULT 0,
                    UNIQUE (server_id, member),
                    FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE
                );
            """);

        //  Table "AutoRoles"
            statement.execute("""
                CREATE TABLE IF NOT EXISTS AutoRoles (
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    server_id INTEGER NOT NULL,
                    role_id   BIGINT NOT NULL,
                    'trigger'   VARCHAR(50) NOT NULL DEFAULT 'on_join',
                    UNIQUE (server_id, role_id, trigger),
                    FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE
                );
            """);

        } catch (SQLException e) {
            LOGGER.error("Error initializing database: {}", e.getMessage());
        }
     }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
