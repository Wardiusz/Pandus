package com.wardiusz.SidorBot.Handler;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.Command.Type;

import java.util.List;
import java.util.Optional;

public class CommandData {
    Long id;
    String commandString;
    Type type;
    String name;
    String fullName;
    List<OptionMapping> options;

    public CommandData(Long id, String commandString, Type type, String name, String fullName, List<OptionMapping> options) {
        this.id = id;
        this.commandString = commandString;
        this.type = type;
        this.name = name;
        this.fullName = fullName;
        this.options = options;
    }

    public Long getId() {
        return id;
    }

    public String getCommandString() {
        return commandString;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public List<OptionMapping> getOptions() {
        return options;
    }

    public OptionMapping getOption(String name) {
        List<OptionMapping> allOptions = getOptions().stream().filter(opt -> opt.getName().equals(name)).toList();
        return allOptions.isEmpty() ? null : allOptions.getFirst();
    }

    public static Optional<String> searchThroughOptions(EventData event, String name) {
        return event.getCommand().getOptions().stream()
                .filter(option -> name.equals(option.getName()))
                .map(OptionMapping::getAsString)
                .findFirst();
    }
}
