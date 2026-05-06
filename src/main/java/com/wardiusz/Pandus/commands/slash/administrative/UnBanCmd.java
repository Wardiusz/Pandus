package com.wardiusz.Pandus.commands.slash.administrative;

import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import com.wardiusz.Pandus.Prv;
import com.wardiusz.Pandus.commands.embeds.EmbedOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.List;

import static com.wardiusz.Pandus.Prv.getLogChannel;


public class UnBanCmd extends SlashExecutor {
    public final Provider provider;
    public UnBanCmd(Provider provider) {
        this.provider = provider;
    }
    @Override
    public void execute(EventData event) {
        UserSnowflake target = UserSnowflake.fromId(event.getCommand().getOptions().getFirst().getAsLong());
        String reason = event.getCommand().getOptions().size() != 2 ? "" : event.getCommand().getOptions().get(1).getAsString();
        String id = getLogChannel(event.getGuild().getId()).isEmpty() ? event.getEvent().getChannel().getId() : getLogChannel(event.getGuild().getId());
        assert id != null;
        TextChannel textChannel = event.getGuild().getTextChannelById(id);

        event.deferReply();

        event.getGuild()
                .unban(target)
                .reason(reason)
                .queue(
                        (success) -> {
                            Prv.deleteBanRecord(event.getGuild().getId(), target.getId());
                            event.getHook().deleteOriginal().queue();
                            assert textChannel != null;
                            textChannel.sendMessageEmbeds(messageEmbed(target, event.getCommandSender().getUser(), reason)).queue();
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
        return "unban";
    }

    @Override
    public String getDescription() {
        return "Unban a member.";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.STRING, "user", "ID of user to unban.", true));
        options.add(new OptionData(OptionType.STRING, "reason", "Reason of giving user a unban.", false));
        return options;
    }

    @Override
    public void updateAuthorizedPermissions(JDA jda) {
        authorizedPermissions.add(Permission.BAN_MEMBERS);
    }

    private MessageEmbed messageEmbed(UserSnowflake targetUser, User admin, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.SUCCESS_COLOR);
        embed.setTitle("User ID: ||" + targetUser.getId() +"||");
        embed.setDescription("Member " + targetUser.getAsMention() + " has been unbanned from the server.");
        embed.addField("**Admin:** ", admin.getAsMention(), false);
        if (!reason.isEmpty()) {
            embed.addField("**Reason:** ", reason, false);
        }
        embed.setTimestamp(Instant.now());
        return embed.build();
    }
}
