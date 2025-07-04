package fun.mntale.midnightSlashBlock.managers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CanvasManager {
    private final ServerLevel world;
    private final int size;
    private final int baseY = 64;
    private final BlockState baseBlock = Blocks.WHITE_CONCRETE.defaultBlockState();
    private final Plugin plugin;

    public CanvasManager(ServerLevel world, int size, Plugin plugin) {
        this.world = world;
        this.size = size;
        this.plugin = plugin;
    }

    public void createOrLoadCanvas() {
        if (plugin.getConfig().getBoolean("canvas-initialized", false)) {
            Bukkit.getLogger().info("[MidnightSlashBlock] Canvas already initialized, skipping generation.");
            return;
        }
        Bukkit.getLogger().info("[MidnightSlashBlock] Generating canvas in world: " + world.dimension().location());
        int min = -(size / 2);
        int max = (size / 2) - 1;
        for (int x = min; x <= max; x++) {
            for (int z = min; z <= max; z++) {
                BlockPos pos = new BlockPos(x, baseY, z);
                world.setBlockAndUpdate(pos, baseBlock);
                // Add barrier under canvas
                BlockPos barrierPos = new BlockPos(x, baseY - 1, z);
                world.setBlockAndUpdate(barrierPos, net.minecraft.world.level.block.Blocks.BARRIER.defaultBlockState());
            }
        }
        plugin.getConfig().set("canvas-initialized", true);
        plugin.saveConfig();
        Bukkit.getLogger().info("[MidnightSlashBlock] Canvas generation complete.");
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
    }
} 