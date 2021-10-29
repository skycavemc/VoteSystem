package de.hakuyamu.skybee.votesystem.util;

import de.hakuyamu.skybee.votesystem.enums.EventReward;
import org.bson.Document;
import org.bson.types.ObjectId;

public class EventAdaptor {

    public static Document generateNewEvent() {
        Document completion = new Document();
        for (EventReward reward : EventReward.values()) {
            completion.append(reward.toString(), "never");
        }
        return new Document("_id", new ObjectId())
                .append("votes", 0L)
                .append("started", "false")
                .append("skipNextWeek", "false")
                .append("completion", completion)
                .append("start", "never")
                .append("end", "never");
    }

}
