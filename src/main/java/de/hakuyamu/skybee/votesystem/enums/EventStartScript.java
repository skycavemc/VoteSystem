package de.hakuyamu.skybee.votesystem.enums;

public enum EventStartScript {

    LINE_1("screen -S skybee -X stuff \"voteadmin start\""),
    LINE_2("screen -S skybee -X eval \"stuff \\015\""),
    ;

    private final String line;

    EventStartScript(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }

}
