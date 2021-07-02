package de.hakuyamu.skybee.votesystem.runnables;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteEventBroadcast extends BukkitRunnable {

    private final VoteSystem main;

    public VoteEventBroadcast(VoteSystem main) {
        this.main = main;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        if (main.getDataManager().getEvent().isStarted()) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(Message.VOTE_EVENT_BROADCAST.getWithPrefix());
            Bukkit.broadcastMessage("");
        }
    }

}
