package com.wardiusz.Pandus.commands.slash.administrative;

import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import com.wardiusz.Pandus.commands.embeds.EmbedOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.List;


public class WarnCmd extends SlashExecutor {
    public final Provider provider;

    public WarnCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        event.getJda().retrieveUserById(event.getCommand().getOptions().getFirst().getAsLong()).queue();
        User target = event.getJda().getUserById(event.getCommand().getOptions().getFirst().getAsLong());
        String message = event.getCommand().getOptions().get(1).getAsString();

        assert target != null;
        PrivateChannel privChannel = target.openPrivateChannel().complete();

        event.deferReply();

        event.getChannel().sendMessageEmbeds(messageEmbed(target, message)).queue(
                (success) -> {
                    event.getHook().deleteOriginal().queue();
                    privChannel.sendMessageEmbeds(privMessageEmbed(event.getGuild(), event.getCommandSender().getUser(), message)).queue();
                },
                (error) -> {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(EmbedOptions.ERROR_COLOR);
                    embed.setDescription("Error ID " + error.getMessage());
                    event.getHook().editOriginalEmbeds(embed.build()).queue();
                }
        );

    }

    @Override
    public String getName() {
        return "warn";
    }

    @Override
    public String getDescription() {
        return "Warn a member.";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.USER, "user", "@member to warn.", true));
        options.add(new OptionData(OptionType.STRING, "reason", "Reason of the warn.", true));

        return options;
    }

    @Override
    public void updateAuthorizedPermissions(JDA jda) {
        authorizedPermissions.add(Permission.MESSAGE_MANAGE);
    }



    public static MessageEmbed messageEmbed(User target, String message) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.ERROR_COLOR);
        embed.setAuthor(target.getName() + " has been warned!", null, target.getEffectiveAvatarUrl());
        embed.setDescription("**Reason:** " + message);

        return embed.build();
    }

    public static MessageEmbed privMessageEmbed(Guild guild, User admin, String message) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.ERROR_COLOR);
        embed.setTitle("You have been warned!");
        embed.addField("**Server:** ", guild.getName(), false);
        embed.addField("**Admin:** ", admin.getAsMention(), false);
        embed.addField("**Reason:** ", message, false);
        embed.setTimestamp(Instant.now());

        return embed.build();
    }
}
