package fr.aerwyn81.featuredplots.data;

import com.plotsquared.core.plot.Plot;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class FPlot extends Item {
    private Plot plot;
    private String configPlotId;

    private Category category;
    private String configCategory;

    private String configWorld;

    public FPlot(String configPlotId, String name, ArrayList<String> description, ItemStack icon, String configCategory, String configWorld) {
        super(name, description, icon);

        this.configPlotId = configPlotId;
        this.configCategory = configCategory;
        this.configWorld = configWorld;
    }

    public FPlot(String name, ArrayList<String> description, ItemStack icon, Plot plot, Category category) {
        super(name, description, icon);

        this.plot = plot;
        this.category = category;
    }

    public FPlot(String name, Plot plot, Category category) {
        this(name, new ArrayList<>(), new ItemStack(Material.PLAYER_HEAD), plot, category);
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

    @Nullable
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getConfigWorld() {
        return configWorld;
    }

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
        var icon = section.getString("icon", Material.PLAYER_HEAD.name());
        var configCategory = section.getString("category");

        return new FPlot(configPlotId, name, description, new ItemStack(Material.valueOf(icon)), configCategory, configWorld);
    }

    public void addIntoConfig(FileConfiguration config) {
        var plotId = plot.getId().toString();

        config.set("plots." + plotId + ".name", name);
        config.set("plots." + plotId + ".description", description);
        config.set("plots." + plotId + ".world", plot.getWorldName());
        config.set("plots." + plotId + ".icon", icon.getType().name());
        config.set("plots." + plotId + ".category", category.getName());
    }

    public void removeFromConfig(FileConfiguration config) {
        config.set("plots." + name, null);
    }
}
