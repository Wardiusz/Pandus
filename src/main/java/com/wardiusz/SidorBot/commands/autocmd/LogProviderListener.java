package com.wardiusz.SidorBot.commands.autocmd;

import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.wardiusz.SidorBot.Handler.Config;
import com.wardiusz.SidorBot.Provider;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.EnumSet;

public class LogProviderListener extends ListenerAdapter {
        @Override
        public void onShutdown(ShutdownEvent event) {
            Guild guild = event.getJDA().getGuildById(Config.get("OWNER_SERVER_ID"));

            if (guild == null) return;

            GuildChannel gch = guild.getChannels().stream().findFirst().filter(x -> x.getName().equals("logs")).orElse(null);

            if (gch == null) {
                EnumSet<Permission> perms = EnumSet.of(Permission.ADMINISTRATOR);
                guild.createTextChannel("logs").addMemberPermissionOverride(Long.parseLong(Config.get("OWNER")), perms, null).queue();

            } else {
                File file = new File("target/logs/tmp_log.log");
                if (!file.exists()) return;
                guild.getTextChannelById(gch.getId()).sendFiles(FileUpload.fromData(file)).complete();
            }
        }
}