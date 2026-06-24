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
import net.normlroyal.descendedangel.common.config.ModConfigs;
import net.normlroyal.descendedangel.events.DominionEventHandlers;
import net.normlroyal.descendedangel.util.HaloUtils;
import net.normlroyal.descendedangel.util.NetworkUtils;

public class DominionAbilities {

    public static final String TAG_SPACE = "da_unlocked_space";
    public static final String TAG_TIME  = "da_unlocked_time";
    public static final String TAG_CELESTIAL = "da_unlocked_celestial";
    public static final String TAG_RESONANCE = "da_unlocked_resonance";

    private static final String CD_TELEPORT = "da_cd_dom_teleport_until";
    private static final String CD_SPACE_CHEST = "da_cd_dom_ender_until";
    private static final String CD_FIELD = "da_cd_dom_field_until";
    private static final String CD_ACCELERATE = "da_cd_dom_timeacc_until";
    private static final String CD_ASTRAL_LANCE = "da_cd_dom_astral_lance_until";
    private static final String CD_HEAVENS_MAP = "da_cd_dom_heavens_map_until";
    private static final String CD_RESONANCE_PULSE = "da_cd_dom_resonance_pulse_until";
    private static final String CD_SACRED_SILENCE = "da_cd_dom_sacred_silence_until";

    public static boolean tryUse(ServerPlayer sp, HaloAbility ability) {
        int tier = HaloUtils.getEquippedHaloTier(sp);
        if (tier != 6 && tier != 7 && tier != 8 && tier != 9) return false;

        long now = sp.level().getGameTime();

        switch (ability) {
            case TELEPORT -> {
                if (!hasDominion(sp, TAG_SPACE, "You have not unlocked Teleport")) return false;
                if (now < sp.getPersistentData().getLong(CD_TELEPORT)) return false;

                int baseRange = ModConfigs.COMMON.TELEPORT_RANGE.get();
                int range = HaloScaling.addInt(baseRange, tier, 2);

                boolean ok = tryTeleport(sp, range);
                if (!ok) return false;

                sp.getPersistentData().putLong(CD_TELEPORT, now + scaledCooldown(sp, ability));
                return true;
            }

            case SPACE_CHEST -> {
                if (!hasDominion(sp, TAG_SPACE, "You have not unlocked Portable Chest")) return false;
                if (now < sp.getPersistentData().getLong(CD_SPACE_CHEST)) return false;

                openEnderChest(sp);

                sp.getPersistentData().putLong(CD_SPACE_CHEST, now + scaledCooldown(sp, ability));
                return true;
            }

            case FIELD -> {
                if (!hasDominion(sp, TAG_TIME, "You have not unlocked Time Field")) return false;
                if (now < sp.getPersistentData().getLong(CD_FIELD)) return false;

                double baseRadius = ModConfigs.COMMON.FIELD_RADIUS.get();
                double radius = HaloScaling.scaleUp(baseRadius, tier);

                int baseDur = ModConfigs.COMMON.FIELD_DURATION_TICKS.get();
                int dur = HaloScaling.scaleIntDuration(baseDur, tier);

                int baseAmp = ModConfigs.COMMON.FIELD_SLOWNESS_AMPLIFIER.get();
                int amp = HaloScaling.addIntCapped(baseAmp, tier, 1, 30);

                doDominionField(sp, radius, dur, amp);

                sp.getPersistentData().putLong(CD_FIELD, now + scaledCooldown(sp, ability));
                return true;
            }

            case ACCELERATE -> {
                if (!hasDominion(sp, TAG_TIME, "You have not unlocked Acceleration")) return false;
                if (now < sp.getPersistentData().getLong(CD_ACCELERATE)) return false;

                int baseDur = ModConfigs.COMMON.ACCEL_DURATION_TICKS.get();
                int dur = HaloScaling.scaleIntDuration(baseDur, tier);

                int baseAmp = ModConfigs.COMMON.ACCEL_SPEED_AMPLIFIER.get();
                int amp = HaloScaling.addIntCapped(baseAmp, tier, 1, 30);

                sp.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        dur,
                        amp,
                        true,
                        false,
                        false
                ));

                sp.getPersistentData().putLong(CD_ACCELERATE, now + scaledCooldown(sp, ability));
                return true;
            }

            case ASTRAL_LANCE -> {
                if (!hasDominion(sp, TAG_CELESTIAL, "You have not unlocked Astral Lance")) return false;
                if (now < sp.getPersistentData().getLong(CD_ASTRAL_LANCE)) return false;

                doAstralLance(sp, tier);

                sp.getPersistentData().putLong(CD_ASTRAL_LANCE, now + scaledCooldown(sp, ability));
                return true;
            }

