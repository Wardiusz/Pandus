package com.wardiusz.Pandus.commands.slash.administrative;

import com.wardiusz.Pandus.Handler.CommandData;
import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import com.wardiusz.Pandus.Provider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class CleanCmd extends SlashExecutor {
    public final Provider provider;

    public CleanCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        TextChannel channel = event.getChannel().asTextChannel();
        String tempTarget = CommandData.searchThroughOptions(event, "user").orElse("");
        User target = null;
        if (!tempTarget.isEmpty())
            target = event.getJda().getUsersByName(CommandData.searchThroughOptions(event, "user").orElse(""),true).stream().findFirst().orElse(null);
        String amount = CommandData.searchThroughOptions(event, "amount").orElse("");


        if (target != null) {
            if (amount.isEmpty()) {
                deleteFromUser(channel, target);
            } else {
                deleteFromUser(channel, target, Integer.parseInt(amount));
            }
        } else {
            if (amount.isEmpty()) {
                deleteMessages(channel, 100);
//                channel.createCopy().queue();
//                channel.delete().queue();
            } else {
                deleteMessages(channel, Integer.parseInt(amount));
            }
        }

    }

    @Override
    public String getName() {
        return "clean";
    }

    @Override
    public String getDescription() {
        return "Delete messages from the channel (without any options -> wipes all messages).";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.USER, "user", "Messages to remove of specific @member", false));
        options.add(new OptionData(OptionType.STRING, "amount", "Number of messages to remove", false));
        return options;
    }

    @Override
    public void updateAuthorizedPermissions(JDA jda) {
        authorizedPermissions.add(Permission.MANAGE_CHANNEL);
        authorizedPermissions.add(Permission.MESSAGE_MANAGE);
    }

    private void deleteMessages(MessageChannel channel, int amount) {
        channel.getIterableHistory()
                .takeAsync(amount)
                .thenAccept(channel::purgeMessages);
    }

    private void deleteFromUser(MessageChannel channel, User author, int amount) {
        List<Message> messages = new ArrayList<>();
        channel.getIterableHistory()
                .forEachAsync(m -> {
                    if (m.getAuthor().equals(author))
                        messages.add(m);
                    return messages.size() < amount;
                })
                .thenRun(() -> channel.purgeMessages(messages));
    }

    private void deleteFromUser(MessageChannel channel, User author) {
        List<Message> messages = new ArrayList<>();
        channel.getIterableHistory()
                .forEachAsync(m -> {
                    if (m.getAuthor().equals(author))
                        messages.add(m);
                    return messages.size() < 100;
                })
                .thenRun(() -> channel.purgeMessages(messages));
    }

    private void deleteUntil(MessageChannel channel, OffsetDateTime time) {
        channel.getIterableHistory()
                .takeUntilAsync(message -> message.getTimeCreated().isBefore(time))
                .thenAccept(channel::purgeMessages);
    }
}
