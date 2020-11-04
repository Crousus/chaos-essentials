package de.chaosolymp.chaosessentials.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EditPermission {

    private final LuckPerms api = LuckPermsProvider.get();

    public void addPermission(Player player, String nodeString, long duration, List<String> servers, List<String> worlds) {

        api.getUserManager().modifyUser(player.getUniqueId(), user -> {

            PermissionNode.Builder builder = PermissionNode.builder(nodeString);

            builder.value(true);

            if (duration > 0)
                builder.expiry(duration, TimeUnit.HOURS);

            for (String s : servers)
                builder.withContext(DefaultContextKeys.SERVER_KEY, s);

            for (String w : worlds)
                builder.withContext(DefaultContextKeys.WORLD_KEY, w);

            PermissionNode node = builder.build();
            user.data().add(node);

        });
    }

    public void removePermission(Player player, String nodeString) {
        api.getUserManager().modifyUser(player.getUniqueId(), user -> {
            Collection<PermissionNode> nodeCollection = user.getNodes(NodeType.PERMISSION);
            for (PermissionNode node : nodeCollection) {
                if (node.getKey().equals(nodeString)) {
                    user.data().remove(node);
                    break;
                }
            }
        });
    }

    public boolean hasPermission(Player player, String nodeString) {
        User user = api.getUserManager().getUser(player.getUniqueId());
        Collection<PermissionNode> nodeCollection = user.getNodes(NodeType.PERMISSION);
        for (PermissionNode node : nodeCollection) {
            if (node.getKey().equals(nodeString)) {
                return true;
            }
        }
        return false;
    }

    public void addGroupPermission(String groupName, String nodeString) {
        api.getGroupManager().modifyGroup(groupName, group -> {
            PermissionNode.Builder builder = PermissionNode.builder(nodeString);

            builder.value(true).expiry(24, TimeUnit.HOURS);
            group.data().add(builder.build());

        });

    }
}