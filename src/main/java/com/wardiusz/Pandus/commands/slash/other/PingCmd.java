package com.wardiusz.Pandus.commands.slash.other;

import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import com.wardiusz.Pandus.Provider;
import net.dv8tion.jda.api.JDA;


public class PingCmd extends SlashExecutor {
    public final Provider provider;

    public PingCmd(Provider provider) {
        this.provider = provider;
    }
    String text = """
                   Pong!
                   Processing: `%sms`
                   Round-trip: `%sms`
                   """;

    @Override
    public void execute(EventData event) {
        JDA pong = event.getJda();
        pong.getRestPing().queue((ping) -> {
            event.getEvent()
                    .replyFormat(text,
                            ping, pong.getGatewayPing())
                    .queue();
        });

//        EmbedBuilder embed = new EmbedBuilder();
//        embed.setColor(EmbedOptions.NEUTRAL_COLOR);
//        embed.setAuthor(null);
//        embed.setTitle("Thanks for using Sidor!");
//        embed.setDescription("We're delighted that you choose Sidor over other existing bots! To start your journey with Sidor use /help to get a grip what commands you can send! Remember you can also use a custom commands and set a prefix you wanna use! For default is " + Config.get("PREFIX"));
//        event.getGuild().getOwner().getUser().openPrivateChannel().queue(pc -> pc.sendMessageEmbeds(embed.build()).queue());

//        Container container = Container.of(
//                // Displays content on the left and an "accessory" on the right.
//                Section.of(
//                        // A thumbnail, it should work with all image formats Discord supports.
//                        // You can make it a spoiler and also give it a description (alternative text)
//                        Thumbnail.fromUrl("https://cdn-icons-png.flaticon.com/512/1384/1384060.png"),
//                        // The section's children
//                        TextDisplay.of("## A container"),
//                        TextDisplay.of("Quite different from embeds"),
//                        TextDisplay.of("-# You can even put small text")),
//
//                // A separator; can be made invisible or be larger.
//                Separator.createDivider(Separator.Spacing.SMALL),
//
//                // Another section, note that you can have at most 3 children (excluding the accessory).
//                // You're always free to use newlines in your text displays,
//                // but keep in mind a new TextDisplay will display as a different paragraph.
//                Section.of(
//                        // For the sake of the example, this button will do nothing.
//                        Button.danger("feature_disable:moderation", "Disable moderation"),
//                        TextDisplay.of("**Moderation:** Moderates the messages"),
//                        TextDisplay.of("**Status:** Enabled")),
//                // A row of actionable components.
//                ActionRow.of(
//                                // For the sake of the example, this select menu will do nothing.
//                                StringSelectMenu.create("feature")
//                                        .setPlaceholder("Select a module to configure")
//                                        .addOption("Moderation", "moderation", "Configure the moderation module")
//                                        .addOption("Fun", "fun", "Configure the fun module")
//                                        .setDefaultValues("moderation")
//                                        .build())
//                        // Set an identifier, this may be useful to specifically remove this action row later
//                        .withUniqueId(42),
//
//                // Separate things a bit.
//                Separator.createDivider(Separator.Spacing.SMALL));

//                event.reply(container, true)
//                // This is required any time you are using Components V2
//                    .useComponentsV2()
//                    .queue();

//        event.getEvent().replyModal(Modal.create("modal", "My Modal")
//                .addComponents(
//                        Label.of("My Custom Label", TextInput.create("input", TextInputStyle.SHORT).build())
//                ).build()).queue();

//        TextInput subject = TextInput.create("subject", TextInputStyle.SHORT)
//                .setPlaceholder("Subject of this ticket")
//                .setMinLength(10)
//                .setMaxLength(100) // or setRequiredRange(10, 100)
//                .build();
//
//        TextInput body = TextInput.create("body", TextInputStyle.PARAGRAPH)
//                .setPlaceholder("Your concerns go here")
//                .setMinLength(30)
//                .setMaxLength(1000)
//                .build();
//
//        Modal modal = Modal.create("modmail", "Modmail")
//                .addComponents(Label.of("Subject", String.valueOf(subject), body))
//                .build();
//
//        event.getEvent().replyModal(modal).queue();
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Return websocket processing and round-trip time.";
    }
}
