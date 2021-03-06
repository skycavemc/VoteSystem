package de.hakuyamu.skybee.votesystem.commands;

import com.mongodb.client.model.Filters;
import de.hakuyamu.skybee.votesystem.VoteSystem;
import de.hakuyamu.skybee.votesystem.enums.Message;
import de.hakuyamu.skybee.votesystem.enums.VCShopItem;
import de.hakuyamu.skybee.votesystem.models.User;
import de.leonheuer.mcguiapi.gui.GUI;
import de.leonheuer.mcguiapi.gui.GUIPattern;
import de.leonheuer.mcguiapi.utils.ItemBuilder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bson.conversions.Bson;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VCShopCommand implements CommandExecutor {

    private final VoteSystem main;

    public VCShopCommand(VoteSystem main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Message.PLAYER_ONLY.getString().get(false));
            return true;
        }

        GUIPattern pattern = GUIPattern
                .ofPattern("b_______b", "b_______b", "b_______b", "b_______b", "b_______b")
                .startAtLine(1)
                .withMaterial('b', ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name("§0").asItem());
        GUI gui = main.getGuiFactory().createGUI(5, "§3VoteCoin Shop")
                .formatPattern(pattern);
        for (VCShopItem item : VCShopItem.values()) {
            ItemBuilder icon = ItemBuilder.of(item.getIcon());
            if (item.getCost() > 0) {
                icon.description(
                        "&7Klicke, um &b" + item.getTitle(),
                        "&7für &3" + item.getCost() + " VoteCoins &7zu kaufen."
                );
            }
            gui.setItem(item.getRow(), item.getColumn(), icon.asItem(), event -> {
                // TODO check space
                if (item.getCost() < 0) {
                    player.sendMessage(Message.VCSHOP_SOON.getString().get());
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, .7f, .7f);
                    return;
                }
                Bson filter = Filters.eq("uuid", player.getUniqueId().toString());
                User user = main.getUserCollection().find(filter).first();
                if (user == null || user.getVoteCoins() < item.getCost()) {
                    player.sendMessage(Message.VCSHOP_NOT_ENOUGH.getString().get());
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, .7f, .7f);
                    return;
                }
                item.getAction().accept(player);
                user.setVoteCoins(user.getVoteCoins() - item.getCost());
                main.getUserCollection().replaceOne(filter, user);
                player.sendMessage(Message.VCSHOP_BUY.getString()
                        .replace("%item", item.getTitle())
                        .replace("%amount", "" + item.getCost())
                        .get());
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .7f, .7f);
            });
        }
        gui.show(player);
        return true;
    }

}
