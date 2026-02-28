package com.wardiusz.SidorBot.commands.autocmd;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GatekeepSentMsgModule extends ListenerAdapter {
    AutoCmdListener autoCmdListener;

    public GatekeepSentMsgModule(AutoCmdListener autoCmdListener) {
        this.autoCmdListener = autoCmdListener;
    }
}
