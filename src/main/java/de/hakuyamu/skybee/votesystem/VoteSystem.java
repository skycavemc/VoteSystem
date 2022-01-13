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
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class VoteSystem extends JavaPlugin {

    public static final String PREFIX = "&a&l| &2Vote &8» ";
    private DBManager dbManager;

    @Override
    public void onEnable() {
        File dir = getDataFolder();
        if (!dir.isDirectory()) {
            dir.mkdirs();

            File start = new File(dir, "start_event.sh");
            try {
                start.createNewFile();
                FileWriter writer = new FileWriter(start);
                writer.write("screen -S skybee -X stuff \"voteadmin start\"\n");
                writer.write("screen -S skybee -X eval \"stuff \\015\"\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File stop = new File(dir, "stop_event.sh");
            try {
                stop.createNewFile();
                FileWriter writer = new FileWriter(stop);
                writer.write("screen -S skybee -X stuff \"voteadmin stop\"\n");
                writer.write("screen -S skybee -X eval \"stuff \\015\"\n");
                writer.flush();
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

        new VoteBroadcast(this).runTaskTimer(this, TimeUtil.minutesToTicks(10), TimeUtil.minutesToTicks(20));
        new VoteEventBroadcast(this).runTaskTimer(this, TimeUtil.minutesToTicks(20), TimeUtil.minutesToTicks(20));

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new IncomingVoteListener(), this);
        pm.registerEvents(new PlayerJoinListener(this), this);

        getCommand("voteadmin").setExecutor(new VoteAdminCommand(this));
        getCommand("vote").setExecutor(new VoteCommand(this));
    }

    @Override
    public void onDisable() {
        dbManager.disconnect();
    }

    public DBManager getDbManager() {
        return dbManager;
    }

}
