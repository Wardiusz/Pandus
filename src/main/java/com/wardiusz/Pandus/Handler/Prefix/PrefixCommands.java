package com.wardiusz.Pandus.Handler.Prefix;

import com.wardiusz.Pandus.Handler.Config;
import com.wardiusz.Pandus.Provider;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PrefixCommands extends ListenerAdapter {

    private final Provider provider;
    public static String prefix = Config.get("PREFIX");

    public PrefixCommands(Provider provider) {
        this.provider = provider;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        PrefixCommands.prefix = prefix;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        String[] args = event.getMessage()
                .getContentRaw()
                .split("\\s+");

        if(!args[0].contains(prefix) || Objects.requireNonNull(event.getMember()).getUser().isBot()) {
            return;
        }

        if(!args[0].startsWith(prefix)) {
            return;
        }

        String cmdName = args[0].replaceFirst(prefix, "");

        if (provider.getExecutors().containsKey(cmdName) && provider.getExecutors().get(cmdName) instanceof PrefixExecutor executor) {
            List<String> options = new ArrayList<>(List.of(event.getMessage().getContentRaw().split(" ")));
            options.removeIf((getPrefix() + cmdName)::equals);
                if (options.size() != executor.getOptions().size()) {
                        event.getChannel().sendMessage("Wrong command usage.\nSee ``/help " + executor.getName() + "``").queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
                        return;
                } else {
                    for (int i = 0; i < executor.getOptions().size(); i++) {
                        executor.getOptions().get(i).setStringValue(options.get(i));
                    }
                }

            provider.getLogger().info("\u001B[33m'{}'\u001B[0m has been triggered.", cmdName);

            if (!executor.getAuthorizedPermissions().isEmpty() && !Objects.requireNonNull(event.getMember()).hasPermission(executor.getAuthorizedPermissions())) {
                event.getChannel().sendMessage("You do not have permission ``" + executor.getAuthorizedPermissions() + "`` to run this command.").queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
                return;
            }

            if (!executor.getAuthorizedChannels().isEmpty() && !executor.getAuthorizedChannels().contains(event.getChannel())) {
                provider.getLogger().warn("PrefixCommand: '{}' has been triggered but the channel it was executed in isn't authorized.", cmdName);
                return;
            }

            if (executor.getAuthorizedRoles() != null && !executor.getAuthorizedRoles().isEmpty()) {
                for (Role authorizedRole : executor.getAuthorizedRoles()) {
                    if(Objects.requireNonNull(event.getMember()).getRoles().contains(authorizedRole)) {
                        executor.execute(event);
                        event.getMessage().delete().queue();
                        break;
                    }
                }
                return;
            }

            executor.execute(event);
            event.getMessage().delete().queue();
        }
    }

}
