package fr.aerwyn81.featuredplots.commands;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.commands.list.CategoryCommands;
import fr.aerwyn81.featuredplots.commands.list.FPlotsCommands;
import fr.aerwyn81.featuredplots.commands.list.Help;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.utils.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class FPCommandExecutor implements CommandExecutor, TabCompleter {
    private final HashMap<String, FPCommand> registeredCommands;

    private final LanguageHandler languageHandler;
    private final Help helpCommand;

    public FPCommandExecutor(FeaturedPlots main) {
        this.languageHandler = main.getLanguageHandler();
        this.registeredCommands = new HashMap<>();

        this.helpCommand = new Help(main);

        this.register(helpCommand);
        this.register(new CategoryCommands(main, languageHandler));
        this.register(new FPlotsCommands(main, languageHandler));
    }

    private void register(Cmd c) {
        FPCommand command = new FPCommand(c);

        registeredCommands.put(command.getCommand(), command);

        if (command.isVisible()) {
            helpCommand.addCommand(command);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command c, @NotNull String s, String[] args) {
        if (args.length <= 0) {
            sender.sendMessage(languageHandler.getMessage("Messages.ErrorCommand"));
            return false;
        }

        FPCommand command = registeredCommands.get(args[0].toLowerCase());

        if (command == null) {
            sender.sendMessage(languageHandler.getMessage("Messages.ErrorCommand"));
            return false;
        }

        if (!PlayerUtils.hasPermission(sender, command.getPermission())) {
            sender.sendMessage(languageHandler.getMessage("Messages.NoPermission"));
            return false;
        }

        if (command.isPlayerCommand() && !(sender instanceof Player)) {
            sender.sendMessage(languageHandler.getMessage("Messages.PlayerOnly"));
            return false;
        }

        String[] argsWithoutCmd = Arrays.copyOfRange(args, 1, args.length);

        if (command.getArgs().length > 0 && Arrays.stream(command.getArgs()).noneMatch(a -> Arrays.asList(argsWithoutCmd).contains(a))) {
            sender.sendMessage(languageHandler.getMessage("Messages.ErrorCommand"));
            return false;
        }

        return registeredCommands.get(args[0].toLowerCase()).getCmdClass().perform(sender, args);
    }

    @Override
    public ArrayList<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command c, @NotNull String s, String[] args) {
        if (args.length == 1) {
            return registeredCommands.keySet().stream()
                    .filter(arg -> arg.startsWith(args[0].toLowerCase()))
                    .filter(arg -> registeredCommands.get(arg).isVisible())
                    .filter(arg -> PlayerUtils.hasPermission(sender, registeredCommands.get(arg).getPermission())).distinct()
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        if (!registeredCommands.containsKey(args[0].toLowerCase())) {
            return new ArrayList<>();
        }

        FPCommand command = registeredCommands.get(args[0].toLowerCase());

        if (!PlayerUtils.hasPermission(sender, command.getPermission())) {
            return new ArrayList<>();
        }

        return command.getCmdClass().tabComplete(sender, args);
    }
}