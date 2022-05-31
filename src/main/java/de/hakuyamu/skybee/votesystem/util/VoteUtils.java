package de.hakuyamu.skybee.votesystem.util;

import com.mongodb.client.model.Filters;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.EventReward;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.enums.PersonalReward;
import de.hakuyamu.skybee.votesystem.models.AutoSaveConfig;
import de.hakuyamu.skybee.votesystem.models.User;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class VoteUtils {

    private static final VoteSystem main = JavaPlugin.getPlugin(VoteSystem.class);
    public static final DateTimeFormatter DTF =  DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static void processVote(String name) {
        Player player = Bukkit.getPlayer(name);
        UUID uuid;
        boolean offline = false;

        if (player == null || !player.isOnline()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(name);
            if (offlinePlayer == null) {
                return;
            }
            uuid = offlinePlayer.getUniqueId();
            offline = true;
        } else {
            uuid = player.getUniqueId();
        }

        processVoteForUser(uuid, offline);
        processVoteForEvent();

        if (!offline) {
            main.getLogger().info(name + " voted for the server.");
            giveVoteRewards(player);
            return;
        }

        main.getLogger().info(name + " voted for the server, vote saved in their queue.");
    }

    private static void processVoteForUser(@NotNull UUID uuid, boolean offline) {
        Bson filter = Filters.eq("uuid", uuid.toString());
        User user = main.getUserCollection().find(filter).first();
        if (user == null) {
            user = new User(uuid.toString(), 0, 0, LocalDate.now().toString(), 0);
            main.getUserCollection().insertOne(user);
        }

        if (offline) {
            user.setQueuedVotes(user.getQueuedVotes() + 1);
        } else {
            user.setVotes(user.getVotes() + 1);
        }
        user.setLastVoteDate(LocalDate.now().toString());
        main.getUserCollection().replaceOne(filter, user);
    }

    private static void processVoteForEvent() {
        AutoSaveConfig event = main.getEventConfig();
        if (event == null) {
            main.getLogger().severe("Event config does not exist!");
            return;
        }
        event.set("votes", event.getInt("votes") + 1);
    }

    public static void giveVoteRewards(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        Bson filter = Filters.eq("uuid", uuid.toString());
        User user = main.getUserCollection().find(filter).first();
        if (user == null) {
            main.getLogger().severe("User profile for " + player.getName() + "could not be found.");
            return;
        }

        // luck chance = 20% (1 out of 5)
        boolean luck = (Math.round(Math.random() * 5) + 1) == 1;

        if (luck) {
            Utils.broadcast(Message.VOTE_LUCK.getString().replace("%player", player.getName()).get(false));
            user.setVoteCoins(user.getVoteCoins() + 15);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        } else {
            Utils.broadcast(Message.VOTE_DEFAULT.getString().replace("%player", player.getName()).get(false));
            user.setVoteCoins(user.getVoteCoins() + 10);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.8f);
        }
        main.getUserCollection().replaceOne(filter, user);

        for (PersonalReward reward : PersonalReward.values()) {
            if (reward.getVotes() == user.getVotes()) {
                Utils.broadcast(Message.VOTE_ZIEL_REACHED.getString()
                        .replace("%player", player.getName())
                        .replace("%votes", String.valueOf(reward.getVotes()))
                        .replace("%reward", reward.getName())
                        .get());
                reward.getAction().accept(player);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.6f);
            }
        }

        giveEventRewards(player);
    }

    private static void giveEventRewards(Player player) {
        AutoSaveConfig event = main.getEventConfig();
        if (event == null) {
            main.getLogger().severe("Event config does not exist!");
            return;
        }

        if (event.getBoolean("started")) {
            for (EventReward reward : EventReward.values()) {
                if (reward.getVotes() == event.getInt("votes")) {
                    Utils.broadcast("");
                    Utils.broadcast(Message.VOTE_EVENT_REACHED.getString()
                            .replace("%number", String.valueOf(reward.ordinal() + 1))
                            .replace("%votes", String.valueOf(reward.getVotes()))
                            .get(false));
                    Utils.broadcast(Message.VOTE_EVENT_REWARD.getString()
                            .replace("%reward", reward.getName())
                            .get(false));
                    Utils.broadcast("");
                    Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.1f));
                    for (String cmd : reward.getCommands()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player", player.getName()));
                    }

                    event.set("completion." + reward, LocalDateTime.now().toString());
                }
            }
        }
    }

    public static String getVoteEventStatus() {
        AutoSaveConfig event = main.getEventConfig();
        if (event == null) {
            main.getLogger().severe("Event config does not exist!");
            return Message.VOTE_EVENT_STATUS_INACTIVE.getString()
                    .replace("%started", "§cnoch nie")
                    .replace("%ended", "§cnoch nie")
                    .get(false);
        }

        if (event.getBoolean("started")) {
            int global = event.getInt("votes");

            String votesUntil = "§ckeins";
            for (EventReward rew : EventReward.values()) {
                if (rew.getVotes() > global) {
                    votesUntil = "noch " + (rew.getVotes() - global) + " Votes";
                    break;
                }
            }

            String timestamp = event.getString("start-timestamp");
            String result = "§cnoch nie";
            if (timestamp != null) {
                result = DTF.format(LocalDateTime.parse(timestamp)) + " Uhr";
            }

            return Message.VOTE_EVENT_STATUS_ACTIVE.getString()
                    .replace("%votes", "" + global)
                    .replace("%next", votesUntil)
                    .replace("%started", result)
                    .get(false);
        }
        
        String start = event.getString("start-timestamp");
        String end = event.getString("end-timestamp");

        if (start == null || end == null) {
            return Message.VOTE_EVENT_STATUS_INACTIVE.getString()
                    .replace("%started", "§cnoch nie")
                    .replace("%ended", "§cnoch nie")
                    .get(false);
        }

        return Message.VOTE_EVENT_STATUS_INACTIVE.getString()
                .replace("%started", DTF.format(LocalDateTime.parse(start)) + " Uhr")
                .replace("%ended", DTF.format(LocalDateTime.parse(end)) + " Uhr")
                .get(false);
    }

    public static String getVoteEventLine(EventReward reward) {
        AutoSaveConfig event = main.getEventConfig();
        if (event == null) {
            main.getLogger().severe("Event config does not exist!");
            return Message.VOTE_EVENT_LINE_NOT.getString()
                    .replace("%numeral", String.valueOf(reward.ordinal() + 1))
                    .replace("%votes", String.valueOf(reward.getVotes()))
                    .replace("%name", reward.getName())
                    .get(false);
        }


        if (!event.getBoolean("started") || event.getInt("votes") < reward.getVotes()) {
            return Message.VOTE_EVENT_LINE_NOT.getString()
                    .replace("%numeral", String.valueOf(reward.ordinal() + 1))
                    .replace("%votes", String.valueOf(reward.getVotes()))
                    .replace("%name", reward.getName())
                    .get(false);
        }

        String timestamp = event.getString("completion." + reward);
        String timeResult = "§cunbekannt";
        if (timestamp != null) {
            timeResult = DTF.format(LocalDateTime.parse(timestamp)) + " Uhr";
        }

        return Message.VOTE_EVENT_LINE_DONE.getString()
                .replace("%numeral", String.valueOf(reward.ordinal() + 1))
                .replace("%votes", String.valueOf(reward.getVotes()))
                .replace("%name", reward.getName())
                .replace("%date", timeResult)
                .get(false);
    }

    public static String getVoteZielStatus(@NotNull Player player) {
        Bson filter = Filters.eq("uuid", player.getUniqueId().toString());
        User user = main.getUserCollection().find(filter).first();
        if (user == null) {
            main.getLogger().severe("User profile for " + player.getName() + "could not be found.");
            return Message.VOTE_ZIEL_STATUS.getString()
                    .replace("%votes", "0")
                    .replace("%next", String.valueOf(PersonalReward.REWARD1.getVotes()))
                    .get(false);
        }

        String votesUntil = "§ckeins";
        for (PersonalReward rew : PersonalReward.values()) {
            if (rew.getVotes() > user.getVotes()) {
                votesUntil = "noch " + (rew.getVotes() - user.getVotes()) + " Votes";
                break;
            }
        }

        return Message.VOTE_ZIEL_STATUS.getString()
                .replace("%votes", "" + user.getVotes())
                .replace("%next", votesUntil)
                .get(false);
    }

    public static String getVoteZielLine(@NotNull Player player, PersonalReward reward) {
        Bson filter = Filters.eq("uuid", player.getUniqueId().toString());
        User user = main.getUserCollection().find(filter).first();

        Message message;
        if (user == null || user.getVotes() < reward.getVotes()) {
            message = Message.VOTE_ZIEL_LINE_NOT;
        } else {
            message = Message.VOTE_ZIEL_LINE_DONE;
        }

        return message.getString()
                .replace("%numeral", String.valueOf(reward.ordinal() + 1))
                .replace("%votes", String.valueOf(reward.getVotes()))
                .replace("%name", reward.getName())
                .get(false);
    }

    public static void startEvent() {
        AutoSaveConfig event = main.getEventConfig();
        if (event == null) {
            return;
        }
        if (!event.getBoolean("started")) {
            main.getUserCollection().drop();
            event.set("votes", 0);
            event.set("started", true);
            event.set("start-timestamp", LocalDateTime.now().toString());
            Utils.broadcast("");
            Utils.broadcast(Message.VADMIN_START_FIRST.getString().get(false));
            Utils.broadcast("");
        }
    }

    public static void stopEvent() {
        AutoSaveConfig event = main.getEventConfig();
        if (event == null) {
            return;
        }
        event.set("started", false);
        event.set("end-timestamp", LocalDateTime.now().toString());
    }

}
