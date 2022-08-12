package fr.aerwyn81.featuredplots.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public interface Cmd {
    boolean perform(CommandSender sender, String[] args);

    ArrayList<String> tabComplete(CommandSender sender, String[] args);
}
