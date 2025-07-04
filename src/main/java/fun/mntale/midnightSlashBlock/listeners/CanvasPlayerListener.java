package fun.mntale.midnightSlashBlock.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.Location;

public class CanvasPlayerListener implements Listener {
    private final World canvasWorld;

    public CanvasPlayerListener(World canvasWorld) {
        this.canvasWorld = canvasWorld;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location spawn = canvasWorld.getSpawnLocation();
        player.teleportAsync(spawn).thenRun(() -> player.setAllowFlight(true));
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(canvasWorld.getSpawnLocation());
        event.getPlayer().setAllowFlight(true);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().equals(canvasWorld)) {
            player.setAllowFlight(true);
        }
    }
} 