package fun.mntale.midnightSlashBlock.managers;

import fun.mntale.midnightSlashBlock.MidnightSlashBlock;
import java.util.UUID;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BlockCooldownManager {
    private static long getDynamicCooldownMs() {
        LocalDate baseDate = LocalDate.of(2025, 7, 5);
        LocalDate today = LocalDate.now();
        long daysSince = ChronoUnit.DAYS.between(baseDate, today);
        long baseCooldown = 1_000L; // 60 seconds
        long incrementPerDay = 3_000L; // 1 second per day
        long extra = Math.max(0, daysSince) * incrementPerDay;
        return baseCooldown + extra;
    }

    public static boolean isOnCooldown(UUID uuid) {
        Long last = MidnightSlashBlock.cooldowns.get(uuid);
        if (last == null) return false;
        return System.currentTimeMillis() - last < getDynamicCooldownMs();
    }

    public static long getCooldownLeft(UUID uuid) {
        Long last = MidnightSlashBlock.cooldowns.get(uuid);
        if (last == null) return 0;
        long left = getDynamicCooldownMs() - (System.currentTimeMillis() - last);
        return Math.max(0, left / 1000);
    }

    public static void setCooldown(UUID uuid) {
        MidnightSlashBlock.cooldowns.put(uuid, System.currentTimeMillis());
    }
} 