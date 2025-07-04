package fun.mntale.midnightSlashBlock.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import fun.mntale.midnightSlashBlock.MidnightSlashBlock;

@NullMarked
public class PlayerSizeCommand implements BasicCommand {

    private final MidnightSlashBlock plugin;

    public PlayerSizeCommand(MidnightSlashBlock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Only players can use this command.");
            return;
        }
        if (args.length < 1) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /size <size>"));
            return;
        }
        double size;
        try {
            size = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid size value."));
            return;
        }
        if (size < 0.1 || size > 10.0) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Size must be between 0.1 and 10.0."));
            return;
        }
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                AttributeInstance attr = null;
                try {
                    attr = other.getAttribute(Attribute.valueOf("SCALE"));
                } catch (IllegalArgumentException ignored) {}
                if (attr != null) attr.setBaseValue(size);
            }
        }
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Set player size to <yellow>" + size + "</yellow>!"));
    }
} 