package de.hakuyamu.skybee.votesystem.util;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.enums.EventReward;
import de.hakuyamu.skybee.votesystem.enums.PersonalReward;
import de.hakuyamu.skybee.votesystem.manager.DataManager;
import de.hakuyamu.skybee.votesystem.models.Event;
import de.hakuyamu.skybee.votesystem.models.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

public class VoteUtil {

    private static final VoteSystem main = JavaPlugin.getPlugin(VoteSystem.class);
    private static final DataManager dm = main.getDataManager();

    public static void processVote(String name) {
        Player player = Bukkit.getPlayer(name);
        Event event = dm.getEvent();

        // offline vote process
        if (player == null || !player.isOnline()) {
            // filter players who never played before
            OfflinePlayer offline = Bukkit.getOfflinePlayerIfCached(name);
            if (offline == null) {
                return;
            }

            // register user if necessary
            UUID uuid = offline.getUniqueId();
            if (!dm.isRegistered(uuid)) {
                dm.createUser(uuid);
            }
            User user = dm.getUser(uuid);

            // final vote processing
            main.getLogger().info(offline.getName() + " is offline, vote saved for next session."); // announcement in console
            event.setVoteCount(event.getVoteCount() + 1); // 1st step add to global votes
            user.setLastVoteDate(LocalDate.now()); // 2nd step set last vote date
            user.setQueuedVotes(user.getQueuedVotes() + 1); // 3rd step set personal vote count
            main.getDataManager().save();
            return;
        }


        // online vote process

        // register user if necessary
        UUID uuid = player.getUniqueId();
        if (!dm.isRegistered(uuid)) {
            dm.createUser(uuid);
        }
        User user = dm.getUser(uuid);

        // final vote processing
        main.getLogger().info(player.getName() + " voted for the server!"); // announcement in console
        event.setVoteCount(event.getVoteCount() + 1);// 1st step add to global votes
        user.setLastVoteDate(LocalDate.now()); // 2nd step set last vote date
        user.setVotes(user.getVotes() + 1); // 3rd step set personal vote count
        main.getDataManager().save();
        vote(player); // give vote rewards
    }

