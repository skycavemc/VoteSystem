package de.hakuyamu.skybee.votesystem.runnables;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.models.User;
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
            if (!main.getDataManager().isRegistered(player.getUniqueId())) {
                player.sendMessage(Message.VOTE_BROADCAST.getWithPrefix().replaceAll("%votes", "0"));
                continue;
            }

            User user = main.getDataManager().getUser(player.getUniqueId());
            LocalDate lastVoteDate = user.getLastVoteDate();
            if (lastVoteDate == null || lastVoteDate.compareTo(LocalDate.now()) == 0) {
                continue;
            }

            player.sendMessage(Message.VOTE_BROADCAST.getWithPrefix().replaceAll("%votes", String.valueOf(user.getVotes())));
        }
    }

}
