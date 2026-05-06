package com.wardiusz.Pandus.commands.slash.music;

import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.Prv;
import com.wardiusz.Pandus.commands.DTO.GuildOptions;
import com.wardiusz.Pandus.commands.embeds.EmbedOptions;
import com.wardiusz.Pandus.commands.slash.music.lavaplayer.GuildMusicManager;
import com.wardiusz.Pandus.commands.slash.music.lavaplayer.PlayerManager;
import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
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
