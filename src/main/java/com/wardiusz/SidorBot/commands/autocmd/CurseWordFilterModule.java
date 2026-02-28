package com.wardiusz.SidorBot.commands.autocmd;

import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import com.wardiusz.SidorBot.commands.slash.administrative.WarnCmd;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurseWordFilterModule extends ListenerAdapter {
    private static final List<String> CURSE_WORDS = new ArrayList<>();

    public CurseWordFilterModule(String... swearWord) {
        CURSE_WORDS.addAll(Arrays.asList(swearWord));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(EmbedOptions.ERROR_COLOR);

        String message = event.getMessage().getContentRaw().toLowerCase().replaceAll("[^a-zA-Z0-9\\\\s]","");
        for(String curseWord : CURSE_WORDS) {
            if(message.contains(curseWord)) {
                if(!event.getGuild().getSelfMember().hasPermission(event.getChannel().asTextChannel(), Permission.MESSAGE_MANAGE)) {
                    builder.setDescription("No permission to delete messages in " + event.getChannel().getAsMention());
                    event.getChannel().asTextChannel().sendMessageEmbeds(builder.build()).queue();
                    return;
                }

                PrivateChannel privChannel = event.getJDA().getUserById(event.getAuthor().getId()).openPrivateChannel().complete();

                event.getMessage().delete().queue(
                        (done) -> privChannel.sendMessageEmbeds(WarnCmd.privMessageEmbed(event.getGuild(), event.getJDA().getSelfUser(), event.getMessage().getContentRaw())).queue(),
                        (error) -> event.getChannel().asTextChannel().sendMessage("Cannot delete this message. \nError ID " + error.getMessage()).queue()
                );
                return;
            }
        }
    }
}
