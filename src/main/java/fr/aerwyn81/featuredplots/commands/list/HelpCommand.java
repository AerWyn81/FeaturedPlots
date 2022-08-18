package fr.aerwyn81.featuredplots.commands.list;

import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.commands.Cmd;
import fr.aerwyn81.featuredplots.commands.FPAnnotations;
import fr.aerwyn81.featuredplots.commands.FPCommand;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import fr.aerwyn81.featuredplots.utils.chat.ChatPageUtils;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@FPAnnotations(command = "help")
public class HelpCommand implements Cmd {
    private final LanguageHandler languageHandler;
    private final ArrayList<FPCommand> registeredCommands;

    public HelpCommand(FeaturedPlots main) {
        this.languageHandler = main.getLanguageHandler();
        this.registeredCommands = new ArrayList<>();
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        ChatPageUtils cpu = new ChatPageUtils(languageHandler, sender)
                .entriesCount(registeredCommands.size())
                .currentPage(args);

        String message = languageHandler.getMessage("Chat.LineTitle");
        if (sender instanceof Player) {
            TextComponent titleComponent = new TextComponent(message);
            cpu.addTitleLine(titleComponent);
        } else {
            sender.sendMessage(message);
        }

        for (int i = cpu.getFirstPos(); i < cpu.getFirstPos() + cpu.getPageHeight() && i < cpu.getSize(); i++) {
            String command = StringUtils.capitalize(registeredCommands.get(i).getCommand());

            if (!languageHandler.hasMessage("Help." + command)) {
                sender.sendMessage(MessageUtils.colorize("&3/fp " + registeredCommands.get(i).getCommand() + " &8: &c&oNo help message found. Please report to developer!"));
            } else {
                message = languageHandler.getMessage("Help." + command);
                if (sender instanceof Player) {
                    cpu.addLine(new TextComponent(message));
                } else {
                    sender.sendMessage(message);
                }
            }
        }

        cpu.addPageLine("help");
        cpu.build();
        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    public void addCommand(FPCommand command) {
        registeredCommands.add(command);
    }
}
