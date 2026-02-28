package com.wardiusz.SidorBot.Handler.Slash;

import com.wardiusz.SidorBot.Handler.EventData;
import com.wardiusz.SidorBot.Handler.IExecutor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

public abstract class SlashExecutor implements IExecutor {

    public List<OptionData> options = new ArrayList<>();
    public List<String> aliases = new ArrayList<>();
    public List<Role> authorizedRoles = new ArrayList<>();
    public List<Channel> authorizedChannels = new ArrayList<>();
    public List<Permission> authorizedPermissions = new ArrayList<>();

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
    public List<String> getAliases() {
        return aliases;
    }


    @Override
    public boolean isOwnerOnly() {
        return false;
    }


    public void updateOptions() {

    }

    @Override
    public String getDescription() {
        return "There is no description for this command.";
    }

    public List<OptionData> getOptions() {
        return options;
    }


    public abstract void execute(EventData event);

}
