package net.normlroyal.descendedangel.haloabilities.helpers;

import net.minecraft.server.level.ServerPlayer;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.haloabilities.HaloScaling;
import net.normlroyal.descendedangel.util.HaloUtils;

public final class CooldownSnapshots {

    public record CooldownSnapshot(long until, int total) {}

    public static CooldownSnapshot getCooldown(ServerPlayer sp, HaloAbility ability) {
        int tier = HaloUtils.getEquippedHaloTier(sp);
        long until;
        int total;

        switch (ability) {
            // Dominion
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

            // Power
            case FIREBALL -> {
                until = sp.getPersistentData().getLong("da_cd_power_fireball_until");
                int base = ModConfigs.COMMON.FIREBALL_COOLDOWN_TICKS.get();
                total = (int)Math.max(1, Math.round(base * HaloScaling.cooldownMul(tier)));
            }
            case GUST -> {
                until = sp.getPersistentData().getLong("da_cd_power_gust_until");
                int base = ModConfigs.COMMON.GUST_COOLDOWN_TICKS.get();
                total = (int)Math.max(1, Math.round(base * HaloScaling.cooldownMul(tier)));
            }
            case EARTH_WALL -> {
                until = sp.getPersistentData().getLong("da_cd_power_earthwall_until");
                int base = ModConfigs.COMMON.EARTH_WALL_COOLDOWN_TICKS.get();
                total = (int)Math.max(1, Math.round(base * HaloScaling.cooldownMul(tier)));
            }
            case MIST_VEIL -> {
                until = sp.getPersistentData().getLong("da_cd_power_mist_veil_until");
                int base = ModConfigs.COMMON.MIST_VEIL_COOLDOWN_TICKS.get();
                total = (int)Math.max(1, Math.round(base * HaloScaling.cooldownMul(tier)));
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
