package fr.aerwyn81.featuredplots.handlers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigHandler {

    private final File configFile;
    private FileConfiguration config;

    public ConfigHandler(File configFile) {
        this.configFile = configFile;
    }

    public void loadConfiguration() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getLanguage() {
        return config.getString("language", "en").toLowerCase();
    }
}
