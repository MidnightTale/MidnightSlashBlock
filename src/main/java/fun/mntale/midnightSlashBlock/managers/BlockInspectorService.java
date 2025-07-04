package fun.mntale.midnightSlashBlock.managers;

import fun.mntale.midnightSlashBlock.models.BlockPlacementMeta;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class BlockInspectorService implements Listener {
    private final Plugin plugin;
    private final Map<UUID, Integer> tasks = new ConcurrentHashMap<>();;
    private final Map<UUID, TextDisplay> displays = new ConcurrentHashMap<>();;
    private final BlockPlacementMetaManager metaManager;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Map<UUID, String> lastBlockKey = new ConcurrentHashMap<>();;

    public BlockInspectorService(Plugin plugin, BlockPlacementMetaManager metaManager) {
        this.plugin = plugin;
        this.metaManager = metaManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        startFor(player);
    }

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        stopFor(event.getPlayer());
    }

    public void startFor(Player player) {
        stopFor(player);
        int taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (player.getInventory().getItemInMainHand().getType() != Material.STICK) {
                removeDisplay(player);
                lastBlockKey.remove(player.getUniqueId());
                return;
            }
            Block target = player.getTargetBlockExact(64);
            if (target == null || target.getType() == Material.AIR) {
                removeDisplay(player);
                lastBlockKey.remove(player.getUniqueId());
                return;
            }
            String key = target.getWorld().getName() + ":" + target.getX() + ":" + target.getY() + ":" + target.getZ();
            Optional<BlockPlacementMeta> metaOpt = metaManager.getMeta(target.getWorld(), target.getX(), target.getY(), target.getZ());
            if (metaOpt.isPresent()) {
                BlockPlacementMeta meta = metaOpt.get();
                String colorHex = "#AAAAAA";
                String blockName = meta.blockType.name();
                var colorInfo = fun.mntale.midnightSlashBlock.utils.BlockColorUtil.BLOCK_COLOR_INFO.get(meta.blockType);
                if (colorInfo != null) {
                    colorHex = colorInfo.hex();
                    blockName = colorInfo.name();
                }
                String dateStr = dateFormat.format(new Date(meta.timestamp));
                String msg = String.format("<color:%s><bold>%s</bold></color>\n<gray>Placed by <yellow>%s</yellow>\n<gray>at <white>%s</white>",
                        colorHex, blockName, meta.playerName, dateStr);
                Component text = MiniMessage.miniMessage().deserialize(msg);
                String lastKey = lastBlockKey.get(player.getUniqueId());
                if (!key.equals(lastKey)) {
                    showDisplay(player, target, text);
                    lastBlockKey.put(player.getUniqueId(), key);
                } else {
                    updateDisplay(player, target, text);
                }
            } else {
                removeDisplay(player);
                lastBlockKey.remove(player.getUniqueId());
            }
        }, 0L, 4L).getTaskId();
        tasks.put(player.getUniqueId(), taskId);
    }

    public void stopFor(Player player) {
        Integer id = tasks.remove(player.getUniqueId());
        if (id != null) Bukkit.getScheduler().cancelTask(id);
        removeDisplay(player);
    }

    private void showDisplay(Player player, Block block, Component text) {
        TextDisplay display = displays.get(player.getUniqueId());
        if (display != null && (!display.isValid() || !display.getLocation().getBlock().equals(block))) {
            display.remove();
            display = null;
        }
        if (display == null) {
            display = block.getWorld().spawn(block.getLocation().add(0.5, 1.5, 0.5), TextDisplay.class, e -> {
                e.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);
                e.setSeeThrough(true);
                e.setShadowed(true);
                e.text(text);
                e.setViewRange(64f);
            });
            displays.put(player.getUniqueId(), display);
        } else {
            display.text(text);
            display.teleport(block.getLocation().add(0.5, 1.5, 0.5));
        }
    }

    private void updateDisplay(Player player, Block block, Component text) {
        TextDisplay display = displays.get(player.getUniqueId());
        if (display != null && display.isValid()) {
            display.text(text);
            display.teleport(block.getLocation().add(0.5, 1.5, 0.5));
        }
    }

    private void removeDisplay(Player player) {
        TextDisplay display = displays.remove(player.getUniqueId());
        if (display != null && display.isValid()) display.remove();
    }
} 