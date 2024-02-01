package fr.aerwyn81.featuredplots.managers;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.events.TeleportCause;
import com.plotsquared.core.plot.Plot;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.Category;
import fr.aerwyn81.featuredplots.data.FPlot;
import fr.aerwyn81.featuredplots.data.GuiType;
import fr.aerwyn81.featuredplots.data.Item;
import fr.aerwyn81.featuredplots.handlers.ConfigHandler;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.utils.ItemBuilder;
import fr.aerwyn81.featuredplots.utils.gui.FPMenu;
import fr.aerwyn81.featuredplots.utils.gui.ItemGUI;
import fr.aerwyn81.featuredplots.utils.gui.pagination.FPPaginationButtonType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GuiManager {
    private final FeaturedPlots main;
    private final ConfigHandler configHandler;
    private final LanguageHandler languageHandler;

    public GuiManager(FeaturedPlots main) {
        this.main = main;
        this.configHandler = main.getConfigHandler();
        this.languageHandler = main.getLanguageHandler();
    }

    /**
     * Open the inventory for the player
     *
     * @param p     {@link Player} target player
     * @param type  {@link GuiType} inventory type
     * @param items inventory categories or plots
     */
    public void open(Player p, GuiType type, ArrayList<Item> items) {
        FPMenu fpMenu;
        if (type == GuiType.Categories) {
            fpMenu = new FPMenu(main, this, languageHandler.getMessage("Gui.TitleCategories"), false, 5);
        } else {
            fpMenu = new FPMenu(main, this, languageHandler.getMessage("Gui.TitlePlots"), true, 5);
        }

        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);

            var icon = item.getIcon().getType() == Material.PLAYER_HEAD ? HeadCacheManager.getHead(item.getIcon()) : item.getIcon();

            var iconGui = new ItemBuilder(icon.clone())
                    .setName(item.getNameColorized())
                    .setLore(item.getDescriptionColorized())
                    .toItemStack();

            var itemGUI = new ItemGUI(iconGui, item instanceof FPlot || item instanceof Category category && !category.getPlots().isEmpty());

            if (item instanceof Category category) {
                itemGUI.setIconBlocked(configHandler.getCategoryEmptyIcon()
                        .setName(item.getNameColorized() + languageHandler.getMessage("Gui.EmptyCategory"))
                        .setLore(item.getDescriptionColorized())
                        .toItemStack());
                itemGUI.addOnClickEvent(event -> open((Player) event.getWhoClicked(), GuiType.Plots, new ArrayList<>(category.getPlots())));
            } else if (item instanceof FPlot fPlot) {
                var psPlot = Plot.fromString(null, fPlot.getPlotCompleteId());
                if (psPlot == null) {
                    p.sendMessage(languageHandler.getMessage("Messages.PlotNotFound")
                            .replaceAll("%plot%", fPlot.getPlotCompleteId()));
                    return;
                }

                itemGUI.addOnClickEvent(event -> psPlot.teleportPlayer(BukkitUtil.adapt((Player) event.getWhoClicked()), TeleportCause.COMMAND_VISIT, (result) -> {
                }));
            }

            fpMenu.addItem(i, itemGUI);
        }

        p.openInventory(fpMenu.getInventory());
    }

    /**
     * Build the pagination layout for each type of paginate button
     *
     * @param type      {@link FPPaginationButtonType} pagination button type
     * @param inventory {@link FPMenu} inventory to fill
     */
    public ItemGUI getDefaultPaginationButtonBuilder(FPPaginationButtonType type, FPMenu inventory) {
        switch (type) {
            case BACK_BUTTON -> {
                if (inventory.isNestedMenu()) {
                    return new ItemGUI(configHandler.getBackIcon()
                            .setName(languageHandler.getMessage("Gui.Back"))
                            .setLore(languageHandler.getMessages("Gui.BackLore"))
                            .toItemStack()
                    ).addOnClickEvent(event -> open((Player) event.getWhoClicked(), GuiType.Categories,
                            new ArrayList<>(main.getFeaturedPlotsManager().getCategoryHandler().getCategories())));
                } else {
                    return new ItemGUI(configHandler.getBorderIcon().setName("§7").toItemStack());
                }
            }
            case PREV_BUTTON -> {
                if (inventory.getCurrentPage() > 0) {
                    return new ItemGUI(configHandler.getPreviousIcon()
                            .setName(languageHandler.getMessage("Gui.Previous"))
                            .setLore(languageHandler.getMessages("Gui.PreviousLore")
                                    .stream().map(s -> s.replaceAll("%page%", String.valueOf(inventory.getCurrentPage()))).collect(Collectors.toList()))
                            .toItemStack()
                    ).addOnClickEvent(event -> inventory.previousPage(event.getWhoClicked()));
                } else {
                    return new ItemGUI(configHandler.getBorderIcon().setName("§7").toItemStack());
                }
            }
            case CURRENT_BUTTON -> {
                if (configHandler.isDisplayInfoIcon()) {
                    return new ItemGUI(configHandler.getInfoIcon()
                            .setName(languageHandler.getMessage("Gui.Info")
                                    .replaceAll("%page%", String.valueOf((inventory.getCurrentPage() + 1)))
                                    .replaceAll("%max%", String.valueOf(inventory.getMaxPage())))
                            .setLore(languageHandler.getMessages("Gui.InfoLore")
                                    .stream().map(s -> s.replaceAll("%page%", String.valueOf((inventory.getCurrentPage() + 1)))).collect(Collectors.toList()))
                            .toItemStack()
                    );
                } else {
                    return new ItemGUI(configHandler.getBorderIcon().setName("§7").toItemStack());
                }
            }
            case NEXT_BUTTON -> {
                if (inventory.getCurrentPage() < inventory.getMaxPage() - 1) {
                    return new ItemGUI(configHandler.getNextIcon()
                            .setName(languageHandler.getMessage("Gui.Next"))
                            .setLore(languageHandler.getMessages("Gui.NextLore")
                                    .stream().map(s -> s.replaceAll("%page%", String.valueOf((inventory.getCurrentPage() + 2)))).collect(Collectors.toList()))
                            .toItemStack()
                    ).addOnClickEvent(event -> inventory.nextPage(event.getWhoClicked()));
                } else {
                    return new ItemGUI(configHandler.getBorderIcon().setName("§7").toItemStack());
                }
            }
            case CLOSE_BUTTON -> {
                return new ItemGUI(configHandler.getCloseIcon()
                        .setName(languageHandler.getMessage("Gui.Close"))
                        .setLore(languageHandler.getMessages("Gui.CloseLore"))
                        .toItemStack()
                ).addOnClickEvent(event -> event.getWhoClicked().closeInventory());
            }
            default -> {
                return new ItemGUI(configHandler.getBorderIcon().setName("§7").toItemStack());
            }
        }
    }
}
