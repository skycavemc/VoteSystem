package de.hakuyamu.skybee.votesystem.runnables;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.models.AutoSaveConfig;
import de.hakuyamu.skybee.votesystem.util.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteEventBroadcast extends BukkitRunnable {

    private final VoteSystem main;

    public VoteEventBroadcast(VoteSystem main) {
        this.main = main;
    }

    @Override
    public void run() {
        AutoSaveConfig event = main.getEventConfig();
        if (event == null || !event.getBoolean("started")) {
            return;
        }
        Utils.broadcast("");
        Utils.broadcast(Message.VOTE_EVENT_BROADCAST.getString().get());
        Utils.broadcast("");
    }

}
