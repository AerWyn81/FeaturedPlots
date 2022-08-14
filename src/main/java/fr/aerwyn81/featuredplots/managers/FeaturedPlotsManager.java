package fr.aerwyn81.featuredplots.managers;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.Category;
import fr.aerwyn81.featuredplots.data.FPlot;
import fr.aerwyn81.featuredplots.handlers.CategoryHandler;
import fr.aerwyn81.featuredplots.handlers.FPlotHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class FeaturedPlotsManager {
    private final FeaturedPlots main;

    private final CategoryHandler categoryHandler;
    private final FPlotHandler fPlotHandler;

    private final HashMap<Category, ArrayList<FPlot>> featuredPlots;

    public FeaturedPlotsManager(FeaturedPlots main) {
        this.main = main;
        this.categoryHandler = new CategoryHandler(main);
        this.fPlotHandler = new FPlotHandler(main);

        this.featuredPlots = new HashMap<>();
    }

    public HashMap<Category, ArrayList<FPlot>> getFeaturedPlots() {
        return featuredPlots;
    }

    public CategoryHandler getCategoryHandler() {
        return categoryHandler;
    }

    public FPlotHandler getPlotHandler() {
        return fPlotHandler;
    }

    public void loadStorage() {
        this.categoryHandler.loadCategories();
        this.fPlotHandler.loadPlots();

        getPlotHandler().getPlots().forEach(p -> {
            var category = getCategoryHandler().getCategoryByName(p.getConfigCategory());
            if (category != null) {
                p.setCategory(category);
            }
        });
    }

    //region Categories

    public void createCategory(String name) throws Exception {
        if (name.isEmpty()) {
            throw new Exception("Category name cannot be empty");
        }

        if (getCategoryHandler().getCategoryByName(name) != null) {
            throw new Exception("Category " + name + " already exist");
        }

        categoryHandler.create(name);
    }

    public void deleteCategory(Category category) throws Exception {
        categoryHandler.delete(category);
    }

    //endregion

    //region Plots

    public void createPlot(@Nullable Plot plot, Category category) throws Exception {
        if (plot == null) {
            throw new Exception("Plot location not found");
        }

        PlotPlayer<?> plotPlayer = PlotSquared.platform().playerManager().getPlayerIfExists(plot.getOwnerAbs());
        if (plotPlayer == null && !plot.hasOwner()) {
            throw new Exception("Plot has no owner");
        }

        var plotFound = getPlotHandler().getPlotsById(plot.getId().toDashSeparatedString());
        if (plotFound != null) {
            throw new Exception("This plot already exist in category " + plotFound.getCategory().getName());
        }

        var name = main.getLanguageHandler().getMessageWithoutColoring("Config.PlotDefaultName")
                .replaceAll("%plotId%", plot.getId().toDashSeparatedString())
                .replaceAll("%player%", plotPlayer == null ? "UnknownPlayer" : plotPlayer.getName());

        fPlotHandler.create(name, plot, category);
    }

    public void deletePlot(FPlot fPlot) throws Exception {
        fPlotHandler.delete(fPlot);
    }

    //endregion
}
