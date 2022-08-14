package fr.aerwyn81.featuredplots.handlers;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.Category;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CategoryHandler extends ConfigFileHandler {
    private ArrayList<Category> categories;

    public CategoryHandler(FeaturedPlots main) {
        super(main);

        categories = new ArrayList<>();
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public ArrayList<String> getCategoriesNames() {
        return categories.stream().map(Category::getName).collect(Collectors.toCollection(ArrayList::new));
    }

    public Category getCategoryByName(String name) {
        return categories.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    public void loadCategories() {
        categories.clear();

        ConfigurationSection categories = config.getConfigurationSection("categories");
        if (categories == null) {
            this.categories = new ArrayList<>();
            return;
        }

        categories.getKeys(false).forEach(cat -> {
            ConfigurationSection configSection = config.getConfigurationSection("categories." + cat);

            try {
                this.categories.add(Category.loadFromConfig(configSection));
            } catch (Exception ex) {
                FeaturedPlots.log.sendMessage(MessageUtils.colorize("&cCannot load " + cat + " category: " + ex.getMessage()));
            }
        });
    }

    public void create(String name) throws Exception {
        if (name.isEmpty()) {
            throw new Exception("Category name cannot be empty");
        }

        if (getCategoryByName(name) != null) {
            throw new Exception("Category " + name + " already exist");
        }

        var category = new Category(name);

        category.addIntoConfig(config);
        saveConfig();

        categories.add(category);
    }

    public void delete(Category category) throws Exception {
        category.removeFromConfig(config);
        saveConfig();

        categories.remove(category);
    }
}
