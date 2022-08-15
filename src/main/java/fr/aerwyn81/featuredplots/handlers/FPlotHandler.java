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

    public FPlotHandler(FeaturedPlotsManager manager) {
        this.manager = manager;

        plots = new ArrayList<>();
    }

    public ArrayList<FPlot> getPlots() {
        return plots;
    }

    @Nullable
    public FPlot getPlotsById(String plotId) {
        return getPlots().stream().filter(p -> p.getPlot().getId().toDashSeparatedString().equals(plotId)).findFirst().orElse(null);
    }

    public ArrayList<FPlot> getPlotsByWorld(String worldName) {
        return getPlots().stream().filter(p -> p.getConfigWorld().equals(worldName)).collect(Collectors.toCollection(ArrayList::new));
    }

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

    public FPlot create(String name, @Nullable Plot plot, Category category) throws Exception {
        var fPlot = new FPlot(name, plot, category);
        category.getPlots().add(fPlot);
        fPlot.addIntoConfig(manager.getConfig());
        manager.saveConfig();

        plots.add(fPlot);

        return fPlot;
    }

    public void delete(FPlot fPlot) throws Exception {
        fPlot.removeFromConfig(manager.getConfig());
        manager.saveConfig();

        plots.remove(fPlot);
    }
}
