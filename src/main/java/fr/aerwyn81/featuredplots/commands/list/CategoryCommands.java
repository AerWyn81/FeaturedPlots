package fr.aerwyn81.featuredplots.commands.list;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.commands.Cmd;
import fr.aerwyn81.featuredplots.commands.FPAnnotations;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@FPAnnotations(command = "category", permission = "featuredplots.admin", args = {"add", "delete", "edit", "list"})
public record CategoryCommands(FeaturedPlots main, LanguageHandler languageHandler) implements Cmd {

    private final static ArrayList<String> SUB_COMMANDS = new ArrayList<>(Arrays.asList("add", "delete", "edit", "list"));
    private final static String CONFIRM_ARG = "--confirm";

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (args[1].equals("list")) {
            var categories = main.getFeaturedPlotsManager().getCategoryHandler().getCategories();

            if (categories.size() == 0) {
                sender.sendMessage(languageHandler.getMessage("Messages.CategoryListEmpty"));
            } else {
                sender.sendMessage(languageHandler.getMessage("Messages.CategoryList") + categories.stream()
                        .map(c -> "§e" + c.getName())
                        .collect(Collectors.joining("§a, §e")));
            }

            return true;
        }

        if (args.length != 3 && Arrays.stream(args).noneMatch(a -> a.equals(CONFIRM_ARG))) {
            sender.sendMessage(String.format("%s \n%s", languageHandler.getMessage("Help.CommandUsage"), languageHandler.getMessage("Help.Category")));
            return true;
        }

        var name = args[2];

        try {
            switch (args[1]) {
                case "add" -> {
                    main.getFeaturedPlotsManager().createCategory(name);
                    sender.sendMessage(languageHandler.getMessage("Messages.CategoryCreated")
                            .replaceAll("%category%", name));
                }
                case "delete" -> {
                    var category = main.getFeaturedPlotsManager().getCategoryHandler().getCategoryByName(name);
                    if (category == null) {
                        sender.sendMessage(languageHandler.getMessage("Messages.CategoryNotExist")
                                .replaceAll("%category%", name));
                        return true;
                    }

                    int plotCount = category.getPlots().size();

                    if (args.length != 4 && plotCount > 0) {
                        sender.sendMessage(languageHandler.getMessage("Messages.CategoryDeleteConfirmation")
                                .replaceAll("%category%", name)
                                .replaceAll("%plots%", String.valueOf(plotCount)));
                        return true;
                    }

                    main.getFeaturedPlotsManager().deleteCategory(category);
                    sender.sendMessage(languageHandler.getMessage("Messages.CategoryDeleted")
                            .replaceAll("%category%", name));
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

        if (args.length == 3 && args[1].equals("delete")) {
            return main.getFeaturedPlotsManager().getCategoryHandler().getCategoriesNames().stream()
                    .filter(arg -> arg.startsWith(args[2]))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        if (args.length == 4 && !args[2].isEmpty() && args[1].equals("delete"))
            return new ArrayList<>(Collections.singletonList(CONFIRM_ARG));

        return new ArrayList<>();
    }
}
