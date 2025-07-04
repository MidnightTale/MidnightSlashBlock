package fun.mntale.midnightSlashBlock;

import fun.mntale.midnightSlashBlock.listeners.BlockInteractListener;
import fun.mntale.midnightSlashBlock.listeners.CanvasPlayerListener;
import fun.mntale.midnightSlashBlock.managers.CanvasManager;
import fun.mntale.midnightSlashBlock.world.CanvasWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public final class MidnightSlashBlock extends JavaPlugin {
    private CanvasManager canvasManager;
    private CanvasWorldManager canvasWorldManager;

    @Override
    public void onEnable() {
        this.canvasWorldManager = new CanvasWorldManager(this);
        World canvasWorld = canvasWorldManager.getCanvasWorld();
        Bukkit.getPluginManager().registerEvents(new BlockInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new CanvasPlayerListener(canvasWorld), this);
        // Defer CanvasManager initialization until canvas world is loaded
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onWorldLoad(WorldLoadEvent event) {
                if (event.getWorld().getName().equalsIgnoreCase("canvas")) {
                    MinecraftServer nmsServer = ((org.bukkit.craftbukkit.CraftServer) Bukkit.getServer()).getServer();
                    ServerLevel nmsWorld = nmsServer.getLevel(net.minecraft.resources.ResourceKey.create(
                        net.minecraft.core.registries.Registries.DIMENSION,
                        net.minecraft.resources.ResourceLocation.parse("minecraft:canvas")
                    ));
                    if (nmsWorld != null) {
                        canvasManager = new CanvasManager(nmsWorld, 512, new File(getDataFolder(), "canvas-blocks.txt"));
                        canvasManager.createOrLoadCanvas();
                    }
                }
            }
        }, this);
    }

    @Override
    public void onDisable() {
        if (canvasManager != null) {
            canvasManager.saveCanvas();
        }
    }
}
