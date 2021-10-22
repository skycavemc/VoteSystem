package de.hakuyamu.skybee.votesystem.listener;

import com.mongodb.client.model.Filters;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.manager.DBManager;
import de.hakuyamu.skybee.votesystem.models.User;
import de.hakuyamu.skybee.votesystem.util.VoteUtil;
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

        DBManager dbManager = main.getDbManager();
        dbManager.getUsers(Filters.eq("uuid", uuid.toString()));
        // if the user is not registered, there is nothing to do expect from registering them
        if (!main.getDataManager().isRegistered(uuid)) {
            main.getDataManager().createUser(uuid);
            return;
        }

        // give missed vote rewards due to offline voting
        User user = main.getDataManager().getUser(uuid);
        for (int i = 0; i < user.getQueuedVotes(); i++) {
            VoteUtil.vote(event.getPlayer());
        }

        // there should be no votes left in the queue
        user.setQueuedVotes(0);
    }

}
