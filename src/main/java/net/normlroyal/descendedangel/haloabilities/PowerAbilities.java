package net.normlroyal.descendedangel.haloabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.normlroyal.descendedangel.block.ModBlocks;
import net.normlroyal.descendedangel.block.tempwall.TempEarthWallBlock;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.events.FireEvolutionEvents;
import net.normlroyal.descendedangel.haloabilities.helpers.ClientUnlockState;
import net.normlroyal.descendedangel.util.HaloUtils;
import net.normlroyal.descendedangel.util.NetworkUtils;

public class PowerAbilities {

    public static final String TAG_FIRE  = "da_unlocked_fire";
    public static final String TAG_AIR   = "da_unlocked_air";
    public static final String TAG_EARTH = "da_unlocked_earth";
    public static final String TAG_WATER = "da_unlocked_water";

    public static final String TAG_FIRE_SACRED_FLARE = "da_evolved_fire_sacred_flare";
    public static final String TAG_FIRE_SOL_CORONA = "da_evolved_fire_sol_corona";
    public static final String TAG_FIRE_PILLARS_OF_RADIANCE = "da_evolved_fire_pillars_of_radiance";

    private static final String CD_FIREBALL   = "da_cd_power_fireball_until";
    private static final String CD_GUST       = "da_cd_power_gust_until";
    private static final String CD_EARTH_WALL = "da_cd_power_earthwall_until";
    private static final String CD_MIST_VEIL  = "da_cd_power_mist_veil_until";

    private static final String CD_SACRED_FLARE = "da_cd_power_sacred_flare_until";
    private static final String CD_SOL_CORONA = "da_cd_power_sol_corona_until";
    private static final String CD_PILLARS_OF_RADIANCE = "da_cd_power_pillars_of_radiance_until";

    public static boolean tryUse(ServerPlayer sp, HaloAbility ability) {
        int tier = HaloUtils.getEquippedHaloTier(sp);
        if (tier != 4 && tier != 7 && tier != 8 && tier != 9) return false;

        long now = sp.level().getGameTime();
        CompoundTag data = sp.getPersistentData();

        switch (ability) {
            case FIREBALL -> {
                if (hasFireEvolution(sp)) {
                    NetworkUtils.actionbar(sp, "Your Fireball has already evolved.");
                    return false;
                }

                if (!data.getBoolean(TAG_FIRE)) {
                    NetworkUtils.actionbar(sp, "You have not unlocked Fireball");
                    return false;
                }

                if (now < data.getLong(CD_FIREBALL)) {
                    return false;
                }

                doFireball(sp, tier);
                data.putLong(CD_FIREBALL, now + scaledCooldown(sp, ability));

                return true;
            }

            case SACRED_FLARE, SOL_CORONA, PILLARS_OF_RADIANCE -> {
                return tryUseFireEvolution(sp, ability, tier, now);
            }

            case GUST -> {
                if (!data.getBoolean(TAG_AIR)) {
                    NetworkUtils.actionbar(sp, "You have not unlocked Gust");
                    return false;
                }

                if (now < data.getLong(CD_GUST)) {
                    return false;
                }

                double baseradius = ModConfigs.COMMON.GUST_RADIUS.get();
                double basestrength = ModConfigs.COMMON.GUST_STRENGTH.get();

                double radius = HaloScaling.scaleUp(baseradius, tier);
                double strength = HaloScaling.scaleUp(basestrength, tier);

                doGust(sp, radius, strength);
                data.putLong(CD_GUST, now + scaledCooldown(sp, ability));

                return true;
            }

            case EARTH_WALL -> {
                if (!data.getBoolean(TAG_EARTH)) {
                    NetworkUtils.actionbar(sp, "You have not unlocked Earth Wall");
                    return false;
                }

                if (now < data.getLong(CD_EARTH_WALL)) {
                    return false;
                }

                int basewidth  = ModConfigs.COMMON.EARTH_WALL_WIDTH.get();
                int width = HaloScaling.addIntCapped(basewidth, tier, 1, 7);
                int baseheight = ModConfigs.COMMON.EARTH_WALL_HEIGHT.get();
                int height = HaloScaling.addIntCapped(baseheight, tier, 1, 4);
                int basedur  = ModConfigs.COMMON.EARTH_WALL_DURATION_TICKS.get();
                int dur = HaloScaling.scaleIntDuration(basedur, tier);

                doEarthWall(sp, width, height, dur);
                data.putLong(CD_EARTH_WALL, now + scaledCooldown(sp, ability));

                return true;
            }

            case MIST_VEIL -> {
                if (!data.getBoolean(TAG_WATER)) {
                    NetworkUtils.actionbar(sp, "You have not unlocked Mist Veil");
                    return false;
                }

                if (now < data.getLong(CD_MIST_VEIL)) {
                    return false;
                }

                int basedur = ModConfigs.COMMON.MIST_VEIL_DURATION_TICKS.get();
                int dur = HaloScaling.scaleIntDuration(basedur, tier);

                doMistVeil(sp, dur);
                data.putLong(CD_MIST_VEIL, now + scaledCooldown(sp, ability));

                return true;
            }

            default -> {}
        }

        return false;
    }

