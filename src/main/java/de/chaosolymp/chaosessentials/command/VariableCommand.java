package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import de.chaosolymp.chaosessentials.util.RegionCheck;
import de.chaosolymp.chaosessentials.util.VariableCache;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class VariableCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;
        int line = 2;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.variable")) {
                boolean isCommand = false;
                Material m = null;
                if (args.length > 0) {
                    m = Material.getMaterial(args[0].toUpperCase());
                    if (args[0].equals("clear") || args[0].equals("info") || args[0].equals("i")) {
                        isCommand = true;
                    }
                    if (args.length > 1 && (args[1].equals("bl") || args[1].equals("blacklist"))) {
                        line = 3;
                    }
                } else {
                    if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                        m = player.getInventory().getItemInMainHand().getType();
                    }
                }

                if (m != null || isCommand) {
                    BlockState blockState = player.getTargetBlock(null, 6).getState();
                    if (blockState instanceof Sign) {
                        Sign sign = (Sign) blockState;

                        if (sign.getLine(1).equals("[Pipe]")) {

                            if (RegionCheck.canBuild(player, sign.getLocation())) {

                                String var = null;
                                if (!isCommand)
                                    var = VariableCache.getInstance().getValue(m.toString().toLowerCase());
                                String signText = sign.getLine(line);
                                if (var != null || isCommand) {
                                    if (!isCommand) {
                                        var = "%" + var + "%";
                                        if (signText.contains(var)) {
                                            signText = signText.replaceAll("," + var + ",", "");
                                            signText = signText.replaceAll(var + ",", "");
                                            signText = signText.replaceAll("," + var, "");
                                            signText = signText.replaceAll(var, "");
                                        } else {
                                            if (!signText.equals("")) {
                                                var = "," + var;
                                            }
                                            if (signText.length() + var.length() <= 15) {
                                                signText = signText + var;
                                            } else {
                                                MessageConverter.sendConfMessage(player, "no_sign_space");
                                            }
                                        }
                                    } else {
                                        if (args[0].equals("clear")) {
                                            signText = "";
                                        } else if (args[0].equals("i") || args[0].equals("info")) {
                                            String[] vars = sign.getLine(line).split(",");
                                            String msg = ChaosEssentials.getPlugin().getConfig().getString("pipe_info");
                                            String varString = "";
                                            for (String v : vars) {
                                                if (VariableCache.getInstance().getKeyFromValue(v) != null)
                                                    varString += " " + VariableCache.getInstance().getKeyFromValue(v);
                                            }
                                            msg = msg.replaceAll("%items%", varString);

                                            vars = sign.getLine(line + 1).split(",");
                                            varString = "";
                                            for (String v : vars) {
                                                varString += " " + VariableCache.getInstance().getKeyFromValue(v);
                                            }

                                            msg = msg.replaceAll("%blacklist%", varString);
                                            MessageConverter.sendMessage(player, msg);
                                        }
                                    }
                                } else {
                                    MessageConverter.sendMessage(player, ChaosEssentials.getPlugin().getConfig().getString("no_var_found").
                                            replaceAll("%item%", m.toString().toLowerCase()));
                                }
                                sign.setLine(line, signText);
                                sign.update();
                            } else {
                                MessageConverter.sendConfMessage(player, "no_region_member");
                            }
                        } else {
                            MessageConverter.sendConfMessage(player, "no_pipe");
                        }
                    } else {
                        MessageConverter.sendConfMessage(player, "no_sign");
                    }
                } else {
                    MessageConverter.sendConfMessage(player, "no_valid_item");
                }
            }
        }
        return false;
    }

}
