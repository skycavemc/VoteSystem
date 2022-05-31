package de.hakuyamu.skybee.votesystem.enums;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public enum PersonalReward {

    REWARD1(6, "&e2 Honig Keys", player -> Bukkit.getServer()
            .dispatchCommand(Bukkit.getConsoleSender(), "k give honey " + player.getName() + " 2 silent")),
    REWARD2(12, "&e2 Imker Keys", player -> Bukkit.getServer()
            .dispatchCommand(Bukkit.getConsoleSender(), "k give beekeeper " + player.getName() + " 2 silent")),
    REWARD3(18, "&618 Erde", player -> player.getInventory()
            .addItem(new ItemStack(Material.DIRT, 18))),
    REWARD4(24, "&62 Antiker Schrott", player -> player.getInventory()
            .addItem(new ItemStack(Material.ANCIENT_DEBRIS, 2))),
    REWARD5(30, "&e2 Bienen Keys", player -> Bukkit.getServer()
            .dispatchCommand(Bukkit.getConsoleSender(), "k give bee " + player.getName() + " 2 silent")),
    REWARD6(36, "&b36 Diamanten", player -> player.getInventory()
            .addItem(new ItemStack(Material.DIAMOND, 36))),
    REWARD7(42, "&f1 Hystery Token", player -> Bukkit.getServer()
            .dispatchCommand(Bukkit.getConsoleSender(), "givehystery " + player.getName() + " 1")),
    ;

    private final int votes;
    private final String name;
    private final Consumer<Player> action;

    PersonalReward(int votes, String name, Consumer<Player> action) {
        this.votes = votes;
        this.name = name;
        this.action = action;
    }

    public int getVotes() {
        return votes;
    }

    @Contract(" -> new")
    public @NotNull String getName() {
        return ChatColor.translateAlternateColorCodes('&', name);
    }

    public Consumer<Player> getAction() {
        return action;
    }
}
