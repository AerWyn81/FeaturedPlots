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
import java.util.List;
import java.util.stream.Collectors;

public class FPlot extends Item {

    private static final String CONFIG_PS_SUFFIX = ".ps";
    private static final String CONFIG_FP_SUFFIX = ".fp";

    private String plotCompleteId;

    private String plotId;
    private String plotWorld;
    private String plotAreaId;
    private String plotAreaWorld;

    private final List<Category> categories;
    private List<String> configCategories;

    private FPlot(String plotCompleteId, String name, ArrayList<String> description, ItemStack icon, List<String> configCategories, String plotId, String plotWorld, String plotAreaId, String plotAreaWorld) {
        super(name, description, icon);

        this.plotCompleteId = plotCompleteId;
        this.plotId = plotId;
        this.plotWorld = plotWorld;
        this.plotAreaId = plotAreaId;
        this.plotAreaWorld = plotAreaWorld;

        this.categories = new ArrayList<>();
        this.configCategories = configCategories;
    }

    private FPlot(String name, ArrayList<String> description, ItemStack icon, ArrayList<Category> categories) {
        super(name, description, icon);

        this.categories = categories;
    }

    /**
     * Default constructor, initialize a plot by name, plot and category and default icon
     *
     * @param name     {@link String} of the plot
     */
    public FPlot(String name, ItemStack icon, Plot plot) {
        this(name, new ArrayList<>(), icon, new ArrayList<>());

        this.plotCompleteId = (plot.getArea() != null ? plot.getArea().getWorldName() : plot.getWorldName()) + ";" + plot.getId();
        this.plotId = plot.getId().toString();
        this.plotWorld = plot.getWorldName();

        var plotArea = plot.getArea();
        if (plotArea != null) {
            this.plotAreaId = plotArea.getId() != null ? plotArea.getId() : "";
            this.plotAreaWorld = plotArea.getWorldName();
        }
    }

    /**
     * Used in PersistentDataContainer to retrieve the fPlot in GUI
     *
     * @return plotId of the fPlot
     */
    @Override
    public String getGuiKey() {
        return plotCompleteId;
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

    public String getPlotId() {
        return plotId;
    }

    public String getPlotCompleteId() {
        return plotCompleteId;
    }

    public List<String> getConfigCategories() {
        return configCategories;
    }

    /**
     * Used to retrieve categories of the plot
     *
     * @return the plot {@link List<Category>} (or {@link Category#defaultCategory()} if category is not found)
     */
    public List<Category> getCategories() {
        return categories.isEmpty() ? List.of(Category.defaultCategory()) : categories;
    }

    public void addCategory(Category category) {
        this.categories.add(category);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
    }

    public String getPlotWorld() {
        return plotWorld;
    }

    public String getPlotAreaId() {
        return plotAreaId;
    }

    public String getPlotAreaWorld() {
        return plotAreaWorld;
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

        var plotCompleteId = section.getName();
        if (plotCompleteId.isEmpty()) {
            throw new Exception("PlotId cannot be empty");
        }

        var configId = section.getString(CONFIG_PS_SUFFIX + ".id");
        var configWorld = section.getString(CONFIG_PS_SUFFIX + ".world");
        var configPlotAreaId = section.getString(CONFIG_PS_SUFFIX + ".plotArea.id");
        var configPlotAreaWorld = section.getString(CONFIG_PS_SUFFIX + ".plotArea.worldName", "");

        var name = section.getString(CONFIG_FP_SUFFIX + ".name");
        var description = new ArrayList<>(section.getStringList(CONFIG_FP_SUFFIX + ".description"));
        var configCategories = section.getStringList(CONFIG_FP_SUFFIX + ".categories");

        var icon = new ItemBuilder(Material.valueOf(section.getString(CONFIG_FP_SUFFIX + ".icon.type", Material.PLAYER_HEAD.name())));

        if (icon.toItemStack().getType() == Material.PLAYER_HEAD) {
            if (section.contains(CONFIG_FP_SUFFIX + ".icon.player")) {
                var playerName = section.getString(CONFIG_FP_SUFFIX + ".icon.player", "");
                icon = icon.setSkullOwner(playerName).setPersistentDataContainer(HeadCacheManager.KEY_HEAD, playerName);
            } else if (section.contains(CONFIG_FP_SUFFIX + ".icon.textureId")) {
                var textureId = section.getString(CONFIG_FP_SUFFIX + ".icon.textureId", "");
                icon = icon.setSkullTexture(textureId).setPersistentDataContainer(HeadCacheManager.KEY_HEAD, textureId);
            }
        }

        return new FPlot(plotCompleteId, name, description, icon.toItemStack(), configCategories, configId, configWorld, configPlotAreaId, configPlotAreaWorld);
    }

    /**
     * Add this plot in config
     * Don't forget to call {@link FeaturedPlotsManager#saveConfig()} to save the config
     *
     * @param config {@link FileConfiguration} of the plugin
     */
    public void addIntoConfig(FileConfiguration config, Plot plot) {
        var psSection = getConfigFPlotSection() + CONFIG_PS_SUFFIX;
        var fpSection = getConfigFPlotSection() + CONFIG_FP_SUFFIX;

        var plotWorldName = plot.getWorldName();
        var plotArea = plot.getArea();

        config.set(psSection + ".id", plot.getId().toString());
        config.set(psSection + ".world", plotWorldName);

        if (plotArea != null) {
            if (plotArea.getId() != null) {
                config.set(psSection + ".plotArea.id", plotArea.getWorldName());
            }

            config.set(psSection + ".plotArea.worldName", plotArea.getWorldName());
        }

        config.set(fpSection + ".name", name);
        config.set(fpSection + ".description", description);
        config.set(fpSection + ".icon.type", icon.getType().name());

        if (icon.getItemMeta() instanceof SkullMeta) {
            config.set(fpSection + ".icon.player", icon.getItemMeta().getPersistentDataContainer().get(HeadCacheManager.KEY_HEAD, PersistentDataType.STRING));
        }

        config.set(fpSection + ".categories", categories.stream().map(Item::getName).collect(Collectors.toList()));
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
        return "plots." + plotCompleteId;
    }
}
