package de.hakuyamu.skybee.votesystem.util;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.UUID;

public class UserAdaptor {

    public static Document generateNewUser(UUID uuid, LocalDate lastVoteDate) {
        return new Document("_id", new ObjectId())
                .append("uuid", uuid.toString())
                .append("queuedVotes", 0L)
                .append("votes", 0L)
                .append("lastVoteDate", lastVoteDate.toString());
    }

}
