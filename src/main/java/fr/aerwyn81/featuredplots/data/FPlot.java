package fr.aerwyn81.featuredplots.data;

import com.plotsquared.core.plot.Plot;
import fr.aerwyn81.featuredplots.managers.FeaturedPlotsManager;
import fr.aerwyn81.featuredplots.managers.HeadCacheManager;
import fr.aerwyn81.featuredplots.utils.ItemBuilder;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class FPlot extends Item {
    private Plot plot;
    private String configPlotId;

    private Category category;
    private String configCategory;

    private String configPlotArea;
    private String configWorld;

    private FPlot(String configPlotId, String name, ArrayList<String> description, ItemStack icon, String configCategory, String configWorld, String configPlotArea) {
        super(name, description, icon);

        this.configPlotId = configPlotId;
        this.configCategory = configCategory;
        this.configWorld = configWorld;
        this.configPlotArea = configPlotArea;
    }

    private FPlot(String name, ArrayList<String> description, ItemStack icon, Plot plot, Category category) {
        super(name, description, icon);

        this.plot = plot;
        this.category = category;
    }

    /**
     * Default constructor, initialize a plot by name, plot and category and default icon
     *
     * @param name     {@link String} of the plot
     * @param plot     {@link Plot} PlotSquared plot
     * @param category {@link Category} of the plot
     */
    public FPlot(String name, Plot plot, Category category, ItemStack icon) {
        this(name, new ArrayList<>(), icon, plot, category);
    }

    /**
     * Used in PersistentDataContainer to retrieve the fPlot in GUI
     *
     * @return plotId of the fPlot
     */
    @Override
    public String getGuiKey() {
        return plot.getId().toString();
    }

    /**
     * Retrieve the colored name of the fPlot
     *
     * @return {@link String} colorized string
     */
    @Override
    public String getNameColorized() {
        return MessageUtils.colorize(name);
    }

    /**
     * Retrieve the colored lore of the fPlot
     *
     * @return {@link ArrayList<String>} colorized strings
     */
    @Override
    public ArrayList<String> getDescriptionColorized() {
        return MessageUtils.colorize(description);
    }

    public String getConfigPlotId() {
        return configPlotId;
    }

    public Plot getPlot() {
        return plot;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }

    public String getConfigCategory() {
        return configCategory;
    }

    /**
     * Used to retrieve the category of the plot
     *
     * @return the plot {@link Category} (or {@link Category#defaultCategory()} if category is not found)
     */
    public Category getCategory() {
        return category != null ? category : Category.defaultCategory();
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getConfigWorld() {
        return configWorld;
    }

    public String getConfigPlotArea() {
        return configPlotArea;
    }

    /**
     * Read storage and build a FPlot
     *
     * @param section {@link ConfigurationSection} of a fPlot
     * @return new {@link FPlot}
     * @throws Exception if there is an issue with storage
     */
    public static FPlot loadFromConfig(ConfigurationSection section) throws Exception {
        if (section == null) {
            throw new Exception("Content of section cannot be empty");
        }

        var configPlotId = section.getName();
        if (configPlotId.isEmpty()) {
            throw new Exception("PlotId cannot be empty");
        }

        var name = section.getString("name");
        var description = new ArrayList<>(section.getStringList("description"));
        var configWorld = section.getString("world");
        var configPlotArea = section.getString("plotArea");
        var configCategory = section.getString("category");

        var icon = new ItemBuilder(Material.valueOf(section.getString("icon.type", Material.PLAYER_HEAD.name())));

        if (icon.toItemStack().getType() == Material.PLAYER_HEAD) {
            if (section.contains("icon.player")) {
                var playerName = section.getString("icon.player", "");
                icon = icon.setSkullOwner(playerName).setPersistentDataContainer(HeadCacheManager.KEY_HEAD, playerName);
            } else if (section.contains("icon.textureId")) {
                var textureId = section.getString("icon.textureId", "");
                icon = icon.setSkullTexture(textureId).setPersistentDataContainer(HeadCacheManager.KEY_HEAD, textureId);
            }
        }

        return new FPlot(configPlotId, name, description, icon.toItemStack(), configCategory, configWorld, configPlotArea);
    }

    /**
     * Add this plot in config
     * Don't forget to call {@link FeaturedPlotsManager#saveConfig()} to save the config
     *
     * @param config {@link FileConfiguration} of the plugin
     */
    public void addIntoConfig(FileConfiguration config) {
        var plotWorldName = plot.getWorldName();
        var plotArea = plot.getArea();

        config.set(getConfigFPlotSection() + ".name", name);
        config.set(getConfigFPlotSection() + ".description", description);
        config.set(getConfigFPlotSection() + ".world", plotWorldName);
        config.set(getConfigFPlotSection() + ".plotArea", plotArea != null ? plotArea.toString() : plotWorldName);
        config.set(getConfigFPlotSection() + ".icon.type", icon.getType().name());

        if (icon.getItemMeta() instanceof SkullMeta) {
            config.set(getConfigFPlotSection() + ".icon.player", icon.getItemMeta().getPersistentDataContainer().get(HeadCacheManager.KEY_HEAD, PersistentDataType.STRING));
        }

        config.set(getConfigFPlotSection() + ".category", category.getName());
    }

    /**
     * Remove this plot in config
     * Don't forget to call {@link FeaturedPlotsManager#saveConfig()} to save the config
     *
     * @param config {@link FileConfiguration} of the plugin
     */
    public void removeFromConfig(FileConfiguration config) {
        config.set(getConfigFPlotSection(), null);
    }

    /**
     * Method for concatenating the fPlot section in config and plotId
     *
     * @return {@link String} config plotId root section
     */
    private String getConfigFPlotSection() {
        return "plots." + plot.getId();
    }
}
