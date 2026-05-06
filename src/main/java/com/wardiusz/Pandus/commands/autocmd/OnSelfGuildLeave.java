package com.wardiusz.Pandus.commands.autocmd;

import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.Prv;
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