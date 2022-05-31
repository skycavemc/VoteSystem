package de.hakuyamu.skybee.votesystem.commands;

import com.mongodb.client.model.Filters;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.models.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VCCommand implements CommandExecutor {

    private final VoteSystem main;

    public VCCommand(VoteSystem main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length < 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Message.PLAYER_ONLY.getString().get(false));
                return true;
            }
            User user = main.getUserCollection().find(Filters.eq("uuid", player.getUniqueId())).first();
            long vc = 0;
            if (user != null) {
                vc = user.getVoteCoins();
            }
            player.sendMessage(Message.VC_INFO.getString().replace("%amount", "" + vc).get());
            return true;
        }

        OfflinePlayer other = Bukkit.getOfflinePlayerIfCached(args[0]);
        if (other == null) {
            sender.sendMessage(Message.PLAYER_NOT_FOUND.getString().replace("%player", args[0]).get());
            return true;
        }
        User user = main.getUserCollection().find(Filters.eq("uuid", other.getUniqueId())).first();
        long vc = 0;
        if (user != null) {
            vc = user.getVoteCoins();
        }
        sender.sendMessage(Message.VC_INFO_OTHER.getString()
                .replace("%player", other.getName())
                .replace("%amount", "" + vc).get());
        return true;
    }

}