    @SuppressWarnings("deprecation")
    public static void vote(Player player) {
        UUID uuid = player.getUniqueId();
        User user = dm.getUser(uuid);
        Event event = dm.getEvent();

        // random number for luck
        int random = ((int) (Math.random() * 5)) + 1;

        // player will get more pollen if he is lucky, chance = 20% (1/5)
        if (random == 1) {
            Bukkit.broadcastMessage(Message.VOTE_LUCK.getMessage().replaceAll("%player", player.getName()));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "padmin give " + player.getName() + " 15");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        } else {
            Bukkit.broadcastMessage(Message.VOTE_DEFAULT.getMessage().replaceAll("%player", player.getName()));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "padmin give " + player.getName() + " 10");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.8f);
        }

        // check for personal rewards
        for (PersonalReward reward : PersonalReward.values()) {
            if (reward.getVotes() == user.getVotes()) {
                Bukkit.broadcastMessage(Message.VOTE_ZIEL_REACHED.getWithPrefix()
                        .replaceAll("%player", player.getName())
                        .replaceAll("%votes", String.valueOf(reward.getVotes()))
                        .replaceAll("%reward", reward.getName()));
                for (String cmd : reward.getCommands()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player", player.getName()));
                }
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.6f);
            }
        }


        // check for vote event rewards
        if (event.isStarted()) {
            for (EventReward reward : EventReward.values()) {
                if (reward.getVotes() == event.getVoteCount()) {
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(Message.VOTE_EVENT_REACHED.getMessage()
                            .replaceAll("%number", String.valueOf(reward.ordinal() + 1))
                            .replaceAll("%votes", String.valueOf(reward.getVotes())));
                    Bukkit.broadcastMessage(Message.VOTE_EVENT_REWARD.getMessage()
                            .replaceAll("%reward", reward.getName()));
                    Bukkit.broadcastMessage("");
                    for (String cmd : reward.getCommands()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player", player.getName()));
                    }
                    event.getEventCompletion().put(reward, LocalDateTime.now());
                    Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.1f));
                }
            }
        }
    }

    public static String getVoteEventStatus() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        Event event = main.getDataManager().getEvent();
        long global = event.getVoteCount();
        EventReward next = Arrays.stream(EventReward.values()).filter(reward -> reward.getVotes() > global).findFirst().orElse(null);
        String votesUntil;
        Message message;

        if (!event.isStarted()) {
            message = Message.VOTE_EVENT_STATUS_INACTIVE;
            if (event.getStartDate() == null || event.getEndDate() == null) {
                return message.getMessage()
                        .replaceAll("%started", "§cnoch nie")
                        .replaceAll("%ended", "§cnoch nie");
            }
            return message.getMessage()
                    .replaceAll("%started", event.getStartDate().format(formatter) + " Uhr")
                    .replaceAll("%ended", event.getEndDate().format(formatter) + " Uhr");
        }

        message = Message.VOTE_EVENT_STATUS_ACTIVE;
        if (next == null) {
            votesUntil = "§ckeins";
        } else {
            votesUntil = "noch " + (next.getVotes() - global) + " Votes";
        }
        return message.getMessage()
                .replaceAll("%votes", String.valueOf(global))
                .replaceAll("%next", votesUntil)
                .replaceAll("%started", event.getStartDate().format(formatter) + " Uhr");
    }

    public static String getVoteEventLine(EventReward reward) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        Event event = main.getDataManager().getEvent();
        long global = event.getVoteCount();
        LocalDateTime time = event.getEventCompletion().get(reward);
        String timeResult;
        Message message;

        if (!event.isStarted() || global < reward.getVotes()) {
            message = Message.VOTE_EVENT_LINE_NOT;
            return message.getMessage()
                    .replaceAll("%numeral", String.valueOf(reward.ordinal() + 1))
                    .replaceAll("%votes", String.valueOf(reward.getVotes()))
                    .replaceAll("%name", reward.getName());
        }

        message = Message.VOTE_EVENT_LINE_DONE;
        if (time == null) {
            timeResult = "§cunbekannt";
        } else {
            timeResult = time.format(formatter) + " Uhr";
        }
        return message.getMessage()
                .replaceAll("%numeral", String.valueOf(reward.ordinal() + 1))
                .replaceAll("%votes", String.valueOf(reward.getVotes()))
                .replaceAll("%name", reward.getName())
                .replaceAll("%date", timeResult);
    }

    public static String getVoteZielStatus(UUID uuid) {
        long personal = main.getDataManager().getUser(uuid).getVotes();
        PersonalReward next = Arrays.stream(PersonalReward.values()).filter(reward -> reward.getVotes() > personal).findFirst().orElse(null);
        String votesUntil;

        if (next == null) {
            votesUntil = "§ckeins";
        } else {
            votesUntil = "noch " + (next.getVotes() - personal) + " Votes";
        }
        return Message.VOTE_ZIEL_STATUS.getMessage()
                .replaceAll("%votes", String.valueOf(personal))
                .replaceAll("%next", votesUntil);
    }

    public static String getVoteZielLine(UUID uuid, PersonalReward reward) {
        long personal = main.getDataManager().getUser(uuid).getVotes();
        Message message;

        if (personal < reward.getVotes()) {
            message = Message.VOTE_ZIEL_LINE_NOT;
        } else {
            message = Message.VOTE_ZIEL_LINE_DONE;
        }

        return message.getMessage()
                .replaceAll("%numeral", String.valueOf(reward.ordinal() + 1))
                .replaceAll("%votes", String.valueOf(reward.getVotes()))
                .replaceAll("%name", reward.getName());
    }

}
