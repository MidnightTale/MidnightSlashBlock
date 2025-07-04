package fun.mntale.midnightSlashBlock.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CenterCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Only players can use this command.");
            return;
        }
        player.teleportAsync(new Location(player.getWorld(), 0.5, 210, 0.5, player.getLocation().getYaw(), player.getLocation().getPitch()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Teleported to the center of the canvas!"));
    }
} 