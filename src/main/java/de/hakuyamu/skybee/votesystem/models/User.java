package de.hakuyamu.skybee.votesystem.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private long queuedVotes;
    private long votes;
    private LocalDate lastVoteDate;

    public User(@NotNull UUID uuid, long queuedVotes, long votes, LocalDate lastVoteDate) {
        this.uuid = uuid;
        this.queuedVotes = queuedVotes;
        this.votes = votes;
        this.lastVoteDate = lastVoteDate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getQueuedVotes() {
        return queuedVotes;
    }

    public void setQueuedVotes(long queuedVotes) {
        this.queuedVotes = queuedVotes;
    }

    public long getVotes() {
        return votes;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }

    @Nullable
    public LocalDate getLastVoteDate() {
        return lastVoteDate;
    }

    public String getLastVoteString() {
        if (lastVoteDate == null) {
            return "null";
        } else {
            return lastVoteDate.toString();
        }
    }

    public void setLastVoteDate(LocalDate lastVoteDate) {
        this.lastVoteDate = lastVoteDate;
    }

}
