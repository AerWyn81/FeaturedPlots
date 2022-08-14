package fr.aerwyn81.featuredplots.handlers;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.Category;
import fr.aerwyn81.featuredplots.data.FPlot;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FPlotHandler extends ConfigFileHandler {
    private ArrayList<FPlot> plots;

    public FPlotHandler(FeaturedPlots main) {
        super(main);

        plots = new ArrayList<>();
    }

    public ArrayList<FPlot> getPlots() {
        return plots;
    }

    @Nullable
    public FPlot getPlotsById(String plotId) {
        return plots.stream().filter(p -> p.getPlot().getId().toDashSeparatedString().equals(plotId)).findFirst().orElse(null);
    }

    public ArrayList<FPlot> getPlotsByWorld(String worldName) {
        return plots.stream().filter(p -> p.getConfigWorld().equals(worldName)).collect(Collectors.toCollection(ArrayList::new));
    }

    public void loadPlots() {
        plots.clear();

        ConfigurationSection plots = config.getConfigurationSection("plots");
        if (plots == null) {
            this.plots = new ArrayList<>();
            return;
        }

        plots.getKeys(false).forEach(plot -> {
            ConfigurationSection configSection = config.getConfigurationSection("plots." + plot);

            try {
                var loadedPlot = FPlot.loadFromConfig(configSection);

                var category = main.getCategoryHandler().getCategoryByName(loadedPlot.getConfigCategory());
                if (category != null) {
                    loadedPlot.setCategory(category);
                }

                this.plots.add(loadedPlot);
            } catch (Exception ex) {
                FeaturedPlots.log.sendMessage(MessageUtils.colorize("&cCannot load " + plot + " category: " + ex.getMessage()));
            }
        });
    }

    public void create(@Nullable Plot plot, Category category) throws Exception {
        if (plot == null) {
            throw new Exception("Plot location not found");
        }

        PlotPlayer<?> plotPlayer = PlotSquared.platform().playerManager().getPlayerIfExists(plot.getOwnerAbs());
        if (plotPlayer == null && !plot.hasOwner()) {
            throw new Exception("Plot has no owner");
        }

        var plotFound = getPlotsById(plot.getId().toDashSeparatedString());
        if (plotFound != null) {
            throw new Exception("This plot already exist in category " + plotFound.getCategory().getName());
        }

        String name;
        if (plotPlayer == null) {
            name = "UnknownPlayer";
        } else {
            name = main.getLanguageHandler().getMessageWithoutColoring("Config.PlotDefaultName")
                    .replaceAll("%plotId%", plot.getId().toDashSeparatedString())
                    .replaceAll("%player%", plotPlayer.getName());
        }

        var fPlot = new FPlot(name, plot, category);
        fPlot.addIntoConfig(config);
        saveConfig();

        plots.add(fPlot);
    }

    public void delete(FPlot fPlot) throws Exception {
        fPlot.removeFromConfig(config);
        saveConfig();

        plots.remove(fPlot);
    }
}
