package de.hakuyamu.skybee.votesystem;

import com.mongodb.client.MongoCollection;
import de.hakuyamu.skybee.votesystem.commands.VoteAdminCommand;
import de.hakuyamu.skybee.votesystem.commands.VoteCommand;
import de.hakuyamu.skybee.votesystem.listener.IncomingVoteListener;
import de.hakuyamu.skybee.votesystem.listener.PlayerJoinListener;
import de.hakuyamu.skybee.votesystem.manager.DBManager;
import de.hakuyamu.skybee.votesystem.runnables.VoteBroadcast;
import de.hakuyamu.skybee.votesystem.runnables.VoteEventBroadcast;
import de.hakuyamu.skybee.votesystem.util.EventAdaptor;
import de.hakuyamu.skybee.votesystem.util.TimeUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoteSystem extends JavaPlugin {

    public static final String PREFIX = "&a&l| &2Vote &8» ";
    private DBManager dbManager;

    @Override
    public void onEnable() {
        File dir = getDataFolder();
        if (!dir.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();

            try {
                extractFile(dir, "start_event.sh");
                extractFile(dir, "stop_event.sh");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dbManager = new DBManager();
        dbManager.connect();

        MongoCollection<Document> eventDocs = dbManager.getDatabase().getCollection("event");
        if (eventDocs.countDocuments() < 1) {
            eventDocs.insertOne(EventAdaptor.generateNewEvent());
        }

        new VoteBroadcast(this).runTaskTimer(this, TimeUtil.minutesToTicks(10),
            TimeUtil.minutesToTicks(20));
        new VoteEventBroadcast(this).runTaskTimer(this, TimeUtil.minutesToTicks(20),
            TimeUtil.minutesToTicks(20));

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new IncomingVoteListener(), this);
        pm.registerEvents(new PlayerJoinListener(this), this);

        PluginCommand command = getCommand("voteadmin");
        if (command != null) {
            command.setExecutor(new VoteAdminCommand(this));
        }
        command = getCommand("vote");
        if(command != null) {
            command.setExecutor(new VoteCommand(this));
        }
    }

    private void extractFile(File dir, String resource) throws IOException {
        File start = new File(dir, resource);
        if (start.isFile()) {
            return;
        }
        InputStream stream = getResource(start.getName());
        if (stream == null) {
            getLogger().warning(String.format("Missing \"%s\" file in resources.", resource));
            return;
        }
        Files.copy(stream, start.toPath());
    }

    @Override
    public void onDisable() {
        dbManager.disconnect();
    }

    public DBManager getDbManager() {
        return dbManager;
    }

}
