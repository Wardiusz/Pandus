package com.wardiusz.SidorBot.commands.slash.music;

import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.Prv;
import com.wardiusz.SidorBot.commands.DTO.GuildOptions;
import com.wardiusz.SidorBot.commands.slash.music.lavaplayer.PlayerManager;
import com.wardiusz.SidorBot.Handler.EventData;
import com.wardiusz.SidorBot.Handler.Slash.SlashExecutor;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class PlayCmd extends SlashExecutor {
    public final Provider provider;
    public PlayCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        if (!Prv.canSendCommand(event.getGuild().getId(), event.getChannel().asTextChannel(), GuildOptions.MUSIC_CHANNEL)) {
            event.getEvent().reply("You can't use this command in this channel.").setEphemeral(true).queue();
            return;
        }

        if (!event.getMemberVoiceState().inAudioChannel()) {
            event.getEvent().reply("You must be in an audio channel to perform that command.").setEphemeral(true).queue();
            return;
        }

        event.deferReply();

        if (!event.getSelfVoiceState().inAudioChannel()){
            AudioManager audioManager = event.getGuild().getAudioManager();
            VoiceChannel memberChannel = (VoiceChannel) event.getMemberVoiceState().getChannel();

            if (memberChannel == null) {
                event.getEvent().reply("There are not in channel.").setEphemeral(true).queue();
                return;
            }

            audioManager.openAudioConnection(memberChannel);
        }

        String link = event.getCommand().getOptions().getFirst().getAsString();

        if (!isURL(link)) {
            link = "ytsearch:" + String.join(" ", event.getCommand().getOptions().getFirst().getAsString() + " music");
        }

        if (link.contains("open.spotify.com")) {
            link = "spsearch:" + String.join(" ", event.getCommand().getOptions().getFirst().getAsString());
        }

        PlayerManager.getInstance(provider).loadAndPlay(event.getTextChannel(), event, link);
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Play given music. (Makes Sidor join to channel if he's not there)";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.STRING, "link", "The link or name to your music.", true));
        return options;
    }

    private boolean isURL(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
