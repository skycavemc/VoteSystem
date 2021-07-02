package de.hakuyamu.skybee.votesystem.manager;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.EventReward;
import de.hakuyamu.skybee.votesystem.models.Event;
import de.hakuyamu.skybee.votesystem.models.User;
import de.hakuyamu.skybee.votesystem.util.FileUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class DataManager {

    private final VoteSystem main;
    private final String dir;
    private final HashMap<UUID, User> userHashMap = new HashMap<>();
    private Event event;

    public DataManager(VoteSystem main) {
        this.main = main;
        dir = main.getDataFolder().getPath();

        File directory = new File(dir);
        boolean exists = directory.exists();
        while (!exists) {
            exists = directory.mkdirs();
        }

        try {
            // deserializing all userdata
            File fileVotes = FileUtil.getFileIfExists(dir, "votes.json");
            if (fileVotes != null) {
                JSONArray array = FileUtil.readJsonArray(fileVotes);
                for (Object object : array) {
                    try {
                        JSONObject userObject = (JSONObject) object;
                        UUID uuid = UUID.fromString((String) userObject.get("uuid"));
                        User user = new User(
                                uuid,
                                (long) userObject.get("queuedVotes"),
                                (long) userObject.get("votes"),
                                LocalDate.parse((String) userObject.get("lastVoteDate"))
                        );
                        userHashMap.put(uuid, user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // deserializing event object
            File fileEvent = FileUtil.getFileIfExists(dir, "event.json");
            if (fileEvent != null) {
                JSONObject object = FileUtil.readJsonObject(fileEvent);

                // deserializing ziel completion hashmap
                JSONObject eventTimesObject = (JSONObject) object.get("zielCompletion");
                HashMap<EventReward, LocalDateTime> eventTimes = new HashMap<>();
                Arrays.stream(EventReward.values()).forEach(reward -> {
                    String time = (String) eventTimesObject.get(Integer.toString(reward.ordinal()));
                    if (!time.equals("never")) {
                        eventTimes.put(reward, LocalDateTime.parse(time));
                    }
                });

                // setting the event object
                event = new Event(
                        eventTimes,
                        Boolean.parseBoolean((String) object.get("eventStarted")),
                        Boolean.parseBoolean((String) object.get("weekStarted")),
                        (long) object.get("voteCount"),
                        LocalDateTime.parse((String) object.get("startDate")),
                        LocalDateTime.parse((String) object.get("endDate"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            event = new Event(new HashMap<>(), false, false, 0, null, null);
        }
    }

    @SuppressWarnings("unchecked")
    public void save() {
        // serializing all users
        JSONArray array = new JSONArray();

        userHashMap.keySet().forEach(uuid -> {
            JSONObject userObject = new JSONObject();
            User user = userHashMap.get(uuid);
            userObject.put("uuid", uuid.toString());
            userObject.put("queuedVotes", user.getQueuedVotes());
            userObject.put("votes", user.getVotes());
            userObject.put("lastVoteDate", user.getLastVoteDate().toString());
            array.add(userObject);
        });

        FileUtil.writeJsonToFile(array, FileUtil.getFileAndCreate(dir, "votes.json"));


        // serializing event object
        try {
            JSONObject object = new JSONObject();

            JSONObject zielCompletion = new JSONObject();
            Arrays.stream(EventReward.values()).forEach(reward -> {
                LocalDateTime time = event.getEventCompletion().get(reward);
                if (time == null) {
                    zielCompletion.put(reward.ordinal(), "never");
                } else {
                    zielCompletion.put(reward.ordinal(), time.toString());
                }
            });

            object.put("eventStarted", Boolean.toString(event.isStarted()));
            object.put("weekStarted", Boolean.toString(event.isWeekStarted()));
            object.put("voteCount", event.getVoteCount());
            object.put("startDate", event.getStartDate().toString());
            object.put("endDate", event.getEndDate().toString());
            object.put("zielCompletion", zielCompletion);

            FileUtil.writeJsonToFile(object, FileUtil.getFileAndCreate(dir, "event.json"));
        } catch (NullPointerException e) {
            main.getLogger().warning("Some data was corrupted.");
        }
    }

    public void clear() {
        userHashMap.clear();
        event.setVoteCount(0);
        main.getLogger().info("UserData and VoteCount successfully reset.");
    }

    public boolean isRegistered(UUID uuid) {
        return userHashMap.containsKey(uuid);
    }

    public User getUser(UUID uuid) {
        return userHashMap.getOrDefault(uuid, new User(uuid, 0, 0, null));
    }

    public void createUser(UUID uuid) {
        User user = new User(uuid, 0, 0, null);
        userHashMap.put(uuid, user);
    }

    public Event getEvent() {
        return event;
    }

}
