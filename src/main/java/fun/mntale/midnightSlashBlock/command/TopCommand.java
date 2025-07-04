package fun.mntale.midnightSlashBlock.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TopCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Only players can use this command.");
            return;
        }
        var loc = player.getLocation();
        player.teleportAsync(new Location(loc.getWorld(), loc.getX(), 500, loc.getZ(), loc.getYaw(), loc.getPitch()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Teleported to Y 500!"));
    }
} 