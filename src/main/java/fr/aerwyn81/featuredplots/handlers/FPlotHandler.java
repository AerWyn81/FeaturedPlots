package fr.aerwyn81.featuredplots.handlers;

import com.plotsquared.core.plot.Plot;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.Category;
import fr.aerwyn81.featuredplots.data.FPlot;
import fr.aerwyn81.featuredplots.managers.FeaturedPlotsManager;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FPlotHandler {
    private final FeaturedPlotsManager manager;
    private ArrayList<FPlot> plots;

    /**
     * Default constructor for the FPlotHandler
     *
     * @param manager {@link FeaturedPlotsManager} to manage I/O off this class
     */
    public FPlotHandler(FeaturedPlotsManager manager) {
        this.manager = manager;

        plots = new ArrayList<>();
    }

    /**
     * Used to retrieve all plots
     *
     * @return list of {@link FPlot} object
     */
    public ArrayList<FPlot> getPlots() {
        return plots;
    }

    /**
     * Used to find a {@link FPlot} by his plotId
     *
     * @param plotId {@link String} PlotSquared plotId
     * @return a {@link FPlot} object
     */
    @Nullable
    public FPlot getPlotsById(String plotId) {
        return getPlots().stream().filter(p -> p.getPlot().getId().toDashSeparatedString().equals(plotId)).findFirst().orElse(null);
    }

    /**
     * Used to retrieve all plots from a specific world
     *
     * @param worldName {@link String} world name
     * @return list of {@link FPlot} object
     */
    public ArrayList<FPlot> getPlotsByWorld(String worldName) {
        return getPlots().stream().filter(p -> p.getConfigWorld().equals(worldName)).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Used to load all plots from storage
     */
    public void loadPlots() {
        plots.clear();

        ConfigurationSection plots = manager.getConfig().getConfigurationSection("plots");
        if (plots == null) {
            this.plots = new ArrayList<>();
            return;
        }

        plots.getKeys(false).forEach(plot -> {
            ConfigurationSection configSection = manager.getConfig().getConfigurationSection("plots." + plot);

            try {
                this.plots.add(FPlot.loadFromConfig(configSection));
            } catch (Exception ex) {
                FeaturedPlots.log.sendMessage(MessageUtils.colorize("&cCannot load " + plot + " category: " + ex.getMessage()));
            }
        });
    }

    /**
     * Used to create a new FPlot
     *
     * @param name     {@link String} name of the fPlot
     * @param plot     {@link Plot} PlotSquared plot object
     * @param category {@link Category} category of the plot
     * @return fPlot {@link FPlot} FPlot object
     * @throws Exception if there is a storage issue
     */
    public FPlot create(String name, @Nullable Plot plot, Category category) throws Exception {
        var fPlot = new FPlot(name, plot, category);
        category.getPlots().add(fPlot);
        fPlot.addIntoConfig(manager.getConfig());
        manager.saveConfig();

        plots.add(fPlot);

        return fPlot;
    }

    /**
     * Used to remove a {@link FPlot}
     *
     * @param fPlot {@link FPlot} FPlot to delete
     * @throws Exception if there is a storage issue
     */
    public void delete(FPlot fPlot) throws Exception {
        fPlot.removeFromConfig(manager.getConfig());
        manager.saveConfig();

        plots.remove(fPlot);
    }
}
