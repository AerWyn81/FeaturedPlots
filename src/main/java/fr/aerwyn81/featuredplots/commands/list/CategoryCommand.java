package fr.aerwyn81.featuredplots.commands.list;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.commands.Cmd;
import fr.aerwyn81.featuredplots.commands.HBAnnotations;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;

@HBAnnotations(command = "category", permission = "featuredplots.admin", args = {"add", "delete"})
public record CategoryCommand(FeaturedPlots main, LanguageHandler languageHandler) implements Cmd {

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(languageHandler.getMessage("Messages.ErrorCommand"));
            return true;
        }

        switch (args[1]) {
            case "add":
                break;
            case "delete":
                break;
        }

        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args) {
        return args.length == 2 ? new ArrayList<>(Arrays.asList("add", "delete")) : new ArrayList<>();
    }
}
