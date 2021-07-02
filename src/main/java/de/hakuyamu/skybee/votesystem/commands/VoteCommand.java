package de.hakuyamu.skybee.votesystem.commands;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.EventReward;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.enums.PersonalReward;
import de.hakuyamu.skybee.votesystem.enums.TrustedServices;
import de.hakuyamu.skybee.votesystem.util.VoteUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class VoteCommand implements CommandExecutor, TabCompleter {

    private final VoteSystem main;

    public VoteCommand(VoteSystem main) {
        this.main = main;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            main.getLogger().severe("This command is for players only.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("");
            player.sendMessage(TrustedServices.MINECRAFT_SERVER_EU.getBaseComponent(player.getName()));
            player.sendMessage(TrustedServices.MINECRAFT_SERVERLIST_NET.getBaseComponent(player.getName()));
            player.sendMessage(TrustedServices.SERVERLISTE_NET.getBaseComponent(player.getName()));
            player.sendMessage(Message.VOTE_INFO1.getMessage());
            player.sendMessage(Message.VOTE_INFO2.getMessage());
            player.sendMessage(Message.VOTE_INFO3.getMessage());
            player.sendMessage("");
            return true;
        }

        switch (args[0]) {
            case "event":
                player.sendMessage("");
                player.sendMessage(VoteUtil.getVoteEventStatus());
                player.sendMessage("");
                for (EventReward reward : EventReward.values()) {
                    player.sendMessage(VoteUtil.getVoteEventLine(reward));
                }
                break;
            case "ziel":
                UUID uuid = player.getUniqueId();
                sender.sendMessage("");
                sender.sendMessage(VoteUtil.getVoteZielStatus(uuid));
                sender.sendMessage("");
                for (PersonalReward reward : PersonalReward.values()) {
                    sender.sendMessage(VoteUtil.getVoteZielLine(uuid, reward));
                }
                break;
            case "help":
                sender.sendMessage(Message.VOTE_HELP.getMessage());
                sender.sendMessage(Message.VOTE_HELP_EVENT.getMessage());
                sender.sendMessage(Message.VOTE_HELP_ZIEL.getMessage());
                break;
            default:
                sender.sendMessage(Message.VOTE_WRONG_ARGS.getWithPrefix());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            arguments.add("help");
            arguments.add("event");
            arguments.add("ziel");

            StringUtil.copyPartialMatches(args[0], arguments, completions);
        }

        Collections.sort(completions);
        return completions;
    }

}
