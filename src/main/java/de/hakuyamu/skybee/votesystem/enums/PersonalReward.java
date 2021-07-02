package de.hakuyamu.skybee.votesystem.enums;

import org.bukkit.ChatColor;

public enum PersonalReward {

    REWARD1(6, "&e2 Honig Keys", "k give honey %player 2 silent"),
    REWARD2(12, "&e2 Imker Keys", "k give beekeeper %player 2 silent"),
    REWARD3(18, "&618 Erde", "give %player dirt 18"),
    REWARD4(24, "&62 Antiker Schrott", "give %player ancient_debris 2"),
    REWARD5(30, "&e2 Bienen Keys", "k give bee %player 2 silent"),
    REWARD6(36, "&b36 Diamanten", "give %player diamond 36"),
    REWARD7(42, "&f1 Hystery Token", "givehystery %player 1"),
    ;

    private final int votes;
    private final String name;
    private final String[] commands;

    PersonalReward(int votes, String name, String... commands) {
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
