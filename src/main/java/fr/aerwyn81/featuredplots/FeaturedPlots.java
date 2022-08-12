package fr.aerwyn81.featuredplots;

import com.plotsquared.core.PlotAPI;
import fr.aerwyn81.featuredplots.commands.HBCommandExecutor;
import fr.aerwyn81.featuredplots.handlers.ConfigHandler;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.utils.MessageUtils;
import fr.aerwyn81.featuredplots.utils.config.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

@SuppressWarnings("ConstantConditions")
public final class FeaturedPlots extends JavaPlugin {
    public static ConsoleCommandSender log;
    private static FeaturedPlots instance;

    private ConfigHandler configHandler;
    private LanguageHandler languageHandler;

    private PlotAPI plotSquaredAPI;

    @Override
    public void onEnable() {
        instance = this;
        log = Bukkit.getConsoleSender();

        log.sendMessage(MessageUtils.colorize("&3&lF&beatured&2&lP&alots &einitializing..."));

        File configFile = new File(getDataFolder(), "config.yml");

        saveDefaultConfig();
        try {
            ConfigUpdater.update(this, "config.yml", configFile, Collections.emptyList());
        } catch (IOException e) {
            log.sendMessage(MessageUtils.colorize("&cError while loading config file: " + e.getMessage()));
            this.setEnabled(false);
            return;
        }
        reloadConfig();

        if (!Bukkit.getPluginManager().isPluginEnabled("PlotSquared")) {
            log.sendMessage(MessageUtils.colorize("&cPlotSquared required to run this plugin! Disabling..."));
            this.setEnabled(false);
            return;
        }

        this.configHandler = new ConfigHandler(configFile);
        this.configHandler.loadConfiguration();

        this.languageHandler = new LanguageHandler(this, configHandler.getLanguage());
        this.languageHandler.pushMessages();

        this.plotSquaredAPI = new PlotAPI();

        getCommand("featuredplots").setExecutor(new HBCommandExecutor(this));
    }

    @Override
    public void onDisable() {
        log.sendMessage(MessageUtils.colorize("&3&lF&beatured&2&lP&alots &cdisabled!"));
    }

    public static FeaturedPlots getInstance() {
        return instance;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public PlotAPI getPlotSquaredAPI() {
        return plotSquaredAPI;
    }
}