package fr.aerwyn81.featuredplots.managers;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.events.TeleportCause;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.Category;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.utils.ItemBuilder;
import fr.aerwyn81.featuredplots.utils.gui.FPMenu;
import fr.aerwyn81.featuredplots.utils.gui.ItemGUI;
import fr.aerwyn81.featuredplots.utils.gui.pagination.FPPaginationButtonType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GuiManager {
    private final FeaturedPlots main;
    private final LanguageHandler languageHandler;

    private FPMenu fpMenu;

    public GuiManager(FeaturedPlots main) {
        this.main = main;
        this.languageHandler = main.getLanguageHandler();
    }

    public void openCategories(Player p, ArrayList<Category> categories) {
        fpMenu = new FPMenu(main, this, languageHandler.getMessage("Gui.TitleCategories"), 5);

        for (int i = 0; i < categories.size(); i++) {
            var category = categories.get(i);

            fpMenu.addItem(i, new ItemGUI(new ItemBuilder(Material.PLAYER_HEAD)
                    .skullOwner(p.getName())
                    .setName(category.getNameColorized())
                    .setLore(category.getDescriptionColorized())
                    .toItemStack()).addOnClickEvent((event) -> openCategory((Player) event.getWhoClicked(), category)));
        }

        p.openInventory(fpMenu.getInventory());
    }

    public void openCategory(Player p, Category category) {
        fpMenu = new FPMenu(main, this, languageHandler.getMessage("Gui.TitlePlots"), 5);

        for (int i = 0; i < category.getPlots().size(); i++) {
            var plot = category.getPlots().get(i);

            fpMenu.addItem(i, new ItemGUI(new ItemBuilder(Material.PLAYER_HEAD)
                    .skullOwner(p.getName())
                    .setName(category.getNameColorized())
                    .setLore(category.getDescriptionColorized())
                    .toItemStack()).addOnClickEvent((event) -> plot.getPlot().teleportPlayer(BukkitUtil.adapt((Player) event.getWhoClicked()), TeleportCause.COMMAND_VISIT, (result) -> {
            })));
        }

        p.openInventory(fpMenu.getInventory());
    }

    public ItemGUI getDefaultPaginationButtonBuilder(FPPaginationButtonType type, FPMenu inventory) {
        switch (type) {
            case PREV_BUTTON -> {
                if (inventory.getCurrentPage() > 0) {
                    return new ItemGUI(new ItemBuilder(Material.ARROW)
                            .setName("&a&l\u2190 Previous Page")
                            .setLore(
                                    "&aClick to move back to",
                                    "&apage " + inventory.getCurrentPage() + ".")
                            .toItemStack()
                    ).addOnClickEvent(event -> inventory.previousPage(event.getWhoClicked()));
                } else {
                    return null;
                }
            }
            case CURRENT_BUTTON -> {
                return new ItemGUI(new ItemBuilder(Material.NAME_TAG)
                        .setName("&7&lPage " + (inventory.getCurrentPage() + 1) + " of " + inventory.getMaxPage())
                        .setLore(
                                "&7You are currently viewing",
                                "&7page " + (inventory.getCurrentPage() + 1) + "."
                        ).toItemStack()
                ).addOnClickEvent(event -> event.setCancelled(true));
            }
            case NEXT_BUTTON -> {
                if (inventory.getCurrentPage() < inventory.getMaxPage() - 1) {
                    return new ItemGUI(new ItemBuilder(Material.ARROW)
                            .setName("&a&lNext Page \u2192")
                            .setLore(
                                    "&aClick to move forward to",
                                    "&apage " + (inventory.getCurrentPage() + 2) + "."
                            ).toItemStack()
                    ).addOnClickEvent(event -> inventory.nextPage(event.getWhoClicked()));
                } else {
                    return null;
                }
            }
            default -> {
                return null;
            }
        }
    }

    ;
}
