package fun.mntale.midnightSlashBlock.utils;

import org.bukkit.DyeColor;
import java.util.HashMap;
import java.util.Map;

public class BlockColorUtil {
    public static final Map<DyeColor, String> COLOR_HEX = new HashMap<>();
    static {
        COLOR_HEX.put(DyeColor.WHITE, "#f0f0f0");
        COLOR_HEX.put(DyeColor.ORANGE, "#ffb347");
        COLOR_HEX.put(DyeColor.MAGENTA, "#ff55ff");
        COLOR_HEX.put(DyeColor.LIGHT_BLUE, "#55ffff");
        COLOR_HEX.put(DyeColor.YELLOW, "#ffff55");
        COLOR_HEX.put(DyeColor.LIME, "#55ff55");
        COLOR_HEX.put(DyeColor.PINK, "#ffb6c1");
        COLOR_HEX.put(DyeColor.GRAY, "#888888");
        COLOR_HEX.put(DyeColor.LIGHT_GRAY, "#cccccc");
        COLOR_HEX.put(DyeColor.CYAN, "#00aaaa");
        COLOR_HEX.put(DyeColor.PURPLE, "#aa00aa");
        COLOR_HEX.put(DyeColor.BLUE, "#5555ff");
        COLOR_HEX.put(DyeColor.BROWN, "#a0522d");
        COLOR_HEX.put(DyeColor.GREEN, "#00aa00");
        COLOR_HEX.put(DyeColor.RED, "#ff5555");
        COLOR_HEX.put(DyeColor.BLACK, "#222222");
    }
} 