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
import net.normlroyal.descendedangel.util.NetworkUtils;

public class DominionAbilities {

    public static final String TAG_SPACE = "da_unlocked_space";
    public static final String TAG_TIME  = "da_unlocked_time";

    private static final String CD_TELEPORT = "da_cd_dom_teleport_until";
    private static final String CD_FIELD    = "da_cd_dom_field_until";

    public static void tryUse(ServerPlayer sp, HaloAbility ability) {
        int tier = HaloUtils.getEquippedHaloTier(sp);
        if (tier != 6 && tier != 7 && tier != 8 && tier != 9) return;

        long now = sp.level().getGameTime();

        switch (ability) {
            case TELEPORT -> {
                if (!sp.getPersistentData().getBoolean(TAG_SPACE)){
                    NetworkUtils.actionbar(sp, "You have not unlocked Teleport");
                    return;
                }

                long until = sp.getPersistentData().getLong(CD_TELEPORT);
                if (now < until){
                    NetworkUtils.actionbar(sp, "Ability is on cooldown.");
                    return;
                }

                int baserange = ModConfigs.COMMON.TELEPORT_RANGE.get();
                int range = HaloScaling.addInt(baserange, tier, 2);

                boolean ok = tryTeleport(sp, range);
                if (!ok) return;

                int basecd = ModConfigs.COMMON.TELEPORT_COOLDOWN_TICKS.get();
                int cd = HaloScaling.scaleIntDuration(basecd, tier);
                sp.getPersistentData().putLong(CD_TELEPORT, now + cd);
            }

            case SPACE_CHEST -> {
                if (!sp.getPersistentData().getBoolean(TAG_SPACE)){
                    NetworkUtils.actionbar(sp, "You have not unlocked Portable Chest");
                    return;
                }
                long until = sp.getPersistentData().getLong("da_cd_dom_ender_until");
                if (now < until){
                    NetworkUtils.actionbar(sp, "Ability is on cooldown.");
                    return;
                }

                openEnderChest(sp);

                int cd = ModConfigs.COMMON.SPACE_CHEST_COOLDOWN_TICKS.get();
                sp.getPersistentData().putLong("da_cd_dom_ender_until", now + cd);
            }

            case FIELD -> {
                if (!sp.getPersistentData().getBoolean(TAG_TIME)){
                    NetworkUtils.actionbar(sp, "You have not unlocked Time Field");
                    return;
                }

                long until = sp.getPersistentData().getLong(CD_FIELD);
                if (now < until) {
                    NetworkUtils.actionbar(sp, "Ability is on cooldown.");
                    return;
                }

                double baseradius = ModConfigs.COMMON.FIELD_RADIUS.get();
                double radius = HaloScaling.scaleUp(baseradius, tier);
                int basedur = ModConfigs.COMMON.FIELD_DURATION_TICKS.get();
                int dur = HaloScaling.scaleIntDuration(basedur, tier);
                int baseamp = ModConfigs.COMMON.FIELD_SLOWNESS_AMPLIFIER.get();
                int amp = HaloScaling.addIntCapped(baseamp, tier, 1, 30);


                doDominionField(sp, radius, dur, amp);

                int basecd = ModConfigs.COMMON.FIELD_COOLDOWN_TICKS.get();
                int cd = HaloScaling.scaleIntDuration(basecd, tier);
                sp.getPersistentData().putLong(CD_FIELD, now + cd);
            }

            case ACCELERATE -> {
                if (!sp.getPersistentData().getBoolean(TAG_TIME)){
                    NetworkUtils.actionbar(sp, "You have not unlocked Acceleration");
                    return;
                }
                long until = sp.getPersistentData().getLong("da_cd_dom_timeacc_until");
                if (now < until) {
                    NetworkUtils.actionbar(sp, "Ability is on cooldown.");
                    return;
                }
                int basedur = ModConfigs.COMMON.ACCEL_DURATION_TICKS.get();
                int dur = HaloScaling.scaleIntDuration(basedur, tier);
                int baseamp = ModConfigs.COMMON.ACCEL_SPEED_AMPLIFIER.get();
                int amp = HaloScaling.addIntCapped(baseamp, tier, 1, 30);


                sp.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        dur,
                        amp,
                        true,
                        false,
                        false
                ));

                int basecd = ModConfigs.COMMON.ACCEL_COOLDOWN_TICKS.get();
                int cd = HaloScaling.scaleIntDuration(basecd, tier);
                sp.getPersistentData().putLong("da_cd_dom_timeacc_until", now + cd);
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

    private static void openEnderChest(ServerPlayer sp) {
        var inv = sp.getEnderChestInventory();

        sp.openMenu(new net.minecraft.world.SimpleMenuProvider(
                (id, playerInv, player) -> net.minecraft.world.inventory.ChestMenu.threeRows(id, playerInv, inv),
                net.minecraft.network.chat.Component.translatable("container.enderchest")
        ));
    }
}
