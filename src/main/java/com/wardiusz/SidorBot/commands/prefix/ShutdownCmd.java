package com.wardiusz.SidorBot.commands.prefix;

import com.wardiusz.SidorBot.Handler.Config;
import com.wardiusz.SidorBot.Handler.Prefix.PrefixCommands;
import com.wardiusz.SidorBot.Handler.Prefix.PrefixExecutor;
import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import static com.sedmelluq.discord.lavaplayer.container.matroska.format.MatroskaElementType.Duration;

public class ShutdownCmd extends PrefixExecutor {
    public ShutdownCmd() {
        super();
    }

    @Override
    public String getName() {
        return "shutdown";
    }

    @Override
    public String getDescription() {
        String example = "``" + PrefixCommands.prefix + getName() + "``";
        String quickDescription = "Command " + getName() + " makes bot to shutdown.";
        return quickDescription.concat("\n\n").concat(getHelp()).concat("\n\n").concat("Example: ").concat(example);
    }

    @Override
    public String getHelp() {
        return "Command to shutdown the bot.";
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        if (event.getAuthor().getIdLong() == Long.parseLong(Config.get("OWNER_ID"))) {
                event.getChannel().sendMessage("Shutting down.").queue(shutting -> {
                    sendLog(event);
                    shutting.delete().queue();
                    event.getJDA().shutdown();
                });
        } else {
            event.getChannel().sendMessage(EmbedOptions.FAIL_EMOJI.getFormatted() + "You are not allowed to use this command.").queue((success) -> success.delete().queueAfter(5, TimeUnit.SECONDS));
        }
    }

    private void sendLog(MessageReceivedEvent event) {
        Guild guild = event.getJDA().getGuildById(Config.get("OWNER_SERVER_ID"));

        if (guild == null) return;

        GuildChannel gch = guild.getChannels().stream().findFirst().filter(x -> x.getName().equals("logs")).orElse(null);

        if (gch == null) {
            EnumSet<Permission> perms = EnumSet.of(Permission.ADMINISTRATOR);
            guild.createTextChannel("logs").addMemberPermissionOverride(Long.parseLong(Config.get("OWNER")), perms, null).queue();
        } else {
            File file = new File("target/logs/logback.log");
            if (!file.exists()) return;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            guild.getTextChannelById(gch.getId()).sendMessage("`` Log from: " + (LocalDateTime.now()).format(dtf) + " ``").addFiles(FileUpload.fromData(file)).queue();
        }
    }
}