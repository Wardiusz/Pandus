package com.wardiusz.Pandus.commands.DTO;

import com.wardiusz.Pandus.Handler.DBAction;

import java.sql.*;
import java.util.Properties;

import static com.wardiusz.Pandus.commands.DTO.MyDatabase.LOGGER;
import static com.wardiusz.Pandus.commands.DTO.MyDatabase.getConnection;

public class GuildRepository {
    public final String ID;
    public Properties guildProperties;

    public GuildRepository(String id) {
        this.ID = id;
        fetchProperties();
    }

    public void fetchProperties() {
        guildProperties = new Properties();

        String query = "SELECT * FROM guilds WHERE server = ?";
        String query2 = "INSERT INTO guilds(server, bot_role, log_channel, member_role, music_channel) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = connection
                    .prepareStatement(query)) {
                ps.setString(1, ID);


                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String member_role = rs.getString("member_role");
                        String bot_role = rs.getString("bot_role");
                        String music_channel = rs.getString("music_channel");
                        String log_channel = rs.getString("log_channel");

                        guildProperties.put("MEMBER_ROLE", member_role != null ? member_role : "");
                        guildProperties.put("BOT_ROLE", bot_role != null ? bot_role : "");
                        guildProperties.put("MUSIC_CHANNEL", music_channel != null ? music_channel : "");
                        guildProperties.put("LOG_CHANNEL", log_channel != null ? log_channel : "");

                        LOGGER.info("Loaded properties for guild \u001B[33m{}\u001B[0m from SQLite", ID);
                        return;
                    } else {
                        LOGGER.error("Couldn't load properties for guild \u001B[91m{}\u001B[0m from SQLite", ID);
                    }
                }
            }

            try (PreparedStatement ps2 = connection
                    .prepareStatement(query2)) {
                ps2.setString(1, ID);
                ps2.setString(2, "");
                ps2.setString(3, "");
                ps2.setString(4, "");
                ps2.setString(5, "");

                if (ps2.executeUpdate() > 0) {
                    LOGGER.info("Created new properties for guild: {} from SQLite", ID);
                    guildProperties.setProperty("MEMBER_ROLE", "");
                    guildProperties.setProperty("BOT_ROLE", "");
                    guildProperties.setProperty("MUSIC_ROLE", "");
                    guildProperties.setProperty("LOG_CHANNEL", "");
                } else {
                    LOGGER.error("Couldn't created new properties for guild: {} from SQLite", ID);
                }
            } catch (SQLException e) {
                LOGGER.error("Cannot load properties for guild {}: {}", ID, e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveProperties() {
        try (Connection connection = MyDatabase.getConnection()) {
            try (PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("UPDATE guilds SET bot_role = ?, log_channel = ?, member_role = ?, music_channel = ? WHERE server = ?")) {

                ps.setString(1, guildProperties.getProperty(GuildOptions.BOT_ROLE.name()));
                ps.setString(2, guildProperties.getProperty(GuildOptions.LOG_CHANNEL.name()));
                ps.setString(3, guildProperties.getProperty(GuildOptions.MEMBER_ROLE.name()));
                ps.setString(4, guildProperties.getProperty(GuildOptions.MUSIC_CHANNEL.name()));
                ps.setString(5, ID);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild properties successfully saved in SQLite database.");
                } else {
                    LOGGER.error("Couldn't save guild properties in SQLite database. 1");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't save guild properties in SQLite database: {}", e.getMessage());
        }
    }

    public void addBanRecord(String member, String admin, String reason) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("INSERT INTO Bans(server_id, member, admin, reason) VALUES ((SELECT id FROM guilds WHERE server = ?), ?, ?, ?)")) {

                ps.setString(1, ID);
                ps.setString(2, member);
                ps.setString(3, admin);
                ps.setString(4, reason);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild ban record successfully saved in SQLite database.");
                } else {
                    LOGGER.error("Couldn't save guild ban record in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't save guild ban record in SQLite database: {}", e.getMessage());
        }
    }

    public void deleteBanRecord(String member) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("DELETE FROM Bans WHERE server_id = (SELECT id FROM guilds WHERE server = ?) AND member = ?")) {

                ps.setString(1, ID);
                ps.setString(2, member);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild ban record successfully deleted in SQLite database.");
                } else {
                    LOGGER.error("Couldn't delete guild ban record in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't delete guild ban record in SQLite database: {}", e.getMessage());
        }
    }

    public void deleteRecords(String id) {
        try (Connection connection = MyDatabase.getConnection()) {
            PreparedStatement ps1 = connection
                    //  language=SQLite
                    .prepareStatement("PRAGMA foreign_keys = ON");
            PreparedStatement ps2 = connection
                    //  language=SQLite
                    .prepareStatement("DELETE FROM guilds WHERE server = ?");

            ps1.execute();
            ps2.setString(1, id);

            if (ps2.executeUpdate() > 0) {
                LOGGER.info("Guild properties successfully deleted in SQLite database.");
            } else {
                LOGGER.error("Couldn't delete guild properties in SQLite database.");
            }

        } catch (SQLException e) {
            LOGGER.error("Couldn't delete guild properties in SQLite database: {}", e.getMessage());
        }
    }

    public boolean isMuted(String member) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("SELECT COUNT(id) AS COUNT FROM mutes WHERE server_id = (SELECT id FROM guilds WHERE server = ?) AND member = ?")) {

                ps.setString(1, ID);
                ps.setString(2, member);

                ResultSet data = ps.executeQuery();

                int count = 0;

                while (data.next()) {
                    count = data.getInt("COUNT");
                }

                ps.closeOnCompletion();

                return count > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't search for record in SQLite database: {}", e.getMessage());
        }
        return false;
    }

    public void addMute(String member, String admin, String time, String reason) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (final PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("INSERT INTO mutes(server_id, member, admin, reason, cmd_expiration) VALUES ((SELECT id FROM guilds WHERE server = ?), ?, ?, ?, ?);")) {
                ps.setString(1, ID);
                ps.setString(2, member);
                ps.setString(3, admin);
                ps.setString(4, reason);
                ps.setString(5, time);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild mute record successfully saved in SQLite database.");
                } else {
                    LOGGER.error("Couldn't save guild mute record in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't save guild mute record in SQLite database: {}", e.getMessage());
        }
    }

    public void deleteMute(String member) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (final PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("DELETE FROM mutes WHERE server_id = (SELECT id FROM guilds WHERE server = ?) AND member = ?;")) {
                ps.setString(1, ID);
                ps.setString(2, member);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild mute record successfully deleted in SQLite database.");
                } else {
                    LOGGER.error("Couldn't delete guild mute record in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't delete guild mute record in SQLite database: {}", e.getMessage());
        }
    }

    public boolean isThereThingies() {
        try (Connection connection = MyDatabase.getConnection()) {
            try (PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("SELECT COUNT(id) AS COUNT FROM thingies WHERE server_id = (SELECT id FROM guilds WHERE server = ?)")) {

                ps.setString(1, ID);

                ResultSet data = ps.executeQuery();

                int count = 0;

                while (data.next()) {
                    count = data.getInt("COUNT");
                }

                ps.closeOnCompletion();

                return count > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't search for record in SQLite database: {}", e.getMessage());
        }
        return false;
    }

    public void addWelcomeMsg(String msg, boolean useCard) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (final PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("INSERT INTO thingies(server_id, welcome_msg, use_card) VALUES ((SELECT id FROM guilds WHERE server = ?), ?, ?);")) {
                ps.setString(1, ID);
                ps.setString(2, msg);
                ps.setString(3, String.valueOf(useCard));

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild welcome message successfully saved in SQLite database.");
                } else {
                    LOGGER.error("Couldn't save guild welcome message in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't save guild welcome message in SQLite database: {}", e.getMessage());
        }
    }

    public void addGoodbyeMsg(String msg) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (final PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("INSERT INTO thingies(server_id, goodbye_msg) VALUES ((SELECT id FROM guilds WHERE server = ?), ?);")) {
                ps.setString(1, ID);
                ps.setString(2, msg);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild welcome message successfully saved in SQLite database.");
                } else {
                    LOGGER.error("Couldn't save guild welcome message in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't save guild welcome message in SQLite database: {}", e.getMessage());
        }
    }

    public void updateWelcomeMsg(String msg, boolean useCard) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("UPDATE thingies SET welcome_msg = ?, use_card = ? WHERE server_id = (SELECT id FROM guilds WHERE server = ?)")) {

                ps.setString(1, msg);
                ps.setString(2, String.valueOf(useCard));
                ps.setString(3, ID);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild welcome message successfully updated in SQLite database.");
                } else {
                    LOGGER.error("Couldn't update guild welcome message in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't update guild welcome message in SQLite database: {}", e.getMessage());
        }
    }

    public void updateGoodbyeMsg(String msg) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("UPDATE thingies SET goodbye_msg = ? WHERE server_id = (SELECT id FROM guilds WHERE server = ?)")) {

                ps.setString(1, msg);
                ps.setString(2, ID);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild goodbye message successfully updated in SQLite database.");
                } else {
                    LOGGER.error("Couldn't update guild goodbye message in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't update guild goodbye message in SQLite database: {}", e.getMessage());
        }
    }

    public String getWelcomeMsg() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = connection
                    .prepareStatement("SELECT welcome_msg FROM thingies WHERE server_id = (SELECT id FROM guilds WHERE server = ?)")) {
                ps.setString(1, ID);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.getString("welcome_msg") != null ? rs.getString("welcome_msg") : ConfigOptions.WELCOME_MSG.getValue();
                }
            } catch (SQLException e) {
                LOGGER.error("Cannot load properties for guild {}: {}", ID, e.getMessage());
            }

            return ConfigOptions.WELCOME_MSG.getValue();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getGoodbyeMsg() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = connection
                    .prepareStatement("SELECT goodbye_msg FROM thingies WHERE server_id = (SELECT id FROM guilds WHERE server = ?)")) {
                ps.setString(1, ID);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.getString("goodbye_msg") != null ? rs.getString("goodbye_msg") : ConfigOptions.GOODBYE_MSG.getValue();
                }
            } catch (SQLException e) {
                LOGGER.error("Cannot load properties for guild {}: {}", ID, e.getMessage());
            }

            return ConfigOptions.GOODBYE_MSG.getValue();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getUseCard() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = connection
                    .prepareStatement("SELECT use_card FROM thingies WHERE server_id = (SELECT id FROM guilds WHERE server = ?)")) {
                ps.setString(1, ID);

                try (ResultSet rs = ps.executeQuery()) {
                    return Boolean.parseBoolean(rs.getString("use_card"));
                }
            } catch (SQLException e) {
                LOGGER.error("Cannot load properties for guild {}: {}", ID, e.getMessage());
            }

            return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAutoBotRole() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = connection
                    .prepareStatement("SELECT auto_bot_role FROM thingies WHERE server_id = (SELECT id FROM guilds WHERE server = ?)")) {
                ps.setString(1, ID);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.getString("auto_bot_role");
                }
            } catch (SQLException e) {
                LOGGER.error("Cannot load properties for guild {}: {}", ID, e.getMessage());
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAutoNewMemberRole() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = connection
                    .prepareStatement("SELECT auto_new_member_role FROM thingies WHERE server_id = (SELECT id FROM guilds WHERE server = ?)")) {
                ps.setString(1, ID);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.getString("auto_new_member_role");
                }
            } catch (SQLException e) {
                LOGGER.error("Cannot load properties for guild {}: {}", ID, e.getMessage());
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

// TODO: Dokończyć wpisywanie tego query - dokończ metodę
    public void addAutoBotRole(String role) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (final PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("INSERT INTO thingies(server_id, auto_bot_role) VALUES ((SELECT id FROM guilds WHERE server = ?), ?);")) {
                ps.setString(1, ID);
                ps.setString(2, role);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild auto bot role successfully saved in SQLite database.");
                } else {
                    LOGGER.error("Couldn't guild auto bot role message in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't save guild auto bot role in SQLite database: {}", e.getMessage());
        }
    }
