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
import fun.mntale.midnightSlashBlock.managers.BlockPlaceDataManager;
import fun.mntale.midnightSlashBlock.managers.BlockPlacementMetaManager;
import fun.mntale.midnightSlashBlock.managers.BlockInspectorService;
import fun.mntale.midnightSlashBlock.managers.PlayerViewSettingsManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import fun.mntale.midnightSlashBlock.command.CenterCommand;
import fun.mntale.midnightSlashBlock.command.HidePlayersCommand;
import fun.mntale.midnightSlashBlock.command.PlayerSizeCommand;
import fun.mntale.midnightSlashBlock.command.TopCommand;

public final class MidnightSlashBlock extends JavaPlugin {
    private CanvasManager canvasManager;
    private CanvasWorldManager canvasWorldManager;
    public static final java.util.concurrent.ConcurrentMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    public static final java.util.concurrent.ConcurrentMap<UUID, net.minecraft.core.BlockPos> pendingPlacements = new ConcurrentHashMap<>();
    public static final java.util.Set<UUID> openColorPickers = ConcurrentHashMap.newKeySet();
    private static BlockPlaceDataManager blockPlaceDataManager;
    private static BlockPlacementMetaManager blockPlacementMetaManager;
    public static BlockPlaceDataManager getBlockPlaceDataManager() { return blockPlaceDataManager; }
    public static BlockPlacementMetaManager getBlockPlacementMetaManager() { return blockPlacementMetaManager; }

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
        blockPlaceDataManager = new BlockPlaceDataManager(this);
        blockPlacementMetaManager = new BlockPlacementMetaManager(this);
        blockPlacementMetaManager.load(); // Migrate old data format
        new BlockInspectorService(this, blockPlacementMetaManager);
        
        // Periodic cache cleanup for block placement data (every 5 minutes)
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (blockPlacementMetaManager != null) {
                int cacheSize = blockPlacementMetaManager.getCacheSize();
                if (cacheSize > 1000) { // Clear if more than 1000 positions cached
                    blockPlacementMetaManager.clearCache();
                    getLogger().info("Cleared block placement cache (" + cacheSize + " positions)");
                }
            }
        }, 6000L, 6000L); // 5 minutes (6000 ticks)
        
        // Register player view commands using BasicCommand
        BasicCommand centerCommand = new CenterCommand();
        registerCommand("center", centerCommand);
        BasicCommand hidePlayersCommand = new HidePlayersCommand(this);
        registerCommand("hide", hidePlayersCommand);
        BasicCommand playerSizeCommand = new PlayerSizeCommand(this);
        registerCommand("size", playerSizeCommand);
        BasicCommand topCommand = new TopCommand();
        registerCommand("top", topCommand);
        getLogger().info("[DEBUG] MidnightSlashBlock onEnable end");
    }

    @Override
    public void onDisable() {
        if (blockPlaceDataManager != null) blockPlaceDataManager.saveAll();
    }
}
