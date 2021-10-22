package de.hakuyamu.skybee.votesystem;

import de.hakuyamu.skybee.votesystem.commands.VoteAdminCommand;
import de.hakuyamu.skybee.votesystem.commands.VoteCommand;
import de.hakuyamu.skybee.votesystem.enums.EventStartScript;
import de.hakuyamu.skybee.votesystem.enums.EventStopScript;
import de.hakuyamu.skybee.votesystem.listener.PlayerJoinListener;
import de.hakuyamu.skybee.votesystem.listener.VotifierListener;
import de.hakuyamu.skybee.votesystem.manager.DBManager;
import de.hakuyamu.skybee.votesystem.manager.DataManager;
import de.hakuyamu.skybee.votesystem.runnables.VoteBroadcast;
import de.hakuyamu.skybee.votesystem.runnables.VoteEventBroadcast;
import de.hakuyamu.skybee.votesystem.util.FileUtil;
import de.hakuyamu.skybee.votesystem.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class VoteSystem extends JavaPlugin {

    private DataManager dataManager;
    private DBManager dbManager;

    @Override
    public void onEnable() {
        createScripts();
        dataManager = new DataManager(this);
        dbManager = new DBManager();
        dbManager.setup();

        new VoteBroadcast(this).runTaskTimer(this, TimeUtil.minutesToTicks(10), TimeUtil.minutesToTicks(20));
        new VoteEventBroadcast(this).runTaskTimer(this, TimeUtil.minutesToTicks(20), TimeUtil.minutesToTicks(20));

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new VotifierListener(), this);
        pm.registerEvents(new PlayerJoinListener(this), this);

        getCommand("voteadmin").setExecutor(new VoteAdminCommand(this));
        getCommand("vote").setExecutor(new VoteCommand(this));
    }

    @Override
    public void onDisable() {
        dataManager.save();
        dbManager.shutdown();
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    private void createScripts() {
        String dir = "plugins/SkyBeeVoteSystem/scripts/";
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file1 = FileUtil.getFileIfExists(dir, "start_event.sh");
        File file2 = FileUtil.getFileIfExists(dir, "stop_event.sh");

        if (file1 == null) {
            getLogger().warning("Start script missing, creating a new one");
            try {
                FileWriter writer = new FileWriter(FileUtil.getFileAndCreate(dir, "start_event.sh"));
                for (EventStartScript line : EventStartScript.values()) {
                    writer.write(line.getLine() + "\n");
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (file2 == null) {
            getLogger().warning("Stop script missing, creating a new one");
            try {
                FileWriter writer = new FileWriter(FileUtil.getFileAndCreate(dir, "stop_event.sh"));
                for (EventStopScript line : EventStopScript.values()) {
                    writer.write(line.getLine() + "\n");
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
