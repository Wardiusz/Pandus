package com.wardiusz.SidorBot.Handler.Prefix;

import com.wardiusz.SidorBot.Handler.IExecutor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

public abstract class PrefixExecutor implements IExecutor {

    public List<PrefixOption> options = new ArrayList<>();
    public List<String> aliases = new ArrayList<>();
    public List<Role> authorizedRoles = new ArrayList<>();
    public List<Channel> authorizedChannels = new ArrayList<>();
    public List<Permission> authorizedPermissions = new ArrayList<>();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public String getDescription() {
        return "There is no description for this command.";
    }

    protected final List<PrefixOption> getOptions(){
        return options;
    }

    public String usage() {
        StringBuilder usage = new StringBuilder("```" + PrefixCommands.prefix + getName());
        for (PrefixOption option : options) {
            usage.append(" [").append(option.getName().replace("[", "").replace("]", "")).append("]");
        }
        usage.append("```").append("Description: \n").append(getDescription()).append("\n");

        return usage.toString();
    }
    abstract public String getHelp();
    @Override
    public void updateAliases() {

    }

    @Override
    public void updateAuthorizedRoles(JDA jda) {

    }

    @Override
    public void updateAuthorizedChannels(JDA jda) {

    }
    @Override
    public void updateAuthorizedPermissions(JDA jda) {

    }

    @Override
    public List<Channel> getAuthorizedChannels() {
        return authorizedChannels;
    }

    @Override
    public List<Role> getAuthorizedRoles() {
        return authorizedRoles;
    }
    @Override
    public List<Permission> getAuthorizedPermissions() {
        return authorizedPermissions;
    }

    @Override
    public boolean isOwnerOnly() {
        return false;
    }

    abstract public void execute(MessageReceivedEvent event);
}
