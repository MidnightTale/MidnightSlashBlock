package fun.mntale.midnightSlashBlock.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.RenderType;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TablistUtil {
    public static void updateTablist(Player player, int blockCount, boolean onCooldown) {
        String color = onCooldown ? "#AAAAAA" : "#FFFFFF";
        var nameComponent = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<" + color + ">" + player.getName() + "</" + color + ">");
        player.playerListName(nameComponent);
        player.displayName(nameComponent);
        player.customName(nameComponent);
        player.setCustomNameVisible(true);
        // Scoreboard sorting
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective obj = scoreboard.getObjective("blocks_placed");
        if (obj == null) obj = scoreboard.registerNewObjective("blocks_placed", Criteria.DUMMY, Component.text("Blocks Placed"), RenderType.INTEGER);
        obj.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        obj.getScore(player.getName()).setScore(blockCount);
        // Below name display
        Objective below = scoreboard.getObjective("blocks_placed_below");
        if (below == null) below = scoreboard.registerNewObjective(
            "blocks_placed_below",
            Criteria.DUMMY,
            Component.text("placed"),
            RenderType.INTEGER
        );
        below.setDisplaySlot(DisplaySlot.BELOW_NAME);
        below.getScore(player.getName()).setScore(blockCount);
        player.setScoreboard(scoreboard);
    }

    public static void updateTablistHeaderFooter() {
        int total = fun.mntale.midnightSlashBlock.MidnightSlashBlock.getBlockPlaceDataManager().getTotalBlockCount();
        int online = Bukkit.getOnlinePlayers().size();
        net.kyori.adventure.text.Component header = MiniMessage.miniMessage().deserialize("<gradient:#00BFFF:#8A2BE2><bold>MN/Block</bold></gradient>");
        net.kyori.adventure.text.Component footer = MiniMessage.miniMessage().deserialize(
            "<#FF5AF7>Total Placed: <#FFD700>" + total + "</#FFD700> <#3A3A4A>|</#3A3A4A> <#B266FF>Online: <#00FFD0>" + online + "</#00FFD0></#B266FF>"
        );
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            player.sendPlayerListHeader(header);
            player.sendPlayerListFooter(footer);
        }
    }
} 