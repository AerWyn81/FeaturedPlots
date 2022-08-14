package fr.aerwyn81.featuredplots.handlers;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class ConfigFileHandler {
    protected final File configFile;
    protected final FeaturedPlots main;
    protected FileConfiguration config;

    public ConfigFileHandler(FeaturedPlots main) {
        this.configFile = new File(main.getDataFolder(), "storage.yml");
        this.main = main;

        loadConfiguration();
    }

    private void loadConfiguration() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() throws Exception {
        config.save(configFile);
    }
}
