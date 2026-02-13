package net.normlroyal.descendedangel.haloabilities;

import net.minecraft.client.player.LocalPlayer;
import net.normlroyal.descendedangel.util.HaloUtils;

public class ClientAbilityState {
    private static int index = 0;

    private static final HaloAbility[] DOMINION_ABILITIES = {
            HaloAbility.TELEPORT,
            HaloAbility.SPACE_CHEST,
            HaloAbility.FIELD,
            HaloAbility.ACCELERATE
    };

    private static final HaloAbility[] POWER_ABILITIES = {
            HaloAbility.FIREBALL,
            HaloAbility.GUST,
            HaloAbility.EARTH_WALL,
            HaloAbility.MIST_VEIL
    };

    private static final HaloAbility[] ALL_ABILITIES = {
            HaloAbility.FIREBALL,
            HaloAbility.GUST,
            HaloAbility.EARTH_WALL,
            HaloAbility.MIST_VEIL,
            HaloAbility.TELEPORT,
            HaloAbility.SPACE_CHEST,
            HaloAbility.FIELD,
            HaloAbility.ACCELERATE
    };

    private static final HaloAbility[] NO_ABILITIES = {};

    private static HaloAbility[] getListForTier(int tier) {
        return switch (tier) {
            case 4 -> POWER_ABILITIES;
            case 6 -> DOMINION_ABILITIES;
            case 7, 8, 9 -> ALL_ABILITIES;
            default -> NO_ABILITIES;
        };
    }

    public static HaloAbility get(LocalPlayer player) {
        if (player == null) return null;

        HaloAbility[] list = getListForTier(HaloUtils.getEquippedHaloTier(player));
        if (list.length == 0) return null;

        if (index < 0 || index >= list.length) index = 0;
        return list[index];
    }

    public static void cycle(LocalPlayer player) {
        if (player == null) return;

        HaloAbility[] list = getListForTier(HaloUtils.getEquippedHaloTier(player));
        if (list.length == 0) return;

        index = (index + 1) % list.length;
    }

    public static void reset() {
        index = 0;
    }
}