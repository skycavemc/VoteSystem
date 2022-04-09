package de.hakuyamu.skybee.votesystem.listener;

import com.mongodb.client.model.Filters;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.models.User;
import de.hakuyamu.skybee.votesystem.util.VoteUtils;
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
        User user = main.getUserCollection().find(filter).first();
        if (user == null) {
            return;
        }

        for (int i = 0; i < user.getQueuedVotes(); i++) {
            user.setVotes(user.getVotes() + 1);
            VoteUtils.giveVoteRewards(event.getPlayer());
        }
        user.setQueuedVotes(0);
        main.getUserCollection().replaceOne(filter, user);
    }

}
