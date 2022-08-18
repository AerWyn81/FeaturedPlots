package fr.aerwyn81.featuredplots.managers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.UUID;

public class HeadCacheManager {
    private static HashMap<UUID, ItemStack> headsCache;

    public static void initialise() {
        headsCache = new HashMap<>();
    }

    public static void clear() {
        headsCache.clear();
    }

    public static ItemStack getHead(ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta meta)) {
            return itemStack;
        }

        if (headsCache.containsKey(meta.getOwnerProfile().getUniqueId())) {
            return headsCache.get(meta.getOwnerProfile().getUniqueId());
        }

        headsCache.put(meta.getOwnerProfile().getUniqueId(), itemStack);
        return itemStack;
    }
}
