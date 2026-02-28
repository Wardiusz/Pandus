package com.wardiusz.SidorBot.commands.autocmd;

import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CapsWarningModule extends ListenerAdapter {
    AutoCmdListener autoCmdListener;
    int wordLength = 5;

    public CapsWarningModule(AutoCmdListener autoCmdListener) {
        this.autoCmdListener = autoCmdListener;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(EmbedOptions.ERROR_COLOR);
        builder.setDescription("**" + event.getAuthor().getName() + "** capital letters!");

        String message = event.getMessage().getContentRaw();
        char[] messageLetters = message.toCharArray();
        int count = 0;
        for(int i = 0; i < messageLetters.length && messageLetters.length > wordLength; i++) {
            if(Character.isUpperCase(messageLetters[i])) count++;
        }

        if((double)count/messageLetters.length > 0.9d) {
            if(!event.getGuild().getSelfMember().hasPermission(event.getChannel().asTextChannel(), Permission.MESSAGE_MANAGE)) {
                System.out.println("No permission to delete messages in #" + event.getChannel().getName());
                return;
            }
            event.getMessage().replyEmbeds(builder.build()).queue(success -> success.delete().queueAfter(3, TimeUnit.SECONDS));
        }
    }
}
