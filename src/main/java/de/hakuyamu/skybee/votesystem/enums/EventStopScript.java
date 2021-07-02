package de.hakuyamu.skybee.votesystem.enums;

public enum EventStopScript {

    LINE_1("screen -S skybee -X stuff \"voteadmin stop\""),
    LINE_2("screen -S skybee -X eval \"stuff \\015\""),
    ;

    private final String line;

    EventStopScript(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }
}
