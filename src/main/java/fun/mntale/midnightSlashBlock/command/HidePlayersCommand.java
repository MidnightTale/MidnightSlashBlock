package fun.mntale.midnightSlashBlock.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import fun.mntale.midnightSlashBlock.managers.PlayerViewSettingsManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class HidePlayersCommand implements BasicCommand {
    private final PlayerViewSettingsManager viewManager;
    private final Plugin plugin;
    public HidePlayersCommand(Plugin plugin) {
        this.plugin = plugin;
        this.viewManager = new PlayerViewSettingsManager(plugin);
    }
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Only players can use this command.");
            return;
        }
        boolean hide = !viewManager.isHidingPlayers(player);
        viewManager.setHidePlayers(player, hide);
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(player)) continue;
            if (hide) {
                player.hidePlayer(plugin, other);
            } else {
                player.showPlayer(plugin, other);
            }
        }
        player.sendMessage(MiniMessage.miniMessage().deserialize(hide ? "<gray>All players hidden!" : "<green>Players visible!"));
    }
} 