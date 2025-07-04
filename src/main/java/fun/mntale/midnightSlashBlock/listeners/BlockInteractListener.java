package fun.mntale.midnightSlashBlock.listeners;

import fun.mntale.midnightSlashBlock.MidnightSlashBlock;
import fun.mntale.midnightSlashBlock.managers.BlockCooldownManager;
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
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.concurrent.ConcurrentHashMap;
import fun.mntale.midnightSlashBlock.utils.TablistUtil;

public class BlockInteractListener implements Listener {
    private static final int CANVAS_Y = 64;
    private static final int CANVAS_MIN = -256, CANVAS_MAX = 255;

    private static final ConcurrentHashMap<UUID, Integer> actionBarTasks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, Boolean> wasOnCooldown = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.SPYGLASS) return;
        // Blaze rod: cycle through 5 fly speed modes
        if (event.getHand() == EquipmentSlot.HAND && event.getAction().toString().contains("RIGHT") && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
            org.bukkit.NamespacedKey speedKey = new org.bukkit.NamespacedKey(player.getServer().getPluginManager().getPlugin("MidnightSlashBlock"), "fly_speed_mode");
            int[] colors = {0x00FFFF, 0x00FF00, 0xFFFF00, 0xFFA500, 0xFF5555};
            String[] labels = {"Very Slow", "Slow", "Normal", "Fast", "Very Fast"};
            float[] speeds = {0.05f, 0.1f, 0.2f, 0.3f, 0.5f};
            int mode = player.getPersistentDataContainer().getOrDefault(speedKey, org.bukkit.persistence.PersistentDataType.INTEGER, 2); // default to Normal
            mode = (mode + 1) % 5;
            player.getPersistentDataContainer().set(speedKey, org.bukkit.persistence.PersistentDataType.INTEGER, mode);
            player.setFlySpeed(speeds[mode]);
            String color = String.format("#%06X", colors[mode]);
            String msg = String.format("<bold><color:%s>Fly speed: %s</color>", color, labels[mode]);
            player.sendActionBar(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(msg));
            event.setCancelled(true);
            // Update blaze rod lore in main hand
            org.bukkit.inventory.ItemStack rod = player.getInventory().getItemInMainHand();
            if (rod.getType() == org.bukkit.Material.BLAZE_ROD) {
                org.bukkit.inventory.meta.ItemMeta meta = rod.getItemMeta();
                java.util.List<net.kyori.adventure.text.Component> lore = new java.util.ArrayList<>();
                for (int i = 0; i < labels.length; i++) {
                    String prefix = (i == mode) ? "> " : "";
                    String colorTag = (i == mode) ? "<green>" : "<gray>";
                    lore.add(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(colorTag + prefix + labels[i]));
                }
                meta.lore(lore);
                rod.setItemMeta(meta);
            }
            return;
        }
        // Teleport Breeze Rod: teleport 100 blocks in look direction
        if (event.getHand() == EquipmentSlot.HAND && (event.getAction().toString().contains("RIGHT") || event.getAction().toString().contains("LEFT")) && player.getInventory().getItemInMainHand().getType() == Material.BREEZE_ROD) {
            org.bukkit.inventory.ItemStack breeze_rod = player.getInventory().getItemInMainHand();
            if (breeze_rod.hasItemMeta()) {
                org.bukkit.Location eye = player.getEyeLocation();
                org.bukkit.util.Vector dir = eye.getDirection().normalize();
                org.bukkit.Location dest = eye.clone().add(dir.multiply(100));
                // Clamp Y to canvas Y or highest block at X/Z
                org.bukkit.World world = player.getWorld();
                int x = dest.getBlockX();
                int z = dest.getBlockZ();
                int y = 64; // Default canvas Y
                if (world.isChunkLoaded(x >> 4, z >> 4)) {
                    y = Math.max(world.getHighestBlockYAt(x, z) + 1, 64);
                }
                dest.setY(y + 0.5);
                dest.setX(x + 0.5);
                dest.setZ(z + 0.5);
                player.teleportAsync(dest);
                world.spawnParticle(org.bukkit.Particle.PORTAL, dest, 60, 0.5, 1, 0.5, 0.2);
                world.playSound(dest, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.2f, 1.1f);
                event.setCancelled(true);
                return;
            }
        }
        if (event.getHand() != EquipmentSlot.HAND) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null) return;
        if (clicked.getY() != CANVAS_Y || clicked.getX() < CANVAS_MIN || clicked.getX() > CANVAS_MAX || clicked.getZ() < CANVAS_MIN || clicked.getZ() > CANVAS_MAX) return;
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
            fun.mntale.midnightSlashBlock.utils.BlockColorPickerGUI.handleInventoryClose(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String plainTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!plainTitle.contains("Pick a Color")) return;
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        int slot = event.getRawSlot();
        fun.mntale.midnightSlashBlock.utils.BlockColorPickerGUI.handleInventoryClick(player, slot);
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
            Boolean prev = wasOnCooldown.getOrDefault(uuid, false);
            if (prev && !onCooldown) {
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
            }
            wasOnCooldown.put(uuid, onCooldown);
            int placeCount = fun.mntale.midnightSlashBlock.MidnightSlashBlock.getBlockPlaceDataManager().getBlockCount(uuid);
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
            TablistUtil.updateTablist(player, placeCount, onCooldown);
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