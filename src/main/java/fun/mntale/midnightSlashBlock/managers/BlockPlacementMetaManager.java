package fun.mntale.midnightSlashBlock.managers;

import fun.mntale.midnightSlashBlock.models.BlockPlacementMeta;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlockPlacementMetaManager {
    private final File dataDir;
    private final Yaml yaml = new Yaml();
    private final Map<String, List<BlockPlacementMeta>> metaMap = new ConcurrentHashMap<>();

    public BlockPlacementMetaManager(Plugin plugin) {
        this.dataDir = new File(plugin.getDataFolder(), "block_placements");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private String key(String world, int x, int y, int z) {
        return world + ":" + x + ":" + y + ":" + z;
    }

    private File getPositionFile(String world, int x, int y, int z) {
        File worldDir = new File(dataDir, world);
        if (!worldDir.exists()) {
            worldDir.mkdirs();
        }
        return new File(worldDir, x + "_" + y + "_" + z + ".yml");
    }

    public void recordPlacement(World world, int x, int y, int z, UUID playerUuid, String playerName, Material type, long timestamp) {
        BlockPlacementMeta meta = new BlockPlacementMeta(world.getName(), x, y, z, playerUuid, playerName, type, timestamp);
        String k = key(world.getName(), x, y, z);
        metaMap.computeIfAbsent(k, s -> new ArrayList<>()).add(meta);
        savePosition(world.getName(), x, y, z);
    }

    // Returns the latest placement meta for a block
    public Optional<BlockPlacementMeta> getMeta(World world, int x, int y, int z) {
        String k = key(world.getName(), x, y, z);
        List<BlockPlacementMeta> list = metaMap.get(k);
        if (list == null) {
            // Try to load from file
            list = loadPosition(world.getName(), x, y, z);
            if (list != null) {
                metaMap.put(k, list);
            }
        }
        if (list == null || list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(list.size() - 1));
    }

    // Returns the full history for a block
    public List<BlockPlacementMeta> getHistory(World world, int x, int y, int z) {
        String k = key(world.getName(), x, y, z);
        List<BlockPlacementMeta> list = metaMap.get(k);
        if (list == null) {
            // Try to load from file
            list = loadPosition(world.getName(), x, y, z);
            if (list != null) {
                metaMap.put(k, list);
            }
        }
        return list != null ? list : Collections.emptyList();
    }

    private List<BlockPlacementMeta> loadPosition(String world, int x, int y, int z) {
        File file = getPositionFile(world, x, y, z);
        if (!file.exists()) return null;
        
        try (FileReader reader = new FileReader(file)) {
            List<Map<String, Object>> data = yaml.load(reader);
            if (data == null) return null;
            
            List<BlockPlacementMeta> metas = new ArrayList<>();
            for (Map<String, Object> m : data) {
                BlockPlacementMeta meta = new BlockPlacementMeta(
                    (String)m.get("world"),
                    (int)m.get("x"),
                    (int)m.get("y"),
                    (int)m.get("z"),
                    UUID.fromString((String)m.get("playerUuid")),
                    (String)m.get("playerName"),
                    Material.valueOf((String)m.get("blockType")),
                    ((Number)m.get("timestamp")).longValue()
                );
                metas.add(meta);
            }
            return metas;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void savePosition(String world, int x, int y, int z) {
        String k = key(world, x, y, z);
        List<BlockPlacementMeta> metas = metaMap.get(k);
        if (metas == null) return;
        
        File file = getPositionFile(world, x, y, z);
        List<Map<String, Object>> data = new ArrayList<>();
        for (BlockPlacementMeta meta : metas) {
            Map<String, Object> m = new ConcurrentHashMap<>();
            m.put("world", meta.world);
            m.put("x", meta.x);
            m.put("y", meta.y);
            m.put("z", meta.z);
            m.put("playerUuid", meta.playerUuid.toString());
            m.put("playerName", meta.playerName);
            m.put("blockType", meta.blockType.name());
            m.put("timestamp", meta.timestamp);
            data.add(m);
        }
        
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Legacy method for loading old single-file format
    public void load() {
        File oldFile = new File(dataDir.getParentFile(), "block_placements.yml");
        File migrationFlag = new File(dataDir.getParentFile(), "migration_completed.flag");
        
        // Skip if migration already completed or no old file exists
        if (migrationFlag.exists() || !oldFile.exists()) return;
        
        try (FileReader reader = new FileReader(oldFile)) {
            Map<String, List<Map<String, Object>>> data = yaml.load(reader);
            if (data != null) {
                int migratedCount = 0;
                for (Map.Entry<String, List<Map<String, Object>>> entry : data.entrySet()) {
                    String[] parts = entry.getKey().split(":");
                    if (parts.length == 4) {
                        String world = parts[0];
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int z = Integer.parseInt(parts[3]);
                        
                        List<BlockPlacementMeta> metas = new ArrayList<>();
                        for (Map<String, Object> m : entry.getValue()) {
                            BlockPlacementMeta meta = new BlockPlacementMeta(
                                (String)m.get("world"),
                                (int)m.get("x"),
                                (int)m.get("y"),
                                (int)m.get("z"),
                                UUID.fromString((String)m.get("playerUuid")),
                                (String)m.get("playerName"),
                                Material.valueOf((String)m.get("blockType")),
                                ((Number)m.get("timestamp")).longValue()
                            );
                            metas.add(meta);
                        }
                        metaMap.put(entry.getKey(), metas);
                        savePosition(world, x, y, z);
                        migratedCount++;
                    }
                }
                
                // Move old file to backup
                File backupFile = new File(dataDir.getParentFile(), "block_placements.yml.backup");
                oldFile.renameTo(backupFile);
                
                // Create migration flag
                try {
                    migrationFlag.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                System.out.println("[MidnightSlashBlock] Migrated " + migratedCount + " positions from old format to new split-file format");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        // Save all cached positions
        for (Map.Entry<String, List<BlockPlacementMeta>> entry : metaMap.entrySet()) {
            String[] parts = entry.getKey().split(":");
            if (parts.length == 4) {
                String world = parts[0];
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int z = Integer.parseInt(parts[3]);
                savePosition(world, x, y, z);
            }
        }
    }

    /**
     * Clears the cache to free memory. Call this periodically or when memory usage is high.
     */
    public void clearCache() {
        metaMap.clear();
    }

    /**
     * Gets the number of cached positions
     */
    public int getCacheSize() {
        return metaMap.size();
    }
} 