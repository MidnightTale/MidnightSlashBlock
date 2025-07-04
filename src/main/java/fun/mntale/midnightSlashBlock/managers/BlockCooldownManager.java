package fun.mntale.midnightSlashBlock.managers;

import fun.mntale.midnightSlashBlock.MidnightSlashBlock;
import java.util.UUID;

public class BlockCooldownManager {
    public static boolean isOnCooldown(UUID uuid) {
        Long last = MidnightSlashBlock.cooldowns.get(uuid);
        if (last == null) return false;
        return System.currentTimeMillis() - last < MidnightSlashBlock.COOLDOWN_MS;
    }

    public static long getCooldownLeft(UUID uuid) {
        Long last = MidnightSlashBlock.cooldowns.get(uuid);
        if (last == null) return 0;
        long left = MidnightSlashBlock.COOLDOWN_MS - (System.currentTimeMillis() - last);
        return Math.max(0, left / 1000);
    }

    public static void setCooldown(UUID uuid) {
        MidnightSlashBlock.cooldowns.put(uuid, System.currentTimeMillis());
    }
} 