package fun.mntale.midnightSlashBlock.managers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BlockPlacementHandler {
    private static final Set<UUID> animatingPlayers = ConcurrentHashMap.newKeySet();

    public static final Map<Material, BlockState> ALL_PICKER_BLOCKS_TO_NMS = Map.ofEntries(
            // Red
            Map.entry(Material.RED_CONCRETE, net.minecraft.world.level.block.Blocks.RED_CONCRETE.defaultBlockState()),
            Map.entry(Material.RED_WOOL, net.minecraft.world.level.block.Blocks.RED_WOOL.defaultBlockState()),
            Map.entry(Material.RED_TERRACOTTA, net.minecraft.world.level.block.Blocks.RED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.RED_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.RED_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.RED_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.RED_CONCRETE_POWDER.defaultBlockState()),
            // Orange
            Map.entry(Material.ORANGE_CONCRETE, net.minecraft.world.level.block.Blocks.ORANGE_CONCRETE.defaultBlockState()),
            Map.entry(Material.ORANGE_WOOL, net.minecraft.world.level.block.Blocks.ORANGE_WOOL.defaultBlockState()),
            Map.entry(Material.ORANGE_TERRACOTTA, net.minecraft.world.level.block.Blocks.ORANGE_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.ORANGE_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.ORANGE_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.ORANGE_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.ORANGE_CONCRETE_POWDER.defaultBlockState()),
            // Yellow
            Map.entry(Material.YELLOW_CONCRETE, net.minecraft.world.level.block.Blocks.YELLOW_CONCRETE.defaultBlockState()),
            Map.entry(Material.YELLOW_WOOL, net.minecraft.world.level.block.Blocks.YELLOW_WOOL.defaultBlockState()),
            Map.entry(Material.YELLOW_TERRACOTTA, net.minecraft.world.level.block.Blocks.YELLOW_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.YELLOW_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.YELLOW_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.YELLOW_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.YELLOW_CONCRETE_POWDER.defaultBlockState()),
            // Lime/Green
            Map.entry(Material.LIME_CONCRETE, net.minecraft.world.level.block.Blocks.LIME_CONCRETE.defaultBlockState()),
            Map.entry(Material.LIME_WOOL, net.minecraft.world.level.block.Blocks.LIME_WOOL.defaultBlockState()),
            Map.entry(Material.LIME_TERRACOTTA, net.minecraft.world.level.block.Blocks.LIME_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.LIME_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.LIME_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.LIME_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.LIME_CONCRETE_POWDER.defaultBlockState()),
            Map.entry(Material.GREEN_CONCRETE, net.minecraft.world.level.block.Blocks.GREEN_CONCRETE.defaultBlockState()),
            Map.entry(Material.GREEN_WOOL, net.minecraft.world.level.block.Blocks.GREEN_WOOL.defaultBlockState()),
            Map.entry(Material.GREEN_TERRACOTTA, net.minecraft.world.level.block.Blocks.GREEN_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.GREEN_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.GREEN_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.GREEN_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.GREEN_CONCRETE_POWDER.defaultBlockState()),
            // Blue
            Map.entry(Material.LIGHT_BLUE_CONCRETE, net.minecraft.world.level.block.Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState()),
            Map.entry(Material.LIGHT_BLUE_WOOL, net.minecraft.world.level.block.Blocks.LIGHT_BLUE_WOOL.defaultBlockState()),
            Map.entry(Material.LIGHT_BLUE_TERRACOTTA, net.minecraft.world.level.block.Blocks.LIGHT_BLUE_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.LIGHT_BLUE_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.LIGHT_BLUE_CONCRETE_POWDER.defaultBlockState()),
            Map.entry(Material.BLUE_CONCRETE, net.minecraft.world.level.block.Blocks.BLUE_CONCRETE.defaultBlockState()),
            Map.entry(Material.BLUE_WOOL, net.minecraft.world.level.block.Blocks.BLUE_WOOL.defaultBlockState()),
            Map.entry(Material.BLUE_TERRACOTTA, net.minecraft.world.level.block.Blocks.BLUE_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.BLUE_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.BLUE_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.BLUE_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.BLUE_CONCRETE_POWDER.defaultBlockState()),
            Map.entry(Material.CYAN_CONCRETE, net.minecraft.world.level.block.Blocks.CYAN_CONCRETE.defaultBlockState()),
            Map.entry(Material.CYAN_WOOL, net.minecraft.world.level.block.Blocks.CYAN_WOOL.defaultBlockState()),
            Map.entry(Material.CYAN_TERRACOTTA, net.minecraft.world.level.block.Blocks.CYAN_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.CYAN_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.CYAN_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.CYAN_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.CYAN_CONCRETE_POWDER.defaultBlockState()),
            // Purple
            Map.entry(Material.PURPLE_CONCRETE, net.minecraft.world.level.block.Blocks.PURPLE_CONCRETE.defaultBlockState()),
            Map.entry(Material.PURPLE_WOOL, net.minecraft.world.level.block.Blocks.PURPLE_WOOL.defaultBlockState()),
            Map.entry(Material.PURPLE_TERRACOTTA, net.minecraft.world.level.block.Blocks.PURPLE_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.PURPLE_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.PURPLE_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.PURPLE_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.PURPLE_CONCRETE_POWDER.defaultBlockState()),
            Map.entry(Material.MAGENTA_CONCRETE, net.minecraft.world.level.block.Blocks.MAGENTA_CONCRETE.defaultBlockState()),
            Map.entry(Material.MAGENTA_WOOL, net.minecraft.world.level.block.Blocks.MAGENTA_WOOL.defaultBlockState()),
            Map.entry(Material.MAGENTA_TERRACOTTA, net.minecraft.world.level.block.Blocks.MAGENTA_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.MAGENTA_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.MAGENTA_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.MAGENTA_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.MAGENTA_CONCRETE_POWDER.defaultBlockState()),
            // Pink
            Map.entry(Material.PINK_CONCRETE, net.minecraft.world.level.block.Blocks.PINK_CONCRETE.defaultBlockState()),
            Map.entry(Material.PINK_WOOL, net.minecraft.world.level.block.Blocks.PINK_WOOL.defaultBlockState()),
            Map.entry(Material.PINK_TERRACOTTA, net.minecraft.world.level.block.Blocks.PINK_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.PINK_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.PINK_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.PINK_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.PINK_CONCRETE_POWDER.defaultBlockState()),
            // Brown
            Map.entry(Material.BROWN_CONCRETE, net.minecraft.world.level.block.Blocks.BROWN_CONCRETE.defaultBlockState()),
            Map.entry(Material.BROWN_WOOL, net.minecraft.world.level.block.Blocks.BROWN_WOOL.defaultBlockState()),
            Map.entry(Material.BROWN_TERRACOTTA, net.minecraft.world.level.block.Blocks.BROWN_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.BROWN_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.BROWN_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.BROWN_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.BROWN_CONCRETE_POWDER.defaultBlockState()),
            // White/Gray
            Map.entry(Material.WHITE_CONCRETE, net.minecraft.world.level.block.Blocks.WHITE_CONCRETE.defaultBlockState()),
            Map.entry(Material.WHITE_WOOL, net.minecraft.world.level.block.Blocks.WHITE_WOOL.defaultBlockState()),
            Map.entry(Material.WHITE_TERRACOTTA, net.minecraft.world.level.block.Blocks.WHITE_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.WHITE_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.WHITE_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.WHITE_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.WHITE_CONCRETE_POWDER.defaultBlockState()),
            Map.entry(Material.LIGHT_GRAY_CONCRETE, net.minecraft.world.level.block.Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState()),
            Map.entry(Material.LIGHT_GRAY_WOOL, net.minecraft.world.level.block.Blocks.LIGHT_GRAY_WOOL.defaultBlockState()),
            Map.entry(Material.LIGHT_GRAY_TERRACOTTA, net.minecraft.world.level.block.Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.LIGHT_GRAY_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.LIGHT_GRAY_CONCRETE_POWDER.defaultBlockState()),
            Map.entry(Material.GRAY_CONCRETE, net.minecraft.world.level.block.Blocks.GRAY_CONCRETE.defaultBlockState()),
            Map.entry(Material.GRAY_WOOL, net.minecraft.world.level.block.Blocks.GRAY_WOOL.defaultBlockState()),
            Map.entry(Material.GRAY_TERRACOTTA, net.minecraft.world.level.block.Blocks.GRAY_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.GRAY_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.GRAY_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.GRAY_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.GRAY_CONCRETE_POWDER.defaultBlockState()),
            // Black
            Map.entry(Material.BLACK_CONCRETE, net.minecraft.world.level.block.Blocks.BLACK_CONCRETE.defaultBlockState()),
            Map.entry(Material.BLACK_WOOL, net.minecraft.world.level.block.Blocks.BLACK_WOOL.defaultBlockState()),
            Map.entry(Material.BLACK_TERRACOTTA, net.minecraft.world.level.block.Blocks.BLACK_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.BLACK_GLAZED_TERRACOTTA, net.minecraft.world.level.block.Blocks.BLACK_GLAZED_TERRACOTTA.defaultBlockState()),
            Map.entry(Material.BLACK_CONCRETE_POWDER, net.minecraft.world.level.block.Blocks.BLACK_CONCRETE_POWDER.defaultBlockState())
    );

    public static void handleBlockPlacement(Player player, Material material, BlockPos pos) {
        UUID uuid = player.getUniqueId();
        // Prevent double placement during animation
        if (animatingPlayers.contains(uuid)) {
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            return;
        }
        animatingPlayers.add(uuid);
        // Cooldown check
        if (fun.mntale.midnightSlashBlock.managers.BlockCooldownManager.isOnCooldown(uuid)) {
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f); // Fail/cooldown sound
            animatingPlayers.remove(uuid);
            return;
        }
        BlockState nmsState = ALL_PICKER_BLOCKS_TO_NMS.get(material);
        if (nmsState == null) {
            player.sendMessage("§cInvalid color selected.");
            animatingPlayers.remove(uuid);
            return;
        }
        // Animate placement
        Plugin plugin = Bukkit.getPluginManager().getPlugin("MidnightSlashBlock");
        if (plugin == null) {
            player.sendMessage("§cPlugin not loaded.");
            animatingPlayers.remove(uuid);
            return;
        }
        Location loc = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.4f);
        new BlockPlacementAnimationManager(plugin).playBlockPlacementAnimation(player, material, loc, () -> {
            ServerPlayer nmsPlayer = (ServerPlayer) ((CraftPlayer) player).getHandle();
            ServerLevel nmsWorld = (ServerLevel) nmsPlayer.level();
            nmsWorld.setBlockAndUpdate(pos, nmsState);
            BlockCooldownManager.setCooldown(uuid);
            fun.mntale.midnightSlashBlock.MidnightSlashBlock.getBlockPlaceDataManager().incrementBlockCount(uuid);
            int blockCount = fun.mntale.midnightSlashBlock.MidnightSlashBlock.getBlockPlaceDataManager().getBlockCount(uuid);
            fun.mntale.midnightSlashBlock.utils.TablistUtil.updateTablist(player, blockCount, true);
            fun.mntale.midnightSlashBlock.utils.TablistUtil.updateTablistHeaderFooter();
            // Broadcast global message
            fun.mntale.midnightSlashBlock.utils.BlockColorUtil.BlockColorInfo colorInfo = fun.mntale.midnightSlashBlock.utils.BlockColorUtil.BLOCK_COLOR_INFO.get(material);
            String colorHex = colorInfo != null ? colorInfo.hex() : "#AAAAAA";
            String blockName = colorInfo != null ? colorInfo.name() : material.name();
            String msg = String.format("<gray><bold>%s</bold> placed <color:%s><bold>%s</bold></color> at <yellow>%d %d %d</yellow></gray>",
                player.getName(), colorHex, blockName, pos.getX(), pos.getY(), pos.getZ());
            net.kyori.adventure.text.Component component = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(msg);
            org.bukkit.Bukkit.getServer().sendMessage(component);
            // Immersive feedback: sound and particles
            player.getWorld().playSound(loc, org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.2f);
            player.getWorld().spawnParticle(org.bukkit.Particle.END_ROD, loc.clone().add(0.5, 1, 0.5), 20, 0.2, 0.4, 0.2, 0.01);
            // Record block placement metadata
            fun.mntale.midnightSlashBlock.MidnightSlashBlock.getBlockPlacementMetaManager().recordPlacement(
                player.getWorld(),
                pos.getX(), pos.getY(), pos.getZ(),
                player.getUniqueId(),
                player.getName(),
                material,
                System.currentTimeMillis()
            );
            animatingPlayers.remove(uuid);
        });
    }
} 