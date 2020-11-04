package de.chaosolymp.chaosessentials.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeathsTabCompleter implements TabCompleter {

    private final List<String> completions = new ArrayList<>();
    private final List<String> COMMANDS = new ArrayList<>();

    public DeathsTabCompleter() {
        COMMANDS.add("armor");
        COMMANDS.add("info");
        COMMANDS.add("inv");
        completions.add("armor");
        completions.add("info");
        completions.add("inv");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
            Collections.sort(completions);
            return completions;
        }
        return null;
    }
}