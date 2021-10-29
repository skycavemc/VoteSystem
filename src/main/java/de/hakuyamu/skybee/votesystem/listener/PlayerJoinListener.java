package de.hakuyamu.skybee.votesystem.listener;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.util.VoteUtil;
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
        MongoDatabase db = main.getDbManager().getDatabase();
        Document user = db.getCollection("users").find(filter).first();
        if (user == null) {
            return;
        }

        long votes = (Long) user.get("votes");
        for (int i = 0; i < (Long) user.get("queuedVotes"); i++) {
            votes += 1L;
            db.getCollection("users").updateOne(filter, Updates.set("votes", votes));
            VoteUtil.giveVoteRewards(event.getPlayer());
        }

        db.getCollection("users").updateOne(filter, Updates.set("queuedVotes", 0L));
    }

}
