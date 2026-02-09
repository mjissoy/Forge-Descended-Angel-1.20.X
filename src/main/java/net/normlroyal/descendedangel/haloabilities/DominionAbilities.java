package net.normlroyal.descendedangel.haloabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.util.HaloUtils;

public class DominionAbilities {

    public static final String TAG_SPACE = "da_unlocked_space";
    public static final String TAG_TIME  = "da_unlocked_time";

    private static final String CD_TELEPORT = "da_cd_dom_teleport_until";
    private static final String CD_FIELD    = "da_cd_dom_field_until";

    public static void tryUse(ServerPlayer sp, HaloAbility ability) {
        int tier = HaloUtils.getEquippedHaloTier(sp);
        if (tier != 6) return;

        long now = sp.level().getGameTime();

        switch (ability) {
            case TELEPORT -> {
                if (!sp.getPersistentData().getBoolean(TAG_SPACE)) return;

                long until = sp.getPersistentData().getLong(CD_TELEPORT);
                if (now < until) return;

                int range = ModConfigs.COMMON.TELEPORT_RANGE.get();
                boolean ok = tryTeleport(sp, range);
                if (!ok) return;

                int cd = ModConfigs.COMMON.TELEPORT_COOLDOWN_TICKS.get();
                sp.getPersistentData().putLong(CD_TELEPORT, now + cd);
            }

            case FIELD -> {
                if (!sp.getPersistentData().getBoolean(TAG_TIME)) return;

                long until = sp.getPersistentData().getLong(CD_FIELD);
                if (now < until) return;

                double radius = ModConfigs.COMMON.FIELD_RADIUS.get();
                int dur = ModConfigs.COMMON.FIELD_DURATION_TICKS.get();
                int amp = ModConfigs.COMMON.FIELD_SLOWNESS_AMPLIFIER.get();

                doDominionField(sp, radius, dur, amp);

                int cd = ModConfigs.COMMON.FIELD_COOLDOWN_TICKS.get();
                sp.getPersistentData().putLong(CD_FIELD, now + cd);
            }
        }
    }

    private static boolean tryTeleport(ServerPlayer sp, int range) {
        ServerLevel level = sp.serverLevel();

        Vec3 eye = sp.getEyePosition();
        Vec3 look = sp.getLookAngle();
        Vec3 end = eye.add(look.scale(range));

        ClipContext ctx = new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, sp);
        BlockHitResult hit = level.clip(ctx);

        Vec3 target = (hit.getType() == HitResult.Type.MISS) ? end : hit.getLocation();
        BlockPos base = BlockPos.containing(target);
        BlockPos start = base.above();

        for (int i = 0; i < 6; i++) {
            BlockPos p = start.above(i);
            double x = p.getX() + 0.5;
            double y = p.getY();
            double z = p.getZ() + 0.5;

            if (level.noCollision(sp, sp.getBoundingBox().move(x - sp.getX(), y - sp.getY(), z - sp.getZ()))) {
                float yaw = sp.getYRot();
                float pitch = sp.getXRot();
                sp.teleportTo(level, x, y, z, yaw, pitch);
                return true;
            }
        }
        return false;
    }

    private static void doDominionField(ServerPlayer sp, double radius, int durationTicks, int amplifier) {
        ServerLevel level = sp.serverLevel();
        AABB box = sp.getBoundingBox().inflate(radius);

        int amp = Mth.clamp(amplifier, 0, 255);

        MobEffectInstance slow = new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                durationTicks,
                amp,
                true,
                false,
                false
        );

        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, e -> e != sp)) {
            e.addEffect(new MobEffectInstance(slow));
        }
    }
}
