package fun.mntale.midnightSlashBlock.models;

import org.bukkit.Material;
import java.util.UUID;

public class BlockPlacementMeta {
    public final String world;
    public final int x, y, z;
    public final UUID playerUuid;
    public final String playerName;
    public final Material blockType;
    public final long timestamp;

    public BlockPlacementMeta(String world, int x, int y, int z, UUID playerUuid, String playerName, Material blockType, long timestamp) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.blockType = blockType;
        this.timestamp = timestamp;
    }
} 