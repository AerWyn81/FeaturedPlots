package fr.aerwyn81.featuredplots.utils.gui;

import fr.aerwyn81.featuredplots.managers.GuiManager;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import fr.aerwyn81.featuredplots.utils.gui.pagination.FPPaginationButtonBuilder;
import fr.aerwyn81.featuredplots.utils.gui.pagination.FPPaginationButtonType;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * All credits of this code go to @SamJakob (SpiGUI)
 * https://github.com/SamJakob/SpiGUI
 */
public class FPMenu implements InventoryHolder {
    private final JavaPlugin owner;
    private final GuiManager guiManager;

    private int rowsPerPage;
    private String name;
    private int currentPage;

    private final Map<Integer, ItemGUI> items;

    private Consumer<FPMenu> onClose;

    private FPPaginationButtonBuilder paginationButtonBuilder;

    public FPMenu(JavaPlugin owner, GuiManager guiManager, String name, int rowsPerPage) {
        this.owner = owner;
        this.guiManager = guiManager;

        setName(name);
        this.rowsPerPage = rowsPerPage;
        this.items = new HashMap<>();

        this.currentPage = 0;
    }

    public JavaPlugin getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setRowName(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = MessageUtils.colorize(name);
    }

    public void addItem(int slot, ItemGUI item) {
        items.put(slot, item);
    }

    public int getPageSize() {
        return rowsPerPage * 9;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public void setPaginationButtonBuilder(FPPaginationButtonBuilder paginationButtonBuilder) {
        this.paginationButtonBuilder = paginationButtonBuilder;
    }

    public FPPaginationButtonBuilder getPaginationButtonBuilder() {
        return this.paginationButtonBuilder;
    }

    public void setItem(int page, int slot, ItemGUI button) {
        if (slot < 0 || slot > getPageSize())
            return;

        addItem((page * getPageSize()) + slot, button);
    }

    public void removeItem(int slot) {
        items.remove(slot);
    }

    public void removeItem(int page, int slot) {
        if (slot < 0 || slot > getPageSize())
            return;

        removeItem((page * getPageSize()) + slot);
    }

    public ItemGUI getItem(int slot) {
        if (slot < 0 || slot > getHighestFilledSlot())
            return null;

        return items.get(slot);
    }

    public ItemGUI getItem(int page, int slot) {
        if (slot < 0 || slot > getPageSize())
            return null;

        return getItem((page * getPageSize()) + slot);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
    }

    public int getMaxPage() {
        return (int) Math.ceil(((double) getHighestFilledSlot() + 1) / ((double) getPageSize()));
    }

    public int getHighestFilledSlot() {
        int slot = 0;

        for (int nextSlot : items.keySet()) {
            if (items.get(nextSlot) != null && nextSlot > slot)
                slot = nextSlot;
        }

        return slot;
    }

    public boolean nextPage(HumanEntity viewer) {
        if (currentPage < getMaxPage() - 1) {
            currentPage++;
            refreshInventory(viewer);
            return true;
        }

        return false;
    }

    public boolean previousPage(HumanEntity viewer) {
        if (currentPage > 0) {
            currentPage--;
            refreshInventory(viewer);
            return true;
        }

        return false;
    }

    public Consumer<FPMenu> getOnClose() {
        return this.onClose;
    }

    public void setOnClose(Consumer<FPMenu> onClose) {
        this.onClose = onClose;
    }

    public void refreshInventory(HumanEntity viewer) {
        if (!(viewer.getOpenInventory().getTopInventory().getHolder() instanceof FPMenu)
                || viewer.getOpenInventory().getTopInventory().getHolder() != this) {
            return;
        }

        if (viewer.getOpenInventory().getTopInventory().getSize() != getPageSize() + (getMaxPage() > 0 ? 9 : 0)) {
            viewer.openInventory(getInventory());
            return;
        }

        viewer.getOpenInventory().getTopInventory().setContents(getInventory().getContents());
    }


    @NotNull
    @Override
    public Inventory getInventory() {
        boolean needsPagination = getMaxPage() > 0;

        Inventory inventory = Bukkit.createInventory(this, (needsPagination ? getPageSize() + 9 : getPageSize()), name);

        for (int key = currentPage * getPageSize(); key < (currentPage + 1) * getPageSize(); key++) {
            if (key > getHighestFilledSlot()) break;

            if (items.containsKey(key)) {
                inventory.setItem(key - (currentPage * getPageSize()), items.get(key).getIcon());
            }
        }

        if (needsPagination) {
            int pageSize = getPageSize();
            for (int i = pageSize; i < pageSize + 9; i++) {
                int offset = i - pageSize;

                ItemGUI paginationButton = guiManager.getDefaultPaginationButtonBuilder(FPPaginationButtonType.forSlot(offset), this);
                inventory.setItem(i, paginationButton != null ? paginationButton.getIcon() : null);
            }
        }

        return inventory;
    }
}
