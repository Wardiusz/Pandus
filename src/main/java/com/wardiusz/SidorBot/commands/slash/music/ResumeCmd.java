package com.wardiusz.SidorBot.commands.slash.music;

import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.Prv;
import com.wardiusz.SidorBot.commands.DTO.GuildOptions;
import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import com.wardiusz.SidorBot.commands.slash.music.lavaplayer.GuildMusicManager;
import com.wardiusz.SidorBot.commands.slash.music.lavaplayer.PlayerManager;
import com.wardiusz.SidorBot.Handler.EventData;
import com.wardiusz.SidorBot.Handler.Slash.SlashExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;

import java.util.Objects;

public class ResumeCmd extends SlashExecutor {
    public Provider provider;

    public ResumeCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        if (!Prv.canSendCommand(event.getGuild().getId(), event.getChannel().asTextChannel(), GuildOptions.MUSIC_CHANNEL)) {
            event.getEvent().reply("You can't use this command in this channel.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        final GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = event.getCommandSender().getVoiceState();

        assert memberVoiceState != null;

        if (!memberVoiceState.inAudioChannel() || !Objects.requireNonNull(selfVoiceState).inAudioChannel()) {
            event.getEvent().reply("You need to be in a voice channel for this command to work.").setEphemeral(true).queue();
            return;
        }

        if (!selfVoiceState.inAudioChannel()) {
            event.getEvent().reply("You need to be in a voice channel for this command to work.").setEphemeral(true).queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (musicManager.audioPlayer.getPlayingTrack() == null) {
            event.getEvent().reply("There is no track paused currently.").setEphemeral(true).queue();
            return;
        }

        if (musicManager.scheduler.player.isPaused()) {
            musicManager.scheduler.player.setPaused(false);

            builder.setColor(EmbedOptions.NEUTRAL_COLOR);
            builder.setDescription("The player has been resumed.");
            event.getEvent().replyEmbeds(builder.build()).queue();
        }
    }

    @Override
    public String getName() {
        return "resume";
    }

    @Override
    public String getDescription() {
        return "Resume currently paused music.";
    }
}
