package fr.aerwyn81.featuredplots.data;

import com.plotsquared.core.plot.PlotId;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class FPlot extends Item {
    private final PlotId Id;
    private final Category Category;

    public FPlot(String name, ArrayList<String> description, ItemStack icon, PlotId id, Category category) {
        super(name, description, icon);

        this.Id = id;
        this.Category = category;
    }

    public PlotId getId() {
        return Id;
    }

    public Category getCategory() {
        return Category;
    }
}
