package fr.aerwyn81.featuredplots.commands.list.explore;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.commands.Cmd;
import fr.aerwyn81.featuredplots.commands.FPAnnotations;
import fr.aerwyn81.featuredplots.data.GuiType;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

@FPAnnotations(command = "explore", permission = "featuredplots.use", isPlayerCommand = true)
public record ExploreCommand(FeaturedPlots main, LanguageHandler languageHandler) implements Cmd {

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        var player = (Player) sender;

        var featuredPlots = main.getFeaturedPlotsManager().getFeaturedPlots();
        if (featuredPlots.keySet().isEmpty()) {
            player.sendMessage(languageHandler.getMessage("Messages.FeaturedPlotsEmpty"));
            return true;
        }

        main.getGuiManager().open(player, GuiType.Categories, new ArrayList<>(main.getFeaturedPlotsManager().getCategoryHandler().getCategories()));
        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return main.getFeaturedPlotsManager().getCategoryHandler().getCategoriesNames().stream()
                    .filter(arg -> arg.startsWith(args[1]))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return new ArrayList<>();
    }
}
