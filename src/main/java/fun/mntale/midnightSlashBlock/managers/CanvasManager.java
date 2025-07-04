package fun.mntale.midnightSlashBlock.managers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CanvasManager {
    private final ServerLevel world;
    private final int size;
    private final int baseY = 64;
    private final BlockState baseBlock = Blocks.WHITE_CONCRETE.defaultBlockState();
    // Map of (x,z) to BlockState for persistence
    private final Map<String, String> placedBlocks = new HashMap<>();
    private final File dataFile;

    public CanvasManager(ServerLevel world, int size, File dataFile) {
        this.world = world;
        this.size = size;
        this.dataFile = dataFile;
    }

    public void createOrLoadCanvas() {
        int min = -(size / 2);
        int max = (size / 2) - 1;
        if (dataFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        int x = Integer.parseInt(parts[0]);
                        int z = Integer.parseInt(parts[1]);
                        String blockName = parts[2];
                        BlockState state = getBlockStateByName(blockName);
                        if (state != null) {
                            setBlockColor(new BlockPos(x, baseY, z), state);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            for (int x = min; x <= max; x++) {
                for (int z = min; z <= max; z++) {
                    BlockPos pos = new BlockPos(x, baseY, z);
                    world.setBlockAndUpdate(pos, baseBlock);
                }
            }
        }
    }

    public boolean isInCanvas(BlockPos pos) {
        int min = -(size / 2);
        int max = (size / 2) - 1;
        return pos.getY() == baseY &&
                pos.getX() >= min && pos.getX() <= max &&
                pos.getZ() >= min && pos.getZ() <= max;
    }

    public void setBlockColor(BlockPos pos, BlockState state) {
        world.setBlockAndUpdate(pos, state);
        String key = pos.getX() + ":" + pos.getZ();
        if (state == baseBlock) {
            placedBlocks.remove(key);
        } else {
            placedBlocks.put(key, getBlockName(state));
        }
    }

    public void saveCanvas() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Map.Entry<String, String> entry : placedBlocks.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BlockState getBlockStateByName(String name) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse(name))
                .map(Block::defaultBlockState)
                .orElse(null);
    }

    private String getBlockName(BlockState state) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }
} 