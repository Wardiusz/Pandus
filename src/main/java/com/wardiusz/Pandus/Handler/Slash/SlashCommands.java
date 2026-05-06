package com.wardiusz.Pandus.Handler.Slash;

import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.Handler.EventData;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SlashCommands extends ListenerAdapter {

    private final Provider provider;

    public SlashCommands(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (provider.getExecutors().containsKey(event.getName()) && provider.getExecutors().get(event.getName()) instanceof SlashExecutor executor) {
            provider.getLogger().info("\u001B[33m'{}'\u001B[0m has been triggered.", executor.getName());

            if (!executor.getAuthorizedPermissions().isEmpty() && !Objects.requireNonNull(event.getMember()).hasPermission(executor.getAuthorizedPermissions())) {
                event.reply("You do not have permission ``" + executor.getAuthorizedPermissions() + "`` to run this command.").setEphemeral(true).queue();
                return;
            }

            if (executor.isOwnerOnly() && !(Objects.requireNonNull(event.getMember())).isOwner()) {
                event.reply("This command can only be used by the server owner.").setEphemeral(true).queue();
                return;
            }

            if (!executor.getAuthorizedChannels().isEmpty() && !executor.getAuthorizedChannels().contains(event.getChannel())) {
                event.reply("This command cannot be used in this channel.").setEphemeral(true).queue();
                return;
            }

            if (executor.getAuthorizedRoles() != null && !executor.getAuthorizedRoles().isEmpty()) {
                for (Role authorizedRole : executor.getAuthorizedRoles()) {
                    if(Objects.requireNonNull(event.getMember()).getRoles().contains(authorizedRole)) {
                        executor.execute(new EventData(event));
                        break;
                    }
                }
                return;
            }

            executor.execute(new EventData(event));
        }
    }

}
