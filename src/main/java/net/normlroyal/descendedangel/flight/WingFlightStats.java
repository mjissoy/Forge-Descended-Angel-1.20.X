package net.normlroyal.descendedangel.flight;

import net.normlroyal.descendedangel.config.ModConfigs;

public class WingFlightStats {
    private WingFlightStats() {}

    public static double speedMultiplier(int tier) {
        double global = ModConfigs.COMMON.GLOBAL_FLIGHT_SPEED_MULTIPLIER.get();

        return switch (tier) {
            case 3 -> ModConfigs.COMMON.T3_SPEED_MULTIPLIER.get() * global;
            case 2 -> ModConfigs.COMMON.T2_SPEED_MULTIPLIER.get() * global;
            default -> 0.0;
        };
    }

    public static double accelMultiplier(int tier) {
        return switch (tier) {
            case 3 -> 1.20;
            case 2 -> 1.0;
            default -> 0.0;
        };
    }
}
