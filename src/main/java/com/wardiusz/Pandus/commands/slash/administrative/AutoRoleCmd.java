package com.wardiusz.Pandus.commands.slash.administrative;

import com.wardiusz.Pandus.Handler.DBAction;
import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.Prv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

public class AutoRoleCmd extends SlashExecutor {
    public final Provider provider;

    public AutoRoleCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        String action = event.getCommand().getOption("action").getAsString();
        String type = event.getCommand().getOption("type").getAsString();

        Role role = event.getCommand().getOption("role").getAsRole();

        switch (action) {
            case "add" -> {
                if (type.equals("bot")) {
                    Prv.changeAutoBotRole(Objects.requireNonNull(event.getEvent().getGuild()).getId(), role.getId(), DBAction.ADD);
                }
                if (type.equals("joined")) {
                    Prv.changeAutoNewMemberRole(Objects.requireNonNull(event.getEvent().getGuild()).getId(), role.getId());
                }
            }
            case "remove" -> {
                if (type.equals("bot")) {
                    Prv.changeAutoBotRole(Objects.requireNonNull(event.getEvent().getGuild()).getId(), role.getId(), DBAction.REMOVE);
                }
                if (type.equals("joined")) {
                    Prv.changeAutoNewMemberRole(Objects.requireNonNull(event.getEvent().getGuild()).getId(), role.getId());
                }
            }
        }

    }

    @Override
    public String getName() {
        return "autorole";
    }

    @Override
    public String getDescription() {
        return "Set which role to assign when joining to guild for bots or members.";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.STRING, "action", "Action to perform", true)
                .addChoices(
                        new Command.Choice("add", "add"),
                        new Command.Choice("remove", "remove")
                ));
        options.add(new OptionData(OptionType.STRING, "type", "Action to perform", true)
                .addChoices(
                        new Command.Choice("bot", "bot"),
                        new Command.Choice("joined", "joined")
                ));
        options.add(new OptionData(OptionType.ROLE, "role", "Role to assign upon given action", true));
        return options;
    }

    @Override
    public void updateAuthorizedPermissions(JDA jda) {
        authorizedPermissions.add(Permission.MANAGE_ROLES);
    }
}
