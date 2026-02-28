package com.wardiusz.SidorBot.commands.slash.music.handlers;

import com.wardiusz.SidorBot.commands.autocmd.AutoCmdListener;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VCModule extends ListenerAdapter {
    AutoCmdListener autoCmdListener;

    public VCModule(AutoCmdListener autoCmdListener) {
        this.autoCmdListener = autoCmdListener;
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        final Member self = event.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        long numOfUsers = 0;

        if (event.getMember().getId().equals(self.getId()) && event.getChannelLeft() != null) {
            AutoDisconnectEvent.startAudioTimerIfNotPlaying(event.getGuild(), true);
            return;
        }

        if (!selfVoiceState.inAudioChannel()) {
            return;
        }

        VoiceChannel voiceChannel = selfVoiceState.getChannel().asVoiceChannel();

        if (voiceChannel.equals(event.getChannelJoined()) || voiceChannel.equals(event.getChannelLeft())) {
            numOfUsers = voiceChannel.getMembers().size();
        }

        AutoDisconnectEvent.startAudioTimerIfNotPlaying(event.getGuild(), numOfUsers > 1);
    }
}
