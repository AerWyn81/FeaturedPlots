package fr.aerwyn81.featuredplots.commands.list;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.commands.Cmd;
import fr.aerwyn81.featuredplots.commands.FPAnnotations;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@FPAnnotations(command = "plot", permission = "featuredplots.admin", isPlayerCommand = true, args = {"add", "edit"})
public record FPlotsCommands(FeaturedPlots main, LanguageHandler languageHandler) implements Cmd {

    private final static ArrayList<String> SUB_COMMANDS = new ArrayList<>(Arrays.asList("add", "edit"));

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(String.format("%s \n%s", languageHandler.getMessage("Help.CommandUsage"), languageHandler.getMessage("Help.Plot")));
            return true;
        }

        var player = (Player) sender;

        try {
            switch (args[1]) {
                case "add" -> {
                    var catName = args[2];
                    var category = main.getFeaturedPlotsManager().getCategoryHandler().getCategoryByName(catName);
                    if (category == null) {
                        sender.sendMessage(languageHandler.getMessage("Messages.CategoryNotExist")
                                .replaceAll("%category%", catName));
                        return true;
                    }

                    main.getFeaturedPlotsManager().createPlot(Plot.getPlot(BukkitUtil.adapt(player.getLocation())), category);

                    sender.sendMessage(languageHandler.getMessage("Messages.PlotAddedCategory")
                            .replaceAll("%category%", category.getName()));
                }
                case "edit" -> sender.sendMessage(languageHandler.getPrefix() + MessageUtils.colorize(" &cThis sub command is not yet available"));
                default -> sender.sendMessage(languageHandler.getMessage("Messages.UnknownCommand"));
            }
        } catch (Exception ex) {
            sender.sendMessage(languageHandler.getPrefix() + " " + MessageUtils.colorize("&c" + ex.getMessage()));
        }

        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return SUB_COMMANDS.stream()
                    .filter(arg -> arg.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        if (args.length == 3 && args[1].equals("add")) {
            return main.getFeaturedPlotsManager().getCategoryHandler().getCategoriesNames().stream()
                    .filter(arg -> arg.startsWith(args[2]))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return new ArrayList<>();
    }
}