package com.wardiusz.Pandus.commands.autocmd;

import com.wardiusz.Pandus.Prv;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class UserLeftModule extends ListenerAdapter {
    AutoCmdListener autoCmdListener;

    public UserLeftModule(AutoCmdListener autoCmdListener) {
        this.autoCmdListener = autoCmdListener;
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
//            JDA client = event.getJDA();
//
//            String message = Prv.getGoodbyeMsg(event.getGuild().getId())
//                    .replace("@user", event.getUser().getAsMention())
//                    .replace("#guild", event.getGuild().getName())
//                    .replace("\\n", "\n");
//
//            client.getRestPing().queue((it) -> Objects.requireNonNull(event.getGuild()
//                    .getDefaultChannel())
//                    .asStandardGuildMessageChannel()
//                    .sendMessage(message)
//                    .queue()
//            );
        String message = Prv.getGoodbyeMsg(event.getGuild().getId())
                .replace("{user}", event.getUser().getName())
                .replace("\\n", "\n");

        Objects.requireNonNull(event.getGuild().getDefaultChannel()).asStandardGuildMessageChannel().sendMessage(message).queue();
    }
}