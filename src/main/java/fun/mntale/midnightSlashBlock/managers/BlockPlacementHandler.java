package fun.mntale.midnightSlashBlock.managers;

import fun.mntale.midnightSlashBlock.MidnightSlashBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;

public class BlockPlacementHandler {
    public static final Map<Material, BlockState> CONCRETE_TO_NMS = Map.ofEntries(
            Map.entry(Material.WHITE_CONCRETE, net.minecraft.world.level.block.Blocks.WHITE_CONCRETE.defaultBlockState()),
            Map.entry(Material.ORANGE_CONCRETE, net.minecraft.world.level.block.Blocks.ORANGE_CONCRETE.defaultBlockState()),
            Map.entry(Material.MAGENTA_CONCRETE, net.minecraft.world.level.block.Blocks.MAGENTA_CONCRETE.defaultBlockState()),
            Map.entry(Material.LIGHT_BLUE_CONCRETE, net.minecraft.world.level.block.Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState()),
            Map.entry(Material.YELLOW_CONCRETE, net.minecraft.world.level.block.Blocks.YELLOW_CONCRETE.defaultBlockState()),
            Map.entry(Material.LIME_CONCRETE, net.minecraft.world.level.block.Blocks.LIME_CONCRETE.defaultBlockState()),
            Map.entry(Material.PINK_CONCRETE, net.minecraft.world.level.block.Blocks.PINK_CONCRETE.defaultBlockState()),
            Map.entry(Material.GRAY_CONCRETE, net.minecraft.world.level.block.Blocks.GRAY_CONCRETE.defaultBlockState()),
            Map.entry(Material.LIGHT_GRAY_CONCRETE, net.minecraft.world.level.block.Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState()),
            Map.entry(Material.CYAN_CONCRETE, net.minecraft.world.level.block.Blocks.CYAN_CONCRETE.defaultBlockState()),
            Map.entry(Material.PURPLE_CONCRETE, net.minecraft.world.level.block.Blocks.PURPLE_CONCRETE.defaultBlockState()),
            Map.entry(Material.BLUE_CONCRETE, net.minecraft.world.level.block.Blocks.BLUE_CONCRETE.defaultBlockState()),
            Map.entry(Material.BROWN_CONCRETE, net.minecraft.world.level.block.Blocks.BROWN_CONCRETE.defaultBlockState()),
            Map.entry(Material.GREEN_CONCRETE, net.minecraft.world.level.block.Blocks.GREEN_CONCRETE.defaultBlockState()),
            Map.entry(Material.RED_CONCRETE, net.minecraft.world.level.block.Blocks.RED_CONCRETE.defaultBlockState()),
            Map.entry(Material.BLACK_CONCRETE, net.minecraft.world.level.block.Blocks.BLACK_CONCRETE.defaultBlockState())
    );

    public static void handleBlockPlacement(Player player, Material material, BlockPos pos) {
        UUID uuid = player.getUniqueId();
        BlockState nmsState = CONCRETE_TO_NMS.get(material);
        if (nmsState == null) {
            player.sendMessage("§cInvalid color selected.");
            player.closeInventory();
            return;
        }
        ServerPlayer nmsPlayer = (ServerPlayer) ((CraftPlayer) player).getHandle();
        ServerLevel nmsWorld = (ServerLevel) nmsPlayer.level();
        nmsWorld.setBlockAndUpdate(pos, nmsState);
        BlockCooldownManager.setCooldown(uuid);
        MidnightSlashBlock.blockChangeCount.put(uuid, MidnightSlashBlock.blockChangeCount.getOrDefault(uuid, 0) + 1);
        player.sendMessage("§aBlock placed!");
        player.closeInventory();
    }
} 