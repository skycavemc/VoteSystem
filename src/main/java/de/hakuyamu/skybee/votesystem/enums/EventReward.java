package de.hakuyamu.skybee.votesystem.enums;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum EventReward {

    REWARD1(24, "&61 Honig Key", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give honey a 1 silent")),
    REWARD2(48, "&62 Honig Keys", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give honey a 2 silent")),
    REWARD3(72, "&61 Imker Key", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give beekeeper a 1 silent")),
    REWARD4(96, "&62 Honig Keys", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give honey a 2 silent")),
    REWARD5(120, "&61 Imker Key", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give beekeeper a 1 silent")),
    REWARD6(144, "&63 Honig Keys", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give honey a 3 silent")),
    REWARD7(168, "&61 Imker Key", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give beekeeper a 1 silent")),
    REWARD8(192, "&62 Imker Keys", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give beekeeper a 2 silent")),
    REWARD9(216, "&63 Honig Keys", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give honey a 3 silent")),
    REWARD10(240, "&61 Bienen Key", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give bee a 1 silent")),
    REWARD11(264, "&62 Imker Keys", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give beekeeper a 2 silent")),
    REWARD12(288, "&61 Bienen Key", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give bee a 1 silent")),
    REWARD13(312, "&63 Imker Keys", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give beekeeper a 3 silent")),
    REWARD14(336, "&62 Bienen Keys", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give bee a 2 silent")),
    REWARD15(360, "&63 Bienen Keys", () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "k give bee a 3 silent")),
    REWARD16(384, "&664 Erde", () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(new ItemStack(Material.DIRT, 64));
        }
    }),
    REWARD17(408, "&632 Diamanten", () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(new ItemStack(Material.DIAMOND, 32));
        }
    }),
    REWARD18(432, "&68 Antiker Schrott", () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(new ItemStack(Material.ANCIENT_DEBRIS, 8));
        }
    }),
    REWARD19(456, "&61 Shulkerschale", () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(new ItemStack(Material.SHULKER_SHELL, 1));
        }
    }),
    REWARD20(480, "&664 Amethystscherben", () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(new ItemStack(Material.AMETHYST_SHARD, 64));
        }
    }),
    ;

    private final int votes;
    private final String name;
    private final Runnable action;

    EventReward(int votes, String name, Runnable action) {
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

    public Runnable getAction() {
        return action;
    }

}
