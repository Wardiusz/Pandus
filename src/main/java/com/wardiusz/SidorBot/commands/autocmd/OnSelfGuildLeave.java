package com.wardiusz.SidorBot.commands.autocmd;

import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.Prv;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnSelfGuildLeave extends ListenerAdapter {
    AutoCmdListener autoCmdListener;

    public OnSelfGuildLeave(AutoCmdListener autoCmdListener) {
        this.autoCmdListener = autoCmdListener;
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        Prv.deleteGuildRecords(event.getGuild().getId());
        Provider.loadGuildProperties();
    }
}