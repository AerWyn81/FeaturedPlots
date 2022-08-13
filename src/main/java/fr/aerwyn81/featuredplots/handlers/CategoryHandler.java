package fr.aerwyn81.featuredplots.handlers;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.Category;
import fr.aerwyn81.featuredplots.utils.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CategoryHandler {

    private final File configFile;
    private FileConfiguration config;

    private ArrayList<Category> categories;

    public CategoryHandler(FeaturedPlots main) {
        this.configFile = new File(main.getDataFolder(), "categories.yml");

        categories = new ArrayList<>();
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public ArrayList<String> getCategoriesNames() {
        return categories.stream().map(Category::getName).collect(Collectors.toCollection(ArrayList::new));
    }

    @Nullable
    public Category getCategoryByName(String name) {
        return categories.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    public void loadCategories() {
        config = YamlConfiguration.loadConfiguration(configFile);

        this.categories.clear();

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

        if (categories.stream().anyMatch(c -> c.getName().equals(name))) {
            throw new Exception("Category with name " + name + " already exist");
        }

        var category = new Category(name);

        category.addIntoConfig(config);
        config.save(configFile);

        this.categories.add(category);
    }

    public void delete(Category category) throws Exception {
        category.removeFromConfig(config);
        config.save(configFile);

        categories.remove(category);
    }
}