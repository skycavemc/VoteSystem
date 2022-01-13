package de.hakuyamu.skybee.votesystem.runnables;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.hakuyamu.skybee.votesystem.enums.Message;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteEventBroadcast extends BukkitRunnable {

    private final MongoCollection<Document> collection;

    public VoteEventBroadcast(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        Document event = collection.find(Filters.exists("votes")).first();
        if (event == null || !event.getBoolean("started")) {
            return;
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(Message.VOTE_EVENT_BROADCAST.getString().get());
        Bukkit.broadcastMessage("");
    }

}
