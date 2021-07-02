package de.hakuyamu.skybee.votesystem.commands;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.models.Event;
import de.hakuyamu.skybee.votesystem.util.VoteUtil;
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
            sender.sendMessage(Message.VADMIN_HELP_START.getMessage());
            sender.sendMessage(Message.VADMIN_HELP_STOP.getMessage());
            sender.sendMessage(Message.VADMIN_HELP_CLEAR.getMessage());
            sender.sendMessage(Message.VADMIN_HELP_FAKE.getMessage());
            return true;
        }

        Event event = main.getDataManager().getEvent();

        switch (args[0]) {
            case "start":
                if (event.isStarted()) {
                    sender.sendMessage(Message.VADMIN_START_ALREADY.getWithPrefix());
                    break;
                }
                if (event.isWeekStarted()) {
                    sender.sendMessage(Message.VADMIN_START_WEEK.getWithPrefix());
                    event.setWeekStarted(false);
                    break;
                }
                main.getDataManager().clear();
                event.setStarted(true);
                event.setWeekStarted(true);
                event.setStartDate(LocalDateTime.now());
                main.getDataManager().save();
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(Message.VADMIN_START_FIRST.getMessage());
                Bukkit.broadcastMessage("");
                break;
            case "stop":
                if (!event.isStarted()) {
                    sender.sendMessage(Message.VADMIN_STOP_NOT.getWithPrefix());
                    break;
                }
                event.setStarted(false);
                event.setEndDate(LocalDateTime.now());
                main.getDataManager().save();
                sender.sendMessage(Message.VADMIN_STOP_SUCCESS.getWithPrefix());
                break;
            case "fake":
                if (args.length < 2) {
                    sender.sendMessage(Message.VADMIN_FAKE.getWithPrefix());
                    break;
                }
                sender.sendMessage(Message.VADMIN_FAKE_EXE.getWithPrefix().replaceAll("%name", args[1]));
                VoteUtil.processVote(args[1]);
                break;
            case "clear":
                main.getDataManager().clear();
                main.getDataManager().save();
                sender.sendMessage(Message.VADMIN_CLEAR.getWithPrefix());
                break;
            case "help":
                sender.sendMessage(Message.VADMIN_HELP_START.getMessage());
                sender.sendMessage(Message.VADMIN_HELP_STOP.getMessage());
                sender.sendMessage(Message.VADMIN_HELP_CLEAR.getMessage());
                break;
            default:
                sender.sendMessage(Message.VADMIN_WRONG_ARGS.getWithPrefix());
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
