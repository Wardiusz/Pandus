package com.wardiusz.SidorBot.commands.slash.administrative;

import com.wardiusz.SidorBot.Handler.CommandData;
import com.wardiusz.SidorBot.Handler.EventData;
import com.wardiusz.SidorBot.Handler.Slash.SlashExecutor;
import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.Prv;
import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.RestAction;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wardiusz.SidorBot.Prv.getLogChannel;

public class MuteCmd extends SlashExecutor {
    public final Provider provider;

    public MuteCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        event.getJda().retrieveUserById(event.getCommand().getOptions().getFirst().getAsLong()).queue();
        Member target = event.getGuild().getMemberById(event.getCommand().getOptions().getFirst().getAsLong());
        String reason = CommandData.searchThroughOptions(event, "reason").orElse("");
        String time = CommandData.searchThroughOptions(event, "limit").orElse("");
        Role muted = getOrCreateMutedRole(event.getGuild());

        assert target != null;
        PrivateChannel privChannel = target.getUser().openPrivateChannel().complete();
        String id = getLogChannel(event.getGuild().getId()).isEmpty() ? event.getEvent().getChannel().getId() : getLogChannel(event.getGuild().getId());
        TextChannel textChannel = event.getGuild().getTextChannelById(id);

        if (Prv.isThereMuteRecord(event.getGuild().getId(), target.getId())) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(EmbedOptions.NEUTRAL_COLOR);

            if (target.getRoles().contains(muted)) {
                embed.setDescription("Member " + target.getAsMention() + " is already muted.");
                event.getEvent().replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }

            embed.setDescription("Member " + target.getAsMention() + " should be already muted. For unknown reasons role " + Objects.requireNonNull(event.getGuild().getRolesByName("Muted", true).stream().findFirst().orElse(null)).getAsMention() +
                    " is not assigned to this member based on records in our database.\n" +
                    "Do you want to correct this?\n" +
                    "If yes click " + Emoji.fromCustom("Success", 1115778398212591676L, false).getFormatted() +
                    " or "+ Emoji.fromCustom("Fail", 1115782788227010610L, false).getFormatted() +
                    " to remove this record from database.");

            event.deferReply();

