package com.wardiusz.SidorBot.commands.slash.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.Prv;
import com.wardiusz.SidorBot.commands.DTO.GuildOptions;
import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import com.wardiusz.SidorBot.commands.slash.music.lavaplayer.GuildMusicManager;
import com.wardiusz.SidorBot.commands.slash.music.lavaplayer.PlayerManager;
import com.wardiusz.SidorBot.Handler.EventData;
import com.wardiusz.SidorBot.Handler.Slash.SlashExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JumpCmd extends SlashExecutor {
    public Provider provider;
    private AudioTrack track;

    public JumpCmd(Provider provider) {
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
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        track = audioPlayer.getPlayingTrack();

        if (audioPlayer.getPlayingTrack() == null){
            builder.setColor(EmbedOptions.ERROR_COLOR);
            builder.setDescription("There is currently no music playing.");
            event.getEvent().replyEmbeds(builder.build()).queue();
            return;
        }

        String timeString = event.getCommand().getOptions().getFirst().getAsString();
        long time = parseTime(timeString);

        if (time == -1) {
            event.getEvent().reply("Invalid time format. Please use **mm:ss** or **hh:mm:ss**").setEphemeral(true).queue();
            return;
        }
        if (time > track.getDuration()) {
            event.getEvent().reply("Invalid time. Duration of music is **" + formatTime(track.getDuration()) + "**.").setEphemeral(true).queue();
            return;
        }

        if (track != null) {
            track.setPosition(time);
            builder.setColor(EmbedOptions.NEUTRAL_COLOR);
            builder.setDescription("Jumped to " + formatTime(time) + "/" + formatTime(track.getDuration()));
        }

        event.getEvent().replyEmbeds(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "jump";
    }

    @Override
    public String getDescription() {
        return "Jump to certain moment of music based on given time.";
    }

    @Override
    public void updateAliases() {
        aliases.add("np");
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.STRING, "time", "The certain time moment of music to jump to. Exp. 1:3, 01:30, 11:3, 1:20:30", true));
        return options;
    }

    private long parseTime(String timeString) {
        Pattern pattern = Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+)");
        Matcher matcher = pattern.matcher(timeString);
        if (matcher.matches()) {
            String[] parts = timeString.split(":");
            int hours = 0, minutes, seconds;

            if (parts.length == 2) {
                minutes = Integer.parseInt(parts[0]);
                seconds = Integer.parseInt(parts[1]);
            } else if (parts.length == 3) {
                hours = Integer.parseInt(parts[0]);
                minutes = Integer.parseInt(parts[1]);
                seconds = Integer.parseInt(parts[2]);
            } else {
                return -1;
            }

            if (seconds < 10 && parts[1].length() == 1) {
                seconds *= 10;
            }

            return (hours * 3600L + minutes * 60L + seconds) * 1000;
        }
        return -1;
    }

    @NotNull
    private String formatTime(long millis) {
        long seconds = millis/1000;
        long minutes = seconds/60;
        long hours = minutes/60;

        seconds %= 60;
        minutes %= 60;

        if (track.getDuration()/3600000 >= 1) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
