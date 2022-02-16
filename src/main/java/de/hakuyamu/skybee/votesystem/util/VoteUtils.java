package de.hakuyamu.skybee.votesystem.util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.EventReward;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.enums.PersonalReward;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

public class VoteUtils {

    private static final VoteSystem main = JavaPlugin.getPlugin(VoteSystem.class);
    public static final DateTimeFormatter DTF =  DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static void processVote(String name) {
        MongoCollection<Document> userCollection = main.getUserCollection();
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

        processVoteForUser(userCollection, uuid, offline);
        processVoteForEvent();

        if (!offline) {
            main.getLogger().info(name + " voted for the server.");
            giveVoteRewards(player);
            return;
        }

        main.getLogger().info(name + " voted for the server, vote saved in their queue.");
    }

    private static void processVoteForUser(MongoCollection<Document> userCollection, UUID uuid, boolean offline) {
        Bson filter = Filters.eq("uuid", uuid.toString());
        Document user = userCollection.find(filter).first();
        if (user == null) {
            user = UserAdaptor.generateNewUser(uuid, LocalDate.now());
            userCollection.insertOne(user);
        }

        Bson update;
        if (offline) {
            update = Updates.set("queuedVotes", (Long) user.get("queuedVotes") + 1L);
        } else {
            update = Updates.set("votes", (Long) user.get("votes") + 1L);
        }
        userCollection.updateOne(filter,
                Updates.combine(Updates.set("lastVoteDate", LocalDate.now().toString()), update));
    }

    private static void processVoteForEvent() {
        YamlConfiguration event = main.getEventConfig();
        if (event == null) {
            main.getLogger().severe("Event config does not exist!");
            return;
        }
        event.set("votes", event.getInt("votes") + 1);
    }

    public static void giveVoteRewards(Player player) {
        MongoCollection<Document> userCollection = main.getUserCollection();
        UUID uuid = player.getUniqueId();
        Bson filter = Filters.eq("uuid", uuid.toString());
        Document user = userCollection.find(filter).first();
        if (user == null) {
            main.getLogger().severe("User profile for " + player.getName() + "could not be found.");
            return;
        }

        // luck chance = 20% (1 out of 5)
        boolean luck = (Math.round(Math.random() * 5) + 1) == 1;

        if (luck) {
            Utils.broadcast(Message.VOTE_LUCK.getString().replace("%player", player.getName()).get(false));
            Utils.executeConsoleCommand("padmin give " + player.getName() + " 15");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        } else {
            Utils.broadcast(Message.VOTE_DEFAULT.getString().replace("%player", player.getName()).get(false));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "padmin give " + player.getName() + " 10");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.8f);
        }

        for (PersonalReward reward : PersonalReward.values()) {
            if (reward.getVotes() == (Long) user.get("votes")) {
                Utils.broadcast(Message.VOTE_ZIEL_REACHED.getString()
                        .replace("%player", player.getName())
                        .replace("%votes", String.valueOf(reward.getVotes()))
                        .replace("%reward", reward.getName())
                        .get());
                for (String cmd : reward.getCommands()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player", player.getName()));
                }
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.6f);
            }
        }

        giveEventRewards(player);
    }

    private static void giveEventRewards(Player player) {
        YamlConfiguration event = main.getEventConfig();
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
        YamlConfiguration event = main.getEventConfig();
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
        YamlConfiguration event = main.getEventConfig();
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

    public static String getVoteZielStatus(Player player) {
        Bson filter = Filters.eq("uuid", player.getUniqueId().toString());
        Document user = main.getUserCollection().find(filter).first();
        if (user == null) {
            main.getLogger().severe("User profile for " + player.getName() + "could not be found.");
            return Message.VOTE_ZIEL_STATUS.getString()
                    .replace("%votes", "0")
                    .replace("%next", String.valueOf(PersonalReward.REWARD1.getVotes()))
                    .get(false);
        }

        PersonalReward next = Arrays.stream(PersonalReward.values())
                .filter(reward -> reward.getVotes() > (Long) user.get("votes")).findFirst().orElse(null);
        String votesUntil;
        if (next == null) {
            votesUntil = "§ckeins";
        } else {
            votesUntil = "noch " + (next.getVotes() - (Long) user.get("votes")) + " Votes";
        }
        return Message.VOTE_ZIEL_STATUS.getString()
                .replace("%votes", String.valueOf(user.get("votes")))
                .replace("%next", votesUntil)
                .get(false);
    }

    public static String getVoteZielLine(Player player, PersonalReward reward) {
        Bson filter = Filters.eq("uuid", player.getUniqueId().toString());
        Document user = main.getUserCollection().find(filter).first();

        Message message;
        if (user == null) {
            message = Message.VOTE_ZIEL_LINE_NOT;
        } else {
            if ((Long) user.get("votes") < reward.getVotes()) {
                message = Message.VOTE_ZIEL_LINE_NOT;
            } else {
                message = Message.VOTE_ZIEL_LINE_DONE;
            }
        }

        return message.getString()
                .replace("%numeral", String.valueOf(reward.ordinal() + 1))
                .replace("%votes", String.valueOf(reward.getVotes()))
                .replace("%name", reward.getName())
                .get(false);
    }

    public static void startEvent() {
        YamlConfiguration event = main.getEventConfig();
        if (event == null) {
            return;
        }
        if (!event.getBoolean("started")) {
            main.getUserCollection().drop();
            event.set("started", true);
            event.set("start-timestamp", LocalDateTime.now().toString());
            Utils.broadcast("");
            Utils.broadcast(Message.VADMIN_START_FIRST.getString().get(false));
            Utils.broadcast("");
        }
    }

    public static void stopEvent() {
        YamlConfiguration event = main.getEventConfig();
        if (event == null) {
            return;
        }
        event.set("started", false);
        event.set("end-timestamp", LocalDateTime.now().toString());
    }

}
