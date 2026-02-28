package com.wardiusz.SidorBot.commands.slash.administrative;

import com.wardiusz.SidorBot.Handler.EventData;
import com.wardiusz.SidorBot.Handler.Slash.SlashExecutor;
import com.wardiusz.SidorBot.Provider;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

import static com.wardiusz.SidorBot.Prv.changeGoodbyeMsg;

public class SetGoodbyeMsgCmd extends SlashExecutor {
    public final Provider provider;

    public SetGoodbyeMsgCmd(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void execute(EventData event) {
        String msg = event.getCommand().getOptions().getFirst().getAsString();
        changeGoodbyeMsg(event.getGuild().getId(), msg);

        event.reply("Goodbye message changed to:\n**\"** " + msg.replace("\\n", "\n") + " **\"**", false).queue();
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public String getName() {
        return "setgoodbyemsg";
    }

    @Override
    public String getDescription() {
        return "Set a goodbye message.";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.STRING, "message", "Use '{user}' for name of member.", true));
        return options;
    }
}
