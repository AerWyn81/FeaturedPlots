package fr.aerwyn81.featuredplots.data;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Category extends Item {
    private final ArrayList<FPlot> plots;

    private static final ItemStack DEFAULT_ITEM_STACK = new ItemStack(Material.PODZOL);

    public Category(String name) {
        super(name, new ArrayList<>(), DEFAULT_ITEM_STACK);

        this.plots = new ArrayList<>();
    }

    public Category(String name, ArrayList<String> description, ItemStack icon) {
        super(name, description, icon);

        this.plots = new ArrayList<>();
    }

    public ArrayList<FPlot> getPlots() {
        return plots;
    }

    public static Category loadFromConfig(ConfigurationSection section) throws Exception {
        if (section == null) {
            throw new Exception("Content of section cannot be empty");
        }

        var name = section.getName();
        if (name.isEmpty()) {
            throw new Exception("Name cannot be empty");
        }

        var description = new ArrayList<>(section.getStringList("description"));
        var icon = section.getString("icon", Material.PODZOL.name());

        return new Category(name, description, new ItemStack(Material.valueOf(icon)));
    }

    public void removeFromConfig(FileConfiguration config) {
        config.set("categories." + name, null);
    }
}