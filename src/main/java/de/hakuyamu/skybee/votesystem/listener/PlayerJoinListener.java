package de.hakuyamu.skybee.votesystem.listener;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.hakuyamu.skybee.votesystem.util.VoteUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final JavaPlugin plugin;
    private final MongoCollection<Document> usersCollection;

    public PlayerJoinListener(JavaPlugin plugin, MongoCollection<Document> usersCollection) {
        this.plugin = plugin;
        this.usersCollection = usersCollection;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Bson filter = Filters.eq("uuid", uuid.toString());
            Document user = usersCollection.find(filter).first();
            if (user == null) {
                return;
            }

            Long queuedVotes = user.getLong("queuedVotes");
            for (int i = 0; i < queuedVotes; i++) {
                VoteUtil.giveVoteRewards(event.getPlayer(), user.getLong("votes") + i + 1);
            }

            usersCollection.updateOne(filter,
                    Updates.combine(Updates.set("queuedVotes", 0L), // Reset queued votes
                            Updates.inc("votes", queuedVotes))); // Increase by votes in queue
        });
    }

}
