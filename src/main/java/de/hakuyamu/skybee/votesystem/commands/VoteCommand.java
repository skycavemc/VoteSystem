package de.hakuyamu.skybee.votesystem.commands;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.EventReward;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.enums.PersonalReward;
import de.hakuyamu.skybee.votesystem.enums.TrustedServices;
import de.hakuyamu.skybee.votesystem.util.VoteUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
            sender.sendMessage("");
            sender.sendMessage(TrustedServices.MINECRAFT_SERVER_EU.getVoteLink(player.getName()));
            sender.sendMessage(TrustedServices.MINECRAFT_SERVERLIST_NET.getVoteLink(player.getName()));
            sender.sendMessage(TrustedServices.SERVERLISTE_NET.getVoteLink(player.getName()));
            sender.sendMessage(Message.VOTE_INFO1.getString().get(false));
            sender.sendMessage(Message.VOTE_INFO2.getString().get(false));
            sender.sendMessage(Message.VOTE_INFO3.getString().get(false));
            sender.sendMessage("");
            return true;
        }

        MongoCollection<Document> userCollection = main.getDbManager().getDatabase().getCollection("users");

        switch (args[0]) {
            case "event":
                sender.sendMessage("");
                sender.sendMessage(VoteUtil.getVoteEventStatus());
                sender.sendMessage("");
                for (EventReward reward : EventReward.values()) {
                    sender.sendMessage(VoteUtil.getVoteEventLine(reward));
                }
                break;
            case "ziel":
                sender.sendMessage("");
                sender.sendMessage(VoteUtil.getVoteZielStatus(player));
                sender.sendMessage("");
                for (PersonalReward reward : PersonalReward.values()) {
                    sender.sendMessage(VoteUtil.getVoteZielLine(player, reward));
                }
                break;
            case "help":
                sender.sendMessage(Message.VOTE_HELP.getString().get(false));
                sender.sendMessage(Message.VOTE_HELP_EVENT.getString().get(false));
                sender.sendMessage(Message.VOTE_HELP_ZIEL.getString().get(false));
                break;
            case "count":
                if (args.length < 2) {
                    UUID uuid = player.getUniqueId();
                    Bson filter = Filters.eq("uuid", uuid.toString());
                    Document user = userCollection.find(filter).first();
                    if (user == null) {
                        sender.sendMessage(Message.VOTE_COUNT.getString()
                                .replace("%votes", "0").get());
                    } else {
                        sender.sendMessage(Message.VOTE_COUNT.getString()
                                .replace("%votes", String.valueOf(user.get("votes"))).get());
                    }
                } else {
                    Player other = Bukkit.getPlayer(args[1]);
                    UUID uuid;
                    if (other == null || !other.isOnline()) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(args[1]);
                        if (offlinePlayer == null) {
                            sender.sendMessage(Message.PLAYER_NOT_FOUND.getString()
                                    .replace("%player", args[1]).get());
                            return true;
                        }
                        uuid = offlinePlayer.getUniqueId();
                    } else {
                        uuid = player.getUniqueId();
                    }

                    Bson filter = Filters.eq("uuid", uuid.toString());
                    Document user = userCollection.find(filter).first();
                    if (user == null) {
                        sender.sendMessage(Message.VOTE_COUNT_OTHER.getString()
                                .replace("%player", args[1])
                                .replace("%votes", "0").get(false));
                    } else {
                        sender.sendMessage(Message.VOTE_COUNT_OTHER.getString()
                                .replace("%player", args[1])
                                .replace("%votes", String.valueOf(user.get("votes"))).get());
                    }
                }
                break;
            case "top":
                int page;
                if (args.length < 2) {
                    page = 1;
                } else {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Message.INVALID_NUMBER.getString().get());
                        return true;
                    }
                }

                long entries = userCollection.countDocuments(Filters.exists("votes"));
                if (entries <= 0) {
                    sender.sendMessage(Message.VOTE_TOP_NO_ENTRIES.getString().get());
                    return true;
                }

                int pages = (int) (entries / 10 + 1);
                if (page > pages) {
                    page = pages;
                }
                int skip = (page - 1) * 10;

                List<Document> users = userCollection.find(Filters.gt("votes", 0L))
                        .sort(Sorts.descending("votes"))
                        .skip(skip)
                        .limit(10)
                        .into(new ArrayList<>());

                sender.sendMessage(Message.VOTE_TOP_HEADER_FOOTER.getString()
                        .replace("%page", String.valueOf(page))
                        .replace("%amount", String.valueOf(pages)).get(false));
                int rank = skip + 1;
                for (Document user : users) {
                    UUID uuid = UUID.fromString((String) user.get("uuid"));
                    String name = Bukkit.getOfflinePlayer(uuid).getName();
                    sender.sendMessage(Message.VOTE_TOP_ENTRY.getString()
                            .replace("%rank", String.valueOf(rank))
                            .replace("%player", name)
                            .replace("%votes", String.valueOf(user.get("votes"))).get(false));
                    rank++;
                }
                sender.sendMessage(Message.VOTE_TOP_HEADER_FOOTER.getString()
                        .replace("%page", String.valueOf(page))
                        .replace("%amount", String.valueOf(pages)).get(false));
                break;
            default:
                sender.sendMessage(Message.VOTE_WRONG_ARGS.getString().get());
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
            arguments.add("count");
            arguments.add("top");
            StringUtil.copyPartialMatches(args[0], arguments, completions);
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("count")) {
                Bukkit.getOnlinePlayers().forEach(player -> arguments.add(player.getName()));
                StringUtil.copyPartialMatches(args[1], arguments, completions);
            }
        }

        Collections.sort(completions);
        return completions;
    }

}
