package fr.aerwyn81.featuredplots.events;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public record OnInventoryClick(FeaturedPlots main) implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) {
            return;
        }

        if (e.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        var entry = main.getGuiManager().getOpenedGuis().get(p.getUniqueId());

        if (entry != null) {
            e.setCancelled(true);
            main.getGuiManager().onGuiClick(p, e.getCurrentItem(), entry.getKey());
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        main.getGuiManager().getOpenedGuis().remove(e.getPlayer().getUniqueId());
    }
}
