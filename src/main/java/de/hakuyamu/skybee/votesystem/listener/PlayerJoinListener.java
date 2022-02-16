package de.hakuyamu.skybee.votesystem.listener;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.util.VoteUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final VoteSystem main;

    public PlayerJoinListener(VoteSystem main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Bson filter = Filters.eq("uuid", uuid.toString());
        MongoCollection<Document> userCollection = main.getUserCollection();
        Document user = userCollection.find(filter).first();
        if (user == null) {
            return;
        }

        long votes = (Long) user.get("votes");
        for (int i = 0; i < (Long) user.get("queuedVotes"); i++) {
            votes += 1L;
            userCollection.updateOne(filter, Updates.set("votes", votes));
            VoteUtils.giveVoteRewards(event.getPlayer());
        }

        userCollection.updateOne(filter, Updates.set("queuedVotes", 0L));
    }

}
