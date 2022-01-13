package de.hakuyamu.skybee.votesystem.util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

public class VoteUtil {

    private static final VoteSystem main = JavaPlugin.getPlugin(VoteSystem.class);

    public static void processVote(String username) {
        MongoDatabase db = main.getDbManager().getDatabase();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(username);
        if (offlinePlayer == null)
            return;

        UUID uuid = offlinePlayer.getUniqueId();
        Player player = offlinePlayer.getPlayer();
        boolean offline = player == null;

        long votes = updateUser(db, uuid, offline);
        updateEvent(db);

        if (!offline) {
            main.getLogger().info(username + " voted for the server.");
            giveVoteRewards(player, votes);
            return;
        }

        main.getLogger().info(username + " voted for the server, vote saved in their queue.");
    }

    /**
     * Increase the votes or votes in queue in the database.
     *
     * @return the current votes
     */
    private static long updateUser(MongoDatabase db, UUID uuid, boolean offline) {
        Bson filter = Filters.eq("uuid", uuid.toString());
        Document user = db.getCollection("users").find(filter).first();
        if (user == null) {
            user = UserAdaptor.generateNewUser(uuid, LocalDate.now());
            db.getCollection("users").insertOne(user);
        }

        Bson update = Updates.inc(offline ? "queuedVotes" : "votes", 1); // Increase votes in queue when the player is offline
        db.getCollection("users").
                updateOne(filter,
                        Updates.combine(
                                Updates.set("lastVoteDate", LocalDate.now().toString()),
                                update));
        return user.getLong("votes") + 1; // +1 because of the old cached document
    }

    private static void updateEvent(MongoDatabase db) {
        Document event = db.getCollection("event").find(Filters.exists("votes")).first();
        if (event == null) {
            event = EventAdaptor.generateNewEvent();
            db.getCollection("event").insertOne(event);
        }

        db.getCollection("event").updateOne(Filters.exists("votes"),
                Updates.inc("votes", 1L));
    }

