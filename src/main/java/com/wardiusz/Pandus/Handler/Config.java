package com.wardiusz.Pandus.Handler;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Config {
    Config() {
        if (Dotenv.load() == null) { System.exit(-1); }
    }
    private static final Dotenv dotenv = Dotenv.load();
    private static TextChannel musicChannel, logChannel = null;
    public static String get(String key) {
        return dotenv.get(key);
    }
    public static void assignTempMusicChannel(TextChannel channel) { musicChannel = channel; }
    public static void assignTempLogChannel(TextChannel channel) { logChannel = channel; }
    public static TextChannel getTempMusicChannel() { return musicChannel; }
    public static TextChannel getTempLogChannel() { return logChannel; }
}
