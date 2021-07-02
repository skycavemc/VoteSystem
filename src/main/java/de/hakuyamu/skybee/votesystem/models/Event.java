package de.hakuyamu.skybee.votesystem.models;

import de.hakuyamu.skybee.votesystem.enums.EventReward;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Event {

    private final HashMap<EventReward, LocalDateTime> zielCompletion;
    private boolean eventStarted = false;
    private boolean weekStarted = false;
    private long voteCount = 0;
    private LocalDateTime startDate = null;
    private LocalDateTime endDate = null;

    public Event(
            HashMap<EventReward, LocalDateTime> eventCompletion,
            boolean started,
            boolean weekStarted,
            long voteCount,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        this.zielCompletion = eventCompletion;
        this.eventStarted = started;
        this.weekStarted = weekStarted;
        this.voteCount = voteCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public HashMap<EventReward, LocalDateTime> getEventCompletion() {
        return zielCompletion;
    }

    public boolean isStarted() {
        return eventStarted;
    }

    public void setStarted(boolean eventStarted) {
        this.eventStarted = eventStarted;
    }

    public boolean isWeekStarted() {
        return weekStarted;
    }

    public void setWeekStarted(boolean weekStarted) {
        this.weekStarted = weekStarted;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