            case HEAVENS_MAP -> {
                if (!hasDominion(sp, TAG_CELESTIAL, "You have not unlocked Heaven's Map")) return false;
                if (now < sp.getPersistentData().getLong(CD_HEAVENS_MAP)) return false;

                doHeavensMap(sp, tier);

                sp.getPersistentData().putLong(CD_HEAVENS_MAP, now + scaledCooldown(sp, ability));
                return true;
            }

            case RESONANCE_PULSE -> {
                if (!hasDominion(sp, TAG_RESONANCE, "You have not unlocked Resonance Pulse")) return false;
                if (now < sp.getPersistentData().getLong(CD_RESONANCE_PULSE)) return false;

                doResonancePulse(sp, tier);

                sp.getPersistentData().putLong(CD_RESONANCE_PULSE, now + scaledCooldown(sp, ability));
                return true;
            }

            case SACRED_SILENCE -> {
                if (!hasDominion(sp, TAG_RESONANCE, "You have not unlocked Sacred Silence")) return false;
                if (now < sp.getPersistentData().getLong(CD_SACRED_SILENCE)) return false;

                doSacredSilence(sp, tier);

                sp.getPersistentData().putLong(CD_SACRED_SILENCE, now + scaledCooldown(sp, ability));
                return true;
            }

