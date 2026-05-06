package com.wardiusz.Pandus;

import club.minnced.discord.jdave.interop.JDaveSessionFactory;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.wardiusz.Pandus.commands.autocmd.*;
import com.wardiusz.Pandus.Handler.Config;
import com.wardiusz.Pandus.commands.prefix.EchoCmd;
import com.wardiusz.Pandus.Handler.HelpCmd;
import com.wardiusz.Pandus.Handler.IExecutor;
import com.wardiusz.Pandus.Handler.Prefix.PrefixCommands;
import com.wardiusz.Pandus.Handler.Prefix.PrefixExecutor;
import com.wardiusz.Pandus.Handler.Slash.SlashCommands;
import com.wardiusz.Pandus.Handler.Slash.SlashExecutor;
import com.wardiusz.Pandus.commands.DTO.ConfigOptions;
import com.wardiusz.Pandus.commands.DTO.DBGuilds;
import com.wardiusz.Pandus.commands.prefix.ShutdownCmd;
import com.wardiusz.Pandus.commands.slash.administrative.*;
import com.wardiusz.Pandus.commands.slash.music.*;
import com.wardiusz.Pandus.commands.slash.other.PingCmd;
import com.wardiusz.Pandus.commands.slash.administrative.SetGoodbyeMsgCmd;
import com.wardiusz.Pandus.commands.slash.administrative.SetWelcomeMsgCmd;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class Provider {
    private final List<GatewayIntent> gatewayIntents = new ArrayList<>();
    private final List<CacheFlag> enabledCacheFlags = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(Provider.class);
    private final Map<String, IExecutor> executorMap = new HashMap<>();
    public static JDA jda;
    private final JDABuilder jdaBuilder;
    private final EventWaiter eventWaiter;
    private PrefixCommands prefixCommands;
    private final SlashCommands slashCommands;


    private final MemberCachePolicy cachePolicy = MemberCachePolicy.ALL;
    private final OnlineStatus status = OnlineStatus.ONLINE;
    private final boolean isMusicBotEnabled = Boolean.parseBoolean(ConfigOptions.MUSIC_BOT.getValue());

    private static DBGuilds[] dbGuilds;

    public Provider() {
        loadIntents();
        loadCacheFlags();

        this.slashCommands = new SlashCommands(this);
        this.eventWaiter = new EventWaiter();

        boolean usePrefixCmd = Boolean.parseBoolean(ConfigOptions.PREFIX_COMMANDS.getValue());

        if (ConfigOptions.TOKEN.getValue().isEmpty()) {
            logger.error("Token is invalid, please enter a valid token inside the config file.");
            System.exit(-1);
        }

        jdaBuilder = JDABuilder.createDefault(Config.get("TOKEN"))
                .setAudioModuleConfig(new AudioModuleConfig()
                        .withDaveSessionFactory(new JDaveSessionFactory())
                );

        if (usePrefixCmd) {
            this.prefixCommands = new PrefixCommands(this);
            jdaBuilder.addEventListeners(prefixCommands);
        }

        jdaBuilder.addEventListeners(slashCommands, eventWaiter);
    }

    public JDA buildJDA() throws InterruptedException {
        jdaBuilder.setEnabledIntents(gatewayIntents);
        jdaBuilder.enableCache(enabledCacheFlags);
        jdaBuilder.setMemberCachePolicy(cachePolicy);
        jdaBuilder.setStatus(status);

        jda = jdaBuilder
                .build()
                .awaitReady();

        logger.info("{}\u001B[7m Loading Pandus v1.0.0 \u001B[0m{}", "-".repeat(30), "-".repeat(30));

        if (isMusicBotEnabled) {
            enableMusicBot();
        }

        logger.info("Music mode is {}", isMusicBotEnabled ? "\u001B[92mENABLED\u001B[0m" : "\u001B[31mDISABLED\u001B[0m");

        updateCommands();
        logRegisteredListeners();
        logCurrentExecutors();

        loadGuildProperties();
        return jda;
    }

    public static void loadGuildProperties() {
        dbGuilds = new DBGuilds[jda.getGuilds().size()];

        for (int index = 0; index < jda.getGuilds().size(); index++) {
            Guild jdaGuild = jda.getGuilds().get(index);
            DBGuilds guild = new DBGuilds(jdaGuild.getId());
            dbGuilds[index] = guild;
        }
    }

    private void loadIntents() {
        gatewayIntents.addAll(List.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MODERATION,
                GatewayIntent.GUILD_EXPRESSIONS,
                GatewayIntent.SCHEDULED_EVENTS)
        );
    }

    public List<GatewayIntent> getGatewayIntents() {
        return gatewayIntents;
    }

    public Provider addGatewayIntents(GatewayIntent... intents) {
        this.getGatewayIntents().addAll(List.of(intents));
        return this;
    }

    private void loadCacheFlags() {
        enabledCacheFlags.addAll(List.of(
                CacheFlag.MEMBER_OVERRIDES,
                CacheFlag.VOICE_STATE)
        );
    }

    private void updateCommands() {
        List<SlashCommandData> commands = new ArrayList<>();
        getExecutors().forEach((name, executor) -> {
            if(executor instanceof SlashExecutor executor1) {
                executor1.updateOptions();
                commands.add(Commands.slash(name, executor1.getDescription()).addOptions(executor1.getOptions()));
            }
            executor.updateAliases();
            executor.updateAuthorizedChannels(jda);
            executor.updateAuthorizedRoles(jda);
            executor.updateAuthorizedPermissions(jda);
        });
        jda.updateCommands().addCommands(commands).queue();
    }

    public List<CacheFlag> getEnabledCacheFlags() {
        return enabledCacheFlags;
    }

    public Provider addEnabledCacheFlags(CacheFlag... flags) {
        this.getEnabledCacheFlags().addAll(List.of(flags));
        return this;
    }

    public PrefixCommands getPrefixCommands() {
        return prefixCommands;
    }

    public SlashCommands getSlashCommands() {
        return slashCommands;
    }

    public Provider registerListeners(ListenerAdapter... listeners) {

        if(List.of(listeners).isEmpty()) {
            return this;
        }

        for (Object listener : listeners) {
            jdaBuilder.addEventListeners(listener);
        }

        return this;
    }

    public Provider addExecutor(IExecutor... executors) {
        for (IExecutor executor : executors) {
            if(executor.getName() == null || executor.getName().isEmpty()) {
                logger.warn("Command: '{}' doesn't have a name and could cause errors.", executor.getClass().getSimpleName());
            }

            if(executor.getDescription() == null || executor.getDescription().isEmpty()) {
                logger.warn("Command: '{}' doesn't have a description.", executor.getClass().getName());
            }

            this.executorMap.put(executor.getName(), executor);

            if(executor.getAliases() != null && !executor.getAliases().isEmpty()) {
                for (String alias : executor.getAliases()) {
                    if(alias.isEmpty()) {
                        logger.warn("Alias: '{}' doesn't have a name and could cause errors.", executor.getClass().getSimpleName());
                    }
                    this.executorMap.put(alias, executor);
                }
            }
        }
        return this;
    }
    private void enableMusicBot() {
        this.addExecutor(
                new PlayCmd(this),
                new SkipCmd(this),
                new PauseCmd(this),
                new ResumeCmd(this),
                new JumpCmd(this),
                new JoinCmd(this),
                new LeaveCmd(this),
                new NowPlayingCmd(this),
                new QueueListCmd(this),
                new ClearCmd(this)
        );
    }

    public EventWaiter getWaiter() {
        return eventWaiter;
    }

    public Logger getLogger() {
        return logger;
    }

    public Map<String, IExecutor> getExecutors() {
        return executorMap;
    }

    JDA setupJDA() throws InterruptedException {
        return registerListeners(
                new LogProviderListener(),
                new AutoCmdListener(this)
        ).addExecutor(
                new AutoRoleCmd(this),
                new ShutdownCmd(),
                new EchoCmd(),
                new SetGoodbyeMsgCmd(this),
                new SetWelcomeMsgCmd(this),
                new HelpCmd(this),
                new PingCmd(this),
                new SetMusicChannelCmd(this),
                new SetLogChannelCmd(this),
                new BanCmd(this),
                new UnBanCmd(this),
                new KickCmd(this),
                new WarnCmd(this),
                new CleanCmd(this),
                new MuteCmd(this),
                new UnMuteCmd(this)

        ).addEnabledCacheFlags().addGatewayIntents().buildJDA();
    }
    private void logCurrentExecutors() {
        List<Command> commands = jda.retrieveCommands().complete();
        logger.info("{}\u001B[34m Logging registered Executors \u001B[0m{}", "-".repeat(5), "-".repeat(5));
        logger.info("- Slash: {}", commands.stream()
                .map(s -> "/" + s.getName() + ":" + s.getId())
                .toList());


        List<String> temp = new ArrayList<>();
            getExecutors().forEach((s, iExecutor) -> {
                if(iExecutor instanceof PrefixExecutor) {
                    if(!iExecutor.getAliases().contains(s)) {
                        temp.add(getPrefixCommands().getPrefix() + s);
                    }
                }
            });
        logger.info("- Prefix: {}", temp);
    }

    private void logRegisteredListeners() {
        logger.info("{}\u001B[34m Logging registered Listeners \u001B[0m{}", "-".repeat(5), "-".repeat(5));
        logger.info("{}", jda.getRegisteredListeners()
                .stream()
                .map(s -> s.getClass().getSimpleName())
                .toList());
    }

    public static DBGuilds[] getDbGuilds() {
        return dbGuilds;
    }
}
