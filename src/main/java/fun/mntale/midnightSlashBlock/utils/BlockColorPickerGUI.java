package fun.mntale.midnightSlashBlock.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class BlockColorPickerGUI {
    private static final int ROWS = 6;
    private static final int COLS = 9;
    private static final int GRADIENTS_PER_PAGE = 5;
    private static final List<List<Material>> GRADIENTS = List.of(
        // Red
        List.of(Material.RED_CONCRETE, Material.RED_WOOL, Material.RED_TERRACOTTA, Material.RED_GLAZED_TERRACOTTA, Material.RED_CONCRETE_POWDER),
        // Orange
        List.of(Material.ORANGE_CONCRETE, Material.ORANGE_WOOL, Material.ORANGE_TERRACOTTA, Material.ORANGE_GLAZED_TERRACOTTA, Material.ORANGE_CONCRETE_POWDER),
        // Yellow
        List.of(Material.YELLOW_CONCRETE, Material.YELLOW_WOOL, Material.YELLOW_TERRACOTTA, Material.YELLOW_GLAZED_TERRACOTTA, Material.YELLOW_CONCRETE_POWDER),
        // Lime/Green
        List.of(Material.LIME_CONCRETE, Material.LIME_WOOL, Material.LIME_TERRACOTTA, Material.LIME_GLAZED_TERRACOTTA, Material.LIME_CONCRETE_POWDER, Material.GREEN_CONCRETE, Material.GREEN_WOOL, Material.GREEN_TERRACOTTA, Material.GREEN_GLAZED_TERRACOTTA, Material.GREEN_CONCRETE_POWDER),
        // Blue
        List.of(Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_WOOL, Material.LIGHT_BLUE_TERRACOTTA, Material.LIGHT_BLUE_GLAZED_TERRACOTTA, Material.LIGHT_BLUE_CONCRETE_POWDER, Material.BLUE_CONCRETE, Material.BLUE_WOOL, Material.BLUE_TERRACOTTA, Material.BLUE_GLAZED_TERRACOTTA, Material.BLUE_CONCRETE_POWDER, Material.CYAN_CONCRETE, Material.CYAN_WOOL, Material.CYAN_TERRACOTTA, Material.CYAN_GLAZED_TERRACOTTA, Material.CYAN_CONCRETE_POWDER),
        // Purple
        List.of(Material.PURPLE_CONCRETE, Material.PURPLE_WOOL, Material.PURPLE_TERRACOTTA, Material.PURPLE_GLAZED_TERRACOTTA, Material.PURPLE_CONCRETE_POWDER, Material.MAGENTA_CONCRETE, Material.MAGENTA_WOOL, Material.MAGENTA_TERRACOTTA, Material.MAGENTA_GLAZED_TERRACOTTA, Material.MAGENTA_CONCRETE_POWDER),
        // Pink
        List.of(Material.PINK_CONCRETE, Material.PINK_WOOL, Material.PINK_TERRACOTTA, Material.PINK_GLAZED_TERRACOTTA, Material.PINK_CONCRETE_POWDER),
        // Brown
        List.of(Material.BROWN_CONCRETE, Material.BROWN_WOOL, Material.BROWN_TERRACOTTA, Material.BROWN_GLAZED_TERRACOTTA, Material.BROWN_CONCRETE_POWDER),
        // White/Gray
        List.of(Material.WHITE_CONCRETE, Material.WHITE_WOOL, Material.WHITE_TERRACOTTA, Material.WHITE_GLAZED_TERRACOTTA, Material.WHITE_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_WOOL, Material.LIGHT_GRAY_TERRACOTTA, Material.LIGHT_GRAY_GLAZED_TERRACOTTA, Material.LIGHT_GRAY_CONCRETE_POWDER, Material.GRAY_CONCRETE, Material.GRAY_WOOL, Material.GRAY_TERRACOTTA, Material.GRAY_GLAZED_TERRACOTTA, Material.GRAY_CONCRETE_POWDER),
        // Black
        List.of(Material.BLACK_CONCRETE, Material.BLACK_WOOL, Material.BLACK_TERRACOTTA, Material.BLACK_GLAZED_TERRACOTTA, Material.BLACK_CONCRETE_POWDER)
    );
    private static final Map<UUID, Integer> playerPages = new ConcurrentHashMap<>();;
    private static final String PDC_PAGE_KEY = "color_picker_page";
    private static final Plugin PLUGIN = Bukkit.getPluginManager().getPlugin("MidnightSlashBlock");

    public static void openColorPicker(Player player) {
        int page = 0;
        if (PLUGIN != null) {
            Integer stored = player.getPersistentDataContainer().get(new NamespacedKey(PLUGIN, PDC_PAGE_KEY), PersistentDataType.INTEGER);
            if (stored != null) page = stored;
        }
        openColorPicker(player, page);
    }

    public static void openColorPicker(Player player, int page) {
        UUID uuid = player.getUniqueId();
        int totalPages = (int) Math.ceil((double) GRADIENTS.size() / GRADIENTS_PER_PAGE);
        page = Math.max(0, Math.min(page, totalPages - 1));
        playerPages.put(uuid, page);
        if (PLUGIN != null) {
            player.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, PDC_PAGE_KEY), PersistentDataType.INTEGER, page);
        }
        String titleStr = "<#e67e22>Pick a Color <gray>(Page " + (page + 1) + "/" + totalPages + ")";
        Component title = MiniMessage.miniMessage().deserialize(titleStr);
        Inventory inv = Bukkit.createInventory(null, ROWS * COLS, title);
        int gradientStart = page * GRADIENTS_PER_PAGE;
        for (int row = 0; row < GRADIENTS_PER_PAGE; row++) {
            int gradientIdx = gradientStart + row;
            if (gradientIdx >= GRADIENTS.size()) break;
            List<Material> gradient = GRADIENTS.get(gradientIdx);
            for (int col = 0; col < Math.min(gradient.size(), COLS); col++) {
                Material mat = gradient.get(col);
                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                fun.mntale.midnightSlashBlock.utils.BlockColorUtil.BlockColorInfo info = fun.mntale.midnightSlashBlock.utils.BlockColorUtil.BLOCK_COLOR_INFO.get(mat);
                if (info != null) {
                    String display = "<" + info.hex() + ">" + info.name() + "</" + info.hex() + "> <gray>(" + info.hex() + " rgb(" + info.rgb() + "))</gray>";
                    meta.displayName(MiniMessage.miniMessage().deserialize(display));
                } else {
                    meta.displayName(MiniMessage.miniMessage().deserialize("<white>Unknown Block"));
                }
                meta.lore(List.of(MiniMessage.miniMessage().deserialize("<gray>Click to select")));
                item.setItemMeta(meta);
                int slot = row * COLS + col;
                inv.setItem(slot, item);
            }
        }
        // Navigation buttons (bottom row)
        int navRow = (ROWS - 1) * COLS;
        // Previous page
        ItemStack prev = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.displayName(MiniMessage.miniMessage().deserialize(page > 0 ? "<green><bold>< Previous" : "<gray>< Previous"));
        prev.setItemMeta(prevMeta);
        inv.setItem(navRow + 2, prev);
        // Page indicator
        ItemStack pageItem = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.displayName(MiniMessage.miniMessage().deserialize("<white>Page " + (page + 1) + "/" + totalPages));
        pageItem.setItemMeta(pageMeta);
        inv.setItem(navRow + 4, pageItem);
        // Next page
        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(MiniMessage.miniMessage().deserialize(page < totalPages - 1 ? "<green><bold>Next >" : "<gray>Next >"));
        next.setItemMeta(nextMeta);
        inv.setItem(navRow + 6, next);
        // Close button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.displayName(MiniMessage.miniMessage().deserialize("<red><bold>Close"));
        close.setItemMeta(closeMeta);
        inv.setItem(navRow + 8, close);
        // Always close and reopen to force refresh
        player.closeInventory();
        Bukkit.getScheduler().runTaskLater(PLUGIN, () -> player.openInventory(inv), 1L);
    }

    public static void handleInventoryClick(Player player, int slot) {
        UUID uuid = player.getUniqueId();
        // Always get the current page from PDC for accuracy
        int page = 0;
        if (PLUGIN != null) {
            Integer stored = player.getPersistentDataContainer().get(new NamespacedKey(PLUGIN, PDC_PAGE_KEY), PersistentDataType.INTEGER);
            if (stored != null) page = stored;
        }
        int totalPages = (int) Math.ceil((double) GRADIENTS.size() / GRADIENTS_PER_PAGE);
        int navRow = (ROWS - 1) * COLS;
        if (slot == navRow + 2 && page > 0) {
            openColorPicker(player, page - 1);
            return;
        }
        if (slot == navRow + 6 && page < totalPages - 1) {
            openColorPicker(player, page + 1);
            return;
        }
        if (slot == navRow + 8) {
            if (PLUGIN != null) {
                player.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, PDC_PAGE_KEY), PersistentDataType.INTEGER, page);
            }
            player.closeInventory();
            return;
        }
        // Block selection
        int row = slot / COLS;
        int col = slot % COLS;
        if (row < GRADIENTS_PER_PAGE) {
            int gradientIdx = page * GRADIENTS_PER_PAGE + row;
            if (gradientIdx < GRADIENTS.size()) {
                List<Material> gradient = GRADIENTS.get(gradientIdx);
                if (col < gradient.size()) {
                    Material mat = gradient.get(col);
                    // Call your block placement handler here
                    player.closeInventory();
                    player.updateInventory();
                    fun.mntale.midnightSlashBlock.managers.BlockPlacementHandler.handleBlockPlacement(player, mat, fun.mntale.midnightSlashBlock.MidnightSlashBlock.pendingPlacements.get(uuid));
                    if (PLUGIN != null) {
                        player.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, PDC_PAGE_KEY), PersistentDataType.INTEGER, page);
                    }
                    // Do not close inventory here (let user keep picking)
                }
            }
        }
    }

    public static void handleInventoryClose(Player player) {
        playerPages.remove(player.getUniqueId());
    }
} 