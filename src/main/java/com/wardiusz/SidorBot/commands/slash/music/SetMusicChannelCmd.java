package com.wardiusz.SidorBot.commands.slash.music;

import com.wardiusz.SidorBot.Handler.EventData;
import com.wardiusz.SidorBot.Handler.Slash.SlashExecutor;
import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.Prv;
import com.wardiusz.SidorBot.commands.DTO.GuildOptions;
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