    @SuppressWarnings("deprecation")
    public static void giveVoteRewards(Player player, long voteCount) {

        // luck chance = 20% (1 out of 5)
        boolean luck = Math.round(Math.random() * 5) == 0;

        if (luck) {
            Bukkit.broadcastMessage(Message.VOTE_LUCK.getString().replace("%player", player.getName()).get(false));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "padmin give " + player.getName() + " 15");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        } else {
            Bukkit.broadcastMessage(Message.VOTE_DEFAULT.getString().replace("%player", player.getName()).get(false));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "padmin give " + player.getName() + " 10");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.8f);
        }

        for (PersonalReward reward : PersonalReward.values()) {
            if (reward.getVotes() != voteCount)
                continue;
            Bukkit.broadcastMessage(Message.VOTE_ZIEL_REACHED.getString()
                    .replace("%player", player.getName())
                    .replace("%votes", String.valueOf(reward.getVotes()))
                    .replace("%reward", reward.getName())
                    .get());
            for (String cmd : reward.getCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player", player.getName()));
            }
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.6f);
        }

        giveEventRewards(player);
    }

    @SuppressWarnings("deprecation")
    private static void giveEventRewards(Player player) {
        MongoDatabase db = main.getDbManager().getDatabase();
        MongoCollection<Document> eventCollection = db.getCollection("event");
        Document event = eventCollection.find(Filters.exists("votes")).first();
        if (event == null) {
            main.getLogger().severe("The event data has been requested, but the event data does not exist.");
            return;
        }

        if (!event.getBoolean("started"))
            return;

        for (EventReward reward : EventReward.values()) {
            if (reward.getVotes() != event.getLong("votes"))
                continue;

            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(Message.VOTE_EVENT_REACHED.getString()
                    .replace("%number", String.valueOf(reward.ordinal() + 1))
                    .replace("%votes", String.valueOf(reward.getVotes()))
                    .get(false));
            Bukkit.broadcastMessage(Message.VOTE_EVENT_REWARD.getString()
                    .replace("%reward", reward.getName())
                    .get(false));
            Bukkit.broadcastMessage("");
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.1f));

            for (String cmd : reward.getCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player", player.getName()));
            }

            Document rewards = event.get("completion", Document.class);
            rewards.put(reward.toString(), LocalDateTime.now().toString());
            eventCollection.updateOne(Filters.exists("votes"), Updates.set("completion", rewards));
        }

    }

    public static String getVoteEventStatus() {
        MongoDatabase db = main.getDbManager().getDatabase();
        Document event = db.getCollection("event").find(Filters.exists("votes")).first();
        if (event == null) {
            main.getLogger().severe("The event data has been requested, but the event data does not exist.");
            return Message.VOTE_EVENT_STATUS_INACTIVE.getString()
                    .replace("%started", "§cnoch nie")
                    .replace("%ended", "§cnoch nie")
                    .get(false);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        if (event.getBoolean("started", false)) {
            long global = event.getLong("votes");
            EventReward next = Arrays.stream(EventReward.values()).filter(reward -> reward.getVotes() > global).findFirst().orElse(null);
            String votesUntil;
            if (next == null) {
                votesUntil = "§ckeins";
            } else {
                votesUntil = "noch " + (next.getVotes() - global) + " Votes";
            }
            return Message.VOTE_EVENT_STATUS_ACTIVE.getString()
                    .replace("%votes", String.valueOf(global))
                    .replace("%next", votesUntil)
                    .replace("%started", LocalDateTime.parse((String) event.get("start"))
                            .format(formatter) + " Uhr")
                    .get(false);
        }

        if (event.get("start").equals("never") || event.get("end").equals("never")) {
            return Message.VOTE_EVENT_STATUS_INACTIVE.getString()
                    .replace("%started", "§cnoch nie")
                    .replace("%ended", "§cnoch nie")
                    .get(false);
        }

        return Message.VOTE_EVENT_STATUS_INACTIVE.getString()
                .replace("%started", LocalDateTime.parse((String) event.get("start"))
                        .format(formatter) + " Uhr")
                .replace("%ended", LocalDateTime.parse((String) event.get("end"))
                        .format(formatter) + " Uhr")
                .get(false);
    }

    public static String getVoteEventLine(EventReward reward) {
        MongoDatabase db = main.getDbManager().getDatabase();
        Document event = db.getCollection("event").find(Filters.exists("votes")).first();
        if (event == null) {
            main.getLogger().severe("The event data has been requested, but the event data does not exist.");
            return Message.VOTE_EVENT_LINE_NOT.getString()
                    .replace("%numeral", String.valueOf(reward.ordinal() + 1))
                    .replace("%votes", String.valueOf(reward.getVotes()))
                    .replace("%name", reward.getName())
                    .get(false);
        }


        if (!Boolean.parseBoolean((String) event.get("started")) || (Long) event.get("votes") < reward.getVotes()) {
            return Message.VOTE_EVENT_LINE_NOT.getString()
                    .replace("%numeral", String.valueOf(reward.ordinal() + 1))
                    .replace("%votes", String.valueOf(reward.getVotes()))
                    .replace("%name", reward.getName())
                    .get(false);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
            LocalDateTime time = getCompletionDateTime(event, reward);
            String timeResult;
            if (time == null) {
                timeResult = "§cunbekannt";
            } else {
                timeResult = time.format(formatter) + " Uhr";
            }
            return Message.VOTE_EVENT_LINE_DONE.getString()
                    .replace("%numeral", String.valueOf(reward.ordinal() + 1))
                    .replace("%votes", String.valueOf(reward.getVotes()))
                    .replace("%name", reward.getName())
                    .replace("%date", timeResult)
                    .get(false);
        }
    }

    @Nullable
    private static LocalDateTime getCompletionDateTime(Document event, EventReward reward) {
        Document completion = event.get("completion", Document.class);
        if (completion.get(reward.toString()).equals("never")) {
            return null;
        }
        return LocalDateTime.parse((String) completion.get(reward.toString()));
    }

    public static String getVoteZielStatus(Player player) {
        Bson filter = Filters.eq("uuid", player.getUniqueId().toString());
        Document user = main.getDbManager().getDatabase().getCollection("users").find(filter).first();
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
        Document user = main.getDbManager().getDatabase().getCollection("users").find(filter).first();

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

}
