package de.hakuyamu.skybee.votesystem.enums;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.leonheuer.mcguiapi.utils.ItemBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public enum VCShopItem {

    MONEY_500(1, 2, ItemBuilder.of(Material.SUNFLOWER).name("&6500$ &eGeld")
            .description("&7Klicke, um &b500$ &7Ingame-Geld", "&7für &310 VoteCoins &7zu erhalten.").asItem(),
            player -> {
                Economy econ = JavaPlugin.getPlugin(VoteSystem.class).getEconomy();
                econ.depositPlayer(player, 500);
            }, 10),
    MONEY_1250(1, 3, ItemBuilder.of(Material.SUNFLOWER).name("&61250$ &eGeld")
            .description("&7Klicke, um &b1250$ &7Ingame-Geld", "&7für &325 VoteCoins &7zu erhalten.").asItem(),
            player -> {
                Economy econ = JavaPlugin.getPlugin(VoteSystem.class).getEconomy();
                econ.depositPlayer(player, 1250);
            }, 25),
    MONEY_5000(1, 4, ItemBuilder.of(Material.SUNFLOWER).name("&65000$ &eGeld")
            .description("&7Klicke, um &b5000$ &7Ingame-Geld", "&7für &3100 VoteCoins &7zu erhalten.").asItem(),
            player -> {
                Economy econ = JavaPlugin.getPlugin(VoteSystem.class).getEconomy();
                econ.depositPlayer(player, 5000);
            }, 100),
    HONEY_KEY(3, 2, ItemBuilder.of(Material.TRIPWIRE_HOOK).name("&6Honig &eKey")
            .description("&7Klicke, um einen &bHonig Key", "&7für &310 VoteCoins &7zu kaufen.").asItem(),
            player -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "k give honey " + player.getName() + " 1"), 10),
    BEEKEEPER_KEY(3, 3, ItemBuilder.of(Material.TRIPWIRE_HOOK).name("&6Imker &eKey")
            .description("&7Klicke, um einen &bImker Key", "&7für &325 VoteCoins &7zu kaufen.").asItem(),
            player -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "k give beekeeper " + player.getName() + " 1"), 25),
    BEE_KEY(3, 4, ItemBuilder.of(Material.TRIPWIRE_HOOK).name("&6Bienen &eKey")
            .description("&7Klicke, um einen &bBienen Key", "&7für &3100 VoteCoins &7zu kaufen.").asItem(),
            player -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "k give bee " + player.getName() + " 1"), 100),
    STARTER_PICKAXE(5, 2, ItemBuilder.of(Material.IRON_PICKAXE).name("&bStarter-Picke")
            .description("&7Klicke, um eine &bEisenspitzhacke", "&7für &310 VoteCoins &7zu kaufen.").asItem(),
            player -> player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE)), 10),
    STARTER_FOOD(5, 3, ItemBuilder.of(Material.BREAD).name("&bStarter-Essen")
            .description("&7Klicke, um &b32 Brote", "&7für &310 VoteCoins &7zu kaufen.").asItem(),
            player -> player.getInventory().addItem(new ItemStack(Material.BREAD, 32)), 10),
    STARTER_BLOCKS(5, 4, ItemBuilder.of(Material.OAK_PLANKS).name("&bStarter-Blöcke")
            .description("&7Klicke, um &b64 Eichenbretter", "&7für &310 VoteCoins &7zu kaufen.").asItem(),
            player -> player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 64)), 10),
    ;

    private final int row;
    private final int column;
    private final ItemStack icon;
    private final Consumer<Player> action;
    private final int cost;

    VCShopItem(int row, int column, ItemStack icon, Consumer<Player> action, int cost) {
        this.row = row;
        this.column = column;
        this.icon = icon;
        this.action = action;
        this.cost = cost;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public Consumer<Player> getAction() {
        return action;
    }

    public int getCost() {
        return cost;
    }
}
