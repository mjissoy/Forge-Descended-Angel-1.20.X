package net.normlroyal.descendedangel.haloabilities.helpers;

import net.normlroyal.descendedangel.haloabilities.DominionAbilities;
import net.normlroyal.descendedangel.haloabilities.PowerAbilities;

public final class ClientUnlockState {
    private static boolean fire;
    private static boolean air;
    private static boolean earth;
    private static boolean water;
    private static boolean space;
    private static boolean time;

    public static void set(
            boolean fireIn,
            boolean airIn,
            boolean earthIn,
            boolean waterIn,
            boolean spaceIn,
            boolean timeIn
    ) {
        fire = fireIn;
        air = airIn;
        earth = earthIn;
        water = waterIn;
        space = spaceIn;
        time = timeIn;
    }

    public static boolean has(String tag) {
        return switch (tag) {
            case PowerAbilities.TAG_FIRE -> fire;
            case PowerAbilities.TAG_AIR -> air;
            case PowerAbilities.TAG_EARTH -> earth;
            case PowerAbilities.TAG_WATER -> water;
            case DominionAbilities.TAG_SPACE -> space;
            case DominionAbilities.TAG_TIME -> time;
            default -> false;
        };
    }

    public static void reset() {
        fire = false;
        air = false;
        earth = false;
        water = false;
        space = false;
        time = false;
    }

    private ClientUnlockState() {}
}