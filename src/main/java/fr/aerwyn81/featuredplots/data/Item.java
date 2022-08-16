package fr.aerwyn81.featuredplots.data;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public abstract class Item {
    protected final String name;
    protected final ArrayList<String> description;
    protected final ItemStack icon;

    public Item(String name, ArrayList<String> description, ItemStack icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public abstract String getNameColorized();

    public abstract ArrayList<String> getDescriptionColorized();

    public ItemStack getIcon() {
        return icon;
    }
}
