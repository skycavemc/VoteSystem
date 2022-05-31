package de.hakuyamu.skybee.votesystem;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.lang.Nullable;
import de.hakuyamu.skybee.votesystem.commands.VCCommand;
import de.hakuyamu.skybee.votesystem.commands.VCShopCommand;
import de.hakuyamu.skybee.votesystem.commands.VoteAdminCommand;
import de.hakuyamu.skybee.votesystem.commands.VoteCommand;
import de.hakuyamu.skybee.votesystem.listener.IncomingVoteListener;
import de.hakuyamu.skybee.votesystem.listener.PlayerJoinListener;
import de.hakuyamu.skybee.votesystem.models.AutoSaveConfig;
import de.hakuyamu.skybee.votesystem.models.User;
import de.hakuyamu.skybee.votesystem.runnables.VoteBroadcast;
import de.hakuyamu.skybee.votesystem.runnables.VoteEventBroadcast;
import de.hakuyamu.skybee.votesystem.util.Utils;
import de.hakuyamu.skybee.votesystem.util.VoteUtils;
import de.leonheuer.mcguiapi.gui.GUIFactory;
import net.milkbowl.vault.economy.Economy;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class VoteSystem extends JavaPlugin {

    public static final String PREFIX = "&a&l| &2Vote &8» ";
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private MongoClient mongoClient;
    private MongoDatabase database;
    private AutoSaveConfig eventConfig;
    private GUIFactory guiFactory;
    private Economy economy;

    @Override
    public void onEnable() {
        reloadResources();
        guiFactory = new GUIFactory(this);
        if (!setupEconomy()) {
            getServer().getLogger().severe("Vault Dependency nicht gefunden, deaktiviere Plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(pojoCodecProvider)
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .codecRegistry(pojoCodecRegistry)
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("sb_vote_system");

        new VoteBroadcast(this).runTaskTimer(this, Utils.minutesToTicks(10),
                Utils.minutesToTicks(20));
        new VoteEventBroadcast(this).runTaskTimer(this, Utils.minutesToTicks(20),
                Utils.minutesToTicks(20));

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new IncomingVoteListener(), this);
        pm.registerEvents(new PlayerJoinListener(this), this);

        registerCommand("voteadmin", new VoteAdminCommand(this));
        registerCommand("vote", new VoteCommand(this));
        registerCommand("vcshop", new VCShopCommand(this));
        registerCommand("vc", new VCCommand(this));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public void reloadResources() {
        File dir = getDataFolder();
        if (!dir.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }

        if (Utils.extractPluginResource(this, "event.yml")) {
            eventConfig = new AutoSaveConfig(new File(dir, "event.yml"));
            if (eventConfig.getBoolean("started")) {
                LocalDateTime stop = LocalDateTime.now()
                        .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);
                long stopDelay = stop.toInstant(ZoneOffset.ofHours(1)).toEpochMilli() - System.currentTimeMillis();
                executorService.schedule(VoteUtils::stopEvent, stopDelay, TimeUnit.MILLISECONDS);
                getLogger().info("Event is running, event END scheduled at: " + stop.format(VoteUtils.DTF));
                return;
            }

            String scheduled = eventConfig.getString("next-event");
            LocalDateTime next;
            if (scheduled == null) {
                next = LocalDateTime.now()
                        .with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
                        .withHour(8)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);
            } else if ((next = LocalDateTime.parse(scheduled)).isBefore(LocalDateTime.now())) {
                next = LocalDateTime.now()
                        .with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
                        .with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
                        .withHour(8)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);
            }
            long startDelay = next.toInstant(ZoneOffset.ofHours(1)).toEpochMilli() - System.currentTimeMillis();
            executorService.schedule(VoteUtils::startEvent, startDelay, TimeUnit.MILLISECONDS);
            getLogger().info("Next event START scheduled at: " + next.format(VoteUtils.DTF));

            LocalDateTime stop = next.plusDays(3).withHour(0);
            long stopDelay = stop.toInstant(ZoneOffset.ofHours(1)).toEpochMilli() - System.currentTimeMillis();
            executorService.schedule(VoteUtils::stopEvent, stopDelay, TimeUnit.MILLISECONDS);
            getLogger().info("Next event END scheduled at: " + stop.format(VoteUtils.DTF));

            eventConfig.set("next-event", next.toString());
        }
    }

    private void registerCommand(String command, CommandExecutor executor) {
        PluginCommand cmd = getCommand(command);
        if (cmd == null) {
            getLogger().severe("No entry for command " + command + " found in the plugin.yml.");
            return;
        }
        cmd.setExecutor(executor);
    }

    @Override
    public void onDisable() {
        mongoClient.close();
    }

    public @NotNull MongoCollection<User> getUserCollection() {
        return database.getCollection("users", User.class);
    }

    @Nullable
    public AutoSaveConfig getEventConfig() {
        return eventConfig;
    }

    public GUIFactory getGuiFactory() {
        return guiFactory;
    }

    public Economy getEconomy() {
        return economy;
    }
}
