package fun.mntale.midnightSlashBlock.world;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;

public class CanvasWorldManager {
    private World canvasWorld;

    public CanvasWorldManager(Object plugin) {
        this.canvasWorld = createOrGetCanvasWorld();
    }

    public World getCanvasWorld() {
        return canvasWorld;
    }

    public static World createOrGetCanvasWorld() {
        World world = Bukkit.getWorld("canvas");
        if (world == null) {
            WorldCreator creator = new WorldCreator("canvas")
                    .generator(new VoidChunkGenerator())
                    .type(WorldType.FLAT)
                    .generateStructures(false);
            world = creator.createWorld();
        }
        if (world != null) {
            world.getWorldBorder().setSize(512);
            world.getWorldBorder().setCenter(0, 0);
            world.setStorm(false);
            world.setThundering(false);
            world.setWeatherDuration(Integer.MAX_VALUE);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setTime(6000L);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
            world.setGameRule(GameRule.DO_INSOMNIA, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
            world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
            world.setGameRule(GameRule.FALL_DAMAGE, false);
            world.setGameRule(GameRule.FIRE_DAMAGE, false);
            world.setGameRule(GameRule.DROWNING_DAMAGE, false);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            world.setGameRule(GameRule.DO_TILE_DROPS, false);
            world.setSpawnLocation(0, world.getHighestBlockYAt(0, 0) + 32, 0);
            Bukkit.getServer().setDefaultGameMode(GameMode.SURVIVAL);
        }
        return world;
    }

    public static class VoidChunkGenerator extends ChunkGenerator {
        @Override
        public boolean shouldGenerateNoise() { return false; }
        @Override
        public boolean shouldGenerateSurface() { return false; }
        @Override
        public boolean shouldGenerateCaves() { return false; }
        @Override
        public boolean shouldGenerateDecorations() { return false; }
        @Override
        public boolean shouldGenerateMobs() { return false; }
        @Override
        public boolean shouldGenerateStructures() { return false; }

        @Override
        public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
            return new BiomeProvider() {
                @Override
                public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
                    return Biome.THE_VOID;
                }
                @NotNull
                @Override
                public List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
                    return Collections.singletonList(Biome.THE_VOID);
                }
            };
        }
    }
} 