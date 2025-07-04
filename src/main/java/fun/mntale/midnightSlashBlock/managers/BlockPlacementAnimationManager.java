package fun.mntale.midnightSlashBlock.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class BlockPlacementAnimationManager {
    private final Plugin plugin;
    public BlockPlacementAnimationManager(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Cubic ease-in-out for smooth animation.
     */
    private static float easeInOutCubic(float t) {
        return t < 0.5f ? 4 * t * t * t : 1 - (float)Math.pow(-2 * t + 2, 3) / 2;
    }

    /**
     * Quadratic Bézier interpolation for arc.
     */
    private static Vector3f bezier(Vector3f start, Vector3f control, Vector3f end, float t) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        Vector3f p = new Vector3f(start).mul(uu);
        p.add(new Vector3f(control).mul(2 * u * t));
        p.add(new Vector3f(end).mul(tt));
        return p;
    }

    /**
     * Spawns a BlockDisplay entity and animates it from the player in an arc to the target block position with scaling and easing.
     * At the end, places the real block and runs onComplete.
     */
    public void playBlockPlacementAnimation(Player player, Material material, Location targetBlockLocation, Runnable onComplete) {
        World world = targetBlockLocation.getWorld();
        if (world == null) {
            onComplete.run();
            return;
        }
        // Start at player eye, offset forward
        Location eye = player.getEyeLocation();
        org.bukkit.util.Vector look = eye.getDirection().normalize().multiply(0.8);
        Location start = eye.clone().add(look);
        Location end = targetBlockLocation.clone().add(0.5, 0.5, 0.5);
        // Control point: midpoint +1.5Y for arc
        Vector3f startVec = new Vector3f((float)start.getX(), (float)start.getY(), (float)start.getZ());
        Vector3f endVec = new Vector3f((float)end.getX(), (float)end.getY(), (float)end.getZ());
        Vector3f mid = new Vector3f(startVec).lerp(endVec, 0.5f);
        mid.y += 1.5f;
        BlockData blockData = Bukkit.createBlockData(material);
        // Initial transformation: small scale, initial rotation
        Transformation startTransform = new Transformation(
            new Vector3f(0, 0, 0),
            new AxisAngle4f(0, 1, 0, 0),
            new Vector3f(0.3f, 0.3f, 0.3f),
            new AxisAngle4f(0, 1, 0, 0)
        );
        BlockDisplay display = world.spawn(start, BlockDisplay.class, e -> {
            e.setBlock(blockData);
            e.setTransformation(startTransform);
            e.setTeleportDuration(4);
            e.setInterpolationDuration(10);
            e.setInterpolationDelay(2);
        });
        int steps = 14;
        int interval = 2; // ticks between steps
        for (int i = 1; i <= steps; i++) {
            final int step = i;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!display.isValid()) return;
                float t = easeInOutCubic(step / (float)steps);
                Vector3f pos = bezier(startVec, mid, endVec, t);
                Location next = new Location(world, pos.x, pos.y, pos.z);
                display.setTeleportDuration(interval);
                display.teleportAsync(next);
                // Dynamic, physically-inspired rotation
                float spinProgress = 1f - t; // 1 at start, 0 at end
                float ySpin = (float) Math.toRadians(720 * spinProgress * spinProgress); // 2 full spins, ease out
                float xWobble = (float) Math.toRadians(15 * Math.sin(Math.PI * t) * spinProgress); // max 15°, fades out
                AxisAngle4f rotY = new AxisAngle4f(ySpin, 0, 1, 0);
                AxisAngle4f rotX = new AxisAngle4f(xWobble, 1, 0, 0);
                // rotZ removed since it's not used
                // Compose: Y, then X, then Z (approximate by applying Y, then X, then Z)
                // Paper/MC multiplies these in order for the display entity
                float scale = (t < 0.85f)
                    ? 0.3f + 0.8f * t
                    : 1.1f - 0.1f * ((t - 0.85f) / 0.15f);
                scale = Math.max(0.3f, Math.min(scale, 1.1f));
                display.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    rotY,
                    new Vector3f(scale, scale, scale),
                    rotX // X wobble (Paper only supports two axes, so use X for secondary)
                ));
                // For Z wobble, you could alternate between X and Z every frame for extra effect, or combine with X if supported
            }, i * interval);
        }
        // Fade/pop and finish at the end
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!display.isValid()) return;
            display.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                new AxisAngle4f((float)Math.toRadians(360), 0, 1, 0),
                new Vector3f(0.1f, 0.1f, 0.1f),
                new AxisAngle4f(0, 1, 0, 0)
            ));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                display.remove();
                onComplete.run();
            }, 4L);
        }, (steps + 1) * interval);
    }
} 