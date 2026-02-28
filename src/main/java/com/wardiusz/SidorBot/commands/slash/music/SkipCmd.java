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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class SkipCmd extends SlashExecutor {
    public final Provider provider;
    public SkipCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        if (!Prv.canSendCommand(event.getGuild().getId(), event.getChannel().asTextChannel(), GuildOptions.MUSIC_CHANNEL)) {
            event.getEvent().reply("You can't use this command in this channel.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        final Member self = event.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();


        Emoji emojiFail = event.getJda().getEmojisByName("fail", true).getFirst();

        final GuildVoiceState memberVoiceState = event.getCommandSender().getVoiceState();


        assert memberVoiceState != null;

        if (!memberVoiceState.inAudioChannel()) {
            event.getEvent().reply(emojiFail.getFormatted() + " You need to be in a voice channel for this command to work.").setEphemeral(true).queue();
            return;
        }

        assert selfVoiceState != null;

        if (!selfVoiceState.inAudioChannel() || !memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            event.getEvent().reply(emojiFail.getFormatted() + " You need to be in the same voice channel with " + self.getAsMention() + " for this command to work.").setEphemeral(true).queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        // 239, 73, 68
        if (musicManager.audioPlayer.getPlayingTrack() == null) {
            event.getEvent().reply(emojiFail.getFormatted() + " There is no track playing currently.").setEphemeral(true).queue();
            return;
        }

        musicManager.scheduler.nextTrack();

        builder.setColor(EmbedOptions.NEUTRAL_COLOR);
        builder.setDescription("Skipping.");
        event.getEvent().replyEmbeds(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skip current playing track to the next one.";
    }
}
