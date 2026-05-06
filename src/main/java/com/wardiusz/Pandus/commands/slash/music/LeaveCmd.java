package com.wardiusz.Pandus.commands.slash.music;

import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.Prv;
import com.wardiusz.Pandus.commands.DTO.GuildOptions;
import com.wardiusz.Pandus.commands.embeds.EmbedOptions;
import com.wardiusz.Pandus.commands.slash.music.handlers.AutoDisconnectEvent;
import com.wardiusz.Pandus.commands.slash.music.lavaplayer.GuildMusicManager;
import com.wardiusz.Pandus.commands.slash.music.lavaplayer.PlayerManager;
import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.managers.AudioManager;

public class LeaveCmd extends SlashExecutor  {

    public final Provider provider;
    public LeaveCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        if (!Prv.canSendCommand(event.getGuild().getId(), event.getChannel().asTextChannel(), GuildOptions.MUSIC_CHANNEL)) {
            event.getEvent().reply("You can't use this command in this channel.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();

        final GuildVoiceState memberVoiceState = event.getCommandSender().getVoiceState();
        final GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();

        Emoji emojiFail = event.getJda().getEmojisByName("fail", true).getFirst();

        assert selfVoiceState != null;
        if (!selfVoiceState.inAudioChannel() || !memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            builder.clear();
            builder.setColor(EmbedOptions.SUCCESS_COLOR);
            builder.setDescription(emojiFail.getFormatted() + " I need to be in the same voice channel with you for this command to work.");
            event.getEvent().replyEmbeds(builder.build()).queue();
            return;
        }

        final AudioManager audioManager = event.getGuild().getAudioManager();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        musicManager.scheduler.queue.clear();
        musicManager.scheduler.player.stopTrack();

        audioManager.closeAudioConnection();

        AutoDisconnectEvent.startAudioTimerIfNotPlaying(event.getGuild(), true);

        builder.setColor(EmbedOptions.NEUTRAL_COLOR);
        builder.setDescription("Left the channel.");
        event.getEvent().replyEmbeds(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Forces Sidor to leave a channel and clear queue.";
    }
}
