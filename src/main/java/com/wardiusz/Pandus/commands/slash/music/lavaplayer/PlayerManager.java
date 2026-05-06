package com.wardiusz.Pandus.commands.slash.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wardiusz.Pandus.Handler.Config;
import com.wardiusz.Pandus.Handler.EventData;
import com.wardiusz.Pandus.Provider;
import com.wardiusz.Pandus.commands.embeds.EmbedOptions;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;

import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wardiusz.Pandus.commands.slash.music.handlers.AutoDisconnectEvent.startAudioTimerIfNotPlaying;


public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagerMap;
    private final AudioPlayerManager audioPlayerManager;
    private final Provider provider;
    private final int max = 5;
    List<RestAction<Void>> a = new ArrayList<>();

    public PlayerManager(Provider provider) {
        this.musicManagerMap = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.provider = provider;

        YoutubeAudioSourceManager yt = new YoutubeAudioSourceManager(
                true,
                        new MusicWithThumbnail(),
//                        new WebWithThumbnail(),
//                        new IosWithThumbnail(),
//                        new AndroidMusicWithThumbnail(),
//                        new TvHtml5EmbeddedWithThumbnail(),
                        new AndroidVrWithThumbnail()
        );

//        SpotifySourceManager sp = new SpotifySourceManager(null, "", "", "00-001", audioPlayerManager);

        yt.useOauth2(Config.get("YT_TOKEN"), true);

        this.audioPlayerManager.registerSourceManager(yt/*, sp*/);

//        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
//        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagerMap.computeIfAbsent(Long.valueOf(guild.getIdLong()), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, guild);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, EventData event, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            final EmbedBuilder embed = new EmbedBuilder();

            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);

                event.getHook().editOriginalComponents(Container.of(
                        Section.of(
                            Thumbnail.fromUrl(track.getInfo().artworkUrl),
                            TextDisplay.of("## Adding to queue"),
                            TextDisplay.of(" **Title:** " + track.getInfo().title),
                            TextDisplay.of("-# **Position:** " + (musicManager.scheduler.queue.size() + 1))
                        ))
                ).useComponentsV2().queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                try {
                    if (trackUrl.startsWith("ytsearch:")) {
                        List<AudioTrack> audioTracks = playlist.getTracks().stream().limit(max).toList();

                        event.getHook().editOriginalComponents(
                                buildSearchResultContainer("YouTube",
                                        audioTracks,
                                        event.getCommand().getOptions().getFirst().getAsString(),
                                        false)
                                )
                                .useComponentsV2()
                                .queue(message -> provider.getWaiter()
                                        .waitForEvent(
                                        ButtonInteractionEvent.class,
                                        e -> {
                                            if (!e.getMessageId().equals(message.getId())) return false;
                                            if (e.getUser().isBot()) return false;
                                            return e.getUser().getIdLong() == event.getCommandSender().getIdLong();
                                        },
                                        e -> {
                                            String buttonId = e.getComponentId();
                                            int selectedIndex = Integer.parseInt(buttonId.split("_")[1]);

                                            if (selectedIndex >= 0 && selectedIndex < audioTracks.size()) {
                                                e.deferEdit().queue();
                                                trackLoaded(audioTracks.get(selectedIndex));
                                            }
                                        },
                                        30, TimeUnit.SECONDS,
                                        () -> message.editMessageComponents(
                                                        buildSearchResultContainer("YouTube",
                                                                audioTracks,
                                                                event.getCommand().getOptions().getFirst().getAsString(),
                                                                true)
                                                )
                                                .useComponentsV2()
                                                .queue()
                                        )
                                );

                        return;
                    } else if (trackUrl.startsWith("spsearch:")) {

                    }

                    final List<AudioTrack> tracks = playlist.getTracks();
                    AudioTrack firstTrack = playlist.getSelectedTrack();

                    if (firstTrack == null) {
                        firstTrack = playlist.getTracks().getFirst();
                    }

                    event.getHook().editOriginalComponents(Container.of(
                            Section.of(
                                    Thumbnail.fromUrl(firstTrack.getInfo().artworkUrl),
                                    TextDisplay.of("## Adding a playlist"),
                                    TextDisplay.of(" **Title:** " + firstTrack.getInfo().title),
                                    TextDisplay.of("-# **Size:** " + (tracks.size()))
                            ))
                    ).useComponentsV2().queue();

                    for (final AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);
                    }
                } catch (FriendlyException _) {}
            }

            @Override
            public void noMatches() {
                embed.setColor(EmbedOptions.ERROR_COLOR);
                embed.setDescription("No matches found.");
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                startAudioTimerIfNotPlaying(event.getGuild(), false);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                embed.setColor(EmbedOptions.ERROR_COLOR);
                embed.setDescription("Problem with loading track has occurred. Try again. " + exception.getMessage());
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                startAudioTimerIfNotPlaying(event.getGuild(), false);
            }

        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager(null);
        }
        return INSTANCE;
    }

    public static PlayerManager getInstance(Provider provider) {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager(provider);
        }
        return INSTANCE;
    }

    private Container buildSearchResultContainer(String platform, List<AudioTrack> audioTracks, String searchPhrase, boolean disabled) {
        List<ContainerChildComponent> components = new ArrayList<>();
        String platformIcon = platform.equals("YouTube") ? "https://cdn-icons-png.flaticon.com/512/1384/1384060.png" : platform.equals("Spotify") ? "https://cdn-icons-png.flaticon.com/512/174/174872.png" : "https://cdn-icons-png.flaticon.com/512/10309/10309127.png";

        components.add(Section.of(
                Thumbnail.fromUrl(platformIcon),
                TextDisplay.of("## " + platform + " search results"),
                TextDisplay.of("Search phrase: " + searchPhrase),
                TextDisplay.of("-# **Choose which music you want to play using the buttons below.**")
        ));

        components.add(Separator.createDivider(Separator.Spacing.SMALL));

        AtomicInteger index = new AtomicInteger(0);

        audioTracks.forEach(track -> {
            Button button = Button.secondary("track_" + index.get(), "Play");
            if (disabled) {
                button = button.asDisabled();
            }

            components.add(Section.of(
                    button,
                    TextDisplay.of("**" + (index.getAndIncrement() + 1) + ".** " + track.getInfo().title + " **by** " + track.getInfo().author),
                    TextDisplay.of("-# Duration: " + formatTime(track.getInfo().length))
            ));
        });

        return Container.of(components);
    }

    private String formatTime(long millis) {
        long seconds = millis/1000;
        long minutes = seconds/60;
        long hours = minutes/60;
        seconds %= 60;
        minutes %= 60;
        if (hours != 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}