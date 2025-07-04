package fun.mntale.midnightSlashBlock.world;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CanvasWorldManager {
    private World canvasWorld;

    public CanvasWorldManager(Plugin plugin) {
        setupWorld();
    }

    private void setupWorld() {
        // Create/load world
        WorldCreator creator = new WorldCreator("canvas");
        creator.generator(new BukkitVoidChunkGenerator());
        this.canvasWorld = Bukkit.createWorld(creator);
        if (canvasWorld == null) throw new IllegalStateException("Failed to create canvas world");
        // Set border and rules
        canvasWorld.getWorldBorder().setSize(512);
        canvasWorld.getWorldBorder().setCenter(0, 0);
        canvasWorld.setStorm(false);
        canvasWorld.setThundering(false);
        canvasWorld.setWeatherDuration(Integer.MAX_VALUE);
        canvasWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        canvasWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        canvasWorld.setTime(6000L);
        canvasWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        canvasWorld.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        canvasWorld.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        canvasWorld.setGameRule(GameRule.DO_INSOMNIA, false);
        canvasWorld.setGameRule(GameRule.DO_FIRE_TICK, false);
        canvasWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        canvasWorld.setGameRule(GameRule.FALL_DAMAGE, false);
        canvasWorld.setGameRule(GameRule.FIRE_DAMAGE, false);
        canvasWorld.setGameRule(GameRule.DROWNING_DAMAGE, false);
        canvasWorld.setGameRule(GameRule.KEEP_INVENTORY, true);
        canvasWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        canvasWorld.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
        canvasWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        canvasWorld.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
        canvasWorld.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        canvasWorld.setGameRule(GameRule.DO_TILE_DROPS, false);
        canvasWorld.setSpawnLocation(0, canvasWorld.getHighestBlockYAt(0, 0) + 1, 0);
        Bukkit.getServer().setDefaultGameMode(GameMode.ADVENTURE);
        // Allow flight for all players
        for (Player player : canvasWorld.getPlayers()) {
            player.setAllowFlight(true);
        }
    }

    public World getCanvasWorld() {
        return canvasWorld;
    }
} 