    private static boolean tryUseFireEvolution(ServerPlayer sp, HaloAbility ability, int tier, long now) {
        if (tier < 7) {
            NetworkUtils.actionbar(sp, "Fire evolutions require a Cherubim Halo or higher.");
            return false;
        }

        CompoundTag data = sp.getPersistentData();

        if (!data.getBoolean(ability.unlockTag())) {
            NetworkUtils.actionbar(sp, "You have not unlocked this Fire evolution.");
            return false;
        }

        String cooldownTag = cooldownTag(ability);

        if (now < data.getLong(cooldownTag)) {
            return false;
        }

        boolean used = switch (ability) {
            case SACRED_FLARE -> {
                doSacredFlare(sp, tier);
                yield true;
            }
            case SOL_CORONA -> {
                doSolCorona(sp, tier);
                yield true;
            }
            case PILLARS_OF_RADIANCE -> doPillarsOfRadiance(sp, tier);
            default -> false;
        };

        if (!used) {
            return false;
        }

        data.putLong(cooldownTag, now + scaledCooldown(sp, ability));
        return true;
    }

    public static boolean hasFireEvolution(Player player) {
        return hasTag(player, TAG_FIRE_SACRED_FLARE)
                || hasTag(player, TAG_FIRE_SOL_CORONA)
                || hasTag(player, TAG_FIRE_PILLARS_OF_RADIANCE);
    }

    public static HaloAbility currentFireEvolution(Player player) {
        if (hasTag(player, TAG_FIRE_SACRED_FLARE)) return HaloAbility.SACRED_FLARE;
        if (hasTag(player, TAG_FIRE_SOL_CORONA)) return HaloAbility.SOL_CORONA;
        if (hasTag(player, TAG_FIRE_PILLARS_OF_RADIANCE)) return HaloAbility.PILLARS_OF_RADIANCE;
        return null;
    }

    public static void setFireEvolution(ServerPlayer sp, HaloAbility ability) {
        if (!isFireEvolution(ability)) {
            return;
        }

        CompoundTag data = sp.getPersistentData();

        data.putBoolean(TAG_FIRE, false);
        data.putBoolean(TAG_FIRE_SACRED_FLARE, false);
        data.putBoolean(TAG_FIRE_SOL_CORONA, false);
        data.putBoolean(TAG_FIRE_PILLARS_OF_RADIANCE, false);

        data.putBoolean(ability.unlockTag(), true);
    }

    public static boolean isFireEvolution(HaloAbility ability) {
        return ability == HaloAbility.SACRED_FLARE
                || ability == HaloAbility.SOL_CORONA
                || ability == HaloAbility.PILLARS_OF_RADIANCE;
    }

    private static boolean hasTag(Player player, String tag) {
        if (player == null) return false;

        if (player.level().isClientSide) {
            return ClientUnlockState.has(tag);
        }

        return player.getPersistentData().getBoolean(tag);
    }

    public static String cooldownTag(HaloAbility ability) {
        return switch (ability) {
            case FIREBALL -> CD_FIREBALL;
            case GUST -> CD_GUST;
            case EARTH_WALL -> CD_EARTH_WALL;
            case MIST_VEIL -> CD_MIST_VEIL;
            case SACRED_FLARE -> CD_SACRED_FLARE;
            case SOL_CORONA -> CD_SOL_CORONA;
            case PILLARS_OF_RADIANCE -> CD_PILLARS_OF_RADIANCE;
            default -> "";
        };
    }

