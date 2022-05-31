package de.hakuyamu.skybee.votesystem.enums;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.leonheuer.mcguiapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public enum VCShopItem {

    MONEY_500(1, 2, ItemBuilder.of(Material.SUNFLOWER).name("&6500$ &eGeld").glowing().asItem(),
            "500$ Ingame-Geld", player -> {
        Economy econ = JavaPlugin.getPlugin(VoteSystem.class).getEconomy();
        econ.depositPlayer(player, 500);
    }, 10),
    MONEY_1250(1, 3, ItemBuilder.of(Material.SUNFLOWER).name("&61250$ &eGeld").glowing().asItem(),
            "1250$ Ingame-Geld", player -> {
        Economy econ = JavaPlugin.getPlugin(VoteSystem.class).getEconomy();
        econ.depositPlayer(player, 1250);
    }, 25),
    MONEY_5000(1, 4, ItemBuilder.of(Material.SUNFLOWER).name("&65000$ &eGeld").glowing().asItem(),
            "5000$ Ingame-Geld", player -> {
        Economy econ = JavaPlugin.getPlugin(VoteSystem.class).getEconomy();
        econ.depositPlayer(player, 5000);
    }, 100),
    HONEY_KEY(3, 2, ItemBuilder.of(Material.TRIPWIRE_HOOK).name("&6Honig &eKey").glowing().asItem(),
            "Honig Key", player ->
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "k give honey " + player.getName() + " 1"), 10),
    BEEKEEPER_KEY(3, 3, ItemBuilder.of(Material.TRIPWIRE_HOOK).name("&6Imker &eKey").glowing().asItem(),
            "Imker Key", player ->
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "k give beekeeper " + player.getName() + " 1"), 25),
    BEE_KEY(3, 4, ItemBuilder.of(Material.TRIPWIRE_HOOK).name("&6Bienen &eKey").glowing().asItem(),
            "Bienen Key", player ->
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "k give bee " + player.getName() + " 1"), 100),
    STARTER_PICKAXE(5, 2, ItemBuilder.of(Material.IRON_PICKAXE).name("&bStarter-Picke").asItem(),
            "Eisenspitzhacke", player ->
            player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE)), 10),
    STARTER_FOOD(5, 3, ItemBuilder.of(Material.BREAD).name("&bStarter-Essen").asItem(),
            "32 Brote", player ->
            player.getInventory().addItem(new ItemStack(Material.BREAD, 32)), 10),
    STARTER_BLOCKS(5, 4, ItemBuilder.of(Material.OAK_PLANKS).name("&bStarter-Blöcke").asItem(),
            "64 Eichenbretter", player ->
            player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 64)), 10),
    THE_UNBREAKABLE(1, 6, ItemBuilder.of(Material.DIAMOND_PICKAXE).name("&fThe &3Unbreakable").glowing().asItem(),
            "The Unbreakable", player ->
            player.getInventory().addItem(ItemBuilder.of(Material.DIAMOND_PICKAXE).name("&fThe &3Unbreakable")
                    .enchant(Enchantment.DURABILITY, 10).asItem()), 150),
    THE_FAST(1, 7, ItemBuilder.of(Material.DIAMOND_PICKAXE).name("&fThe &3Fast &fand the &3Furious").glowing().asItem(),
            "The Fast and the Furious", player ->
            player.getInventory().addItem(ItemBuilder.of(Material.DIAMOND_PICKAXE).name("&fThe &3Fast &fand the &3Furious")
                    .enchant(Enchantment.DIG_SPEED, 6).asItem()), 150),
    DROHNE(3, 6, ItemBuilder.of(Material.BEE_SPAWN_EGG).name("&6Drohne (30 Tage)").asItem(),
            "Drohne (30 Tage)", player -> {
        Bukkit.broadcast(Component.text(Message.VCSHOP_DROHNE.getString().replace("%player", player.getName()).get()));
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                "/lp user " + player.getName() + " parent addtemp drohne 30d");
    }, 1000),
    FLY(3, 7, ItemBuilder.of(Material.BARRIER).name("&cBald verfügbar").asItem(),
            "Fly (30 Tage)", player -> {}, -1),
    ;

    private final int row;
    private final int column;
    private final ItemStack icon;
    private final String title;
    private final Consumer<Player> action;
    private final int cost;

    VCShopItem(int row, int column, ItemStack icon, String title, Consumer<Player> action, int cost) {
        this.row = row;
        this.column = column;
        this.icon = icon;
        this.title = title;
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

    public String getTitle() {
        return title;
    }

    public Consumer<Player> getAction() {
        return action;
    }

    public int getCost() {
        return cost;
    }
}
