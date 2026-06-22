package net.normlroyal.descendedangel.haloabilities.helpers;

import net.minecraft.server.level.ServerPlayer;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.haloabilities.HaloScaling;
import net.normlroyal.descendedangel.haloabilities.PowerAbilities;
import net.normlroyal.descendedangel.util.HaloUtils;

public final class CooldownSnapshots {

    public record CooldownSnapshot(long until, int total) {}

    public static CooldownSnapshot getCooldown(ServerPlayer sp, HaloAbility ability) {
        int tier = HaloUtils.getEquippedHaloTier(sp);
        long until;
        int total;

        switch (ability) {
            case TELEPORT -> {
                until = sp.getPersistentData().getLong("da_cd_dom_teleport_until");
                int base = ModConfigs.COMMON.TELEPORT_COOLDOWN_TICKS.get();
                total = HaloScaling.scaleIntDuration(base, tier);
            }
            case FIELD -> {
                until = sp.getPersistentData().getLong("da_cd_dom_field_until");
                int base = ModConfigs.COMMON.FIELD_COOLDOWN_TICKS.get();
                total = HaloScaling.scaleIntDuration(base, tier);
            }
            case SPACE_CHEST -> {
                until = sp.getPersistentData().getLong("da_cd_dom_ender_until");
                total = ModConfigs.COMMON.SPACE_CHEST_COOLDOWN_TICKS.get();
            }
            case ACCELERATE -> {
                until = sp.getPersistentData().getLong("da_cd_dom_timeacc_until");
                int base = ModConfigs.COMMON.ACCEL_COOLDOWN_TICKS.get();
                total = HaloScaling.scaleIntDuration(base, tier);
            }

            case FIREBALL,
                 SACRED_FLARE,
                 SOL_CORONA,
                 PILLARS_OF_RADIANCE,
                 GUST,
                 VACUUM_VORTEX,
                 ZEPHYR_SCYTHES,
                 HEAVENLY_DOWNDRAFT,
                 EARTH_WALL,
                 HOLY_BASTION,
                 AEGIS_PILLAR,
                 CRYSTAL_CHRYSALIS,
                 MIST_VEIL -> {
                until = sp.getPersistentData().getLong(PowerAbilities.cooldownTag(ability));
                total = PowerAbilities.scaledCooldown(sp, ability);
            }

            default -> {
                until = 0;
                total = 0;
            }
        }

        return new CooldownSnapshot(until, total);
    }

    private CooldownSnapshots() {}
}