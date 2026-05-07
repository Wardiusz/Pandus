package com.wardiusz.Pandus.commands.autocmd;

import com.wardiusz.Pandus.Handler.Config;
import com.wardiusz.Pandus.Provider;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.wardiusz.Pandus.commands.slash.music.handlers.VCModule;


public class AutoCmdListener extends ListenerAdapter {
    Provider provider;

    public AutoCmdListener(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getPresence().setActivity(Activity.customStatus("Use /help" + (Boolean.parseBoolean(Config.get("PREFIX_COMMANDS")) ? (" or " + Config.get("PREFIX") + "help") : "")));

        event.getJDA().addEventListener(
                new CapsWarningModule(this),
                new CurseWordFilterModule("nigga", "nigger"),
                new UserJoinModule(this),
                new UserLeftModule(this),
                new OnSelfGuildJoin(this),
                new OnSelfGuildLeave(this),
                new VCModule(this),
                new GatekeepSentMsgModule(this)
        );
    }
}
