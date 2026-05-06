package com.wardiusz.Pandus.commands.slash.administrative;

import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class AutoDelMsgCmd extends SlashExecutor {
    @Override
    public void execute(EventData event) {
//        if (event.getCommand().getOption("user") != null) {
//            executeQueries(event.getCommand().getOption("action").getAsInt() != 0 ? DBAction.ADD : DBAction.REMOVE,
//                    event.getCommand().getOption("user").getAsMember().getId(), // I accept using here null cause it will later check in required logic if it's null or not
//                    event.getCommand().getOption("channel").getAsChannel().getId(),
//                    event.getCommand().getOption("phrase").getAsString());
//        }

    }
//
//    void executeQueries(DBAction action, String userId, String channelId, String phrase) {
//        if (Prv.getDelMsg().isEmpty() && action == DBAction.REMOVE) {
//            return;
//        }
//        if (action == DBAction.ADD) {
//            Prv.addDelMsg(userId, channelId, phrase);
//        }
//        if (action == DBAction.REMOVE) {
//            Prv.deleteDelMsg(userId, channelId, phrase);
//        }
//    }

    @Override
    public String getName() {
        return "autodelmsg";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.STRING, "action", "Choose an action.", true).addChoice("add", 1).addChoice("remove", 0));
        options.add(new OptionData(OptionType.USER, "user", "Choose a @member.", false));
        options.add(new OptionData(OptionType.CHANNEL, "channel", "Choose what channel to monitor.", true));
        options.add(new OptionData(OptionType.STRING, "phrase", "Type phrase to censor.", true));
        return options;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Deletes message for a specific irritating person.";
    }
}
