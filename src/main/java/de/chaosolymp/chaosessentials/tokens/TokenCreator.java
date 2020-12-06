package de.chaosolymp.chaosessentials.tokens;

import de.chaosolymp.chaosessentials.DatabaseController;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;

public class TokenCreator {

    public static String tokenize(ItemStack item, Player player, String command, boolean isMultiUse, LocalDate date) {
        if (item.getType() != Material.AIR) {
            TagEditor editor = new TagEditor(item);
            String uuid = editor.setTag();
            Token token = new Token();
            token.setMultiUse(isMultiUse);
            token.setValidUntil(date);
            token.setPlayer(player.getUniqueId().toString());
            token.setUuid(uuid);
            token.setCommand(command);

            DatabaseController db = new DatabaseController();
            db.addToken(token);

            return uuid;
        }
        return null;
    }

    public static boolean isValid(Token token){
        if(token !=null) {
            if(token.getRedeem() == null){
                return true;
            }
        }
        return false;

    }

    public static boolean isValid(ItemStack item) {
        return isValid(getToken(item));
    }

    public static Token getToken(ItemStack item){
        if(item.getType() != Material.AIR){
            DatabaseController db = new DatabaseController();
            TagEditor editor = new TagEditor(item);
            return db.getToken(editor.getTag());
        }
        else
            return null;

    }
}
