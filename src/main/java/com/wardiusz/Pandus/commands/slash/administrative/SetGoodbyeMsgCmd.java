package com.wardiusz.Pandus.commands.slash.administrative;

import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import com.wardiusz.Pandus.Provider;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

import static com.wardiusz.Pandus.Prv.changeGoodbyeMsg;

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
