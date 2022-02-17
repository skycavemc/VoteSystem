package de.hakuyamu.skybee.votesystem.models;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@SuppressWarnings("unused")
public class User {

    private ObjectId id;
    private String uuid;
    @BsonProperty(value = "queued_votes")
    private int queuedVotes;
    private int votes;
    @BsonProperty(value = "last_vote_date")
    private String lastVoteDate;

    public User() {
    }

    public User(String uuid, int queuedVotes, int votes, String lastVoteDate) {
        this.uuid = uuid;
        this.queuedVotes = queuedVotes;
        this.votes = votes;
        this.lastVoteDate = lastVoteDate;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getQueuedVotes() {
        return queuedVotes;
    }

    public void setQueuedVotes(int queuedVotes) {
        this.queuedVotes = queuedVotes;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getLastVoteDate() {
        return lastVoteDate;
    }

    public void setLastVoteDate(String lastVoteDate) {
        this.lastVoteDate = lastVoteDate;
    }

}
