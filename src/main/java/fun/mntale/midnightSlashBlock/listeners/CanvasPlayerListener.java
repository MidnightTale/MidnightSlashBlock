package fun.mntale.midnightSlashBlock.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import java.util.Objects;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import fun.mntale.midnightSlashBlock.MidnightSlashBlock;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import fun.mntale.midnightSlashBlock.managers.BlockPlaceDataManager;

public class CanvasPlayerListener implements Listener {
    private final World canvasWorld;
    private final Plugin plugin;

    public CanvasPlayerListener(World canvasWorld) {
        this(canvasWorld, null);
    }

    public CanvasPlayerListener(World canvasWorld, Plugin plugin) {
        this.canvasWorld = canvasWorld;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOnline()) {
            MidnightSlashBlock.openColorPickers.remove(player.getUniqueId());
            return;
        }
        BlockPlaceDataManager dataManager = MidnightSlashBlock.getBlockPlaceDataManager();
        if (dataManager != null) dataManager.loadPlayer(player.getUniqueId());
        if (plugin != null) BlockInteractListener.startActionBarTask(player, (org.bukkit.plugin.java.JavaPlugin) plugin);
        Location spawn = canvasWorld.getSpawnLocation();
        if (!player.getWorld().equals(canvasWorld)) {
            player.teleportAsync(spawn).thenRun(() -> player.setAllowFlight(true));
        } else {
            player.setAllowFlight(true);
        }
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

    @EventHandler
    public void onVoidDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Objects.equals(player.getWorld(), canvasWorld)) return;
        if (event.getCause() == DamageCause.VOID) {
            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ();
            int y = canvasWorld.getHighestBlockYAt(x, z) + 1;
            Location safeLoc = new Location(canvasWorld, x + 0.5, y, z + 0.5);
            player.teleportAsync(safeLoc);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getWorld().getName().equalsIgnoreCase("canvas")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getWorld().getName().equalsIgnoreCase("canvas")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MidnightSlashBlock.openColorPickers.remove(player.getUniqueId());
        BlockPlaceDataManager dataManager = MidnightSlashBlock.getBlockPlaceDataManager();
        if (dataManager != null) dataManager.savePlayer(player.getUniqueId());
        BlockInteractListener.stopActionBarTask(player);
    }
} 