package fun.mntale.midnightSlashBlock.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.UUID;

public class BlockColorPickerGUI {
    public static void openColorPicker(Player player) {
        UUID uuid = player.getUniqueId();
        long left = fun.mntale.midnightSlashBlock.managers.BlockCooldownManager.getCooldownLeft(uuid);
        String titleStr = "<#e67e22>Pick a Color <gray>(" + left + "s left)";
        Component title = MiniMessage.miniMessage().deserialize(titleStr);
        Inventory inv = Bukkit.createInventory(null, 27, title);
        for (int i = 0; i < 16; i++) {
            DyeColor color = DyeColor.values()[i];
            Material mat = Material.valueOf(color.name() + "_CONCRETE");
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            String hex = BlockColorUtil.COLOR_HEX.getOrDefault(color, "#ffffff");
            String name = color.name().replace('_', ' ').toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            meta.displayName(MiniMessage.miniMessage().deserialize("<" + hex + ">" + name));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }
} 