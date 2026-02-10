package net.normlroyal.descendedangel.haloabilities;

public class HaloScaling {
    public static int mastery(int tier) {
        return switch (tier) {
            case 7 -> 1;
            case 8 -> 2;
            case 9 -> 3;
            default -> 0;
        };
    }

    public static double cooldownMul(int tier) {
        return switch (tier) {
            case 7 -> 0.90;
            case 8 -> 0.80;
            case 9 -> 0.70;
            default -> 1.0;
        };
    }

    public static double scaleUp(double base, int tier) {
        int m = mastery(tier);
        return base * (1.0 + 0.20 * m);
    }

    public static int addInt(int base, int tier, int perLevel) {
        return base + mastery(tier) * perLevel;
    }

    public static int addIntCapped(int base, int tier, int perLevel, int max) {
        return Math.min(base + mastery(tier) * perLevel, max);
    }

    public static int scaleIntDuration(int base, int tier) {
        double scaled = base * (1.0 + 0.20 * mastery(tier));
        return Math.max(1, (int)Math.round(scaled));
    }
}
