package com.wardiusz.Pandus.commands.autocmd;

import com.wardiusz.Pandus.Handler.Config;
import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.commands.embeds.EmbedOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnSelfGuildJoin extends ListenerAdapter {
    AutoCmdListener autoCmdListener;

    public OnSelfGuildJoin(AutoCmdListener autoCmdListener) {
        this.autoCmdListener = autoCmdListener;
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Provider.loadGuildProperties();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedOptions.NEUTRAL_COLOR);
        embed.setAuthor(null);
        embed.setTitle("Thanks for using Pandus!");
        embed.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        embed.setDescription("""
            We're delighted that you choose Pandus over other existing bots!\s
            To start your journey with Pandus use /help to get a grip what commands you can send!\s
            Remember you can also use a custom commands and set a prefix you wanna use! For default is **%s**\s
        """.formatted(Config.get("PREFIX")));

        DefaultGuildChannelUnion channel = event.getGuild().getDefaultChannel();

        if (channel == null) {
            event.getGuild().createTextChannel("welcome").complete().sendMessageEmbeds(embed.build()).queue();
        }

        channel.asTextChannel().sendMessageEmbeds(embed.build()).queue();
    }
}