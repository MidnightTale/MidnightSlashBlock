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
    private final File file;
    private final Yaml yaml = new Yaml();
    private final Map<String, List<BlockPlacementMeta>> metaMap = new ConcurrentHashMap<>();;

    public BlockPlacementMetaManager(Plugin plugin) {
        this.file = new File(plugin.getDataFolder(), "block_placements.yml");
        load();
    }

    private String key(String world, int x, int y, int z) {
        return world + ":" + x + ":" + y + ":" + z;
    }

    public void recordPlacement(World world, int x, int y, int z, UUID playerUuid, String playerName, Material type, long timestamp) {
        BlockPlacementMeta meta = new BlockPlacementMeta(world.getName(), x, y, z, playerUuid, playerName, type, timestamp);
        String k = key(world.getName(), x, y, z);
        metaMap.computeIfAbsent(k, s -> new ArrayList<>()).add(meta);
        save();
    }

    // Returns the latest placement meta for a block
    public Optional<BlockPlacementMeta> getMeta(World world, int x, int y, int z) {
        List<BlockPlacementMeta> list = metaMap.get(key(world.getName(), x, y, z));
        if (list == null || list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(list.size() - 1));
    }

    // Returns the full history for a block
    public List<BlockPlacementMeta> getHistory(World world, int x, int y, int z) {
        return metaMap.getOrDefault(key(world.getName(), x, y, z), Collections.emptyList());
    }

    public void load() {
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Map<String, List<Map<String, Object>>> data = yaml.load(reader);
            if (data != null) {
                for (Map.Entry<String, List<Map<String, Object>>> entry : data.entrySet()) {
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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        Map<String, List<Map<String, Object>>> data = new ConcurrentHashMap<>();;
        for (Map.Entry<String, List<BlockPlacementMeta>> entry : metaMap.entrySet()) {
            List<Map<String, Object>> metaList = new ArrayList<>();
            for (BlockPlacementMeta meta : entry.getValue()) {
                Map<String, Object> m = new ConcurrentHashMap<>();;
                m.put("world", meta.world);
                m.put("x", meta.x);
                m.put("y", meta.y);
                m.put("z", meta.z);
                m.put("playerUuid", meta.playerUuid.toString());
                m.put("playerName", meta.playerName);
                m.put("blockType", meta.blockType.name());
                m.put("timestamp", meta.timestamp);
                metaList.add(m);
            }
            data.put(entry.getKey(), metaList);
        }
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 