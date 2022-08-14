package fr.aerwyn81.featuredplots.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class PlayerUtils {

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (!(sender instanceof Player)) {
            return true;
        }

        return sender.hasPermission(permission) || sender.isOp();
    }

    public ItemStack getPlayerSkull(UUID owner) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
