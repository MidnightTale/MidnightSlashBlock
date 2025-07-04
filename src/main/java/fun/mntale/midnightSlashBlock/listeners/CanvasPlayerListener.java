package fun.mntale.midnightSlashBlock.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import java.util.Objects;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import fun.mntale.midnightSlashBlock.MidnightSlashBlock;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import fun.mntale.midnightSlashBlock.managers.BlockPlaceDataManager;
import fun.mntale.midnightSlashBlock.utils.TablistUtil;
import fun.mntale.midnightSlashBlock.managers.BlockCooldownManager;

public class CanvasPlayerListener implements Listener {
    private final World canvasWorld;
    private final Plugin plugin;
    private final java.util.Map<java.util.UUID, Long> lastChatTime = new java.util.concurrent.ConcurrentHashMap<>();
    private static final long CHAT_COOLDOWN_MS = 1000; // 1 second

    public CanvasPlayerListener(World canvasWorld) {
        this(canvasWorld, null);
    }

    public CanvasPlayerListener(World canvasWorld, Plugin plugin) {
        this.canvasWorld = canvasWorld;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
        Player player = event.getPlayer();
        if (!player.isOnline()) {
            MidnightSlashBlock.openColorPickers.remove(player.getUniqueId());
            return;
        }
        BlockPlaceDataManager dataManager = MidnightSlashBlock.getBlockPlaceDataManager();
        if (dataManager != null) dataManager.loadPlayer(player.getUniqueId());
        Location spawn = canvasWorld.getSpawnLocation();
        if (!player.getWorld().equals(canvasWorld)) {
            player.teleportAsync(spawn).thenRun(() -> {
                player.setAllowFlight(true);
                player.setFlying(true);
                if (plugin != null) BlockInteractListener.startActionBarTask(player, (org.bukkit.plugin.java.JavaPlugin) plugin);
            });
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            if (plugin != null) BlockInteractListener.startActionBarTask(player, (org.bukkit.plugin.java.JavaPlugin) plugin);
        }
        // Set interaction range to 64
        org.bukkit.attribute.AttributeInstance attr = player.getAttribute(org.bukkit.attribute.Attribute.BLOCK_INTERACTION_RANGE);
        if (attr != null) {
            attr.setBaseValue(64.0);
        }
        // Give inspector and utility items only if not already given
        org.bukkit.NamespacedKey starterKey = new org.bukkit.NamespacedKey(plugin, "received_starter_items");
        if (!player.getPersistentDataContainer().has(starterKey, org.bukkit.persistence.PersistentDataType.BYTE)) {
            // Stick: Block Inspector
            org.bukkit.inventory.ItemStack stick = new org.bukkit.inventory.ItemStack(org.bukkit.Material.STICK);
            org.bukkit.inventory.meta.ItemMeta stickMeta = stick.getItemMeta();
            stickMeta.displayName(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<aqua>Block Inspector"));
            stick.setItemMeta(stickMeta);
            // Spyglass: Zoom Tool
            org.bukkit.inventory.ItemStack spyglass = new org.bukkit.inventory.ItemStack(org.bukkit.Material.SPYGLASS);
            org.bukkit.inventory.meta.ItemMeta spyMeta = spyglass.getItemMeta();
            spyMeta.displayName(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<yellow>Zoom Tool"));
            spyglass.setItemMeta(spyMeta);
            // Blaze Rod: Fly Speed Selector
            org.bukkit.inventory.ItemStack blazeRod = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BLAZE_ROD);
            org.bukkit.inventory.meta.ItemMeta blazeMeta = blazeRod.getItemMeta();
            blazeMeta.displayName(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<gold>Fly Speed Selector"));
            blazeRod.setItemMeta(blazeMeta);
            player.getInventory().addItem(stick, spyglass, blazeRod);
            player.updateInventory();
            player.getPersistentDataContainer().set(starterKey, org.bukkit.persistence.PersistentDataType.BYTE, (byte)1);
        }
        int blockCount = dataManager.getBlockCount(player.getUniqueId());
        boolean onCooldown = BlockCooldownManager.isOnCooldown(player.getUniqueId());
        TablistUtil.updateTablist(player, blockCount, onCooldown);
        TablistUtil.updateTablistHeaderFooter();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(canvasWorld.getSpawnLocation());
        event.getPlayer().setAllowFlight(true);
    }
    
    @EventHandler
    public void onVoidDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Objects.equals(player.getWorld(), canvasWorld)) return;
        if (event.getCause() == DamageCause.VOID) {
            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ();
            int y = canvasWorld.getHighestBlockYAt(x, z) + 1;
            Location safeLoc = new Location(canvasWorld, x + 0.5, y, z + 0.5);
            player.teleportAsync(safeLoc);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getWorld().getName().equalsIgnoreCase("canvas")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getWorld().getName().equalsIgnoreCase("canvas")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
        Player player = event.getPlayer();
        MidnightSlashBlock.openColorPickers.remove(player.getUniqueId());
        BlockPlaceDataManager dataManager = MidnightSlashBlock.getBlockPlaceDataManager();
        if (dataManager != null) dataManager.savePlayer(player.getUniqueId());
        BlockInteractListener.stopActionBarTask(player);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerDropItem(org.bukkit.event.player.PlayerDropItemEvent event) {
        org.bukkit.Material type = event.getItemDrop().getItemStack().getType();
        if (type == org.bukkit.Material.STICK || type == org.bukkit.Material.SPYGLASS || type == org.bukkit.Material.BLAZE_ROD) {
            event.setCancelled(true);
            event.getPlayer().sendActionBar(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<red>You cannot drop this item!"));
        }
    }

    @org.bukkit.event.EventHandler
    public void onEntityDamageByEntity(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Player && event.getDamager() instanceof org.bukkit.entity.Player) {
            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onAsyncPlayerChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        long now = System.currentTimeMillis();
        long last = lastChatTime.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < CHAT_COOLDOWN_MS) {
            event.setCancelled(true);
            player.sendActionBar(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<red>You're sending messages too fast!"));
            return;
        }
        lastChatTime.put(player.getUniqueId(), now);
        int count = fun.mntale.midnightSlashBlock.MidnightSlashBlock.getBlockPlaceDataManager().getBlockCount(player.getUniqueId());
        String displayName = player.displayName() != null ? net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(player.displayName()) : player.getName();
        String msg = event.getMessage();
        String formatted = "<#FFD700>" + count + "</#FFD700> <#444444>-</#444444> <white>" + displayName + "</white><#444444>:</#444444> <white>" + msg + "</white>";
        net.kyori.adventure.text.Component component = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(formatted);
        for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
            p.sendMessage(component);
        }
    }
} 