package de.hakuyamu.skybee.votesystem.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import de.hakuyamu.skybee.votesystem.models.User;

public class UserAdaptor {

    public static DBObject toDBObject (User user) {
        return new BasicDBObject("uuid", user.getUuid().toString())
                .append("queuedVotes", user.getQueuedVotes())
                .append("votes", user.getVotes())
                .append("lastVote", user.getLastVoteString());
    }

}
