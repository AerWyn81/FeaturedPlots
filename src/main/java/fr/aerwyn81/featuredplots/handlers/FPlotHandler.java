package fr.aerwyn81.featuredplots.handlers;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.OfflinePlotPlayer;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.Category;
import fr.aerwyn81.featuredplots.data.FPlot;
import fr.aerwyn81.featuredplots.managers.FeaturedPlotsManager;
import fr.aerwyn81.featuredplots.managers.HeadCacheManager;
import fr.aerwyn81.featuredplots.utils.ItemBuilder;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Optional;
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
    public Optional<FPlot> getPlotsById(String plotId, String worldName) {
        return getPlotsByWorld(worldName).stream().filter(p -> p.getPlotId().equals(plotId)).findFirst();
    }

    /**
     * Used to retrieve all plots from a specific world
     *
     * @param worldName {@link String} world name
     * @return list of {@link FPlot} object
     */
    public ArrayList<FPlot> getPlotsByWorld(String worldName) {
        return getPlots().stream().filter(p -> p.getPlotWorld().equals(worldName)).collect(Collectors.toCollection(ArrayList::new));
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
    public FPlot create(String name, Plot plot, Category category) throws Exception {
        FPlot fPlot;

        var existingPlot = plots.stream().filter(p -> p.getPlotId().equals(plot.getId().toString())).findFirst();

        if (existingPlot.isPresent()) {
            fPlot = existingPlot.get();
            fPlot.addCategory(category);
        } else {
            var icon = new ItemBuilder(Material.PLAYER_HEAD);
            String playerName = "";

            PlotPlayer<?> plotPlayer = PlotSquared.platform().playerManager().getPlayerIfExists(plot.getOwnerAbs());

            if (plotPlayer != null) {
                playerName = plotPlayer.getName();
            } else {
                OfflinePlotPlayer player = PlotSquared.platform().playerManager().getOfflinePlayer(plot.getOwnerAbs());

                if (player != null) {
                    playerName = player.getName();
                }
            }

            if (!playerName.isEmpty()) {
                icon.setSkullOwner(playerName).setPersistentDataContainer(HeadCacheManager.KEY_HEAD, playerName);
            }

            fPlot = new FPlot(name, HeadCacheManager.getHead(icon.toItemStack()), plot);
            fPlot.addCategory(category);
            category.getPlots().add(fPlot);
            plots.add(fPlot);
        }

        fPlot.addIntoConfig(manager.getConfig(), plot);
        manager.saveConfig();

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

    /**
     * Used to remove a {@link Category} in {@link FPlot}
     *
     * @param fPlot {@link FPlot} FPlot to delete
     * @throws Exception if there is a storage issue
     */
    public void delete(FPlot fPlot, Category category) throws Exception {
        fPlot.removeCategory(category);
        fPlot.saveInConfig(manager.getConfig());
        manager.saveConfig();
    }
}
