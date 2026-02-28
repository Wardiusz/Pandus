package com.wardiusz.SidorBot.commands.autocmd;

import com.wardiusz.SidorBot.Prv;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoDelMsgModule extends ListenerAdapter {
    AutoCmdListener autoCmdListener;

    public AutoDelMsgModule(AutoCmdListener autoCmdListener) {
        this.autoCmdListener = autoCmdListener;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

    }
}