package net.normlroyal.descendedangel.haloabilities.helpers;

import net.minecraft.server.level.ServerPlayer;
import net.normlroyal.descendedangel.haloabilities.DominionAbilities;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.haloabilities.PowerAbilities;

public final class CooldownSnapshots {

    public record CooldownSnapshot(long until, int total) {}

    public static CooldownSnapshot getCooldown(ServerPlayer sp, HaloAbility ability) {
        long until;
        int total;

        switch (ability) {
            case TELEPORT,
                 SPACE_CHEST,
                 FIELD,
                 ACCELERATE,
                 ASTRAL_LANCE,
                 HEAVENS_MAP,
                 RESONANCE_PULSE,
                 SACRED_SILENCE -> {
                until = sp.getPersistentData().getLong(DominionAbilities.cooldownTag(ability));
                total = DominionAbilities.scaledCooldown(sp, ability);
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
                 MIST_VEIL,
                 MOVING_FIELD_OF_MIST,
                 SERAPHIC_MIRAGE,
                 DIVINE_SERENITY -> {
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