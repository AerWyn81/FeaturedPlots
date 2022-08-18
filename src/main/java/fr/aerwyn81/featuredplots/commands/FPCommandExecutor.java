package fr.aerwyn81.featuredplots.commands;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.commands.list.CategoryCommands;
import fr.aerwyn81.featuredplots.commands.list.FPlotsCommands;
import fr.aerwyn81.featuredplots.commands.list.Help;
import fr.aerwyn81.featuredplots.commands.list.Reload;
import fr.aerwyn81.featuredplots.commands.list.explore.ExploreCommand;
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

    /**
     * Default constructor used to list commands
     *
     * @param main {@link FeaturedPlots} this plugin
     */
    public FPCommandExecutor(FeaturedPlots main) {
        this.languageHandler = main.getLanguageHandler();
        this.registeredCommands = new HashMap<>();

        this.helpCommand = new Help(main);

        this.register(helpCommand);
        this.register(new CategoryCommands(main, languageHandler));
        this.register(new FPlotsCommands(main, languageHandler));
        this.register(new ExploreCommand(main, languageHandler));
        this.register(new Reload(main, languageHandler));
    }

    /**
     * Default constructor used to register commands
     *
     * @param c {@link Cmd} command object
     */
    private void register(Cmd c) {
        FPCommand command = new FPCommand(c);

        registeredCommands.put(command.getCommand(), command);

        if (command.isVisible()) {
            helpCommand.addCommand(command);
        }
    }

    /**
     * Override onCommand of {@link Command} here to check command shared verifications
     *
     * @param sender Source object which is executing this command
     * @param c      The alias of the command used
     * @param args   All arguments passed to the command, split via ' '
     * @return true if the command was successful, otherwise false
     */
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

    /**
     * Override onTabComplete of {@link TabCompleter} here to fill the tab command with plugin commands with permission check
     *
     * @param sender Source of the command.  For players tab-completing a command inside a command block, this will be the player,
     *               not the command block.
     * @param c      Command which was executed
     * @param s      The alias used
     * @param args   The arguments passed to the command, including final
     *               partial argument to be completed and command label
     * @return A List of possible completions for the final argument
     */
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