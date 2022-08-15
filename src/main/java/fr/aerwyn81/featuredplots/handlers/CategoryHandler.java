package fr.aerwyn81.featuredplots.handlers;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.Category;
import fr.aerwyn81.featuredplots.managers.FeaturedPlotsManager;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CategoryHandler {
    private final FeaturedPlotsManager manager;
    private ArrayList<Category> categories;

    /**
     * Default constructor for the CategoryHandler
     *
     * @param manager {@link FeaturedPlotsManager} to manage I/O off this class
     */
    public CategoryHandler(FeaturedPlotsManager manager) {
        this.manager = manager;

        categories = new ArrayList<>();
    }

    /**
     * Used to retrieve all categories
     *
     * @return List of {@link Category} object
     */
    public ArrayList<Category> getCategories() {
        return categories;
    }

    /**
     * Used to retrieve all category names
     *
     * @return List of {@link String} category name
     */
    public ArrayList<String> getCategoriesNames() {
        return getCategories().stream().map(Category::getName).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Used to find a Category by his name
     *
     * @return A {@link Category} object
     */
    public Category getCategoryByName(String name) {
        return getCategories().stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Used to load all category in storage
     */
    public void loadCategories() {
        categories.clear();

        ConfigurationSection categories = manager.getConfig().getConfigurationSection("categories");
        if (categories == null) {
            this.categories = new ArrayList<>();
            return;
        }

        categories.getKeys(false).forEach(cat -> {
            ConfigurationSection configSection = manager.getConfig().getConfigurationSection("categories." + cat);

            try {
                this.categories.add(Category.loadFromConfig(configSection));
            } catch (Exception ex) {
                FeaturedPlots.log.sendMessage(MessageUtils.colorize("&cCannot load " + cat + " category: " + ex.getMessage()));
            }
        });
    }

    /**
     * Used to create a new Category
     *
     * @param name {@link String} Name of the category
     * @return a {@link Category} object
     * @throws Exception if there is a storage issue
     */
    public Category create(String name) throws Exception {
        var category = new Category(name);

        category.addIntoConfig(manager.getConfig());
        manager.saveConfig();

        categories.add(category);
        return category;
    }

    /**
     * Used to remove a Category
     *
     * @param category {@link Category} Category to delete
     * @throws Exception if there is a storage issue
     */
    public void delete(Category category) throws Exception {
        category.removeFromConfig(manager.getConfig());
        manager.saveConfig();

        categories.remove(category);
    }
}
