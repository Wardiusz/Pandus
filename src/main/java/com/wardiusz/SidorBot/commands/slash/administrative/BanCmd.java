package com.wardiusz.SidorBot.commands.slash.administrative;

import com.wardiusz.SidorBot.Handler.CommandData;
import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.Handler.EventData;
import com.wardiusz.SidorBot.Handler.Slash.SlashExecutor;
import com.wardiusz.SidorBot.Prv;
import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.wardiusz.SidorBot.Prv.getLogChannel;


public class BanCmd extends SlashExecutor {
    public final Provider provider;

    public BanCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        event.getJda().retrieveUserById(event.getCommand().getOptions().getFirst().getAsLong()).queue();
        Member target = event.getGuild().getMemberById(event.getCommand().getOptions().getFirst().getAsLong());
        String reason = CommandData.searchThroughOptions(event, "reason").orElse("");
        int hours = Integer.parseInt(CommandData.searchThroughOptions(event, "hours").orElse("0"));

        if (target == null) {
            event.getEvent().reply(EmbedOptions.FAIL_EMOJI.getFormatted() + " Member is no longer on the server.").setEphemeral(true).queue();
            return;
        }

        PrivateChannel privChannel = target.getUser().openPrivateChannel().complete();
        String id = getLogChannel(event.getGuild().getId()).isEmpty() ? event.getEvent().getChannel().getId() : getLogChannel(event.getGuild().getId());
        TextChannel textChannel = event.getGuild().getTextChannelById(id);

        event.deferReply();

        event.getGuild()
                .ban(target, hours, TimeUnit.HOURS)
                .reason(reason)
                .queue(
                        (_) -> {
                            Prv.addBanRecord(event.getGuild().getId(), target.getId(), event.getCommandSender().getId(), reason);
                            event.getHook().deleteOriginal().queue();
                            assert textChannel != null;
                            textChannel.sendMessageEmbeds(messageEmbed(target.getUser(), event.getCommandSender().getUser(), reason)).queue();
                            privChannel.sendMessageEmbeds(privMessageEmbed(event.getGuild(), event.getCommandSender().getUser(), reason)).queue();
                        },
                        (error) -> {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(EmbedOptions.ERROR_COLOR);
                            embed.setDescription(EmbedOptions.FAIL_EMOJI + " Error ID " + error.getMessage());
                            event.getHook().editOriginalEmbeds(embed.build()).queue();
                        }
                );
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return "Ban a member.";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.USER, "user", "@member to ban.", true));
        options.add(new OptionData(OptionType.STRING, "reason", "Reason of a ban.", false));
        options.add(new OptionData(OptionType.INTEGER, "hours", "Number of hours of sent messages by banned member to delete.", false));
        return options;
    }

    @Override
    public void updateAuthorizedPermissions(JDA jda) {
        authorizedPermissions.add(Permission.BAN_MEMBERS);
    }

    private MessageEmbed messageEmbed(User target, User admin, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.NEUTRAL_COLOR);
        embed.setTitle("User ID: ||" + target.getId() +"||");
        embed.setDescription("Member " + target.getAsMention() + " has been banned from the server.");
        embed.setThumbnail(target.getAvatarUrl());
        embed.addField("**Admin: ** ", admin.getAsMention(), false);
        if (!reason.isEmpty())
            embed.addField("**Reason: ** ", reason, false);
        embed.setTimestamp(Instant.now());
        return embed.build();
    }

    private MessageEmbed privMessageEmbed(Guild guild, User admin, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.ERROR_COLOR);
        embed.setTitle("You have been banned!");
        embed.setDescription("**Server: ** " + guild.getName());
        embed.addField("**Admin: ** ", admin.getAsMention(), false);
        if (!reason.isEmpty())
            embed.addField("**Reason: ** ", reason, false);
        embed.setTimestamp(Instant.now());

        return embed.build();
    }
}
