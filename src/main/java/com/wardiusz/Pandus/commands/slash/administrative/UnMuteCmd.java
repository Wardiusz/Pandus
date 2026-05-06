package com.wardiusz.Pandus.commands.slash.administrative;

import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.Prv;
import com.wardiusz.Pandus.commands.embeds.EmbedOptions;
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

import static com.wardiusz.Pandus.Prv.getLogChannel;


public class UnMuteCmd extends SlashExecutor {
    public final Provider provider;

    public UnMuteCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        event.getJda().retrieveUserById(event.getCommand().getOptions().getFirst().getAsLong()).queue();
        Member target = event.getGuild().getMemberById(event.getCommand().getOptions().getFirst().getAsLong());
        String reason = event.getEvent().getOptions().size() > 1 ? event.getEvent().getOptions().get(1).getAsString() : "";
        Role muted = getOrCreateMutedRole(event.getGuild());

        assert target != null;
        PrivateChannel privChannel = target.getUser().openPrivateChannel().complete();
        String id = getLogChannel(event.getGuild().getId()).isEmpty() ? event.getEvent().getChannel().getId() : getLogChannel(event.getGuild().getId());
        TextChannel textChannel = event.getGuild().getTextChannelById(id);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.NEUTRAL_COLOR);

        if (!target.getRoles().contains(muted)) {
            if (Prv.isThereMuteRecord(event.getGuild().getId(), target.getId()))
                Prv.deleteMuteRecord(event.getGuild().getId(), target.getId());

            embed.setDescription("Member " + target.getAsMention() + " is not muted.");
            event.getEvent().replyEmbeds(embed.build()).queue();

            return;
        }

        event.deferReply();

        target.getGuild().removeRoleFromMember(target.getUser(), muted).queue(
                (success) -> {
                    event.getHook().deleteOriginal().queue();
                    assert textChannel != null;
                    textChannel.sendMessageEmbeds(messageEmbed(target, event.getCommandSender().getUser(), reason)).queue();
                    privChannel.sendMessageEmbeds(privMessageEmbed(event.getGuild(), event.getCommandSender().getUser(), reason)).queue();
                    if (Prv.isThereMuteRecord(event.getGuild().getId(), target.getId()))
                        Prv.deleteMuteRecord(event.getGuild().getId(), target.getId());
                }
        );

    }

    @Override
    public String getName() {
        return "unmute";
    }

    @Override
    public String getDescription() {
        return "test";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.USER, "user", "@member to unmute.", true));
        options.add(new OptionData(OptionType.STRING, "reason", "Reason of unmute the member.", false));
        return options;
    }

    @Override
    public void updateAuthorizedPermissions(JDA jda) {
        authorizedPermissions.add(Permission.MODERATE_MEMBERS);
        authorizedPermissions.add(Permission.MANAGE_CHANNEL);
    }

    private MessageEmbed messageEmbed(Member target, User admin, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.NEUTRAL_COLOR);
        embed.setTitle("User ID: ||" + target.getId() +"||");
        embed.setDescription("Member " + target.getAsMention() + " has been unmuted.");
        embed.setThumbnail(target.getAvatarUrl());
        embed.addField("**Admin: **", admin.getAsMention(), true);
        if (!reason.isEmpty())
            embed.addField("**Reason: **", reason, false);
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    private MessageEmbed privMessageEmbed(Guild guild, User admin, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.SUCCESS_COLOR);
        embed.setTitle("You have been unmuted!");
        embed.setDescription("**Server: **" + guild.getName());
        embed.addField("**Admin: **", admin.getAsMention(), true);
        if (!reason.isEmpty())
            embed.addField("**Reason: **", reason, false);
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    private Role getOrCreateMutedRole(Guild guild) {
        Role mutedRole = guild.getRolesByName("Muted", true).stream().findFirst().orElse(null);
        if (mutedRole == null) {
            mutedRole = guild.createRole().setName("Muted").setPermissions(Permission.MESSAGE_SEND).setMentionable(false).complete();
        }
        return mutedRole;
    }
}
