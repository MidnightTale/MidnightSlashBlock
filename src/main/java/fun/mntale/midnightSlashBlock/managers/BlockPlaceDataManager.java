package fun.mntale.midnightSlashBlock.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockPlaceDataManager {
    private final Plugin plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Integer> blockCounts = new HashMap<>();

    public BlockPlaceDataManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "block_place_data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadAll();
    }

    public void loadAll() {
        blockCounts.clear();
        if (dataFile.exists()) {
            for (String key : dataConfig.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    int count = dataConfig.getInt(key, 0);
                    blockCounts.put(uuid, count);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Integer> entry : blockCounts.entrySet()) {
            dataConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save block place data: " + e.getMessage());
        }
    }

    public void loadPlayer(UUID uuid) {
        int count = dataConfig.getInt(uuid.toString(), 0);
        blockCounts.put(uuid, count);
    }

    public void savePlayer(UUID uuid) {
        int count = blockCounts.getOrDefault(uuid, 0);
        dataConfig.set(uuid.toString(), count);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save block place data for " + uuid + ": " + e.getMessage());
        }
    }

    public int getBlockCount(UUID uuid) {
        return blockCounts.getOrDefault(uuid, 0);
    }

    public void incrementBlockCount(UUID uuid) {
        blockCounts.put(uuid, getBlockCount(uuid) + 1);
    }

    public void setBlockCount(UUID uuid, int count) {
        blockCounts.put(uuid, count);
    }
} 