    public static int scaledCooldown(ServerPlayer sp, HaloAbility ability) {
        int tier = HaloUtils.getEquippedHaloTier(sp);

        int base = switch (ability) {
            case FIREBALL -> ModConfigs.COMMON.FIREBALL_COOLDOWN_TICKS.get();
            case GUST -> ModConfigs.COMMON.GUST_COOLDOWN_TICKS.get();
            case EARTH_WALL -> ModConfigs.COMMON.EARTH_WALL_COOLDOWN_TICKS.get();
            case MIST_VEIL -> ModConfigs.COMMON.MIST_VEIL_COOLDOWN_TICKS.get();

            case SACRED_FLARE -> ModConfigs.COMMON.FIREBALL_COOLDOWN_TICKS.get() + 100;
            case SOL_CORONA -> ModConfigs.COMMON.FIREBALL_COOLDOWN_TICKS.get() + 300;
            case PILLARS_OF_RADIANCE -> ModConfigs.COMMON.FIREBALL_COOLDOWN_TICKS.get() + 400;

            default -> 20;
        };

        return (int)Math.max(1, Math.round(base * HaloScaling.cooldownMul(tier)));
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

    private static void doSacredFlare(ServerPlayer sp, int tier) {
        ServerLevel level = sp.serverLevel();

        Vec3 look = sp.getLookAngle().normalize();
        Vec3 spawn = sp.getEyePosition().add(look.scale(0.8));
        Vec3 dir = look.scale(1.25D + (0.15D * HaloScaling.mastery(tier)));

        LargeFireball flare = new LargeFireball(
                level,
                sp,
                dir.x,
                dir.y,
                dir.z,
                2 + HaloScaling.mastery(tier)
        );

        flare.setPos(spawn.x, spawn.y, spawn.z);
        flare.setDeltaMovement(look.scale(0.55D));
        flare.getPersistentData().putBoolean(FireEvolutionEvents.TAG_SACRED_FLARE_PROJECTILE, true);

        level.addFreshEntity(flare);

        level.sendParticles(
                ParticleTypes.FLAME,
                spawn.x,
                spawn.y,
                spawn.z,
                20,
                0.25,
                0.25,
                0.25,
                0.03
        );
    }

    private static void doSolCorona(ServerPlayer sp, int tier) {
        int duration = HaloScaling.scaleIntDuration(600, tier);

        sp.getPersistentData().putLong(
                FireEvolutionEvents.TAG_SOL_CORONA_UNTIL,
                sp.level().getGameTime() + duration
        );

        sp.getPersistentData().putInt(FireEvolutionEvents.TAG_SOL_CORONA_CHARGES, 3);

        sp.serverLevel().sendParticles(
                ParticleTypes.END_ROD,
                sp.getX(),
                sp.getY() + sp.getBbHeight() + 0.35D,
                sp.getZ(),
                36,
                0.7,
                0.35,
                0.7,
                0.04
        );

        NetworkUtils.actionbar(sp, "Sol Corona orbits your Halo.");
    }

    private static boolean doPillarsOfRadiance(ServerPlayer sp, int tier) {
        ServerLevel level = sp.serverLevel();

        BlockHitResult hit = traceBlock(sp, 32.0D);

        if (hit.getType() == HitResult.Type.MISS) {
            NetworkUtils.actionbar(sp, "No sacred ground was found.");
            return false;
        }

        BlockPos target = hit.getBlockPos();

        if (!level.getBlockState(target).isAir()) {
            target = target.above();
        }

        int duration = HaloScaling.scaleIntDuration(100, tier);
        FireEvolutionEvents.schedulePillar(level, target, sp, 30, duration);

        return true;
    }

    private static BlockHitResult traceBlock(ServerPlayer sp, double range) {
        Vec3 eye = sp.getEyePosition();
        Vec3 look = sp.getLookAngle();
        Vec3 end = eye.add(look.scale(range));

        return sp.level().clip(new ClipContext(
                eye,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                sp
        ));
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

            spawnGustParticles(level, sp.position(), radius, 3, 24);
            level.sendParticles(ParticleTypes.POOF, sp.getX(), sp.getY() + 0.2, sp.getZ(), 12, 0.2, 0.05, 0.2, 0.0);
        }
    }

    public static void spawnGustParticles(ServerLevel level, Vec3 center, double radius, int rings, int pointsPerRing) {
        for (int r = 1; r <= rings; r++) {
            double t = (double) r / rings;
            double ringRadius = radius * t;

            for (int i = 0; i < pointsPerRing; i++) {
                double angle = (i / (double) pointsPerRing) * (Math.PI * 2.0);

                double xOff = Math.cos(angle) * ringRadius;
                double zOff = Math.sin(angle) * ringRadius;

                double x = center.x + xOff;
                double y = center.y + 0.2;
                double z = center.z + zOff;

                Vec3 out = new Vec3(xOff, 0, zOff);
                if (out.lengthSqr() < 1.0e-6) continue;
                out = out.normalize();

                double speed = Mth.lerp(t, 0.02, 0.12);

                level.sendParticles(
                        ParticleTypes.CLOUD,
                        x, y, z,
                        1,
                        0, 0, 0,
                        0
                );

                level.sendParticles(
                        ParticleTypes.SWEEP_ATTACK,
                        x, y + 0.15, z,
                        1,
                        0, 0, 0,
                        0
                );
            }
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