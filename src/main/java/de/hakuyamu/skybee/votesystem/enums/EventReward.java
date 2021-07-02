package de.hakuyamu.skybee.votesystem.enums;

import org.bukkit.ChatColor;

public enum EventReward {

    REWARD1(24, "&61 Honig Key", "k give honey a 1 silent"),
    REWARD2(48, "&62 Honig Keys", "k give honey a 2 silent"),
    REWARD3(72, "&61 Imker Key", "k give beekeeper a 1 silent"),
    REWARD4(96, "&62 Honig Keys", "k give honey a 2 silent"),
    REWARD5(120, "&61 Imker Key", "k give beekeeper a 1 silent"),
    REWARD6(144, "&63 Honig Keys", "k give honey a 3 silent"),
    REWARD7(168, "&61 Imker Key", "k give beekeeper a 1 silent"),
    REWARD8(192, "&62 Imker Keys", "k give beekeeper a 2 silent"),
    REWARD9(216, "&63 Honig Keys", "k give honey a 3 silent"),
    REWARD10(240, "&61 Bienen Key", "k give bee a 1 silent"),
    REWARD11(264, "&62 Imker Keys", "k give beekeeper a 2 silent"),
    REWARD12(288, "&61 Bienen Key", "k give bee a 1 silent"),
    REWARD13(312, "&63 Imker Keys", "k give beekeeper a 3 silent"),
    REWARD14(336, "&62 Bienen Keys", "k give bee a 2 silent"),
    REWARD15(360, "&63 Bienen Keys", "k give bee a 3 silent"),
    ;

    private final int votes;
    private final String name;
    private final String[] commands;

    EventReward(int votes, String name, String... commands) {
        this.votes = votes;
        this.name = name;
        this.commands = commands;
    }

    public int getVotes() {
        return votes;
    }

    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', name);
    }

    public String[] getCommands() {
        return commands;
    }
}
