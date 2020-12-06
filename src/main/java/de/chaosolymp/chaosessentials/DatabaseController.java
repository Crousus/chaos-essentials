package de.chaosolymp.chaosessentials;

import de.chaosolymp.chaosessentials.chestshoplog.AverageResponse;
import de.chaosolymp.chaosessentials.chestshoplog.ChestShopPurchase;
import de.chaosolymp.chaosessentials.perks.Purchase;
import de.chaosolymp.chaosessentials.tokens.Token;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseController {

    private final JavaPlugin plugin;
    private Purchase purchase;
    private final String prefix;

    public DatabaseController() {
        this.plugin = ChaosEssentials.getPlugin();
        prefix = plugin.getConfig().getString("tableprefix");
    }

    public DatabaseController(Purchase purchase) {
        this.purchase = purchase;
        this.plugin = ChaosEssentials.getPlugin();
        prefix = plugin.getConfig().getString("tableprefix");
    }


    public boolean createTables() {

        String buyTable = "CREATE TABLE IF NOT EXISTS " + prefix + "purchases ( purchaseID INT(16) NOT NULL AUTO_INCREMENT , sender INT NOT NULL ,"
                + " receiver INT NOT NULL , command VARCHAR(32) NOT NULL , purchaseTime timestamp NOT NULL ,"
                + " status VARCHAR(10) NOT NULL, duration INT(3) NOT NULL, PRIMARY KEY (purchaseID), FOREIGN KEY (sender) REFERENCES " + prefix + "players (player_id), FOREIGN KEY (receiver) REFERENCES " + prefix + "players (player_id));";

        String chestShopTable = "CREATE TABLE IF NOT EXISTS " + prefix + "shoplogs ( `transactionId` INT NOT NULL AUTO_INCREMENT , `owner` INT NOT NULL , `client` INT NOT NULL ," +
                " `quantity` INT NOT NULL , `price` DOUBLE NOT NULL , `item` VARCHAR(64) NOT NULL , `time` DATE NOT NULL , `special_name` VARCHAR(128), `type` BOOLEAN NOT NULL," +
                " PRIMARY KEY (`transactionId`), FOREIGN KEY (owner) REFERENCES " + prefix + "players (player_id), FOREIGN KEY (client) REFERENCES " + prefix + "players (player_id))";

        String playerTable = "CREATE TABLE IF NOT EXISTS " + prefix + "players ( `player_id` INT NOT NULL AUTO_INCREMENT , `uuid` VARCHAR(36) NOT NULL , `first_join` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                " `last_logout` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                " PRIMARY KEY (`player_id`, `uuid`));";

        String tokenTable = "CREATE TABLE IF NOT EXISTS " + prefix + "tokens ( `token_id` INT NOT NULL AUTO_INCREMENT , `token_uuid` VARCHAR(36) NOT NULL , `creator_id` INT(11) NOT NULL," +
                " `command` VARCHAR(512), `multi_use` BOOLEAN DEFAULT FALSE, valid_until TIMESTAMP," +
                " PRIMARY KEY (`token_id`, `token_uuid`), FOREIGN KEY (`creator_id`) REFERENCES " + prefix + "players (player_id));";

        String devaluationsTable = "CREATE TABLE IF NOT EXISTS " + prefix + "devaluations ( `devaluation_id` INT NOT NULL AUTO_INCREMENT , `token_id` INT(11) NOT NULL , `user_id` INT(11) NOT NULL," +
                " `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                " PRIMARY KEY (`devaluation_id`), FOREIGN KEY (`token_id`) REFERENCES " + prefix + "tokens (token_id), FOREIGN KEY (`user_id`) REFERENCES " + prefix + "players (player_id));";

        String violationsTable = "CREATE TABLE IF NOT EXISTS " + prefix + "violations ( `violation_id` INT NOT NULL AUTO_INCREMENT , `player_id` INT(11) NOT NULL , `token_id` INT(11) NOT NULL," +
                " `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                " PRIMARY KEY (`violation_id`), FOREIGN KEY (`token_id`) REFERENCES " + prefix + "tokens (token_id), FOREIGN KEY (`player_id`) REFERENCES " + prefix + "players (player_id));";

        Connection connection = DatabaseProvider.getConnection(plugin);

        try {
            PreparedStatement cShopStmt = connection.prepareStatement(chestShopTable);
            PreparedStatement buyStmt = connection.prepareStatement(buyTable);
            PreparedStatement playerStmt = connection.prepareStatement(playerTable);
            PreparedStatement tokenStmt = connection.prepareStatement(tokenTable);
            PreparedStatement devaluationsStmt = connection.prepareStatement(devaluationsTable);
            PreparedStatement violationsStmt = connection.prepareStatement(violationsTable);
            playerStmt.executeUpdate();
            buyStmt.executeUpdate();
            cShopStmt.executeUpdate();
            tokenStmt.executeUpdate();
            devaluationsStmt.executeUpdate();
            violationsStmt.executeUpdate();
            connection.close();
            addPlayer("c796b66c-a367-3137-a9dc-55f2befab2b9");
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public void getPurchase() {
        String sql = "SELECT purchaseTime, duration, status FROM " + plugin.getConfig().getString("tableprefix") + "purchases" +
                " WHERE receiver = (SELECT player_id FROM " + prefix + "players WHERE uuid = ?) AND command = ? ORDER BY purchaseID DESC;";
        Connection connection = DatabaseProvider.getConnection(plugin);
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, purchase.getTarget().getUniqueId().toString());
            stmt.setString(2, purchase.getCommand());
            ResultSet result = stmt.executeQuery();
            result.next();
            purchase.setTime(result.getTimestamp("purchaseTime"));
            connection.close();
        } catch (SQLException | NullPointerException e) {
            purchase.setTime(null);
            System.out.println(e);
        }
    }

    public void addPurchase() {

        try {

            String sql = "INSERT INTO " + plugin.getConfig().getString("tableprefix") + "purchases" + " (purchaseID, sender, receiver, command, purchaseTime, status, duration) "
                    + "VALUES (NULL,(SELECT player_id FROM " + prefix + "players WHERE uuid = ?),(SELECT player_id FROM " + prefix + "players WHERE uuid = ?),?,current_timestamp(),?,?);";
            Connection connection = DatabaseProvider.getConnection(plugin);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, purchase.getSender().getUniqueId().toString());
            stmt.setString(2, purchase.getTarget().getUniqueId().toString());
            stmt.setString(3, purchase.getCommand());
            stmt.setString(4, purchase.getType());
            stmt.setInt(5, purchase.getDuration());
            stmt.executeUpdate();
            connection.close();

        } catch (SQLException e) {
        }
    }

    public boolean takeCommandUse(String command, Player player) {
        try {

            String sql = "UPDATE " + prefix + "purchases" + " SET `duration` = `duration` - 1 "
                    + "WHERE `receiver` = (SELECT player_id FROM " + prefix + "players WHERE uuid = ?) AND `command` = ? AND `duration` > 1 ORDER BY purchaseID DESC;";

            System.out.println(sql);
            Connection connection = DatabaseProvider.getConnection(plugin);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, command);
            stmt.executeUpdate();

            if (stmt.getUpdateCount() == 0) {
                connection.close();
                return false;
            }
            connection.close();

        } catch (SQLException | NullPointerException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public void addTransaction(ChestShopPurchase p) {
        try {
            String sql = "INSERT INTO " + prefix + "shoplogs " +
                    "(`transactionId`, `owner`, `client`, `quantity`, `price`, `item`, `time`, `special_name`, `type`) VALUES (NULL, " +
                    "(SELECT player_id FROM " + prefix + "players WHERE uuid = ?), (SELECT player_id FROM " + prefix + "players WHERE uuid = ?), ?, ?, ?, CURDATE(), ?, ?);";
            Connection connection = DatabaseProvider.getConnection(plugin);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, p.getOwner());
            stmt.setString(2, p.getClient());
            stmt.setInt(3, p.getQuantity());
            stmt.setDouble(4, p.getPrice());
            stmt.setString(5, p.getItem());
            stmt.setString(6, p.getSpecialName());
            stmt.setBoolean(7, p.getBoolType());
            stmt.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public AverageResponse getAverage(String item, String specialName, int days) {
        String sql;
        if (specialName == null)
            specialName = "";
        if (days <= 0)
            sql = "SELECT AVG(price / quantity) AS avg, COUNT(*) AS count FROM ce_shoplogs WHERE item= ? AND special_name = ?";

        else
            sql = "SELECT AVG(price / quantity) AS avg, COUNT(*) AS count FROM ce_shoplogs WHERE item= ? AND special_name = ? AND DATEDIFF(CURRENT_DATE,time) <= ?";

        Connection connection = DatabaseProvider.getConnection(plugin);
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, item);
            stmt.setString(2, specialName);
            if (days > 0)
                stmt.setInt(3, days);

            ResultSet result = stmt.executeQuery();
            AverageResponse response = new AverageResponse();

            if (result.next()) {
                response.setAverage(result.getDouble("avg"));
                response.setCount(result.getInt("count"));
            } else {
                response.setAverage(0);
                response.setCount(0);
            }
            connection.close();
            return response;
        } catch (SQLException | NullPointerException e) {
            System.out.println(e);
            return null;
        }
    }

    public void addPlayer(String player) {
        try {
            String sql = "INSERT INTO " + prefix + "players (`player_id`, `uuid`) SELECT player_id, uuid FROM (SELECT NULL AS player_id, ? AS uuid) AS tmp WHERE NOT EXISTS " +
                    "( SELECT uuid FROM " + prefix + "players WHERE uuid = ?) LIMIT 1;";
            Connection connection = DatabaseProvider.getConnection(plugin);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, player);
            stmt.setString(2, player);
            stmt.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void updatePlayer(String player) {
        try {
            String sql = "UPDATE " + plugin.getConfig().getString("tableprefix") + "players " +
                    "  SET `last_logout` = current_timestamp() WHERE uuid = ?;";
            Connection connection = DatabaseProvider.getConnection(plugin);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, player);
            stmt.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void addToken(Token token) {
        try {
            String sql = "INSERT INTO " + prefix + "tokens (`token_id`, `token_uuid`, `creator_id`, `command`, `multi_use`, `valid_until`) VALUES " +
                    "(NULL, ?, (SELECT player_id FROM " + prefix + "players WHERE uuid = ?), ?, ?, ?);";
            System.out.println(sql);
            Connection connection = DatabaseProvider.getConnection(plugin);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, token.getUuid());
            stmt.setString(2, token.getPlayer());
            stmt.setString(3, token.getCommand());
            stmt.setBoolean(4, token.isMultiUse());
            stmt.setDate(5, token.getValidAsDate());
            stmt.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Token getToken(String uuid) {
    String sql = "SELECT token.command, token.token_uuid, token.multi_use, token.valid_until, players.uuid FROM " + prefix + "tokens AS token, "+ prefix + "players AS players" +
                " WHERE token.token_uuid = ? AND token.creator_id = players.player_id;";
        Connection connection = DatabaseProvider.getConnection(plugin);
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, uuid);
            ResultSet result = stmt.executeQuery();
            Token token = null;
            if(result.next()) {
                token = new Token();
                token.setCommand(result.getString(1));
                token.setUuid(result.getString(2));
                token.setMultiUse(result.getBoolean(3));
                token.setValidUntil(result.getDate(4).toLocalDate());
                token.setPlayer(result.getString(5));
                checkValidy(token);
            }
            connection.close();
            return token;
        } catch (SQLException | NullPointerException e) {
            System.out.println(e);
            return null;
        }
    }

    public boolean checkValidy(Token token){
        String sql = "SELECT user_id, time FROM " + prefix + "devaluations WHERE token_id = ?";
        Connection connection = DatabaseProvider.getConnection(plugin);
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1,token.getUuid());
            ResultSet result = stmt.executeQuery();
            if(result.next()){
                token.setPlayer(result.getString(1));
                token.setRedeem(result.getTimestamp(2));
                return true;
            }
            else
                return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

