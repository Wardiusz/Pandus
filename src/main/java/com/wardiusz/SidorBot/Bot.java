package com.wardiusz.SidorBot;

import com.wardiusz.SidorBot.commands.DTO.MyDatabase;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.sql.Connection;
import java.sql.SQLException;

public class Bot {
    private static JDA jda;

    public static JDA getJDA() {
        return jda;
    }

    public static void main(String[] args) throws SQLException, InterruptedException {
        try (Connection connection = MyDatabase.getConnection()) {
            connection.setAutoCommit(false);
        }
        Provider provider = new Provider();
        jda = provider.setupJDA();
//        jda.getPresence().setActivity(Activity.listening("/help"));
    }





































        // Set activity (like "playing Something")
//        jda.setActivity(Activity.playing("test"));

//        configureMemoryUsage(jda);
//        builder.addEventListeners(new MyListener());
//    public void configureMemoryUsage(JDABuilder builder) {
//        // Disable cache for member activities (streaming/games/spotify)
//        builder.disableCache(CacheFlag.ACTIVITY);
////
////        // Only cache members who are either in a voice channel or owner of the guild
////        builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER));
////
////        // Disable member chunking on startup
////        builder.setChunkingFilter(ChunkingFilter.NONE);
////
////        // Disable presence updates and typing events
////        builder.disableIntents(GatewayIntent.GUILD_PRESENCE, GatewayIntent.GUILD_MESSAGE_TYPING);
////
////        // Consider guilds with more than 50 members as "large".
////        // Large guilds will only provide online members in their setup and thus reduce bandwidth if chunking is disabled.
////        builder.setLargeThreshold(50);
//    }
}
//class MyListener extends ListenerAdapter {
//
//    @Override
//    public void onMessageReceived(MessageReceivedEvent event) {
//        if (event.getAuthor().isBot()) return;
//        // We don't want to respond to other bot accounts, including ourself
//        Message message = event.getMessage();
//        String content = message.getContentRaw();
//        // getContentRaw() is an atomic getter
//        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
//        if (content.equals("" + Commands.PREFIX + "ping")) {
//            MessageChannel channel = event.getChannel();
//            channel.sendMessage("Pong!").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
//        }
//    }
//}
//enum Commands {
//    PREFIX("!");
//
//    private final String text;
//
//    Commands(final String text) {
//        this.text = text;
//    }
//
//     @Override
//     public String toString() {
//         return text;
//     }
//}
//
//class Music extends ListenerAdapter {
//
//    public void onMessageReceived(MessageReceivedEvent event) {
//        Guild guild = event.getGuild();
//        Member member = event.getMember();
//
//        Message message = event.getMessage();
//        String content = message.getContentRaw();
//
//
//        MessageChannel channel = event.getChannel();
//        AudioManager audioManager = guild.getAudioManager();
//        if (event.getAuthor().isBot()) return;
//
//        // Make sure we only respond to events that occur in a guild
//        if (!event.isFromGuild()) return;
//
//        // This makes sure we only execute our code when someone sends a message with "!play"
//        if (content.startsWith("" + Commands.PREFIX + "play")) {
//            playTrack(guild, member, channel, audioManager);
//        }
//
//        if (event.getMessage().getContentRaw().startsWith("" + Commands.PREFIX + "leave")) {
//            leaveChannel(guild, channel, audioManager);
//        }
//
//
////        if (!event.getMessage().getContentRaw().startsWith("!leave")) return;
////        leaveChannel(guild, channel);
//         // Important to call .queue() on the RestAction returned by sendMessage(...)
//        // Now we want to exclude messages from bots since we want to avoid command loops in chat!
//        // this will include own messages as well for bot accounts
//        // if this is not a bot make sure to check if this message is sent by yourself!
////        if (event.getAuthor().isBot()) return;
//
//
//    }
//    private void playTrack(Guild guild, @NotNull Member member, MessageChannel channel, AudioManager audioManager) {
//
//        VoiceChannel voiceChannel = (VoiceChannel) member.getVoiceState().getChannel();
//        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
//        AudioSourceManagers.registerRemoteSources(playerManager);
//
//        AudioPlayer player = playerManager.createPlayer();
//        TrackScheduler trackScheduler = new TrackScheduler(player);
//        player.addListener((AudioEventListener) trackScheduler);
////        manager.setSendingHandler(new MySendHandler());
//        if(voiceChannel != null) {
//            channel.sendMessage("Playing X").queue();
//            audioManager.openAudioConnection(voiceChannel);
//        } else {
//            channel.sendMessage("Cannot connect to channel").queue();
//        }
//
////        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
////            @Override
////            public void trackLoaded(AudioTrack track) {
////                trackScheduler.queue(track);
////            }
////
////            @Override
////            public void playlistLoaded(AudioPlaylist playlist) {
////                for (AudioTrack track : playlist.getTracks()) {
////                    trackScheduler.queue(track);
////                }
////            }
////
////            @Override
////            public void noMatches() {
////                // Notify the user that we've got nothing
////            }
////
////
////            @Override
////            public void loadFailed(FriendlyException throwable) {
////                // Notify the user that everything exploded
////            }
////        });
//    }
//    public void leaveChannel(Guild guild, MessageChannel channel, @NotNull AudioManager audioManager) {
//        if(!audioManager.isConnected()) {
//            channel.sendMessage( "I'm not on any channel right now.").queue();
//        } else {
//            audioManager.closeAudioConnection();
//            channel.sendMessage("Leaving...").queue();
//        }
//    }
//}
