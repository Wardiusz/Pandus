package com.wardiusz.Pandus.commands.slash.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.commands.DTO.GuildOptions;
import com.wardiusz.Pandus.commands.DTO.GuildRepository;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

import static com.wardiusz.Pandus.Handler.Config.getTempMusicChannel;
import static com.wardiusz.Pandus.commands.slash.music.handlers.AutoDisconnectEvent.startAudioTimerIfNotPlaying;
import static com.wardiusz.Pandus.commands.slash.music.lavaplayer.PlayerManager.extractReason;
import static com.wardiusz.Pandus.commands.slash.music.lavaplayer.PlayerManager.toUserMessage;


public class TrackScheduler extends AudioEventAdapter {

    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();

        player.addListener(new AudioEventAdapter() {
            @Override
            public void onPlayerPause(AudioPlayer player) {
                startAudioTimerIfNotPlaying(guild, false);
            }

            @Override
            public void onPlayerResume(AudioPlayer player) {
                startAudioTimerIfNotPlaying(guild, true);
            }

            @Override
            public void onTrackStart(AudioPlayer player, AudioTrack track) {
                startAudioTimerIfNotPlaying(guild, true);
            }

            @Override
            public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack track, AudioTrackEndReason endReason) {
                startAudioTimerIfNotPlaying(guild, queue.peek() != null);
                if (endReason.mayStartNext) {
                    nextTrack();
                }
            }

            @Override
            public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
                String reason = extractReason(exception);
                Optional<GuildRepository> opt = Arrays.stream(Provider.getDbGuilds())
                        .filter(guildRepository -> Objects.equals(guildRepository.ID, guild.getId()))
                        .findAny();

                String id = opt.get().guildProperties.getProperty(GuildOptions.MUSIC_CHANNEL.name());

                if (!id.isBlank()) {
                    Objects.requireNonNull(guild.getTextChannelById(id)).sendMessage("Can't play **" + track.getInfo().title + "**: " + toUserMessage(reason)).queue();
                } else if (getTempMusicChannel() != null) {
                    Objects.requireNonNull(guild.getTextChannelById(getTempMusicChannel().getId())).sendMessage("Can't play **" + track.getInfo().title + "**: " + toUserMessage(reason)).queue();
                }
            }
        });
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }

    public void nextTrack() {
        this.player.startTrack(this.queue.poll(), false);
    }
}
