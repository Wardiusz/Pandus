package com.wardiusz.SidorBot.commands.autocmd;

import com.wardiusz.SidorBot.Prv;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.Executors;

public class UserJoinModule extends ListenerAdapter {
    AutoCmdListener autoCmdListener;

    public UserJoinModule(AutoCmdListener autoCmdListener) {
        this.autoCmdListener = autoCmdListener;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        TextChannel welcomeChannel = guild.getSystemChannel();
        
        String botRoleId = Prv.getAutoBotRole(guild.getId());
        String memberRoleId = Prv.getAutoNewMemberRole(guild.getId());

        if (memberRoleId != null) {
            if (memberRoleId.isBlank()) return;

            if (guild.getRoleById(memberRoleId) != null) {
                guild.addRoleToMember(event.getUser(), Objects.requireNonNull(guild.getRoleById(memberRoleId))).queue();
            } else {
                Objects.requireNonNull(guild.getDefaultChannel()).asTextChannel().sendMessage("Error! Cannot find a role to assign!").queue();
            }
        }
        if (botRoleId != null) {
            if (event.getMember().getUser().isBot() && guild.getRoleById(botRoleId) != null) {
                guild.addRoleToMember(event.getUser(), Objects.requireNonNull(guild.getRoleById(botRoleId))).queue();
            } else {
                Objects.requireNonNull(guild.getDefaultChannel()).asTextChannel().sendMessage("Error! Cannot find a role to assign!").queue();
            }
        }
        
        String message = Prv.getWelcomeMsg(guild.getId())
                .replace("{user}", event.getUser().getAsMention())
                .replace("{guild}", guild.getName())
                .replace("\\n", "\n");

        if (welcomeChannel != null && Prv.shouldUseCard(guild.getId())) {
            Executors.newSingleThreadExecutor().submit(() -> WelcomeCard.createAndSendWelcomeCard(event, message, welcomeChannel, guild.getMemberCount()));
        }
    }
}