package net.normlroyal.descendedangel.haloabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.normlroyal.descendedangel.block.ModBlocks;
import net.normlroyal.descendedangel.block.tempwall.TempEarthWallBlock;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.util.HaloUtils;
import net.normlroyal.descendedangel.util.NetworkUtils;

public class PowerAbilities {

    public static final String TAG_FIRE  = "da_unlocked_fire";
    public static final String TAG_AIR   = "da_unlocked_air";
    public static final String TAG_EARTH = "da_unlocked_earth";
    public static final String TAG_WATER = "da_unlocked_water";

    private static final String CD_FIREBALL   = "da_cd_power_fireball_until";
    private static final String CD_GUST       = "da_cd_power_gust_until";
    private static final String CD_EARTHWALL  = "da_cd_power_earthwall_until";
    private static final String CD_MIST_VEIL  = "da_cd_power_mist_veil_until";

    public static void tryUse(ServerPlayer sp, HaloAbility ability) {
        int tier = HaloUtils.getEquippedHaloTier(sp);
        if (tier != 4 && tier != 7 && tier != 8 && tier != 9) return;

        long now = sp.level().getGameTime();

        switch (ability) {
            case FIREBALL -> {
                if (!sp.getPersistentData().getBoolean(TAG_FIRE)) {
                    NetworkUtils.actionbar(sp, "You have not unlocked Fireball");
                    return;
                }

                if (now < sp.getPersistentData().getLong(CD_FIREBALL)) {
                    NetworkUtils.actionbar(sp, "Ability is on cooldown.");
                    return;
                }

                doFireball(sp, tier);

                int baseCd = ModConfigs.COMMON.FIREBALL_COOLDOWN_TICKS.get();
                int cd = (int)Math.max(1, Math.round(baseCd * HaloScaling.cooldownMul(tier)));
                sp.getPersistentData().putLong(CD_FIREBALL, now + cd);
            }

            case GUST -> {
                if (!sp.getPersistentData().getBoolean(TAG_AIR)) {
                    NetworkUtils.actionbar(sp, "You have not unlocked Gust");
                    return;
                }

                if (now < sp.getPersistentData().getLong(CD_GUST)) {
                    NetworkUtils.actionbar(sp, "Ability is on cooldown.");
                    return;
                }

                int baseCd = ModConfigs.COMMON.GUST_COOLDOWN_TICKS.get();
                int cd = (int)Math.max(1, Math.round(baseCd * HaloScaling.cooldownMul(tier)));

                double baseradius = ModConfigs.COMMON.GUST_RADIUS.get();
                double basestrength = ModConfigs.COMMON.GUST_STRENGTH.get();

                double radius = HaloScaling.scaleUp(baseradius, tier);
                double strength = HaloScaling.scaleUp(basestrength, tier);

                doGust(sp, radius, strength);
                sp.getPersistentData().putLong(CD_GUST, now + cd);
            }

            case EARTH_WALL -> {
                if (!sp.getPersistentData().getBoolean(TAG_EARTH)) {
                    NetworkUtils.actionbar(sp, "You have not unlocked Earth Wall");
                    return;
                }

                if (now < sp.getPersistentData().getLong(CD_EARTHWALL)) {
                    NetworkUtils.actionbar(sp, "Ability is on cooldown.");
                    return;
                }

                int basewidth  = ModConfigs.COMMON.EARTHWALL_WIDTH.get();
                int width = HaloScaling.addIntCapped(basewidth, tier, 1, 7);
                int baseheight = ModConfigs.COMMON.EARTHWALL_HEIGHT.get();
                int height = HaloScaling.addIntCapped(baseheight, tier, 1, 4);
                int basedur  = ModConfigs.COMMON.EARTHWALL_DURATION_TICKS.get();
                int dur = HaloScaling.scaleIntDuration(basedur, tier);

                doEarthWall(sp, width, height, dur);

                int baseCd = ModConfigs.COMMON.EARTH_WALL_COOLDOWN_TICKS.get();
                int cd = (int)Math.max(1, Math.round(baseCd * HaloScaling.cooldownMul(tier)));
                sp.getPersistentData().putLong(CD_EARTHWALL, now + cd);
            }

            case MIST_VEIL -> {
                if (!sp.getPersistentData().getBoolean(TAG_WATER)) {
                    NetworkUtils.actionbar(sp, "You have not unlocked Mist Veil");
                    return;
                }

                if (now < sp.getPersistentData().getLong(CD_MIST_VEIL)) {
                    NetworkUtils.actionbar(sp, "Ability is on cooldown.");
                    return;
                }

                int basedur = ModConfigs.COMMON.MIST_VEIL_DURATION_TICKS.get();
                int dur = HaloScaling.scaleIntDuration(basedur, tier);

                doMistVeil(sp, dur);

                int baseCd = ModConfigs.COMMON.MIST_VEIL_COOLDOWN_TICKS.get();
                int cd = (int)Math.max(1, Math.round(baseCd * HaloScaling.cooldownMul(tier)));
                sp.getPersistentData().putLong(CD_MIST_VEIL, now + cd);
            }

            default -> {}
        }
    }


