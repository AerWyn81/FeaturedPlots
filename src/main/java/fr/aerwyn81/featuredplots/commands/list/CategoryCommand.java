package fr.aerwyn81.featuredplots.commands.list;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.commands.Cmd;
import fr.aerwyn81.featuredplots.commands.HBAnnotations;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.utils.MessageUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@HBAnnotations(command = "category", permission = "featuredplots.admin", args = {"add", "delete", "edit", "list"})
public record CategoryCommand(FeaturedPlots main, LanguageHandler languageHandler) implements Cmd {

    private final static ArrayList<String> SUB_COMMANDS = new ArrayList<>(Arrays.asList("add", "delete", "edit", "list"));

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (args[1].equals("list")) {
            var categories = main.getCategoryHandler().getCategories();

            if (categories.size() == 0) {
                sender.sendMessage(languageHandler.getMessage("Messages.CategoryListEmpty"));
            } else {
                sender.sendMessage(languageHandler.getMessage("Messages.CategoryList") + categories.stream()
                        .map(c -> "§e" + c.getName())
                        .collect(Collectors.joining("§a, §e")));
            }

            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(languageHandler.getMessage("Messages.ErrorCommand"));
            return true;
        }

        var name = args[2];

        try {
            switch (args[1]) {
                case "add" -> {
                    main.getCategoryHandler().create(name);
                    sender.sendMessage(languageHandler.getMessage("Messages.CategoryCreated").replaceAll("%category%", name));
                }
                case "delete" -> {
                    var category = main.getCategoryHandler().getCategoryByName(name);
                    if (category == null) {
                        sender.sendMessage(languageHandler.getMessage("Messages.CategoryNotExist"));
                        return true;
                    }

                    main.getCategoryHandler().delete(category);
                    sender.sendMessage(languageHandler.getMessage("Messages.CategoryDeleted").replaceAll("%category%", name));
                }
                case "edit" -> sender.sendMessage(languageHandler.getPrefix() + " &cThis sub command is not yet available");
                default -> sender.sendMessage(languageHandler.getMessage("Messages.ErrorCommand"));
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
            return main.getCategoryHandler().getCategoriesNames().stream()
                    .filter(arg -> arg.startsWith(args[2]))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return new ArrayList<>();
    }
}
