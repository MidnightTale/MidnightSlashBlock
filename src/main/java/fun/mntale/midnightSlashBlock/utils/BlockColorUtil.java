package fun.mntale.midnightSlashBlock.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockColorUtil {
    public static final Map<DyeColor, String> COLOR_HEX = new ConcurrentHashMap<>();
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

    public record BlockColorInfo(String name, String hex, String rgb) {}
    public static final Map<Material, BlockColorInfo> BLOCK_COLOR_INFO = new ConcurrentHashMap<>();
    static {
        BLOCK_COLOR_INFO.put(Material.RED_CONCRETE, new BlockColorInfo("Crimson", "#DC143C", "220, 20, 60"));
        BLOCK_COLOR_INFO.put(Material.RED_WOOL, new BlockColorInfo("Red", "#FF0000", "255, 0, 0"));
        BLOCK_COLOR_INFO.put(Material.RED_TERRACOTTA, new BlockColorInfo("FireBrick", "#B22222", "178, 34, 34"));
        BLOCK_COLOR_INFO.put(Material.RED_GLAZED_TERRACOTTA, new BlockColorInfo("DarkRed", "#8B0000", "139, 0, 0"));
        BLOCK_COLOR_INFO.put(Material.RED_CONCRETE_POWDER, new BlockColorInfo("IndianRed", "#CD5C5C", "205, 92, 92"));
        BLOCK_COLOR_INFO.put(Material.ORANGE_CONCRETE, new BlockColorInfo("Orange", "#FFA500", "255, 165, 0"));
        BLOCK_COLOR_INFO.put(Material.ORANGE_WOOL, new BlockColorInfo("DarkOrange", "#FF8C00", "255, 140, 0"));
        BLOCK_COLOR_INFO.put(Material.ORANGE_TERRACOTTA, new BlockColorInfo("Coral", "#FF7F50", "255, 127, 80"));
        BLOCK_COLOR_INFO.put(Material.ORANGE_GLAZED_TERRACOTTA, new BlockColorInfo("Tomato", "#FF6347", "255, 99, 71"));
        BLOCK_COLOR_INFO.put(Material.ORANGE_CONCRETE_POWDER, new BlockColorInfo("OrangeRed", "#FF4500", "255, 69, 0"));
        BLOCK_COLOR_INFO.put(Material.YELLOW_CONCRETE, new BlockColorInfo("Yellow", "#FFFF00", "255, 255, 0"));
        BLOCK_COLOR_INFO.put(Material.YELLOW_WOOL, new BlockColorInfo("Gold", "#FFD700", "255, 215, 0"));
        BLOCK_COLOR_INFO.put(Material.YELLOW_TERRACOTTA, new BlockColorInfo("Khaki", "#F0E68C", "240, 230, 140"));
        BLOCK_COLOR_INFO.put(Material.YELLOW_GLAZED_TERRACOTTA, new BlockColorInfo("LightYellow", "#FFFFE0", "255, 255, 224"));
        BLOCK_COLOR_INFO.put(Material.YELLOW_CONCRETE_POWDER, new BlockColorInfo("LemonChiffon", "#FFFACD", "255, 250, 205"));
        BLOCK_COLOR_INFO.put(Material.LIME_CONCRETE, new BlockColorInfo("Lime", "#00FF00", "0, 255, 0"));
        BLOCK_COLOR_INFO.put(Material.LIME_WOOL, new BlockColorInfo("Chartreuse", "#7FFF00", "127, 255, 0"));
        BLOCK_COLOR_INFO.put(Material.LIME_TERRACOTTA, new BlockColorInfo("LawnGreen", "#7CFC00", "124, 252, 0"));
        BLOCK_COLOR_INFO.put(Material.LIME_GLAZED_TERRACOTTA, new BlockColorInfo("SpringGreen", "#00FF7F", "0, 255, 127"));
        BLOCK_COLOR_INFO.put(Material.LIME_CONCRETE_POWDER, new BlockColorInfo("MediumSpringGreen", "#00FA9A", "0, 250, 154"));
        BLOCK_COLOR_INFO.put(Material.GREEN_CONCRETE, new BlockColorInfo("Green", "#008000", "0, 128, 0"));
        BLOCK_COLOR_INFO.put(Material.GREEN_WOOL, new BlockColorInfo("ForestGreen", "#228B22", "34, 139, 34"));
        BLOCK_COLOR_INFO.put(Material.GREEN_TERRACOTTA, new BlockColorInfo("SeaGreen", "#2E8B57", "46, 139, 87"));
        BLOCK_COLOR_INFO.put(Material.GREEN_GLAZED_TERRACOTTA, new BlockColorInfo("OliveDrab", "#6B8E23", "107, 142, 35"));
        BLOCK_COLOR_INFO.put(Material.GREEN_CONCRETE_POWDER, new BlockColorInfo("DarkOliveGreen", "#556B2F", "85, 107, 47"));
        BLOCK_COLOR_INFO.put(Material.LIGHT_BLUE_CONCRETE, new BlockColorInfo("LightBlue", "#ADD8E6", "173, 216, 230"));
        BLOCK_COLOR_INFO.put(Material.LIGHT_BLUE_WOOL, new BlockColorInfo("SkyBlue", "#87CEEB", "135, 206, 235"));
        BLOCK_COLOR_INFO.put(Material.LIGHT_BLUE_TERRACOTTA, new BlockColorInfo("DeepSkyBlue", "#00BFFF", "0, 191, 255"));
        BLOCK_COLOR_INFO.put(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, new BlockColorInfo("DodgerBlue", "#1E90FF", "30, 144, 255"));
        BLOCK_COLOR_INFO.put(Material.LIGHT_BLUE_CONCRETE_POWDER, new BlockColorInfo("SteelBlue", "#4682B4", "70, 130, 180"));
        BLOCK_COLOR_INFO.put(Material.BLUE_CONCRETE, new BlockColorInfo("Blue", "#0000FF", "0, 0, 255"));
        BLOCK_COLOR_INFO.put(Material.BLUE_WOOL, new BlockColorInfo("MediumBlue", "#0000CD", "0, 0, 205"));
        BLOCK_COLOR_INFO.put(Material.BLUE_TERRACOTTA, new BlockColorInfo("RoyalBlue", "#4169E1", "65, 105, 225"));
        BLOCK_COLOR_INFO.put(Material.BLUE_GLAZED_TERRACOTTA, new BlockColorInfo("SlateBlue", "#6A5ACD", "106, 90, 205"));
        BLOCK_COLOR_INFO.put(Material.BLUE_CONCRETE_POWDER, new BlockColorInfo("DarkSlateBlue", "#483D8B", "72, 61, 139"));
        BLOCK_COLOR_INFO.put(Material.CYAN_CONCRETE, new BlockColorInfo("Cyan", "#00FFFF", "0, 255, 255"));
        BLOCK_COLOR_INFO.put(Material.CYAN_WOOL, new BlockColorInfo("DarkCyan", "#008B8B", "0, 139, 139"));
        BLOCK_COLOR_INFO.put(Material.CYAN_TERRACOTTA, new BlockColorInfo("Teal", "#008080", "0, 128, 128"));
        BLOCK_COLOR_INFO.put(Material.CYAN_GLAZED_TERRACOTTA, new BlockColorInfo("Turquoise", "#40E0D0", "64, 224, 208"));
        BLOCK_COLOR_INFO.put(Material.CYAN_CONCRETE_POWDER, new BlockColorInfo("MediumTurquoise", "#48D1CC", "72, 209, 204"));
        BLOCK_COLOR_INFO.put(Material.PURPLE_CONCRETE, new BlockColorInfo("Purple", "#800080", "128, 0, 128"));
        BLOCK_COLOR_INFO.put(Material.PURPLE_WOOL, new BlockColorInfo("Indigo", "#4B0082", "75, 0, 130"));
        BLOCK_COLOR_INFO.put(Material.PURPLE_TERRACOTTA, new BlockColorInfo("RebeccaPurple", "#663399", "102, 51, 153"));
        BLOCK_COLOR_INFO.put(Material.PURPLE_GLAZED_TERRACOTTA, new BlockColorInfo("BlueViolet", "#8A2BE2", "138, 43, 226"));
        BLOCK_COLOR_INFO.put(Material.PURPLE_CONCRETE_POWDER, new BlockColorInfo("DarkViolet", "#9400D3", "148, 0, 211"));
        BLOCK_COLOR_INFO.put(Material.MAGENTA_CONCRETE, new BlockColorInfo("Magenta", "#FF00FF", "255, 0, 255"));
        BLOCK_COLOR_INFO.put(Material.MAGENTA_WOOL, new BlockColorInfo("Violet", "#EE82EE", "238, 130, 238"));
        BLOCK_COLOR_INFO.put(Material.MAGENTA_TERRACOTTA, new BlockColorInfo("Orchid", "#DA70D6", "218, 112, 214"));
        BLOCK_COLOR_INFO.put(Material.MAGENTA_GLAZED_TERRACOTTA, new BlockColorInfo("Plum", "#DDA0DD", "221, 160, 221"));
        BLOCK_COLOR_INFO.put(Material.MAGENTA_CONCRETE_POWDER, new BlockColorInfo("MediumOrchid", "#BA55D3", "186, 85, 211"));
        BLOCK_COLOR_INFO.put(Material.PINK_CONCRETE, new BlockColorInfo("Pink", "#FFC0CB", "255, 192, 203"));
        BLOCK_COLOR_INFO.put(Material.PINK_WOOL, new BlockColorInfo("LightPink", "#FFB6C1", "255, 182, 193"));
        BLOCK_COLOR_INFO.put(Material.PINK_TERRACOTTA, new BlockColorInfo("HotPink", "#FF69B4", "255, 105, 180"));
        BLOCK_COLOR_INFO.put(Material.PINK_GLAZED_TERRACOTTA, new BlockColorInfo("DeepPink", "#FF1493", "255, 20, 147"));
        BLOCK_COLOR_INFO.put(Material.PINK_CONCRETE_POWDER, new BlockColorInfo("PaleVioletRed", "#DB7093", "219, 112, 147"));
        BLOCK_COLOR_INFO.put(Material.BROWN_CONCRETE, new BlockColorInfo("Brown", "#A52A2A", "165, 42, 42"));
        BLOCK_COLOR_INFO.put(Material.BROWN_WOOL, new BlockColorInfo("SaddleBrown", "#8B4513", "139, 69, 19"));
        BLOCK_COLOR_INFO.put(Material.BROWN_TERRACOTTA, new BlockColorInfo("Sienna", "#A0522D", "160, 82, 45"));
        BLOCK_COLOR_INFO.put(Material.BROWN_GLAZED_TERRACOTTA, new BlockColorInfo("Peru", "#CD853F", "205, 133, 63"));
        BLOCK_COLOR_INFO.put(Material.BROWN_CONCRETE_POWDER, new BlockColorInfo("Chocolate", "#D2691E", "210, 105, 30"));
        BLOCK_COLOR_INFO.put(Material.WHITE_CONCRETE, new BlockColorInfo("White", "#FFFFFF", "255, 255, 255"));
        BLOCK_COLOR_INFO.put(Material.WHITE_WOOL, new BlockColorInfo("Snow", "#FFFAFA", "255, 250, 250"));
        BLOCK_COLOR_INFO.put(Material.WHITE_TERRACOTTA, new BlockColorInfo("Ivory", "#FFFFF0", "255, 255, 240"));
        BLOCK_COLOR_INFO.put(Material.WHITE_GLAZED_TERRACOTTA, new BlockColorInfo("FloralWhite", "#FFFAF0", "255, 250, 240"));
        BLOCK_COLOR_INFO.put(Material.WHITE_CONCRETE_POWDER, new BlockColorInfo("GhostWhite", "#F8F8FF", "248, 248, 255"));
        BLOCK_COLOR_INFO.put(Material.LIGHT_GRAY_CONCRETE, new BlockColorInfo("LightGray", "#D3D3D3", "211, 211, 211"));
        BLOCK_COLOR_INFO.put(Material.LIGHT_GRAY_WOOL, new BlockColorInfo("Gainsboro", "#DCDCDC", "220, 220, 220"));
        BLOCK_COLOR_INFO.put(Material.LIGHT_GRAY_TERRACOTTA, new BlockColorInfo("Silver", "#C0C0C0", "192, 192, 192"));
        BLOCK_COLOR_INFO.put(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, new BlockColorInfo("DarkGray", "#A9A9A9", "169, 169, 169"));
        BLOCK_COLOR_INFO.put(Material.LIGHT_GRAY_CONCRETE_POWDER, new BlockColorInfo("DimGray", "#696969", "105, 105, 105"));
        BLOCK_COLOR_INFO.put(Material.GRAY_CONCRETE, new BlockColorInfo("Gray", "#808080", "128, 128, 128"));
        BLOCK_COLOR_INFO.put(Material.GRAY_WOOL, new BlockColorInfo("SlateGray", "#708090", "112, 128, 144"));
        BLOCK_COLOR_INFO.put(Material.GRAY_TERRACOTTA, new BlockColorInfo("DarkSlateGray", "#2F4F4F", "47, 79, 79"));
        BLOCK_COLOR_INFO.put(Material.GRAY_GLAZED_TERRACOTTA, new BlockColorInfo("Black", "#000000", "0, 0, 0"));
        BLOCK_COLOR_INFO.put(Material.GRAY_CONCRETE_POWDER, new BlockColorInfo("Jet", "#343434", "52, 52, 52"));
        BLOCK_COLOR_INFO.put(Material.BLACK_CONCRETE, new BlockColorInfo("Black", "#000000", "0, 0, 0"));
        BLOCK_COLOR_INFO.put(Material.BLACK_WOOL, new BlockColorInfo("Onyx", "#353839", "53, 56, 57"));
        BLOCK_COLOR_INFO.put(Material.BLACK_TERRACOTTA, new BlockColorInfo("Charcoal", "#36454F", "54, 69, 79"));
        BLOCK_COLOR_INFO.put(Material.BLACK_GLAZED_TERRACOTTA, new BlockColorInfo("OuterSpace", "#414A4C", "65, 74, 76"));
        BLOCK_COLOR_INFO.put(Material.BLACK_CONCRETE_POWDER, new BlockColorInfo("RaisinBlack", "#242124", "36, 33, 36"));
    }
} 