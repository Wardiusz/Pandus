package com.wardiusz.SidorBot.commands.autocmd;

import com.wardiusz.SidorBot.Prv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
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