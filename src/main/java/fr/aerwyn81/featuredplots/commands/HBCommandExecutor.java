package fr.aerwyn81.featuredplots.commands;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.commands.list.CategoryCommand;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.utils.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HBCommandExecutor implements CommandExecutor, TabCompleter {
    private final HashMap<String, HBCommand> registeredCommands;

    private final LanguageHandler languageHandler;
    //private final Help helpCommand;

    public HBCommandExecutor(FeaturedPlots main) {
        this.languageHandler = main.getLanguageHandler();
        this.registeredCommands = new HashMap<>();

        //this.helpCommand = new Help(main);

        //this.register(helpCommand);
        this.register(new CategoryCommand(main, languageHandler));
    }

    private void register(Cmd c) {
        HBCommand command = new HBCommand(c);

        registeredCommands.put(command.getCommand(), command);

        if (command.isVisible()) {
            //helpCommand.addCommand(command);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        if (args.length <= 0) {
            sender.sendMessage(languageHandler.getMessage("Messages.ErrorCommand"));
            return false;
        }

        HBCommand command = registeredCommands.get(args[0].toLowerCase());

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

        if (argsWithoutCmd.length < command.getArgs().length) {
            sender.sendMessage(languageHandler.getMessage("Messages.ErrorCommand"));
            return false;
        }

        return registeredCommands.get(args[0].toLowerCase()).getCmdClass().perform(sender, args);
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command c, String s, String[] args) {
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

        HBCommand command = registeredCommands.get(args[0].toLowerCase());

        if (!PlayerUtils.hasPermission(sender, command.getPermission())) {
            return new ArrayList<>();
        }

        return command.getCmdClass().tabComplete(sender, args);
    }
}