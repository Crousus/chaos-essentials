package de.chaosolymp.chaosessentials.tabcomplete;

import de.chaosolymp.chaosessentials.util.VariableCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VariableTabCompleter implements TabCompleter {

    private final List<String> completions = new ArrayList<>();
    private final List<String> COMMANDS = new ArrayList<>();
    private final List<String> second = new ArrayList<>();

    public VariableTabCompleter() {
        for (String s : VariableCache.getInstance().getVarMap().keySet()) {
            completions.add(s);
        }
        COMMANDS.add("var");
        second.add("bl");
        completions.add("i");
        completions.add("info");
        completions.add("clear");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
            Collections.sort(completions);
            return completions;
        } else if (args.length == 2) {
            return second;
        }
        return null;
    }
}
