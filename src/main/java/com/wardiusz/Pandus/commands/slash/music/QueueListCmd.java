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

import java.util.concurrent.atomic.AtomicInteger;

public class QueueListCmd extends SlashExecutor {
    public Provider provider;

    public QueueListCmd(Provider provider) {
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
            AtomicInteger index = new AtomicInteger(1);

            builder.setColor(EmbedOptions.NEUTRAL_COLOR);
            builder.setTitle("Music queue:");
            musicManager.scheduler.queue.forEach(b -> builder.appendDescription("`"+ index.getAndIncrement() + ".` " + b.getInfo().title + " by " + b.getInfo().author + "\n"));
        }

        event.getEvent().replyEmbeds(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return "Show a list of queued tracks.";
    }
}
