package com.wardiusz.SidorBot.commands.slash.music;

import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.Handler.EventData;
import com.wardiusz.SidorBot.Handler.Slash.SlashExecutor;
import com.wardiusz.SidorBot.Prv;
import com.wardiusz.SidorBot.commands.DTO.GuildOptions;
import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import com.wardiusz.SidorBot.commands.slash.music.handlers.AutoDisconnectEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.Objects;

public class JoinCmd extends SlashExecutor {
    public Provider provider;

    public JoinCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        if (!Prv.canSendCommand(event.getGuild().getId(), event.getChannel().asTextChannel(), GuildOptions.MUSIC_CHANNEL)) {
            event.getEvent().reply("You can't use this command in this channel.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();

        assert selfVoiceState != null;
        if (selfVoiceState.inAudioChannel()) {
            builder.setColor(EmbedOptions.ERROR_COLOR);
            builder.setDescription("I'm already in a voice channel.");
            event.reply(builder.build(), false).queue();
            return;
        }

        final GuildVoiceState memberVoiceState = event.getCommandSender().getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            builder.setColor(EmbedOptions.ERROR_COLOR);
            builder.setDescription("You need to be in a voice channel for this command to work.");
            event.reply(builder.build(), false).queue();
            return;
        }

        VoiceChannel voiceChannel = Objects.requireNonNull(memberVoiceState.getChannel()).asVoiceChannel();

        event.getGuild().getAudioManager().openAudioConnection(voiceChannel);

        AutoDisconnectEvent.startAudioTimerIfNotPlaying(event.getGuild(), false);

        builder.setColor(EmbedOptions.NEUTRAL_COLOR);
        builder.setDescription("Connecting to `" + voiceChannel.getName() + "`");
        event.reply(builder.build(), false).queue();
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Makes Sidor join to channel you're currently on.";
    }
}
