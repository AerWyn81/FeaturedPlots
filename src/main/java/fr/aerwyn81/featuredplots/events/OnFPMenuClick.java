package fr.aerwyn81.featuredplots.events;

import fr.aerwyn81.featuredplots.managers.GuiManager;
import fr.aerwyn81.featuredplots.utils.gui.FPMenu;
import fr.aerwyn81.featuredplots.utils.gui.ItemGUI;
import fr.aerwyn81.featuredplots.utils.gui.pagination.FPPaginationButtonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

public record OnFPMenuClick(JavaPlugin owner, GuiManager guiManager) implements Listener {

    /**
     * Used to catch interaction with the internal GUI
     *
     * @param event {@link InventoryClickEvent}
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof FPMenu clickedGui) {

            if (!clickedGui.getOwner().equals(owner)) return;

            event.setCancelled(true);

            if (event.getSlot() > clickedGui.getPageSize()) {
                int offset = event.getSlot() - clickedGui.getPageSize();
                FPPaginationButtonType buttonType = FPPaginationButtonType.forSlot(offset);

                ItemGUI paginationButton = guiManager.getDefaultPaginationButtonBuilder(buttonType, clickedGui);

                if (paginationButton != null) {
                    paginationButton.getOnClickEvent().accept(event);
                }

                return;
            }

            ItemGUI button = clickedGui.getItem(clickedGui.getCurrentPage(), event.getSlot());
            if (button != null && button.getOnClickEvent() != null) {
                button.getOnClickEvent().accept(event);
            }
        }
    }

    /**
     * Used to handle closing of the internal GUI
     *
     * @param event {@link InventoryCloseEvent}
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof FPMenu clickedGui) {
            if (!clickedGui.getOwner().equals(owner)) return;

            if (clickedGui.getOnClose() != null)
                clickedGui.getOnClose().accept(clickedGui);
        }
    }
}
