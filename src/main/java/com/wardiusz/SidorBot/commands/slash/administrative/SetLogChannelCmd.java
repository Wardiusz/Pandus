package com.wardiusz.SidorBot.commands.slash.administrative;

import com.wardiusz.SidorBot.Handler.EventData;
import com.wardiusz.SidorBot.Handler.Slash.SlashExecutor;
import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.Prv;
import com.wardiusz.SidorBot.commands.DTO.GuildOptions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class SetLogChannelCmd extends SlashExecutor {
    public Provider provider;

    public SetLogChannelCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public String getName() {
        return "setlogchannel";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.CHANNEL, "channel", "Channel to be set as default for logs such as bans, kick etc.", true));
        return options;
    }

    @Override
    public void updateAliases() {
        aliases.add("slc");
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Set a log channel for this guild.";
    }

    @Override
    public void execute(EventData event) {
        if (Prv.updateGuildProperty(GuildOptions.LOG_CHANNEL, event.getEvent().getOptions().getFirst().getAsString(), event.getGuild().getId())) {
            event.reply("New log channel has been set to: " + event.getEvent().getOptions().getFirst().getAsChannel().getAsMention(), false).queue();
        } else {
            event.reply("Log channel failed to update.", true).queue();
        }
    }
}
