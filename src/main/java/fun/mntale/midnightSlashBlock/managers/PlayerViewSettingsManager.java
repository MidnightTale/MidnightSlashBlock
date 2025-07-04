package fun.mntale.midnightSlashBlock.managers;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class PlayerViewSettingsManager {
    private final Plugin plugin;
    private final NamespacedKey hideKey;

    public PlayerViewSettingsManager(Plugin plugin) {
        this.plugin = plugin;
        this.hideKey = new NamespacedKey(plugin, "hide_players");
    }

    public void setHidePlayers(Player player, boolean hide) {
        player.getPersistentDataContainer().set(hideKey, PersistentDataType.BYTE, hide ? (byte)1 : (byte)0);
    }

    public boolean isHidingPlayers(Player player) {
        return player.getPersistentDataContainer().getOrDefault(hideKey, PersistentDataType.BYTE, (byte)0) == (byte)1;
    }
} 