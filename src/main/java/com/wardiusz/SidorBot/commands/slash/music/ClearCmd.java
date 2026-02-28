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

public class ClearCmd extends SlashExecutor {
    public Provider provider;

    public ClearCmd(Provider provider) {
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

        if (musicManager.scheduler.queue.isEmpty()) {
            builder.setColor(EmbedOptions.ERROR_COLOR);
            builder.setDescription("There is currently no queued tracks.");
        } else {
            builder.setColor(EmbedOptions.SUCCESS_COLOR);
            builder.setDescription("Music queue deleted.");
            musicManager.scheduler.queue.clear();
        }

        event.getEvent().replyEmbeds(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clear current music queue.";
    }
}
