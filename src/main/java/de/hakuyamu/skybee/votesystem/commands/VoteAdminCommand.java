package de.hakuyamu.skybee.votesystem.commands;

import com.mongodb.client.MongoCollection;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.util.VoteUtils;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Message.VADMIN_HELP_START.getString().get(false));
            sender.sendMessage(Message.VADMIN_HELP_STOP.getString().get(false));
            sender.sendMessage(Message.VADMIN_HELP_CLEAR.getString().get(false));
            sender.sendMessage(Message.VADMIN_HELP_FAKE.getString().get(false));
            return true;
        }

        MongoCollection<Document> userCollection = main.getUserCollection();
        YamlConfiguration event = main.getEventConfig();
        if (event == null) {
            main.getLogger().severe("Event config does not exist!");
            sender.sendMessage(Message.INTERNAL_ERROR.getString().get());
            return true;
        }

        switch (args[0]) {
            case "start":
                if (event.getBoolean("started")) {
                    sender.sendMessage(Message.VADMIN_START_ALREADY.getString().get());
                    break;
                }
                VoteUtils.startEvent();
                break;
            case "stop":
                if (!event.getBoolean("started")) {
                    sender.sendMessage(Message.VADMIN_STOP_NOT.getString().get());
                    break;
                }
                VoteUtils.stopEvent();
                sender.sendMessage(Message.VADMIN_STOP_SUCCESS.getString().get());
                break;
            case "fake":
                if (args.length < 2) {
                    sender.sendMessage(Message.VADMIN_FAKE.getString().get());
                    break;
                }
                sender.sendMessage(Message.VADMIN_FAKE_EXE.getString().replace("%name", args[1]).get());
                VoteUtils.processVote(args[1]);
                break;
            case "clear":
                userCollection.drop();
                sender.sendMessage(Message.VADMIN_CLEAR.getString().get());
                break;
            case "help":
                sender.sendMessage(Message.VADMIN_HELP_START.getString().get(false));
                sender.sendMessage(Message.VADMIN_HELP_STOP.getString().get(false));
                sender.sendMessage(Message.VADMIN_HELP_CLEAR.getString().get(false));
                break;
            default:
                sender.sendMessage(Message.VADMIN_WRONG_ARGS.getString().get());
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            arguments.add("start");
            arguments.add("stop");
            arguments.add("clear");
            arguments.add("fake");
            arguments.add("help");

            StringUtil.copyPartialMatches(args[0], arguments, completions);
        }

        Collections.sort(completions);
        return completions;
    }

}
