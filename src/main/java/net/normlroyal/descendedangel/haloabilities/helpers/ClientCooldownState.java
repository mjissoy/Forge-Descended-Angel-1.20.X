package net.normlroyal.descendedangel.haloabilities.helpers;

import net.normlroyal.descendedangel.haloabilities.HaloAbility;

public class ClientCooldownState {
    private static final long[] UNTIL = new long[HaloAbility.values().length];
    private static final int[]  TOTAL = new int[HaloAbility.values().length];

    public static void set(HaloAbility a, long untilGameTime, int totalTicks) {
        UNTIL[a.ordinal()] = untilGameTime;
        TOTAL[a.ordinal()] = totalTicks;
    }

    public static long until(HaloAbility a) { return UNTIL[a.ordinal()]; }
    public static int total(HaloAbility a) { return TOTAL[a.ordinal()]; }

    public static int remainingTicks(HaloAbility a, long now) {
        long until = until(a);
        long rem = until - now;
        return (int)Math.max(0, Math.min(Integer.MAX_VALUE, rem));
    }
}
