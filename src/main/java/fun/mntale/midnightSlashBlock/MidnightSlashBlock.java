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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public final class MidnightSlashBlock extends JavaPlugin {
    private CanvasManager canvasManager;
    private CanvasWorldManager canvasWorldManager;
    public static final long COOLDOWN_MS = 60000;
    public static final java.util.concurrent.ConcurrentMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    public static final java.util.concurrent.ConcurrentMap<UUID, net.minecraft.core.BlockPos> pendingPlacements = new ConcurrentHashMap<>();
    public static final java.util.Set<UUID> openColorPickers = ConcurrentHashMap.newKeySet();
    public static final java.util.concurrent.ConcurrentMap<UUID, Integer> blockChangeCount = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("[DEBUG] MidnightSlashBlock onEnable start");
        this.canvasWorldManager = new CanvasWorldManager(this);
        World canvasWorld = canvasWorldManager.getCanvasWorld();
        getLogger().info("[DEBUG] CanvasWorldManager created, canvasWorld: " + (canvasWorld != null ? canvasWorld.getName() : "null"));
        Bukkit.getPluginManager().registerEvents(new BlockInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new CanvasPlayerListener(canvasWorld, this), this);
        // Defer CanvasManager initialization until canvas world is loaded
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onWorldLoad(WorldLoadEvent event) {
                getLogger().info("[DEBUG] WorldLoadEvent: " + event.getWorld().getName());
                if (event.getWorld().getName().equalsIgnoreCase("canvas")) {
                    getLogger().info("[DEBUG] Canvas world loaded, looking up NMS world");
                    MinecraftServer nmsServer = ((org.bukkit.craftbukkit.CraftServer) Bukkit.getServer()).getServer();
                    ServerLevel nmsWorld = nmsServer.getLevel(net.minecraft.resources.ResourceKey.create(
                        net.minecraft.core.registries.Registries.DIMENSION,
                        net.minecraft.resources.ResourceLocation.parse("minecraft:canvas")
                    ));
                    getLogger().info("[DEBUG] NMS world for canvas: " + (nmsWorld != null ? nmsWorld.dimension().location() : "null"));
                    if (nmsWorld != null) {
                        canvasManager = new CanvasManager(nmsWorld, 512, MidnightSlashBlock.this);
                        getLogger().info("[DEBUG] Calling createOrLoadCanvas()");
                        canvasManager.createOrLoadCanvas();
                    } else {
                        getLogger().warning("[DEBUG] NMS world for 'canvas' is null!");
                    }
                }
            }
        }, this);

        // Eagerly check if canvas world is already loaded
        World loadedCanvas = Bukkit.getWorld("canvas");
        if (loadedCanvas != null) {
            getLogger().info("[DEBUG] Canvas world already loaded at onEnable, running canvas generation immediately.");
            MinecraftServer nmsServer = ((org.bukkit.craftbukkit.CraftServer) Bukkit.getServer()).getServer();
            ServerLevel nmsWorld = nmsServer.getLevel(net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION,
                net.minecraft.resources.ResourceLocation.parse("minecraft:canvas")
            ));
            getLogger().info("[DEBUG] NMS world for canvas: " + (nmsWorld != null ? nmsWorld.dimension().location() : "null"));
            if (nmsWorld != null) {
                canvasManager = new CanvasManager(nmsWorld, 512, this);
                getLogger().info("[DEBUG] Calling createOrLoadCanvas() (eager)");
                canvasManager.createOrLoadCanvas();
            } else {
                getLogger().warning("[DEBUG] NMS world for 'canvas' is null! (eager)");
            }
        }
        getLogger().info("[DEBUG] MidnightSlashBlock onEnable end");
    }

    @Override
    public void onDisable() {
        // No save needed; canvas is persistent in world
    }
}
