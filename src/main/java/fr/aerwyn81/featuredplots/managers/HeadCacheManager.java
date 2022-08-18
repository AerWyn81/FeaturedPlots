package fr.aerwyn81.featuredplots.managers;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

public class HeadCacheManager {
    public static NamespacedKey KEY_HEAD;

    private static HashMap<String, ItemStack> headsCache;

    public static void initialise(FeaturedPlots main) {
        headsCache = new HashMap<>();

        KEY_HEAD = new NamespacedKey(main, "PlayerName");
    }

    public static void clear() {
        headsCache.clear();
    }

    public static ItemStack getHead(ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta meta)) {
            return itemStack;
        }

        var playerName = meta.getPersistentDataContainer().get(KEY_HEAD, PersistentDataType.STRING);

        if (headsCache.containsKey(playerName)) {
            return headsCache.get(playerName);
        }

        headsCache.put(playerName, itemStack);
        return itemStack;
    }
}