// TODO: Dokończyć wpisywanie tego query - dokończ metodę
    public void addAutoNewMemberRole(String role) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (final PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("INSERT INTO thingies(server_id, auto_new_member_role)" +
                            "SELECT id, ? " +
                            "FROM guilds " +
                            "WHERE server = ?;")) {
                ps.setString(1, ID);
                ps.setString(2, role);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild auto bot role successfully saved in SQLite database.");
                } else {
                    LOGGER.error("Couldn't save guild auto new member role in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't save guild auto new member role in SQLite database: {}", e.getMessage());
        }
    }
// TODO: Dokończyć wpisywanie tego query - dokończ metodę
    public void updateAutoBotRole(String msg, DBAction action) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("UPDATE thingies " +
                            "SET auto_bot_role = ? " +
                            "FROM guilds " +
                            "WHERE thingies.server_id = guilds.id " +
                            "AND guilds.server = ?;")) {

                ps.setString(1, msg);
                ps.setString(2, ID);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild auto bot role successfully updated in SQLite database.");
                } else {
                    LOGGER.error("Couldn't update guild auto bot role in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't update guild auto bot role in SQLite database: {}", e.getMessage());
        }
    }
// TODO: Dokończyć wpisywanie tego query - dokończ metodę
    public void updateAutoNewMemberRole(String msg) {
        try (Connection connection = MyDatabase.getConnection()) {
            try (PreparedStatement ps = connection
                    // language=SQLite
                    .prepareStatement("UPDATE thingies SET auto_new_member_role = ? WHERE server_id = (SELECT id FROM guilds WHERE server = ?)")) {

                ps.setString(1, msg);
                ps.setString(2, ID);

                if (ps.executeUpdate() > 0) {
                    LOGGER.info("Guild auto new member role successfully updated in SQLite database.");
                } else {
                    LOGGER.error("Couldn't update guild auto new member role in SQLite database.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't update guild auto new member role in SQLite database: {}", e.getMessage());
        }
    }
}
