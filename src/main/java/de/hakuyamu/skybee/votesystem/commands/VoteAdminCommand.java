package de.hakuyamu.skybee.votesystem.commands;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.util.VoteUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VoteAdminCommand implements CommandExecutor, TabCompleter {

    private final VoteSystem main;

    public VoteAdminCommand(VoteSystem main) {
        this.main = main;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Message.VADMIN_HELP_START.getString().get(false));
            sender.sendMessage(Message.VADMIN_HELP_STOP.getString().get(false));
            sender.sendMessage(Message.VADMIN_HELP_CLEAR.getString().get(false));
            sender.sendMessage(Message.VADMIN_HELP_FAKE.getString().get(false));
            return true;
        }

        Bson filter = Filters.exists("votes");
        MongoDatabase db = main.getDbManager().getDatabase();
        Document event = db.getCollection("event").find(filter).first();
        if (event == null) {
            main.getLogger().severe("The event data has been requested, but the event data does not exist.");
            sender.sendMessage(Message.DATABASE_ERROR.getString().get());
            return true;
        }

        switch (args[0]) {
            case "start":
                if (event.getBoolean("started")) {
                    sender.sendMessage(Message.VADMIN_START_ALREADY.getString().get());
                    break;
                }
                if (event.getBoolean("skipNextWeek")) {
                    sender.sendMessage(Message.VADMIN_START_WEEK.getString().get());
                    db.getCollection("event").updateOne(filter, Updates.set("skipNextWeek", false));
                    break;
                }
                db.getCollection("users").drop();
                db.getCollection("event").updateOne(filter, Updates.combine(
                        Updates.set("started", true),
                        Updates.set("skipNextWeek", true),
                        Updates.set("start", LocalDateTime.now().toString())));
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(Message.VADMIN_START_FIRST.getString().get(false));
                Bukkit.broadcastMessage("");
                break;
            case "stop":
                if (!event.getBoolean("started")) {
                    sender.sendMessage(Message.VADMIN_STOP_NOT.getString().get());
                    break;
                }
                db.getCollection("event").updateOne(filter, Updates.combine(
                        Updates.set("started", false),
                        Updates.set("end", LocalDateTime.now().toString())));
                sender.sendMessage(Message.VADMIN_STOP_SUCCESS.getString().get());
                break;
            case "fake":
                if (args.length < 2) {
                    sender.sendMessage(Message.VADMIN_FAKE.getString().get());
                    break;
                }
                sender.sendMessage(Message.VADMIN_FAKE_EXE.getString().replace("%name", args[1]).get());
                VoteUtil.processVote(args[1]);
                break;
            case "clear":
                db.getCollection("users").drop();
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