    private static void doFireball(ServerPlayer sp, int tier) {
        ServerLevel level = sp.serverLevel();

        Vec3 look = sp.getLookAngle();
        double speedMul = 1.0 + (0.10 * HaloScaling.mastery(tier));
        Vec3 dir = look.normalize().scale(speedMul);
        Vec3 spawn = sp.getEyePosition().add(look.scale(0.6));

        SmallFireball fireball = new SmallFireball(level, sp, dir.x, dir.y, dir.z);
        fireball.setPos(spawn.x, spawn.y, spawn.z);

        level.addFreshEntity(fireball);
    }

    private static void doGust(ServerPlayer sp, double radius, double strength) {
        ServerLevel level = sp.serverLevel();

        AABB box = sp.getBoundingBox().inflate(radius);

        for (Entity e : level.getEntities(sp, box, e -> e != sp && e.isAlive())) {
            Vec3 dir = e.position().subtract(sp.position());
            if (dir.lengthSqr() < 0.0001) continue;

            Vec3 push = dir.normalize().scale(strength);

            Vec3 newVel = e.getDeltaMovement().add(push.x, strength * 0.25, push.z);

            e.setDeltaMovement(newVel);

            e.hurtMarked = true;
        }
    }

    private static void doEarthWall(ServerPlayer sp, int width, int height, int dur) {
        ServerLevel level = sp.serverLevel();

        Vec3 look = sp.getLookAngle();
        Vec3 forward = new Vec3(look.x, 0, look.z);
        if (forward.lengthSqr() < 0.0001) forward = new Vec3(0, 0, 1);
        forward = forward.normalize();

        Vec3 right = new Vec3(-forward.z, 0, forward.x);

        BlockPos base = sp.blockPosition().offset(
                (int)Math.round(forward.x * 2),
                0,
                (int)Math.round(forward.z * 2)
        );

        BlockPos start = base;

        int half = width / 2;

        for (int w = -half; w <= half; w++) {
            BlockPos colBase = start.offset(
                    (int)Math.round(right.x * w),
                    0,
                    (int)Math.round(right.z * w)
            );

            for (int h = 0; h < height; h++) {
                BlockPos p = colBase.above(h);

                if (!level.getBlockState(p).canBeReplaced()) continue;

                level.setBlockAndUpdate(p, ModBlocks.TEMP_EARTH_WALL.get().defaultBlockState());
                TempEarthWallBlock.arm(level, p, dur);
            }
        }
    }

    private static void doMistVeil(ServerPlayer sp, int dur) {

        sp.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, dur, 0, true, false, false));
        sp.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, dur, 0, true, false, false));
        sp.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, dur, 0, true, false, false));

        sp.clearFire();
    }
}