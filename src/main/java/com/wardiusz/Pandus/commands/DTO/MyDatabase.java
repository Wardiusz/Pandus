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
            statement.execute("CREATE TABLE IF NOT EXISTS Guilds (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "server BIGINT NOT NULL," +
                    "prefix VARCHAR(255) NOT NULL DEFAULT '" + Config.get("PREFIX") + "'," +
                    "bot_role BIGINT NOT NULL," +
                    "log_channel BIGINT NOT NULL," +
                    "member_role BIGINT NOT NULL," +
                    "music_channel BIGINT NOT NULL" +
                    ");");

        //  Table "Mutes"
            statement.execute("CREATE TABLE IF NOT EXISTS Mutes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "server_id INTEGER NOT NULL," +
                    "member BIGINT NOT NULL," +
                    "'admin' BIGINT NOT NULL," +
                    "reason VARCHAR(255)," +
                    "cmd_creation DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP,'localtime'))," +
                    "cmd_expiration DATETIME NOT NULL " +
                    "FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE" +
                    ");");

        //  Table "Bans"
            statement.execute("CREATE TABLE IF NOT EXISTS Bans (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "server_id INTEGER NOT NULL," +
                    "member BIGINT NOT NULL," +
                    "'admin' BIGINT NOT NULL," +
                    "reason VARCHAR(255)," +
                    "cmd_creation DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP,'localtime')) " +
                    "FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE" +
                    ");");

        //  Table "Ranks"
            statement.execute("CREATE TABLE IF NOT EXISTS Ranks (" +
                    "id INTEGER PRIMARY KEY," +
                    "server_id INTEGER NOT NULL," +
                    "member BIGINT NOT NULL UNIQUE," +
                    "xp BIGINT NOT NULL," +
                    "rank INTEGER NOT NULL " +
                    "FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE" +
                    ");");

// TODO: Dokończyć podmiankę auto bot role i auto new member role pomiędzy thingies oraz autocommands
        //  Table "Thingies"
            statement.execute("CREATE TABLE IF NOT EXISTS Thingies (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "server_id INTEGER NOT NULL," +
                    "curse_words VARCHAR(255)," +
                    "welcome_msg VARCHAR(255)," +
                    "goodbye_msg VARCHAR(255)," +
                    "use_card VARCHAR(5) NOT NULL," +
                    "auto_bot_role VARCHAR(255)," +
                    "auto_new_member_role VARCHAR(255) " +
                    "FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE" +
                    ");");

        //  Table "AutoCommands"
            statement.execute("CREATE TABLE IF NOT EXISTS AutoCommands (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "server_id INTEGER NOT NULL," +
                    "curse_words VARCHAR(255)," +
                    "del_message VARCHAR(255)," +
                    "bot_role VARCHAR(255)," +
                    "new_member_role VARCHAR(255) " +
                    "FOREIGN KEY (server_id) REFERENCES Guilds(id) ON DELETE CASCADE" +
                    ");");

        //  Table "DelMessageCommand"
            statement.execute("CREATE TABLE IF NOT EXISTS DelMessageCommand (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "autocommand_id INTEGER NOT NULL," +
                    "del_message VARCHAR(255)," +
                    "channel VARCHAR(255)," +
                    "phrase VARCHAR(255) " +
                    "FOREIGN KEY (autocommand_id) REFERENCES AutoCommands(id) ON DELETE CASCADE" +
                    ");");

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
