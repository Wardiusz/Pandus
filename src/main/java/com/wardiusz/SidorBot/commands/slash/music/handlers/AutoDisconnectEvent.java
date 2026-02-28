package com.wardiusz.SidorBot.commands.slash.music.handlers;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.commands.DTO.DBGuilds;
import com.wardiusz.SidorBot.commands.DTO.GuildOptions;
import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import com.wardiusz.SidorBot.commands.slash.music.lavaplayer.GuildMusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import com.wardiusz.SidorBot.commands.slash.music.lavaplayer.PlayerManager;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.wardiusz.SidorBot.Handler.Config.getTempMusicChannel;

public class AutoDisconnectEvent extends AudioEventAdapter {
    private static ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
    static ScheduledFuture<?> future;

    public static void startAudioTimerIfNotPlaying(Guild guild, boolean isPlaying) {
        if (!isPlaying) {
            scheduler.setRemoveOnCancelPolicy(true);
            scheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
            scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

            if (scheduler.isTerminated()) {
                scheduler = new ScheduledThreadPoolExecutor(1);
            }

            future = scheduler.schedule(() -> {
                GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(guild);
                gmm.audioPlayer.stopTrack();
                gmm.scheduler.queue.clear();
                gmm.audioPlayer.setPaused(false);
                guild.getAudioManager().closeAudioConnection();

                Optional<DBGuilds> opt = Arrays.stream(Provider.getDbGuilds())
                        .filter(dbGuilds -> Objects.equals(dbGuilds.ID, guild.getId()))
                        .findAny();

                String id = opt.get().guildProperties.getProperty(GuildOptions.MUSIC_CHANNEL.name());

                if (!id.isBlank()) {
                    Objects.requireNonNull(guild.getTextChannelById(id)).sendMessageEmbeds(embed()).queue();
                } else if (getTempMusicChannel() != null) {
                    Objects.requireNonNull(guild.getTextChannelById(getTempMusicChannel().getId())).sendMessageEmbeds(embed()).queue();

                }
            }, 8, TimeUnit.MINUTES);
        } else {
            scheduler.getQueue().clear();
            scheduler.shutdownNow();
        }
    }

    private static MessageEmbed embed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Left the voice channel due to inactivity.");
        embedBuilder.setColor(EmbedOptions.NEUTRAL_COLOR);
        return embedBuilder.build();
    }

}
