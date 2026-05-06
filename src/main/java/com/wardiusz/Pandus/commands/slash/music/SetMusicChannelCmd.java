package com.wardiusz.Pandus.commands.slash.music;

import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.Prv;
import com.wardiusz.Pandus.commands.DTO.GuildOptions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class SetMusicChannelCmd extends SlashExecutor {
    public Provider provider;

    public SetMusicChannelCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public String getName() {
        return "setmusicchannel";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.CHANNEL, "channel", "Channel to be set as default for music.", true));
        return options;
    }

    @Override
    public void updateAliases() {
        aliases.add("smc");
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Set a music channel for this guild";
    }

    @Override
    public void execute(EventData event) {
        if (Prv.updateGuildProperty(GuildOptions.MUSIC_CHANNEL, event.getEvent().getOptions().getFirst().getAsString(), event.getGuild().getId())) {
            event.reply("New music channel has been set to: " + event.getEvent().getOptions().getFirst().getAsChannel().getAsMention(), false).queue();
        } else {
            event.reply("Music channel failed to updated.", true).queue();
        }
    }
}
