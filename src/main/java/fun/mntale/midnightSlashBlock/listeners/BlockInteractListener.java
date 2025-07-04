package fun.mntale.midnightSlashBlock.listeners;

import fun.mntale.midnightSlashBlock.MidnightSlashBlock;
import fun.mntale.midnightSlashBlock.managers.BlockCooldownManager;
import fun.mntale.midnightSlashBlock.managers.BlockPlacementHandler;
import fun.mntale.midnightSlashBlock.utils.BlockColorPickerGUI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.concurrent.ConcurrentHashMap;

public class BlockInteractListener implements Listener {
    private static final int CANVAS_Y = 64;
    private static final int CANVAS_MIN = -256, CANVAS_MAX = 255;

    private static final ConcurrentHashMap<UUID, Integer> actionBarTasks = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null) return;
        if (clicked.getY() != CANVAS_Y || clicked.getX() < CANVAS_MIN || clicked.getX() > CANVAS_MAX || clicked.getZ() < CANVAS_MIN || clicked.getZ() > CANVAS_MAX) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        MidnightSlashBlock.pendingPlacements.put(uuid, new net.minecraft.core.BlockPos(clicked.getX(), clicked.getY(), clicked.getZ()));
        BlockColorPickerGUI.openColorPicker(player);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();
        String plainTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (plainTitle.contains("Pick a Color")) {
            MidnightSlashBlock.openColorPickers.add(uuid);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();
        String plainTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (plainTitle.contains("Pick a Color")) {
            MidnightSlashBlock.openColorPickers.remove(uuid);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();
        String plainTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!plainTitle.contains("Pick a Color")) return;
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        net.minecraft.core.BlockPos pos = MidnightSlashBlock.pendingPlacements.get(uuid);
        if (pos == null) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            player.updateInventory();
            return;
        }
        // Remove after successful check
        MidnightSlashBlock.pendingPlacements.remove(uuid);

        if (BlockCooldownManager.isOnCooldown(uuid)) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            player.updateInventory();
            return;
        }
        BlockPlacementHandler.handleBlockPlacement(player, clicked.getType(), pos);
        player.updateInventory();
    }

    public static void startActionBarTask(Player player, JavaPlugin plugin) {
        stopActionBarTask(player); // Ensure no duplicate tasks
        UUID uuid = player.getUniqueId();
        int taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || !player.getWorld().getName().equalsIgnoreCase("canvas")) {
                stopActionBarTask(player);
                return;
            }
            boolean onCooldown = BlockCooldownManager.isOnCooldown(uuid);
            int placeCount = fun.mntale.midnightSlashBlock.MidnightSlashBlock.blockChangeCount.getOrDefault(uuid, 0);
            String message;
            if (onCooldown) {
                long left = BlockCooldownManager.getCooldownLeft(uuid);
                long minutes = left / 60;
                long seconds = left % 60;
                message = String.format("<bold><#FF5555>%02d:%02d</#FF5555> - Place: <#00BFFF>%d</#00BFFF>", minutes, seconds, placeCount);
            } else {
                message = String.format("<bold><#00FF00>READY</#00FF00> - Place: <#00BFFF>%d</#00BFFF>", placeCount);
            }
            Component actionBar = MiniMessage.miniMessage().deserialize(message);
            player.sendActionBar(actionBar);
        }, 0L, 2L).getTaskId();
        actionBarTasks.put(uuid, taskId);
    }

    public static void stopActionBarTask(Player player) {
        UUID uuid = player.getUniqueId();
        Integer taskId = actionBarTasks.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
} 