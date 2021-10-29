package de.hakuyamu.skybee.votesystem.runnables;

import com.mongodb.client.model.Filters;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import org.bson.Document;
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
        Document event = main.getDbManager().getDatabase().getCollection("event")
                .find(Filters.exists("votes")).first();
        if (event == null || !Boolean.parseBoolean((String) event.get("started"))) {
            return;
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(Message.VOTE_EVENT_BROADCAST.getString().get());
        Bukkit.broadcastMessage("");
    }

}
