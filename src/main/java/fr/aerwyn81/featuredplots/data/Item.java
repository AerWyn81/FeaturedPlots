package fr.aerwyn81.featuredplots.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public abstract class Item {
    public final String name;
    public final ArrayList<String> description;
    public final ItemStack icon;

    public Item(String name, ArrayList<String> description, ItemStack icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void addIntoConfig(FileConfiguration config) {
        config.set("categories." + name + ".description", description);
        config.set("categories." + name + ".icon", icon.getType().name());
    }
}
