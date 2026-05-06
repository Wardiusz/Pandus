package com.wardiusz.Pandus.Handler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CmdData {
    private MessageReceivedEvent prefixEvent;
    private SlashCommandInteractionEvent slashEvent;
    private List<String> args;
    private final JDA jda;
    private final MessageChannelUnion channel;
    private final GuildMessageChannel guildMessageChannel;
    private final TextChannel textChannel;
    private final Member selfMember;
    private final Member commandSender;
    private final Guild guild;
//    private final CommandData command;

    private final GuildVoiceState selfVoiceState;
    private final GuildVoiceState memberVoiceState;

    private InteractionHook hook;

    public CmdData(@NotNull MessageReceivedEvent event, List<String> args) {
        prefixEvent = event;

        this.args = args;
        this.jda = prefixEvent.getJDA();
        this.channel = prefixEvent.getChannel();
        this.commandSender = prefixEvent.getMember();
        this.guild = prefixEvent.getGuild();

        this.guildMessageChannel = this.channel.asGuildMessageChannel();
        this.textChannel = this.channel.asTextChannel();
        this.selfMember = this.guild != null ? this.guild.getSelfMember() : null;

        this.selfVoiceState = this.selfMember != null ? this.selfMember.getVoiceState() : null;
        this.memberVoiceState = this.commandSender != null ? this.commandSender.getVoiceState() : null;
    }

    public CmdData(@NotNull SlashCommandInteractionEvent event) {
        slashEvent = event;

        this.channel = slashEvent.getChannel();
        this.commandSender = slashEvent.getMember();
        this.guild = slashEvent.getGuild();
        this.jda = slashEvent.getJDA();
        this.hook = slashEvent.getHook();

        this.guildMessageChannel = this.channel.asGuildMessageChannel();
        this.textChannel = this.channel.asTextChannel();
        this.selfMember = this.guild != null ? this.guild.getSelfMember() : null;

        this.selfVoiceState = this.selfMember != null ? this.selfMember.getVoiceState() : null;
        this.memberVoiceState = this.commandSender != null ? this.commandSender.getVoiceState() : null;
    }

    public List<String> getArgs() {
        return this.args;
    }

    public JDA getJDA() {
        return jda;
    }

    public MessageChannelUnion getChannel() {
        return channel;
    }

    public GuildMessageChannel getGuildMessageChannel() {
        return guildMessageChannel;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public Member getSelfMember() {
        return selfMember;
    }

    public Member getCommandSender() {
        return commandSender;
    }

    public Guild getGuild() {
        return guild;
    }

    public GuildVoiceState getSelfVoiceState() {
        return selfVoiceState;
    }

    public GuildVoiceState getMemberVoiceState() {
        return memberVoiceState;
    }

    public void deferReply(boolean isEphemeral) {
        slashEvent.deferReply(isEphemeral).queue();
    }

    public void deferReply() {
        this.deferReply(false);
    }

    public ReplyCallbackAction reply(String content, boolean ephemeral) {
        return slashEvent.reply(content).setEphemeral(ephemeral);
    }

    public ReplyCallbackAction reply(MessageEmbed embed, boolean ephemeral) {
        return slashEvent.replyEmbeds(embed).setEphemeral(ephemeral);
    }

    public InteractionHook getHook() {
        return hook;
    }


    public MessageReceivedEvent getPrefixEvent() {
        return prefixEvent;
    }

    public SlashCommandInteractionEvent getSlashEvent() {
        return slashEvent;
    }
}
