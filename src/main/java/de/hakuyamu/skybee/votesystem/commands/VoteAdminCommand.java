package de.hakuyamu.skybee.votesystem.commands;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.models.AutoSaveConfig;
import de.hakuyamu.skybee.votesystem.models.User;
import de.hakuyamu.skybee.votesystem.util.VoteUtils;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VoteAdminCommand implements CommandExecutor, TabCompleter {

    private final VoteSystem main;

    public VoteAdminCommand(VoteSystem main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }

        MongoCollection<User> userCollection = main.getUserCollection();
        AutoSaveConfig event = main.getEventConfig();
        if (event == null) {
            main.getLogger().severe("Event config does not exist!");
            sender.sendMessage(Message.INTERNAL_ERROR.getString().get());
            return true;
        }

        switch (args[0]) {
            case "start" -> {
                if (event.getBoolean("started")) {
                    sender.sendMessage(Message.VADMIN_START_ALREADY.getString().get());
                    break;
                }
                VoteUtils.startEvent();
            }
            case "stop" -> {
                if (!event.getBoolean("started")) {
                    sender.sendMessage(Message.VADMIN_STOP_NOT.getString().get());
                    break;
                }
                VoteUtils.stopEvent();
                sender.sendMessage(Message.VADMIN_STOP_SUCCESS.getString().get());
            }
            case "fake" -> {
                if (args.length < 2) {
                    sender.sendMessage(Message.VADMIN_FAKE.getString().get());
                    break;
                }
                sender.sendMessage(Message.VADMIN_FAKE_EXE.getString().replace("%name", args[1]).get());
                VoteUtils.processVote(args[1]);
            }
            case "clear" -> {
                userCollection.drop();
                sender.sendMessage(Message.VADMIN_CLEAR.getString().get());
            }
            case "reload" -> {
                main.reloadResources();
                sender.sendMessage(Message.VADMIN_RELOAD.getString().get());
            }
            case "help" -> sendHelp(sender);
            case "setvotecoins" -> {
                if (args.length < 3) {
                    sender.sendMessage(Message.VADMIN_SETVOTECOINS_SYNTAX.getString().get());
                    return true;
                }
                OfflinePlayer other = Bukkit.getOfflinePlayerIfCached(args[1]);
                if (other == null) {
                    sender.sendMessage(Message.PLAYER_NOT_FOUND.getString().replace("%player", args[1]).get());
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                    if (amount < 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(Message.INVALID_NUMBER.getString().replace("%number", args[2]).get());
                    return true;
                }

                Bson filter = Filters.eq("uuid", other.getUniqueId().toString());
                User user = main.getUserCollection().find(filter).first();
                if (user == null) {
                    user = new User(other.getUniqueId().toString(), 0, 0, null, amount);
                    main.getUserCollection().insertOne(user);
                } else {
                    user.setVoteCoins(amount);
                    main.getUserCollection().replaceOne(filter, user);
                }

                sender.sendMessage(Message.VADMIN_SETVOTECOINS.getString()
                        .replace("%player", other.getName())
                        .replace("%amount", "" + amount).get());
            }
            default -> sender.sendMessage(Message.VADMIN_WRONG_ARGS.getString().get());
        }
        return true;
    }

    private void sendHelp(@NotNull CommandSender sender) {
        sender.sendMessage(Message.VADMIN_HELP_START.getString().get(false));
        sender.sendMessage(Message.VADMIN_HELP_STOP.getString().get(false));
        sender.sendMessage(Message.VADMIN_HELP_CLEAR.getString().get(false));
        sender.sendMessage(Message.VADMIN_HELP_FAKE.getString().get(false));
        sender.sendMessage(Message.VADMIN_HELP_RELOAD.getString().get(false));
        sender.sendMessage(Message.VADMIN_HELP_SETVOTECOINS.getString().get(false));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        List<String> arguments = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            arguments.add("start");
            arguments.add("stop");
            arguments.add("clear");
            arguments.add("fake");
            arguments.add("reload");
            arguments.add("setvotecoins");
            arguments.add("help");

            StringUtil.copyPartialMatches(args[0], arguments, completions);
        }

        Collections.sort(completions);
        return completions;
    }

}
