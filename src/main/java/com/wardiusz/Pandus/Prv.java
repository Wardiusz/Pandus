package com.wardiusz.Pandus;

import com.wardiusz.Pandus.Handler.DBAction;
import com.wardiusz.Pandus.commands.DTO.ConfigOptions;
import com.wardiusz.Pandus.commands.DTO.DBGuilds;
import com.wardiusz.Pandus.commands.DTO.GuildOptions;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.wardiusz.Pandus.Handler.Config.assignTempLogChannel;
import static com.wardiusz.Pandus.Handler.Config.assignTempMusicChannel;

public class Prv {
    private static Optional<DBGuilds> findGuildMatches(String guildId) {
        return Arrays.stream(Provider.getDbGuilds())
                .filter(dbGuilds -> Objects.equals(dbGuilds.ID, guildId))
                .findAny();
    }
    
    public static boolean updateGuildProperty(GuildOptions key, String value, String guildId) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return false;
        }

        DBGuilds guild = opt.get();
        guild.guildProperties.put(key.name(), value);
        guild.saveProperties();
        return true;
    }

    public static void addBanRecord(String guildId, String member, String admin, String reason) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return;
        }

        DBGuilds guild = opt.get();
        guild.addBanRecord(member, admin, reason);
    }

    public static void deleteBanRecord(String guildId, String member) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return;
        }

        DBGuilds guild = opt.get();
        guild.deleteBanRecord(member);
    }

    public static void deleteMuteRecord(String guildId, String member) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return;
        }

        DBGuilds guild = opt.get();
        guild.deleteMute(member);
    }

    public static void addMuteRecord(String guildId, String member, String admin, String time, String reason) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return;
        }

        DBGuilds guild = opt.get();
        guild.addMute(member, admin, time, reason);
    }

    public static boolean isThereMuteRecord(String guildId, String member) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return false;
        }

        DBGuilds guild = opt.get();
        return guild.isMuted(member);
    }

    public static void deleteGuildRecords(String guildId) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return;
        }

        DBGuilds guild = opt.get();
        guild.deleteRecords(guildId);
    }

    public static void changeWelcomeMsg(String guildId, String msg, boolean useCard) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return;
        }

        DBGuilds guild = opt.get();

        if (!guild.isThereThingies()) {
            guild.addWelcomeMsg(msg, useCard);
            return;
        }

        guild.updateWelcomeMsg(msg, useCard);
    }

    public static void changeGoodbyeMsg(String guildId, String msg) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return;
        }

        DBGuilds guild = opt.get();

        if (!guild.isThereThingies()) {
            guild.addGoodbyeMsg(msg);
            return;
        }

        guild.updateGoodbyeMsg(msg);
    }

    public static void changeAutoBotRole(String guildId, String roleId, DBAction action) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);
        if (opt.isEmpty()) {
            return;
        }

        DBGuilds guild = opt.get();

        if (!guild.isThereThingies()) {
            guild.addAutoBotRole(roleId);
            return;
        }

        guild.updateAutoBotRole(roleId, action);
    }

    public static void changeAutoNewMemberRole(String guildId, String roleId) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return;
        }

        DBGuilds guild = opt.get();

        if (!guild.isThereThingies()) {
            guild.addAutoNewMemberRole(roleId);
            return;
        }

        guild.updateAutoNewMemberRole(roleId);
    }



    public static boolean canSendCommand(String guildId, TextChannel channel, GuildOptions defaultChannel) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return false;
        }

        DBGuilds guild = opt.get();

        if (guild.guildProperties.containsKey(defaultChannel.name())) {
            String id = guild.guildProperties.getProperty(defaultChannel.name());
            if (id.isBlank()) {
                switch (defaultChannel) {
                    case MUSIC_CHANNEL -> assignTempMusicChannel(channel);
                    case LOG_CHANNEL -> assignTempLogChannel(channel);
                }
                return true;
            }
            return id.equals(channel.getId());
        }
        return false;
    }

//    public static boolean canSendCommand(String guildId, TextChannel channel) {
//        Optional<DBGuilds> opt = findGuildMatches(guildId);
//
//        if (opt.isEmpty()) {
//            return false;
//        }
//
//        DBGuilds guild = opt.get();
//
//        if (guild.guildProperties.containsKey(defaultChannel.name())) {
//            String id = guild.guildProperties.getProperty(defaultChannel.name());
//            if (id.isBlank()) {
//                switch (defaultChannel) {
//                    case MUSIC_CHANNEL -> assignTempMusicChannel(channel);
//                    case LOG_CHANNEL -> assignTempLogChannel(channel);
//                }
//                return true;
//            }
//            return id.equals(channel.getId());
//        }
//        return false;
//    }

    public static String getLogChannel(String guildId) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return "";
        }

        DBGuilds guild = opt.get();
        return guild.guildProperties.getProperty("LOG_CHANNEL");
    }

    public static String getWelcomeMsg(String guildId) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return ConfigOptions.WELCOME_MSG.getValue();
        }

        DBGuilds guild = opt.get();
        return guild.getWelcomeMsg();
    }

    public static String getGoodbyeMsg(String guildId) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return ConfigOptions.GOODBYE_MSG.getValue();
        }

        DBGuilds guild = opt.get();
        return guild.getGoodbyeMsg();
    }

    public static boolean shouldUseCard(String guildId) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return false;
        }

        DBGuilds guild = opt.get();
        return guild.getUseCard();
    }

    public static String getAutoBotRole(String guildId) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return null;
        }

        DBGuilds guild = opt.get();
        return guild.getAutoBotRole();
    }

    public static String getAutoNewMemberRole(String guildId) {
        Optional<DBGuilds> opt = findGuildMatches(guildId);

        if (opt.isEmpty()) {
            return null;
        }

        DBGuilds guild = opt.get();
        return guild.getAutoNewMemberRole();
    }
}
