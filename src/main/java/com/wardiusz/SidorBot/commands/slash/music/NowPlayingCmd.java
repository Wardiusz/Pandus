package com.wardiusz.SidorBot.commands.slash.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.Prv;
import com.wardiusz.SidorBot.commands.DTO.GuildOptions;
import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import com.wardiusz.SidorBot.commands.slash.music.lavaplayer.GuildMusicManager;
import com.wardiusz.SidorBot.commands.slash.music.lavaplayer.PlayerManager;
import com.wardiusz.SidorBot.Handler.EventData;
import com.wardiusz.SidorBot.Handler.Slash.SlashExecutor;
import net.dv8tion.jda.api.EmbedBuilder;

public class NowPlayingCmd extends SlashExecutor {
    public Provider provider;

    public NowPlayingCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        if (!Prv.canSendCommand(event.getGuild().getId(), event.getChannel().asTextChannel(), GuildOptions.MUSIC_CHANNEL)) {
            event.getEvent().reply("You can't use this command in this channel.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if(audioPlayer.getPlayingTrack() == null){
            builder.setDescription("There is currently no music playing.");
            event.getEvent().replyEmbeds(builder.build()).queue();
            return;
        }

        builder.setColor(EmbedOptions.NEUTRAL_COLOR);
        builder.setDescription("**Now playing:**\n" +
                "`" + audioPlayer.getPlayingTrack().getInfo().title + " by " + audioPlayer.getPlayingTrack().getInfo().author + "`\n" +
                "**Duration:**\n" +
                formatTime(audioPlayer.getPlayingTrack().getDuration()));

        event.getEvent().replyEmbeds(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getDescription() {
        return "Show current playing track.";
    }

    @Override
    public void updateAliases() {
        aliases.add("np");
    }

    private String formatTime(long millis) {
        long seconds = millis/1000;
        long minutes = seconds/60;
        long hours = minutes/60;
        seconds %= 60;
        minutes %= 60;
        if (hours != 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
