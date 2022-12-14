package fr.aerwyn81.featuredplots.data;

import fr.aerwyn81.featuredplots.managers.FeaturedPlotsManager;
import fr.aerwyn81.featuredplots.utils.ItemBuilder;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Category extends Item {
    private String displayName;
    private final ArrayList<FPlot> plots;

    private static final ItemStack DEFAULT_ITEM_STACK = new ItemStack(Material.GRASS_BLOCK);

    private Category(String name, ArrayList<String> description, ItemStack icon) {
        super(name, description, icon);

        this.displayName = name;
        this.plots = new ArrayList<>();
    }

    /**
     * Used in PersistentDataContainer to retrieve the category in GUI
     *
     * @return name of the category
     */
    @Override
    public String getGuiKey() {
        return name;
    }

    /**
     * Retrieve the colored name of the category
     *
     * @return {@link String} colorized string
     */
    @Override
    public String getNameColorized() {
        return MessageUtils.colorize(displayName);
    }

    /**
     * Retrieve the colored lore of the category
     *
     * @return {@link ArrayList<String>} colorized strings
     */
    @Override
    public ArrayList<String> getDescriptionColorized() {
        return MessageUtils.colorize(description);
    }

    private Category(String name, String displayName, ArrayList<String> description, ItemStack icon) {
        this(name, description, icon);

        this.displayName = displayName;
    }

    /**
     * Default constructor, initialize a category by name and default icon
     *
     * @param name {@link String} of the category
     */
    public Category(String name) {
        this(name, new ArrayList<>(), DEFAULT_ITEM_STACK);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

        var displayName = section.getString("name", name);
        var description = new ArrayList<>(section.getStringList("description"));
        var icon = new ItemStack(Material.valueOf(section.getString("icon.type", Material.GRASS_BLOCK.name())));

        if (icon.getType() == Material.PLAYER_HEAD) {
            if (section.contains("icon.player")) {
                icon = new ItemBuilder(icon).setSkullOwner(section.getString("icon.player", "")).toItemStack();
            } else if (section.contains("icon.textureId")) {
                icon = new ItemBuilder(icon).setSkullTexture(section.getString("icon.textureId", "")).toItemStack();
            }
        }

        return new Category(name, displayName, description, icon);
    }

    /**
     * Add this category in config
     * Don't forget to call {@link FeaturedPlotsManager#saveConfig()} to save the config
     *
     * @param config {@link FileConfiguration} of the plugin
     */
    public void addIntoConfig(FileConfiguration config) {
        config.set(getConfigCategorySection() + ".name", displayName);
        config.set(getConfigCategorySection() + ".description", description);
        config.set(getConfigCategorySection() + ".icon.type", icon.getType().name());
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