            default -> {}
        }

        return false;
    }

    public static int countUnlockedDominions(ServerPlayer sp) {
        var data = sp.getPersistentData();

        int count = 0;

        if (data.getBoolean(TAG_SPACE)) count++;
        if (data.getBoolean(TAG_TIME)) count++;
        if (data.getBoolean(TAG_CELESTIAL)) count++;
        if (data.getBoolean(TAG_RESONANCE)) count++;

        return count;
    }

    public static String cooldownTag(HaloAbility ability) {
        return switch (ability) {
            case TELEPORT -> CD_TELEPORT;
            case SPACE_CHEST -> CD_SPACE_CHEST;
            case FIELD -> CD_FIELD;
            case ACCELERATE -> CD_ACCELERATE;
            case ASTRAL_LANCE -> CD_ASTRAL_LANCE;
            case HEAVENS_MAP -> CD_HEAVENS_MAP;
            case RESONANCE_PULSE -> CD_RESONANCE_PULSE;
            case SACRED_SILENCE -> CD_SACRED_SILENCE;
            default -> "";
        };
    }

    public static int scaledCooldown(ServerPlayer sp, HaloAbility ability) {
        int tier = HaloUtils.getEquippedHaloTier(sp);

        int base = switch (ability) {
            case TELEPORT -> ModConfigs.COMMON.TELEPORT_COOLDOWN_TICKS.get();
            case SPACE_CHEST -> ModConfigs.COMMON.SPACE_CHEST_COOLDOWN_TICKS.get();
            case FIELD -> ModConfigs.COMMON.FIELD_COOLDOWN_TICKS.get();
            case ACCELERATE -> ModConfigs.COMMON.ACCEL_COOLDOWN_TICKS.get();

            case ASTRAL_LANCE -> 260;
            case HEAVENS_MAP -> 420;
            case RESONANCE_PULSE -> 220;
            case SACRED_SILENCE -> 360;

            default -> 20;
        };

        return HaloScaling.scaleIntDuration(base, tier);
    }

    private static boolean hasDominion(ServerPlayer sp, String tag, String message) {
        if (!sp.getPersistentData().getBoolean(tag)) {
            NetworkUtils.actionbar(sp, message);
            return false;
        }

        return true;
    }

    private static void doAstralLance(ServerPlayer sp, int tier) {
        ServerLevel level = sp.serverLevel();

        Vec3 center = findAstralLanceTarget(sp, 32.0D);

        int lances = Mth.clamp(tier - 5, 1, 4);
        float damage = 11.0F + (2.0F * Math.max(0, tier - 6));

        DominionEventHandlers.scheduleAstralLances(
                level,
                sp,
                center,
                lances,
                22,
                damage
        );

        NetworkUtils.actionbar(sp, lances == 1 ? "An Astral Lance answers." : "The heavens loose " + lances + " Astral Lances.");
    }

    private static Vec3 findAstralLanceTarget(ServerPlayer sp, double range) {
        ServerLevel level = sp.serverLevel();

        Vec3 eye = sp.getEyePosition();
        Vec3 look = sp.getLookAngle().normalize();
        Vec3 end = eye.add(look.scale(range));

        BlockHitResult blockHit = level.clip(new ClipContext(
                eye,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                sp
        ));

        double maxDistance = range;

        if (blockHit.getType() != HitResult.Type.MISS) {
            maxDistance = eye.distanceTo(blockHit.getLocation());
        }

        AABB searchBox = sp.getBoundingBox()
                .expandTowards(look.scale(maxDistance))
                .inflate(1.5D);

        LivingEntity best = null;
        double bestProjected = maxDistance + 1.0D;

        for (LivingEntity target : level.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                e -> e.isAlive() && !e.getUUID().equals(sp.getUUID())
        )) {
            Vec3 targetCenter = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            Vec3 fromEye = targetCenter.subtract(eye);

            double projected = fromEye.dot(look);

            if (projected < 0.0D || projected > maxDistance) {
                continue;
            }

            Vec3 closest = eye.add(look.scale(projected));
            double distanceToLine = targetCenter.distanceTo(closest);

            if (distanceToLine > 1.15D + target.getBbWidth() * 0.5D) {
                continue;
            }

            if (projected < bestProjected) {
                bestProjected = projected;
                best = target;
            }
        }

        if (best != null) {
            return best.position().add(0.0D, 0.1D, 0.0D);
        }

        if (blockHit.getType() != HitResult.Type.MISS) {
            Vec3 hit = blockHit.getLocation();
            return new Vec3(hit.x, blockHit.getBlockPos().getY() + 1.0D, hit.z);
        }

        return end;
    }

    private static void doHeavensMap(ServerPlayer sp, int tier) {
        ServerLevel level = sp.serverLevel();

        int duration = 220 + Math.max(0, tier - 6) * 80;
        double radius = 24.0D + Math.max(0, tier - 6) * 8.0D;
        double sacredBlockRadius = 14.0D + Math.max(0, tier - 6) * 3.0D;

        DominionEventHandlers.createHeavensMap(
                level,
                sp,
                duration,
                radius,
                sacredBlockRadius
        );

        NetworkUtils.actionbar(sp, "Heaven's Map opens above you.");
    }

    private static void doResonancePulse(ServerPlayer sp, int tier) {
        ServerLevel level = sp.serverLevel();

        double radius = 7.0D + Math.max(0, tier - 6) * 1.5D;
        float damage = 3.0F + Math.max(0, tier - 6);
        int interruptTicks = 45 + Math.max(0, tier - 6) * 10;

        DominionEventHandlers.castResonancePulse(
                level,
                sp,
                radius,
                damage,
                interruptTicks
        );

        NetworkUtils.actionbar(sp, "A sacred resonance ripples outward.");
    }

    private static void doSacredSilence(ServerPlayer sp, int tier) {
        ServerLevel level = sp.serverLevel();

        int duration = 120 + Math.max(0, tier - 6) * 35;
        double radius = 8.0D + Math.max(0, tier - 6) * 1.5D;

        DominionEventHandlers.createSacredSilence(
                level,
                sp,
                duration,
                radius
        );

        NetworkUtils.actionbar(sp, "Sacred Silence hushes hostile will.");
    }

    private static boolean tryTeleport(ServerPlayer sp, int range) {
        ServerLevel level = sp.serverLevel();

        Vec3 eye = sp.getEyePosition();
        Vec3 look = sp.getLookAngle();
        Vec3 end = eye.add(look.scale(range));

        ClipContext ctx = new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, sp);
        BlockHitResult hit = level.clip(ctx);

        Vec3 target = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
        BlockPos base = BlockPos.containing(target);
        BlockPos start = base.above();

        for (int i = 0; i < 6; i++) {
            BlockPos p = start.above(i);
            double x = p.getX() + 0.5D;
            double y = p.getY();
            double z = p.getZ() + 0.5D;

            if (level.noCollision(sp, sp.getBoundingBox().move(x - sp.getX(), y - sp.getY(), z - sp.getZ()))) {
                sp.teleportTo(level, x, y, z, sp.getYRot(), sp.getXRot());
                return true;
            }
        }

        return false;
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

    private static void openEnderChest(ServerPlayer sp) {
        var inv = sp.getEnderChestInventory();

        sp.openMenu(new net.minecraft.world.SimpleMenuProvider(
                (id, playerInv, player) -> net.minecraft.world.inventory.ChestMenu.threeRows(id, playerInv, inv),
                net.minecraft.network.chat.Component.translatable("container.enderchest")
        ));
    }
}