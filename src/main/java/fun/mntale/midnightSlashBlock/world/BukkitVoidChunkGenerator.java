package fun.mntale.midnightSlashBlock.world;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Modern Bukkit void world generator for 1.18+ (Paper 1.21+).
 * Uses new generation API: disables all vanilla generation steps for a pure void world.
 * Sets all biomes to plains for a single-biome void world.
 */
public class BukkitVoidChunkGenerator extends ChunkGenerator {
    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        // Do nothing: leave chunk empty (void)
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        // Add a barrier layer at y = 63 (CANVAS_Y - 1) within the canvas bounds
        final int CANVAS_Y = 210;
        final int CANVAS_MIN = -256, CANVAS_MAX = 255;
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = minX + dx;
                int z = minZ + dz;
                if (x >= CANVAS_MIN && x <= CANVAS_MAX && z >= CANVAS_MIN && z <= CANVAS_MAX) {
                    chunkData.setBlock(dx, CANVAS_Y - 1, dz, org.bukkit.Material.BARRIER);
                }
            }
        }
    }

    @Override
    public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        // Do nothing: no bedrock
    }

    @Override
    public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        // Do nothing: no caves
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return new BiomeProvider() {
            @NotNull
            @Override
            public Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
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