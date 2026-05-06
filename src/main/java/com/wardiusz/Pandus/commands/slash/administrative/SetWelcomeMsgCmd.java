package com.wardiusz.Pandus.commands.slash.administrative;

import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import com.wardiusz.Pandus.Provider;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

import static com.wardiusz.Pandus.Prv.changeWelcomeMsg;

public class SetWelcomeMsgCmd extends SlashExecutor {
    public final Provider provider;

    public SetWelcomeMsgCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        String msg = event.getCommand().getOptions().getFirst().getAsString();
        boolean useCard = event.getCommand().getOptions().getLast().getAsBoolean();

        changeWelcomeMsg(event.getGuild().getId(), msg, useCard);

        event.reply("Welcome message changed to:\n**\"** " + msg.replace("\\n", "\n") + " **\"**", false).queue();
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public String getName() {
        return "setwelcomemsg";
    }

    @Override
    public String getDescription() {
        return "Set a welcome message.";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.STRING, "message", "Use '{user}' for name of member and '{guild}' for name of the guild.", true));
        options.add(new OptionData(OptionType.BOOLEAN, "card", "Whenever use the welcome card or not.", true));
        return options;
    }
}
