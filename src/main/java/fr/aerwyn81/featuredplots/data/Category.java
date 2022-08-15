package fr.aerwyn81.featuredplots.data;

import fr.aerwyn81.featuredplots.managers.FeaturedPlotsManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Category extends Item {
    private final ArrayList<FPlot> plots;

    private static final ItemStack DEFAULT_ITEM_STACK = new ItemStack(Material.PODZOL);

    private Category(String name, ArrayList<String> description, ItemStack icon) {
        super(name, description, icon);

        this.plots = new ArrayList<>();
    }

    /**
     * Default constructor, initialize a category by name and default icon
     *
     * @param name {@link String} of the category
     */
    public Category(String name) {
        this(name, new ArrayList<>(), DEFAULT_ITEM_STACK);
    }

    /**
     * Used when category is not found
     *
     * @return a new Unknown {@link Category}
     */
    public static Category defaultCategory() {
        return new Category("Unknown");
    }

    /**
     * Return all plot of this category
     *
     * @return a list of {@link FPlot}
     */
    public ArrayList<FPlot> getPlots() {
        return plots;
    }

    /**
     * Read storage and build a Category
     *
     * @param section {@link ConfigurationSection} of a category
     * @return new {@link Category}
     * @throws Exception if there is an issue with storage
     */
    public static Category loadFromConfig(ConfigurationSection section) throws Exception {
        if (section == null) {
            throw new Exception("Content of section cannot be empty");
        }

        var name = section.getName();
        if (name.isEmpty()) {
            throw new Exception("Name cannot be empty");
        }

        var description = new ArrayList<>(section.getStringList("description"));
        var icon = section.getString("icon", Material.PODZOL.name());

        return new Category(name, description, new ItemStack(Material.valueOf(icon)));
    }

    /**
     * Add this category in config
     * Don't forget to call {@link FeaturedPlotsManager#saveConfig()} to save the config
     *
     * @param config {@link FileConfiguration} of the plugin
     */
    public void addIntoConfig(FileConfiguration config) {
        config.set(getConfigCategorySection() + ".description", description);
        config.set(getConfigCategorySection() + ".icon", icon.getType().name());
    }

    /**
     * Remove this category in config
     * Don't forget to call {@link FeaturedPlotsManager#saveConfig()} to save the config
     *
     * @param config {@link FileConfiguration} of the plugin
     */
    public void removeFromConfig(FileConfiguration config) {
        config.set(getConfigCategorySection(), null);
    }

    /**
     * Method for concatenating the categories section in config and category name
     *
     * @return {@link String} config category name root section
     */
    private String getConfigCategorySection() {
        return "categories." + name;
    }
}