            event.getHook().editOriginalEmbeds(embed.build()).queue((message) -> RestAction.allOf(
                message.addReaction(Emoji.fromCustom("success", 1115778398212591676L, false)),
                message.addReaction(Emoji.fromCustom("fail", 1115782788227010610L, false)))
                    .queue(wait -> provider.getWaiter().waitForEvent(
                    MessageReactionAddEvent.class,
                    e -> {
                        if(!e.getMessageId().equals(message.getId()))
                            return false;

                        if (Objects.requireNonNull(e.getUser()).isBot())
                            return false;

                        Emoji emote = e.getReaction().getEmoji();

                        if (event.getCommandSender().getIdLong() != e.getUser().getIdLong())
                            return false;

                        return emote.getName().equalsIgnoreCase("success") || emote.getName().equalsIgnoreCase("fail");
                    },
                    e -> {
                        Emoji emote = e.getReaction().getEmoji();

                        EmbedBuilder embed2 = new EmbedBuilder();
                        embed2.setColor(EmbedOptions.SUCCESS_COLOR);
                        embed2.setDescription("Corrected!");

                        if (emote.getName().equalsIgnoreCase("success")) {
                            target.getGuild().addRoleToMember(target.getUser(), muted).queue(
                                    (done) -> event.getHook().editOriginalEmbeds(embed2.build()).queue(
                                            (sent) -> sent.delete().queueAfter(3, TimeUnit.SECONDS)
                                    ),
                                    (error) -> event.getChannel().sendMessage("Something went wrong.\nError ID " + error.getMessage()).queue(
                                            (sent) -> sent.delete().queueAfter(3, TimeUnit.SECONDS)
                                    ));
                        } else {
                            Prv.deleteMuteRecord(event.getGuild().getId(), target.getId());
                            event.getHook().editOriginalEmbeds(embed2.build()).queue(
                                    (sent) -> sent.delete().queueAfter(3, TimeUnit.SECONDS)
                            );
                        }
                    },
                    1, TimeUnit.MINUTES,
                    () -> message.delete().queue()
            )));
            return;
        }

            if (!isPatterCorrect(time)) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(EmbedOptions.ERROR_COLOR);
                embed.setDescription("Incorrect use of time format.\nSee `/help " + getName() + "` to get more info.");
                event.reply(embed.build(), true).queue();
                return;
            }

            event.deferReply();

            target.getGuild().addRoleToMember(target.getUser(), muted).queue((success) -> {
                Prv.addMuteRecord(event.getGuild().getId(), target.getId(), event.getCommandSender().getId(), calculateTime(time), reason);
                event.getHook().deleteOriginal().queue();
                assert textChannel != null;
                textChannel.sendMessageEmbeds(messageEmbed(target, event.getCommandSender().getUser(), time, reason)).queue();
                privChannel.sendMessageEmbeds(privMessageEmbed(event.getGuild(), event.getCommandSender().getUser(), time, reason)).queue();
            });
    }

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String getDescription() {
        return "Mute a member.";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.USER, "user", "@member to mute.", true));
        options.add(new OptionData(OptionType.STRING, "reason", "Reason of muting the member.", false));
        options.add(new OptionData(OptionType.STRING, "limit", "Time of the mute. Ex. 1h, 1h5m, 2Y5M5d5h2m", false));
        return options;
    }

    @Override
    public void updateAuthorizedPermissions(JDA jda) {
        authorizedPermissions.add(Permission.MODERATE_MEMBERS);
        authorizedPermissions.add(Permission.MANAGE_CHANNEL);
    }

    private MessageEmbed messageEmbed(Member target, User admin, String time, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.ERROR_COLOR);
        embed.setTitle("User ID: ||" + target.getId() +"||");
        embed.setDescription("Member " + target.getAsMention() + " has been muted.");
        embed.setThumbnail(target.getEffectiveAvatarUrl());
        embed.addField("**Admin: **", admin.getAsMention(), true);
        if (!time.isEmpty())
            embed.addField("**Time: **", time, true);
        if (!reason.isEmpty())
            embed.addField("**Reason: **", reason, false);
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    private MessageEmbed privMessageEmbed(Guild guild, User admin, String time, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.ERROR_COLOR);
        embed.setTitle("You have been muted!");
        embed.setDescription("**Server: **" + guild.getName());
        embed.addField("**Admin: **", admin.getAsMention(), true);
        if (!time.isEmpty())
            embed.addField("**Time: **", time, true);
        if (!reason.isEmpty())
            embed.addField("**Reason: **", reason, false);
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    private static long convertToMilliseconds(long years, long months, long days, long hours, long minutes) {
        long totalDays = years * 365L + months * 30L + days;
        long totalHours = totalDays * 24L + hours;
        long totalMinutes = totalHours * 60L + minutes;
        long totalSeconds = totalMinutes * 60L;

        return totalSeconds * 1000L;
    }

    private String calculateTime(String duration) {
        if (duration.isEmpty()) {
            return duration;
        }

        long years = 0;
        long months = 0;
        long days = 0;
        long hours = 0;
        long minutes = 0;

        String[] parts = duration.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        for (int i = 0; i < parts.length; i += 2) {
            long value = Long.parseLong(parts[i]);
            String unit = parts[i + 1];
            switch (unit) {
                case "Y" -> years += value;
                case "M" -> months += value;
                case "d" -> days += value;
                case "h" -> hours += value;
                case "m" -> minutes += value;
            }
        }

        long milliseconds = convertToMilliseconds(years, months, days, hours, minutes);
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.setTimeInMillis(calendar.getTimeInMillis() + milliseconds);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return formatter.format(calendar.getTime());
    }
    private boolean isPatterCorrect(String time) {
        if (time.isEmpty()) {
            return true;
        }

        System.out.println(time);
        String pattern = "(\\d+[a-zA-Z])?(\\d+[a-zA-Z])+";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(time);
        System.out.println(matcher);
        boolean isValid = false;

        for (char c : time.toCharArray()) {
            if ("YMdhm".indexOf(c) < 0){
                isValid = true;
                break;
            }
        }
        System.out.println(matcher.matches() + " " + isValid);
        return (matcher.matches() && isValid);
    }

    private Role getOrCreateMutedRole(Guild guild) {
        Role mutedRole = guild.getRolesByName("Muted", true).stream().findFirst().orElse(null);
        if (mutedRole == null) {
            mutedRole = guild.createRole().setName("Muted").setPermissions(Permission.MESSAGE_SEND).setMentionable(false).complete();
        }
        return mutedRole;
    }
}
