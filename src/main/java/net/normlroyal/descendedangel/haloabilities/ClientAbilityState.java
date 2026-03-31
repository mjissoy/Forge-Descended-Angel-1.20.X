package net.normlroyal.descendedangel.haloabilities;

import net.minecraft.client.player.LocalPlayer;
import java.util.Arrays;

public class ClientAbilityState {
    private static int index = 0;

    private static HaloAbility[] getVisibleAbilities(LocalPlayer player) {
        if (player == null) return new HaloAbility[0];

        return Arrays.stream(HaloAbility.values())
                .filter(a -> a.isVisibleFor(player))
                .toArray(HaloAbility[]::new);
    }

    public static HaloAbility get(LocalPlayer player) {
        if (player == null) return null;

        HaloAbility[] list = getVisibleAbilities(player);
        if (list.length == 0) {
            index = 0;
            return null;
        }

        if (index < 0 || index >= list.length) {
            index = 0;
        }

        return list[index];
    }

    public static void cycle(LocalPlayer player) {
        if (player == null) return;

        HaloAbility[] list = getVisibleAbilities(player);
        if (list.length == 0) {
            index = 0;
            return;
        }

        index = (index + 1) % list.length;
    }

    public static void reset() {
        index = 0;
    }

    public static void clamp(LocalPlayer player) {
        HaloAbility[] list = getVisibleAbilities(player);
        if (list.length == 0) {
            index = 0;
        } else if (index >= list.length) {
            index = 0;
        }
    }
}