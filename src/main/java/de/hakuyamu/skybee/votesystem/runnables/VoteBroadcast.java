package de.hakuyamu.skybee.votesystem.runnables;

import com.mongodb.client.model.Filters;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDate;

public class VoteBroadcast extends BukkitRunnable {

    private final VoteSystem main;

    public VoteBroadcast(VoteSystem main) {
        this.main = main;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Bson filter = Filters.eq("uuid", player.getUniqueId());
            Document user = main.getUserCollection().find(filter).first();

            if (user == null) {
                player.sendMessage(Message.VOTE_BROADCAST.getString()
                        .replace("%votes", "0").get());
                continue;
            }

            LocalDate lastVoteDate = LocalDate.parse((String) user.get("lastVoteDate"));
            if (lastVoteDate == null || lastVoteDate.compareTo(LocalDate.now()) == 0) {
                continue;
            }

            player.sendMessage(Message.VOTE_BROADCAST.getString()
                    .replace("%votes", String.valueOf(user.get("votes"))).get());
        }
    }

}
