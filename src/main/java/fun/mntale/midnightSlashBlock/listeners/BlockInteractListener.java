package fun.mntale.midnightSlashBlock.listeners;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BlockInteractListener implements Listener {
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, BlockPos> pendingBlock = new HashMap<>();
    private static final long COOLDOWN_MS = 5000;
    private static final String COLOR_GUI_TITLE = "Choose a Color";
    private static final List<BlockState> COLORS = Arrays.asList(
            Blocks.WHITE_CONCRETE.defaultBlockState(),
            Blocks.ORANGE_CONCRETE.defaultBlockState(),
            Blocks.MAGENTA_CONCRETE.defaultBlockState(),
            Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState(),
            Blocks.YELLOW_CONCRETE.defaultBlockState(),
            Blocks.LIME_CONCRETE.defaultBlockState(),
            Blocks.PINK_CONCRETE.defaultBlockState(),
            Blocks.GRAY_CONCRETE.defaultBlockState(),
            Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState(),
            Blocks.CYAN_CONCRETE.defaultBlockState(),
            Blocks.PURPLE_CONCRETE.defaultBlockState(),
            Blocks.BLUE_CONCRETE.defaultBlockState(),
            Blocks.BROWN_CONCRETE.defaultBlockState(),
            Blocks.GREEN_CONCRETE.defaultBlockState(),
            Blocks.RED_CONCRETE.defaultBlockState(),
            Blocks.BLACK_CONCRETE.defaultBlockState()
    );

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        if (!event.getAction().toString().contains("RIGHT")) return;
        ServerPlayer nmsPlayer = ((CraftPlayer) event.getPlayer()).getHandle();
        assert event.getClickedBlock() != null;
        BlockPos pos = new BlockPos(
                event.getClickedBlock().getX(),
                event.getClickedBlock().getY(),
                event.getClickedBlock().getZ()
        );
        UUID uuid = nmsPlayer.getUUID();
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(uuid) && now - cooldowns.get(uuid) < COOLDOWN_MS) {
            long left = (COOLDOWN_MS - (now - cooldowns.get(uuid))) / 1000;
            nmsPlayer.sendSystemMessage(Component.literal("§cYou must wait " + left + "s before placing another block."));
            return;
        }
        pendingBlock.put(uuid, pos);
        openColorPicker(nmsPlayer);
    }

    private void openColorPicker(ServerPlayer nmsPlayer) {
        SimpleMenuProvider provider = new SimpleMenuProvider((windowId, inv, player) -> new ColorPickerMenu(windowId, inv, player.getUUID()), Component.literal(COLOR_GUI_TITLE));
        nmsPlayer.openMenu(provider);
    }

    // Custom NMS menu for color picking
    private class ColorPickerMenu extends ChestMenu {
        private final UUID playerId;
        public ColorPickerMenu(int windowId, net.minecraft.world.entity.player.Inventory inv, UUID playerId) {
            super(MenuType.GENERIC_9x2, windowId, inv, new net.minecraft.world.SimpleContainer(18), 2);
            this.playerId = playerId;
            // Fill with color blocks
            for (int i = 0; i < COLORS.size(); i++) {
                this.getSlot(i).set(new ItemStack(COLORS.get(i).getBlock().asItem()));
            }
        }
        @Override
        public boolean clickMenuButton(net.minecraft.world.entity.player.@NotNull Player nmsPlayer, int slot) {
            if (slot < 0 || slot >= COLORS.size()) return false;
            BlockPos pos = pendingBlock.remove(playerId);
            if (pos == null) return false;
            BlockState state = COLORS.get(slot);
            nmsPlayer.level().setBlockAndUpdate(pos, state);
            cooldowns.put(playerId, System.currentTimeMillis());
            nmsPlayer.closeContainer();
            if (nmsPlayer instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal("Block placed!"));
            }
            return true;
        }
    }
} 