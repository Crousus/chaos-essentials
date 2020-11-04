package de.chaosolymp.chaosessentials.tokens;

import de.chaosolymp.chaosessentials.DatabaseController;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TokenCreator {

    public static String tokenize(ItemStack item, Player player, String command) {
        if (item.getType() != Material.AIR) {
            TagEditor editor = new TagEditor(item);
            String uuid = editor.setTag();
            Token token = new Token();
            token.setPlayer(player);
            token.setUuid(uuid);
            token.setCommand(command);

            DatabaseController db = new DatabaseController();
            db.addToken(token);

            return uuid;
        }
        return null;
    }
}
