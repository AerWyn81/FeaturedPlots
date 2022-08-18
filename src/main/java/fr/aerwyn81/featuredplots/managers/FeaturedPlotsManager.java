package fr.aerwyn81.featuredplots.managers;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.Category;
import fr.aerwyn81.featuredplots.data.FPlot;
import fr.aerwyn81.featuredplots.handlers.CategoryHandler;
import fr.aerwyn81.featuredplots.handlers.FPlotHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FeaturedPlotsManager {
    private final FeaturedPlots main;
    private final File configFile;

    private FileConfiguration config;

    private final CategoryHandler categoryHandler;
    private final FPlotHandler fPlotHandler;

    private final HashMap<Category, ArrayList<FPlot>> featuredPlots;

    public FeaturedPlotsManager(FeaturedPlots main) {
        this.main = main;

        this.configFile = new File(main.getDataFolder(), "storage.yml");
        loadConfiguration();

        this.categoryHandler = new CategoryHandler(this);
        this.fPlotHandler = new FPlotHandler(this);

        this.featuredPlots = new HashMap<>();
    }

    public void loadConfiguration() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() throws Exception {
        config.save(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
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

    /**
     * Load the storage file and assign plot to a category
     */
    public void loadStorage() {
        this.categoryHandler.loadCategories();
        this.fPlotHandler.loadPlots();

        this.featuredPlots.clear();

        for (FPlot p : getPlotHandler().getPlots()) {
            var category = getCategoryHandler().getCategoryByName(p.getConfigCategory());
            if (category == null) {
                continue;
            }

            p.setCategory(category);
            category.getPlots().add(p);

            if (!featuredPlots.containsKey(category))
                featuredPlots.put(category, new ArrayList<>());

            featuredPlots.get(category).add(p);
        }

        getCategoryHandler().getCategories().stream()
                .filter(c -> !featuredPlots.containsKey(c))
                .forEach(category -> featuredPlots.put(category, new ArrayList<>()));
    }

    //region Categories

    /**
     * Method to use to create a new {@link Category}
     *
     * @param name {@link String} of the category
     * @throws Exception if there is an issue with the creation (duplicates, failcheck, storage...)
     */
    public void createCategory(String name) throws Exception {
        if (name.isEmpty()) {
            throw new Exception("Category name cannot be empty");
        }

        if (getCategoryHandler().getCategoryByName(name) != null) {
            throw new Exception("Category " + name + " already exist");
        }

        var cat = categoryHandler.create(name);
        featuredPlots.put(cat, new ArrayList<>());
    }

    /**
     * Method to use to delete a {@link Category}
     *
     * @param category {@link Category}
     * @throws Exception if there is an issue with the deletion (plots, storage...)
     */
    public void deleteCategory(Category category) throws Exception {
        AtomicBoolean hasDeletionError = new AtomicBoolean(false);

        category.getPlots().forEach(p -> {
            try {
                deletePlot(p);
            } catch (Exception e) {
                hasDeletionError.set(true);
            }
        });

        if (!hasDeletionError.get()) {
            categoryHandler.delete(category);
            featuredPlots.remove(category);
        }
    }

    //endregion

    //region Plots

    /**
     * Method to use to create a new {@link FPlot}
     *
     * @param plot     {@link Plot} PlotSquared plot
     * @param category {@link Category} category of the plot
     * @throws Exception if there is an issue with the creation (duplicates, failcheck, storage...)
     */
    public void createPlot(@Nullable Plot plot, Category category) throws Exception {
        if (plot == null) {
            throw new Exception("Plot location not found");
        }

        PlotPlayer<?> plotPlayer = PlotSquared.platform().playerManager().getPlayerIfExists(plot.getOwnerAbs());
        if (plotPlayer == null && !plot.hasOwner()) {
            throw new Exception("Plot has no owner");
        }

        var plotFound = getPlotHandler().getPlotsById(plot.getId().toString());
        if (plotFound != null) {
            throw new Exception("This plot already exist in category " + plotFound.getCategory().getName());
        }

        var name = main.getLanguageHandler().getMessageWithoutColoring("Config.PlotDefaultName")
                .replaceAll("%plotId%", plot.getId().toString())
                .replaceAll("%player%", plotPlayer == null ? "UnknownPlayer" : plotPlayer.getName());

        var fPlot = fPlotHandler.create(name, plot, category);
        featuredPlots.get(category).add(fPlot);
    }

    /**
     * Method to use to delete a {@link FPlot}
     *
     * @param fPlot {@link FPlot}
     * @throws Exception if there is an issue with the deletion (category, storage...)
     */
    public void deletePlot(FPlot fPlot) throws Exception {
        fPlotHandler.delete(fPlot);
        featuredPlots.get(fPlot.getCategory()).remove(fPlot);
    }

    //endregion
}
