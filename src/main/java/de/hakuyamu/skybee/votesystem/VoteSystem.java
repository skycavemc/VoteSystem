package de.hakuyamu.skybee.votesystem;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.lang.Nullable;
import de.hakuyamu.skybee.votesystem.commands.VoteAdminCommand;
import de.hakuyamu.skybee.votesystem.commands.VoteCommand;
import de.hakuyamu.skybee.votesystem.listener.IncomingVoteListener;
import de.hakuyamu.skybee.votesystem.listener.PlayerJoinListener;
import de.hakuyamu.skybee.votesystem.runnables.VoteBroadcast;
import de.hakuyamu.skybee.votesystem.runnables.VoteEventBroadcast;
import de.hakuyamu.skybee.votesystem.util.Utils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class VoteSystem extends JavaPlugin {

    public static final String PREFIX = "&a&l| &2Vote &8» ";
    private MongoClient mongoClient;
    private MongoDatabase database;
    private YamlConfiguration eventConfig;

    @Override
    public void onEnable() {
        File dir = getDataFolder();
        if (!dir.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            Utils.extractPluginResource(this, "start_event.sh");
            Utils.extractPluginResource(this, "stop_event.sh");
        }

        if (Utils.extractPluginResource(this, "event.yml")) {
            eventConfig = YamlConfiguration.loadConfiguration(new File(dir, "event.yml"));
        }

        mongoClient = MongoClients.create();
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

    public MongoCollection<Document> getUserCollection() {
        return database.getCollection("users");
    }

    @Nullable
    public YamlConfiguration getEventConfig() {
        return eventConfig;
    }
}
