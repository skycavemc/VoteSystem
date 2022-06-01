package de.hakuyamu.skybee.votesystem.models;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FormattableString {

    private String result;

    public FormattableString(String base) {
        result = base;
    }

    public FormattableString replace(String from, String to) {
        result = result.replace(from, to);
        return this;
    }

    public String get() {
        result = VoteSystem.PREFIX + result;
        result = ChatColor.translateAlternateColorCodes('&', result);
        return result;
    }

    public String get(@NotNull Boolean prefix) {
        if (prefix) {
            result = VoteSystem.PREFIX + result;
        }
        result = ChatColor.translateAlternateColorCodes('&', result);
        return result;
    }

    public String get(@NotNull Boolean prefix, Boolean formatted) {
        if (prefix) {
            result = VoteSystem.PREFIX + result;
        }
        if (formatted) {
            result = ChatColor.translateAlternateColorCodes('&', result);
        }
        return result;
    }

}
