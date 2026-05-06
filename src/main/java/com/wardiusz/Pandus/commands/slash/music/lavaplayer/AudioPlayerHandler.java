package com.wardiusz.Pandus.commands.slash.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class AudioPlayerHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private final ByteBuffer buffer;
    private final MutableAudioFrame mutableFrame;
//    private AudioFrame lastFrame;

    public AudioPlayerHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.buffer = ByteBuffer.allocate(4096);
        this.mutableFrame = new MutableAudioFrame();
        this.mutableFrame.setBuffer(buffer);
    }

    @Override
    public boolean canProvide() {
//        lastFrame = audioPlayer.provide();
//        return lastFrame != null;
        return this.audioPlayer.provide(this.mutableFrame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
//        return ByteBuffer.wrap(lastFrame.getData());
        return this.buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
