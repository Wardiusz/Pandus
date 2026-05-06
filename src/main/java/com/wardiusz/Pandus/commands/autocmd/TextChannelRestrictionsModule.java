package com.wardiusz.Pandus.commands.autocmd;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TextChannelRestrictionsModule extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
//        if (!Prv.canSendCommand(event.getGuild().getId(), event.getChannel().asTextChannel(), GuildOptions.MUSIC_CHANNEL)) {
//            event.reply("You can't use this command in this channel.").setEphemeral(true).queue();
//            return;
//        }
    }
}
