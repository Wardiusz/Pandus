package com.wardiusz.SidorBot.commands.slash.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.*;

import static com.wardiusz.SidorBot.commands.slash.music.handlers.AutoDisconnectEvent.startAudioTimerIfNotPlaying;


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
