package com.wardiusz.Pandus.commands.slash.administrative;

import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.commands.embeds.EmbedOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.List;

import static com.wardiusz.Pandus.Prv.getLogChannel;

public class KickCmd extends SlashExecutor {
    public final Provider provider;

    public KickCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        event.getJda().retrieveUserById(event.getCommand().getOptions().getFirst().getAsLong()).queue();
        Member target = event.getGuild().getMemberById(event.getCommand().getOptions().get(0).getAsLong());
        String reason = event.getCommand().getOptions().get(1).getAsString();

        assert target != null;
        PrivateChannel privChannel = target.getUser().openPrivateChannel().complete();
        String id = getLogChannel(event.getGuild().getId()).isEmpty() ? event.getEvent().getChannel().getId() : getLogChannel(event.getGuild().getId());
        TextChannel textChannel = event.getGuild().getTextChannelById(id);

        event.deferReply();

        event.getGuild()
                .kick(target)
                .reason(reason)
                .queue(
                        (_) -> {
                            event.getHook().deleteOriginal().queue();
                            assert textChannel != null;
                            textChannel.sendMessageEmbeds(messageEmbed(target, event.getCommandSender().getUser(), reason)).queue();
                            privChannel.sendMessageEmbeds(privMessageEmbed(event.getGuild(), event.getCommandSender().getUser(), reason)).queue();
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
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Kick out member from server.";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.USER, "user", "@member to kick.", true));
        options.add(new OptionData(OptionType.STRING, "reason", "Reason of kicking member.", true));
        return options;
    }

    @Override
    public void updateAuthorizedPermissions(JDA jda) {
        authorizedPermissions.add(Permission.KICK_MEMBERS);
    }

    private MessageEmbed messageEmbed(Member target, User admin, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.ERROR_COLOR);
        embed.setTitle("User ID: ||" + target.getId() +"||");
        embed.setDescription("Member " + target.getAsMention() + " has been kicked out from the server.");
        embed.setThumbnail(target.getAvatarUrl());
        embed.addField("**Admin:** ", admin.getAsMention(), false);
        embed.addField("**Reason:** ", reason, false);

        embed.setTimestamp(Instant.now());
        return embed.build();
    }

    private MessageEmbed privMessageEmbed(Guild guild, User admin, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.ERROR_COLOR);
        embed.setTitle("You have been kicked out!");
        embed.setDescription("**Server:** " + guild.getName());
        embed.addField("**Admin:** ", admin.getAsMention(), false);
        embed.addField("**Reason:** ", reason, false);

        return embed.build();
    }
}
