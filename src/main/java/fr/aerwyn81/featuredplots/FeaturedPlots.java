package fr.aerwyn81.featuredplots;

import com.plotsquared.core.PlotAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class FeaturedPlots extends JavaPlugin {

    private PlotAPI plotSquaredAPI;

    @Override
    public void onEnable() {
        this.plotSquaredAPI = new PlotAPI();
    }

    @Override
    public void onDisable() {
    }

    public PlotAPI getPlotSquaredAPI() {
        return plotSquaredAPI;
    }
}
