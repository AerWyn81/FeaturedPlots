package fr.aerwyn81.featuredplots.commands.list;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.commands.Cmd;
import fr.aerwyn81.featuredplots.commands.FPAnnotations;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.utils.PlayerUtils;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@FPAnnotations(command = "plot", permission = "featuredplots.admin", isPlayerCommand = true, args = {"add", "remove"})
public record FPlotsCommands(FeaturedPlots main, LanguageHandler languageHandler) implements Cmd {

    private final static ArrayList<String> SUB_COMMANDS = new ArrayList<>(Arrays.asList("add", "remove"));
    private final static String CONFIRM_ARG = "--confirm";

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        var player = (Player) sender;

        try {
            switch (args[1]) {
                case "add" -> {
                    if (args.length != 3) {
                        sender.sendMessage(String.format("%s \n%s", languageHandler.getMessage("Help.CommandUsage"), languageHandler.getMessage("Help.Plot")));
                        return true;
                    }

                    if (!PlayerUtils.hasPermission(sender, "featuredplots.admin.add")) {
                        sender.sendMessage(languageHandler.getMessage("Messages.NoPermission"));
                        return true;
                    }

                    var catName = args[2];
                    var optCategory = main.getFeaturedPlotsManager().getCategoryHandler().getCategoryByName(catName);
                    if (optCategory.isEmpty()) {
                        sender.sendMessage(languageHandler.getMessage("Messages.CategoryNotExist")
                                .replaceAll("%category%", catName));
                        return true;
                    }

                    var category = optCategory.get();

                    main.getFeaturedPlotsManager().createPlot(Plot.getPlot(BukkitUtil.adapt(player.getLocation())), category);

                    sender.sendMessage(languageHandler.getMessage("Messages.PlotAddedCategory")
                            .replaceAll("%category%", category.getName()));
                }
                case "remove" -> {
                    if (!PlayerUtils.hasPermission(sender, "featuredplots.admin.remove")) {
                        sender.sendMessage(languageHandler.getMessage("Messages.NoPermission"));
                        return true;
                    }

                    var plot = Plot.getPlot(BukkitUtil.adapt(player.getLocation()));
                    if (plot == null) {
                        sender.sendMessage(languageHandler.getMessage("Messages.NotStandingPlot"));
                        return true;
                    }

                    var optFPlot = main.getFeaturedPlotsManager().getPlotHandler().getPlotsById(plot.getId().toString(), plot.getWorldName());
                    if (optFPlot.isEmpty()) {
                        sender.sendMessage(languageHandler.getMessage("Messages.PlotNotFound")
                                .replaceAll("%plot%", plot.getId().toString()));
                        return true;
                    }

                    var catName = "";
                    var fPlot = optFPlot.get();

                    if (args.length == 3) {
                        catName = args[2];

                        if (!Arrays.asList(args).contains(CONFIRM_ARG)) {
                            sender.sendMessage(languageHandler.getMessage("Messages.PlotCategoryRemoveConfirmation")
                                    .replaceAll("%category%", catName)
                                    .replaceAll("%plot%", plot.getId().toString()));
                            return true;
                        }

                        try {
                            var optCategory = main.getFeaturedPlotsManager().getCategoryHandler().getCategoryByName(catName);

                            if (optCategory.isEmpty()) {
                                throw new RuntimeException(languageHandler.getMessage("Messages.CategoryNotExist")
                                        .replaceAll("%category%", catName));
                            }

                            main.getFeaturedPlotsManager().deletePlot(fPlot, optCategory.get());
                        } catch (Exception ex) {
                            sender.sendMessage(ex.getMessage());
                            return true;
                        }
                    } else {
                        if (!Arrays.asList(args).contains("--confirm")) {
                            sender.sendMessage(languageHandler.getMessage("Messages.PlotRemoveConfirmation")
                                    .replaceAll("%plot%", plot.getId().toString()));
                            return true;
                        }

                        main.getFeaturedPlotsManager().deletePlot(fPlot);
                    }

                    if (catName.isEmpty()) {
                        sender.sendMessage(languageHandler.getMessage("Messages.PlotRemoved")
                                .replaceAll("%plot%", plot.getId().toString()));
                    } else {
                        sender.sendMessage(languageHandler.getMessage("Messages.PlotCategoryRemoved")
                                .replaceAll("%plot%", plot.getId().toString())
                                .replaceAll("%category%", catName));
                    }
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

        if ((args.length == 3 && (args[1].equals("add") || args[1].equals("remove")))) {
            return main.getFeaturedPlotsManager().getCategoryHandler().getCategoriesNames().stream()
                    .filter(arg -> arg.startsWith(args[2]))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return new ArrayList<>();
    }
}