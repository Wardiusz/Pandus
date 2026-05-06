package com.wardiusz.Pandus.Handler.Slash;

import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.modals.Modal;
//import net.dv8tion.jda.api.interactions.components.ActionRow;
//import net.dv8tion.jda.api.interactions.components.buttons.Button;
//import net.dv8tion.jda.api.interactions.components.text.TextInput;
//import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
//import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.List;
import java.util.Objects;

public class SlashManager extends ListenerAdapter {
// TODO: Make slash interactions and everything else with it connected/more applicable to existing classes/commends
    List<CommandData> commands;

    public List<CommandData> getCommands() {
        addSlashCommands("hellothere", "Say hello. 224342342");
        addSlashCommands("modmail", "Send your concerns/bugs to devs.");
        addSlashCommands("ban", "banuje chuja.");
        return commands;
    }

    private void addSlashCommands(String name, String description) {
        commands.add(Commands.slash(name, description));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("hellothere")) {
            event.replyModal(Modal.create("modal", "My Modal")
                    .addComponents(
                            Label.of("My Custom Label", TextInput.create("input", TextInputStyle.SHORT).build())
                    ).build()).queue();
        } else if (event.getName().equals("info")) {
            event.replyModal(// link buttons don't send events, they just open a link in the browser when clicked
                            Modal.create("modal", "My Modal")
                                    .addComponents(
                                            Label.of("My Custom Label",
                                                    StringSelectMenu.create("menu-custom-id")
                                                            .addOption("My option", "option-1")
                                                            .build()
                                            )
                                    )
                                    .build()
                    ) // Link Button with only a label
                    .queue();
        }

        if (event.getName().equals("modmail")) {
            TextInput subject = TextInput.create("subject", TextInputStyle.SHORT)
                    .setPlaceholder("Subject of this ticket")
                    .setMinLength(10)
                    .setMaxLength(100) // or setRequiredRange(10, 100)
                    .build();

            TextInput body = TextInput.create("body", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Your concerns go here")
                    .setMinLength(30)
                    .setMaxLength(1000)
                    .build();

            Modal modal = Modal.create("modmail", "Modmail")
                    .addComponents(Label.of("Subject", String.valueOf(subject), body))
                    .build();

            event.replyModal(modal).queue();
        }
    }
    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("modmail")) {
            String subject = Objects.requireNonNull(event.getValue("subject")).getAsString();
            String body = Objects.requireNonNull(event.getValue("body")).getAsString();

//            createSupportTicket(subject, body);

            event.reply("Thanks for your request!").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("hello")) {
            event.reply("Hello :)").queue(); // send a message in the channel
        } else if (event.getComponentId().equals("emoji")) {
            event.editMessage("That button didn't say click me").queue(); // update the message
        }
    }
}
