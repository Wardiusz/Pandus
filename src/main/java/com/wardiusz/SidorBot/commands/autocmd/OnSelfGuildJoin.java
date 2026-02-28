package com.wardiusz.SidorBot.commands.autocmd;

import com.wardiusz.SidorBot.Handler.Config;
import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
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
//        EmbedBuilder embed = new EmbedBuilder();
//        embed.setColor(EmbedOptions.NEUTRAL_COLOR);
//        embed.setAuthor(null);
//        embed.setTitle("Thanks for using Sidor!");
//        embed.setDescription("We're delighted that you choose Sidor over other existing bots! To start your journey with Sidor use /help to get a grip what commands you can send! Remember you can also use a custom commands and set a prefix you wanna use! For default is " + Config.get("PREFIX"));
//        event.getGuild().getOwner().getUser().openPrivateChannel().queue(pc -> pc.sendMessageEmbeds(embed.build()).complete());
    }
}