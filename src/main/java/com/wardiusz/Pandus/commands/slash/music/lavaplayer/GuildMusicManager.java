package com.wardiusz.Pandus.commands.slash.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;

public class GuildMusicManager {
    public final AudioPlayer audioPlayer;
    public final TrackScheduler scheduler;
    private final AudioPlayerHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager manager, Guild guild) {
        this.audioPlayer = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.audioPlayer, guild);
        this.audioPlayer.addListener(this.scheduler);
        this.sendHandler = new AudioPlayerHandler(this.audioPlayer);
    }

    public AudioPlayerHandler getSendHandler() {
        return sendHandler;
    }
}
