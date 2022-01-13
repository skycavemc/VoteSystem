package de.hakuyamu.skybee.votesystem.runnables;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.hakuyamu.skybee.votesystem.enums.Message;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteBroadcast extends BukkitRunnable {

    private final MongoCollection<Document> collection;

    public VoteBroadcast(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public void run() {

        List<UUID> uuidList = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId)
            .collect(Collectors.toList());

        FindIterable<Document> users = collection.find(Filters.all("uuid", uuidList));
        for (Document user : users) {
            UUID uuid = user.get("uuid", UUID.class);
            uuidList.remove(uuid);

            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }

            LocalDate lastVoteDate = LocalDate.parse((String) user.get("lastVoteDate"));
            if (lastVoteDate == null || lastVoteDate.compareTo(LocalDate.now()) == 0) {
                continue;
            }

            player.sendMessage(Message.VOTE_BROADCAST.getString()
                .replace("%votes", String.valueOf(user.get("votes"))).get());
        }

        for (UUID uuid : uuidList) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            player.sendMessage(Message.VOTE_BROADCAST.getString().replace("%votes", "0").get());
        }
    }

}
