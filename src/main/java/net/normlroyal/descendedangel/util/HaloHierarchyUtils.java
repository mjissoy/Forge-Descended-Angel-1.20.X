package net.normlroyal.descendedangel.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.normlroyal.descendedangel.halohierarchy.HaloHierarchyGlowState;

public class HaloHierarchyUtils {

    public static float getHierarchyGlowIntensity(Player viewer, Player target) {
        int gap = HaloUtils.getTierGap(viewer, target);
        if (gap <= 0) return 0.0F;

        return switch (gap) {
            case 1 -> 0.50F;
            case 2 -> 0.75F;
            case 3 -> 1.00F;
            case 4 -> 1.30F;
            case 5 -> 1.65F;
            case 6 -> 2.05F;
            case 7 -> 2.50F;
            default -> 3.00F;
        };
    }

    public static float getHierarchyGlowScale(Player viewer, Player target) {
        int gap = HaloUtils.getTierGap(viewer, target);
        return 1.0F + Mth.clamp(gap * 0.025F, 0.0F, 0.10F);
    }

    public static boolean isViewerLookingAtTarget(Player viewer, Player target, double threshold) {
        Vec3 viewerEye = viewer.getEyePosition();
        Vec3 look = viewer.getViewVector(1.0F).normalize();

        Vec3 targetCenter = target.getBoundingBox().getCenter();
        Vec3 toTarget = targetCenter.subtract(viewerEye).normalize();

        double dot = look.dot(toTarget);
        return dot >= threshold;
    }

    public static boolean isWithinHierarchyGlowRange(Player viewer, Player target, double maxDistance) {
        return viewer.distanceToSqr(target) <= maxDistance * maxDistance;
    }

    public static boolean shouldRenderHierarchyGlow(Player viewer, Player target) {
        if (viewer == null || target == null) return false;
        if (viewer == target) return false;
        if (!HaloHierarchyGlowState.isEnabled()) return false;

        int viewerTier = HaloUtils.getEquippedHaloTier(viewer);
        int targetTier = HaloUtils.getEquippedHaloTier(target);

        if (viewerTier <= 0 || targetTier <= 0) return false;
        if (targetTier <= viewerTier) return false;

        if (!isWithinHierarchyGlowRange(viewer, target, 48.0D)) return false;
        if (!isViewerLookingAtTarget(viewer, target, 0.75D)) return false;

        return true;
    }

}
