package net.normlroyal.descendedangel.haloabilities;

public class ClientAbilityState {

    private static int index = 0;

    private static final HaloAbility[] DOMINION_ABILITIES = {
            HaloAbility.TELEPORT,
            HaloAbility.FIELD
    };

    public static HaloAbility get() {
        return DOMINION_ABILITIES[index];
    }

    public static void cycle() {
        index = (index + 1) % DOMINION_ABILITIES.length;
    }}
