package fr.aerwyn81.featuredplots.commands.list;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.commands.Cmd;
import fr.aerwyn81.featuredplots.commands.FPAnnotations;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.managers.HeadCacheManager;
import fr.aerwyn81.featuredplots.utils.gui.FPMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

@FPAnnotations(command = "reload", permission = "featuredplots.admin")
public record ReloadCommand(FeaturedPlots main, LanguageHandler languageHandler) implements Cmd {

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        HeadCacheManager.clear();

        main.reloadConfig();
        main.getConfigHandler().loadConfiguration();

        main.getLanguageHandler().setLanguage(main.getConfigHandler().getLanguage());
        main.getLanguageHandler().pushMessages();

        for (Player player : Collections.synchronizedCollection(Bukkit.getOnlinePlayers())) {
            var inventory = player.getOpenInventory().getTopInventory();

            if (inventory.getHolder() != null && inventory.getHolder() instanceof FPMenu) {
                player.closeInventory();
            }
        }

        main.getFeaturedPlotsManager().loadConfiguration();
        main.getFeaturedPlotsManager().loadStorage();

        sender.sendMessage(languageHandler.getMessage("Messages.Reload"));
        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
