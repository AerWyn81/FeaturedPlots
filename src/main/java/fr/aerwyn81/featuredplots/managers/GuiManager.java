package fr.aerwyn81.featuredplots.managers;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.events.TeleportCause;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.Category;
import fr.aerwyn81.featuredplots.data.FPlot;
import fr.aerwyn81.featuredplots.data.GuiType;
import fr.aerwyn81.featuredplots.data.Item;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;


public class GuiManager {
    private final FeaturedPlots main;
    private final LanguageHandler languageHandler;

    private final HashMap<UUID, Map.Entry<GuiType, Inventory>> openedGuis;
    private final HashMap<GuiType, Inventory> guis;

    private static NamespacedKey PDC_NAME;

    public GuiManager(FeaturedPlots main) {
        this.main = main;
        this.languageHandler = main.getLanguageHandler();

        this.openedGuis = new HashMap<>();
        this.guis = new HashMap<>();

        PDC_NAME = new NamespacedKey(main, "Name");

        init();
    }

    public HashMap<UUID, Map.Entry<GuiType, Inventory>> getOpenedGuis() {
        return openedGuis;
    }

    public void init() {
        guis.clear();

        Arrays.stream(GuiType.values())
                .forEach(this::create);
    }

    public void create(GuiType type) {
        Inventory gui = Bukkit.createInventory(null, 54,
                type == GuiType.Categories ?
                        languageHandler.getMessage("Gui.TitleCategories") :
                        languageHandler.getMessage("Gui.TitlePlots"));

        ItemStack edge = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).toItemStack();

        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 49, 51, 52, 53)
                .forEach(i -> gui.setItem(i, edge));

        guis.put(type, gui);
    }

    public void openPage(ArrayList<Item> items, Player player, int page) {
        if (page > 0 && items.size() < (page * 28) + 1) {
            openPage(items, player, page - 1);
            return;
        }

        // Simple verification but should never happen in this case
        var firstItem = items.stream().findFirst();
        if (firstItem.isEmpty()) {
            return;
        }

        var type = items.stream().findFirst().get() instanceof Category ? GuiType.Categories : GuiType.Plots;
        var gui = guis.get(type);

        for (int i = 10; i <= 16; i++)
            gui.setItem(i, null);
        for (int i = 19; i <= 25; i++)
            gui.setItem(i, null);
        for (int i = 28; i <= 34; i++)
            gui.setItem(i, null);

        if (page > 0) {
            gui.setItem(48, new ItemBuilder(Material.ENDER_PEARL).setName(languageHandler.getMessage("Gui.PreviousPage")).toItemStack());
        } else {
            gui.setItem(48, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).toItemStack());
        }

        if (items.size() > (page + 1) * 28) {
            gui.setItem(50, new ItemBuilder(Material.ENDER_PEARL).setName(languageHandler.getMessage("Gui.NextPage")).toItemStack());
        } else {
            gui.setItem(50, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).toItemStack());
        }

        if (items.size() > 28)
            items = new ArrayList<>(items.subList(page * 28, Math.min(((page * 28) + 28), items.size())));

        for (Item item : items) {
            var itemStack = new ItemBuilder(item.getIcon())
                    .setName(item.getNameColorized())
                    .setLore(item.getDescriptionColorized()).toItemStack();

            ItemMeta itemMeta = itemStack.getItemMeta();
            if (item instanceof Category category) {
                itemMeta.getPersistentDataContainer().set(PDC_NAME, PersistentDataType.STRING, category.getName());
            } else if (item instanceof FPlot plot) {
                itemMeta.getPersistentDataContainer().set(PDC_NAME, PersistentDataType.STRING, plot.getPlot().getId().toString());
            }

            itemStack.setItemMeta(itemMeta);
            gui.addItem(itemStack);
        }

        if (!openedGuis.containsKey(player.getUniqueId())) {
            player.openInventory(gui);
        } else {
            player.getOpenInventory().getTopInventory().setContents(gui.getContents());
        }

        Bukkit.getScheduler().runTaskLater(main, () -> openedGuis.put(player.getUniqueId(), Map.entry(type, gui)), 1L);
    }

    public void onGuiClick(Player p, ItemStack is, GuiType type) {
        var name = is.getItemMeta().getPersistentDataContainer().get(PDC_NAME, PersistentDataType.STRING);

        if (type == GuiType.Categories) {
            var category = main.getFeaturedPlotsManager().getCategoryHandler().getCategoryByName(name);
            openPage(new ArrayList<>(category.getPlots()), p, 0);
        } else {
            var fPlot = main.getFeaturedPlotsManager().getPlotHandler().getPlotsById(name);

            if (fPlot != null) {
                fPlot.getPlot().teleportPlayer(BukkitUtil.adapt(p), TeleportCause.COMMAND_VISIT, result -> {
                });
            }
        }
    }
}
