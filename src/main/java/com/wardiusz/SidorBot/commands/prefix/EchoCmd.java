package com.wardiusz.SidorBot.commands.prefix;

import com.wardiusz.SidorBot.Handler.Prefix.PrefixCommands;
import com.wardiusz.SidorBot.Handler.Prefix.PrefixExecutor;
import com.wardiusz.SidorBot.Handler.Prefix.PrefixOption;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

public class EchoCmd extends PrefixExecutor {

    public EchoCmd() {
        super();
        options.add(new PrefixOption("text", "Text to be written by bot."));
    }

    @Override
    public String getName() {
        return "echo";
    }

    @Override
    public String getDescription() {
        String example = "``" + getName() + " SidorBot``";
        String quickDescription = "Command " + getName() + " makes bot to write any text provided by a sender.";
        return quickDescription.concat("\n\n").concat(getHelp()).concat("\n\n").concat("Example: ").concat(example);
    }

    @Override
    public String getHelp() {
        StringBuilder sb = new StringBuilder();
        for (PrefixOption option : options) {
            sb.append(" [").append(option.getName().replace("[", "").replace("]", "")).append("] ").append("- ").append(option.getDescription());
        }
        return sb.toString();
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        String reply = options.stream().map(PrefixOption::getStringValue).reduce("", (r,u) -> r + " " + u);
        event.getChannel().sendMessage(reply).queue();
    }
}