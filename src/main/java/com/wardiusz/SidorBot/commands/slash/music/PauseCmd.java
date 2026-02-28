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

public class PauseCmd extends SlashExecutor  {

    public final Provider provider;
    public PauseCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        if (!Prv.canSendCommand(event.getGuild().getId(), event.getChannel().asTextChannel(), GuildOptions.MUSIC_CHANNEL)) {
            event.getEvent().reply("You can't use this command in this channel.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();

        final Member member = event.getCommandSender();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        final Member self = event.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        Emoji emojiFail = event.getJda().getEmojisByName("fail", true).getFirst();

        if (!selfVoiceState.inAudioChannel() || !memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            embed.setColor(EmbedOptions.ERROR_COLOR);
            embed.setDescription(emojiFail.getFormatted() + " I need to be in the same voice channel with you for this command to work.");
            event.getEvent().replyEmbeds(embed.build()).queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        musicManager.scheduler.player.setPaused(true);

        embed.setColor(EmbedOptions.NEUTRAL_COLOR);
        embed.setDescription("The player has been stopped.");
        event.getEvent().replyEmbeds(embed.build()).queue();
    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return "Pause currently playing music.";
    }
}
