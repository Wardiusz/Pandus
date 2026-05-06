package com.wardiusz.Pandus.Handler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

import java.nio.channels.Channel;
import java.util.List;

public interface IExecutor {
    String getName();

    void updateAliases();

    void updateAuthorizedRoles(JDA jda);

    void updateAuthorizedChannels(JDA jda);
    void updateAuthorizedPermissions(JDA jda);

    List<String> getAliases();
    String getDescription();
    boolean isOwnerOnly();
    List<Channel> getAuthorizedChannels();
    List<Role> getAuthorizedRoles();
    List<Permission> getAuthorizedPermissions();


}
