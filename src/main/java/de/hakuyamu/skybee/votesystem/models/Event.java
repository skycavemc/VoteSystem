package de.hakuyamu.skybee.votesystem.models;

import de.hakuyamu.skybee.votesystem.enums.EventReward;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Event {

    private final Map<EventReward, LocalDateTime> zielCompletion;
    private boolean eventStarted;
    private boolean weekStarted;
    private long voteCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

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

    public Map<EventReward, LocalDateTime> getEventCompletion() {
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

    @Nullable
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public String getStartDateString() {
        if (startDate == null) {
            return "null";
        } else {
            return startDate.toString();
        }
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @Nullable
    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getEndDateString() {
        if (endDate == null) {
            return "null";
        } else {
            return endDate.toString();
        }
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
