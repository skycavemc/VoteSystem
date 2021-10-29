package de.hakuyamu.skybee.votesystem.models;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import org.bukkit.ChatColor;

public class FormattableString {

    private String result;

    public FormattableString(String base) {
        result = base;
    }

    public FormattableString replace(String from, String to) {
        result = result.replaceFirst(from, to);
        return this;
    }

    public FormattableString replaceAll(String from, String to) {
        result = result.replaceAll(from, to);
        return this;
    }

    public String get() {
        result = VoteSystem.PREFIX + result;
        result = ChatColor.translateAlternateColorCodes('&', result);
        return result;
    }

    public String get(Boolean prefix) {
        if (prefix) {
            result = VoteSystem.PREFIX + result;
        }
        result = ChatColor.translateAlternateColorCodes('&', result);
        return result;
    }

    public String get(Boolean prefix, Boolean formatted) {
        if (prefix) {
            result = VoteSystem.PREFIX + result;
        }
        if (formatted) {
            result = ChatColor.translateAlternateColorCodes('&', result);
        }
        return result;
    }

}
