package com.wardiusz.SidorBot.Handler;

import com.wardiusz.SidorBot.Provider;
import com.wardiusz.SidorBot.Handler.Prefix.PrefixExecutor;
import com.wardiusz.SidorBot.Handler.Slash.SlashExecutor;
import com.wardiusz.SidorBot.commands.DTO.ConfigOptions;
import com.wardiusz.SidorBot.commands.embeds.EmbedOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class HelpCmd extends SlashExecutor {

    private final Provider provider;

    public HelpCmd(Provider provider) {
        this.provider = provider;

    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Get help with the commands.";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.STRING, "command", "Help for specific command.", false));
        return options;
    }

    @Override
    public void execute(EventData event) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Help - " + event.getGuild().getName());
        builder.setColor(EmbedOptions.NEUTRAL_COLOR);

        if (event.getCommand().getOptions().isEmpty()) {
            builder.addField("Slash Commands", "--------------------", false);
            provider.getExecutors().forEach((name, commandExecutor) -> {
                if (commandExecutor instanceof SlashExecutor && !commandExecutor.isOwnerOnly() && !commandExecutor.getName().equals("help") && (commandExecutor.getDescription() != null || !commandExecutor.getDescription().isEmpty())) {
                    if (!commandExecutor.getAliases().contains(name)) {
                        builder.addField("/" + name, commandExecutor.getDescription(), false);
                    }
                }
            });

            if (Boolean.parseBoolean(ConfigOptions.PREFIX_COMMANDS.getValue())) {
                builder.addField("Prefix Commands", "--------------------", false);
                provider.getExecutors().forEach((name, commandExecutor) -> {
                    if (commandExecutor instanceof PrefixExecutor && !commandExecutor.isOwnerOnly() && !commandExecutor.getName().equals("help") && (commandExecutor.getDescription() != null || !commandExecutor.getDescription().isEmpty())) {
                        if (!commandExecutor.getAliases().contains(name)) {
                            builder.addField(provider.getPrefixCommands().getPrefix() + name, commandExecutor.getDescription(), false);
                        }
                    }
                });
            }
            builder.setDescription("Here's a list of command you might be able to use on this server.");
        }
        else if (event.getCommand().getOptions().size() == 1) {
            provider.getExecutors().forEach((name, commandExecutor) -> {
                if (commandExecutor instanceof PrefixExecutor && !commandExecutor.isOwnerOnly() && !commandExecutor.getName().equals("help") && (commandExecutor.getDescription() != null || !commandExecutor.getDescription().isEmpty())) {
                    if (event.getCommand().getOptions().getFirst().getAsString().equals(name)) {
                        builder.setColor(EmbedOptions.NEUTRAL_COLOR);
                        builder.setTitle("Help for command `" + provider.getPrefixCommands().getPrefix() + name + "`");
                        builder.setDescription(((PrefixExecutor) commandExecutor).usage());
                    }
                }
            });
        }

        if (builder.getDescriptionBuilder().isEmpty()) {
            builder.setDescription("There's no command to show for this server.");
        }

        event.reply(builder.build(), true).queue();
    }

}
