package net.normlroyal.descendedangel.haloabilities.helpers;

import net.normlroyal.descendedangel.haloabilities.DominionAbilities;
import net.normlroyal.descendedangel.haloabilities.PowerAbilities;

public final class ClientUnlockState {
    private static boolean fire;
    private static boolean fireSacredFlare;
    private static boolean fireSolCorona;
    private static boolean firePillarsOfRadiance;

    private static boolean air;
    private static boolean airVacuumVortex;
    private static boolean airZephyrScythes;
    private static boolean airHeavenlyDowndraft;

    private static boolean earth;
    private static boolean earthHolyBastion;
    private static boolean earthAegisPillar;
    private static boolean earthCrystalChrysalis;

    private static boolean water;
    private static boolean space;
    private static boolean time;

    public static void set(
            boolean fireIn,
            boolean fireSacredFlareIn,
            boolean fireSolCoronaIn,
            boolean firePillarsOfRadianceIn,

            boolean airIn,
            boolean airVacuumVortexIn,
            boolean airZephyrScythesIn,
            boolean airHeavenlyDowndraftIn,

            boolean earthIn,
            boolean earthHolyBastionIn,
            boolean earthAegisPillarIn,
            boolean earthCrystalChrysalisIn,

            boolean waterIn,
            boolean spaceIn,
            boolean timeIn
    ) {
        fire = fireIn;
        fireSacredFlare = fireSacredFlareIn;
        fireSolCorona = fireSolCoronaIn;
        firePillarsOfRadiance = firePillarsOfRadianceIn;

        air = airIn;
        airVacuumVortex = airVacuumVortexIn;
        airZephyrScythes = airZephyrScythesIn;
        airHeavenlyDowndraft = airHeavenlyDowndraftIn;

        earth = earthIn;
        earthHolyBastion = earthHolyBastionIn;
        earthAegisPillar = earthAegisPillarIn;
        earthCrystalChrysalis = earthCrystalChrysalisIn;

        water = waterIn;
        space = spaceIn;
        time = timeIn;
    }

    public static boolean has(String tag) {
        return switch (tag) {
            case PowerAbilities.TAG_FIRE -> fire;
            case PowerAbilities.TAG_FIRE_SACRED_FLARE -> fireSacredFlare;
            case PowerAbilities.TAG_FIRE_SOL_CORONA -> fireSolCorona;
            case PowerAbilities.TAG_FIRE_PILLARS_OF_RADIANCE -> firePillarsOfRadiance;

            case PowerAbilities.TAG_AIR -> air;
            case PowerAbilities.TAG_AIR_VACUUM_VORTEX -> airVacuumVortex;
            case PowerAbilities.TAG_AIR_ZEPHYR_SCYTHES -> airZephyrScythes;
            case PowerAbilities.TAG_AIR_HEAVENLY_DOWNDRAFT -> airHeavenlyDowndraft;

            case PowerAbilities.TAG_EARTH -> earth;
            case PowerAbilities.TAG_EARTH_HOLY_BASTION -> earthHolyBastion;
            case PowerAbilities.TAG_EARTH_AEGIS_PILLAR -> earthAegisPillar;
            case PowerAbilities.TAG_EARTH_CRYSTAL_CHRYSALIS -> earthCrystalChrysalis;

            case PowerAbilities.TAG_WATER -> water;

            case DominionAbilities.TAG_SPACE -> space;
            case DominionAbilities.TAG_TIME -> time;

            default -> false;
        };
    }

    public static void reset() {
        fire = false;
        fireSacredFlare = false;
        fireSolCorona = false;
        firePillarsOfRadiance = false;

        air = false;
        airVacuumVortex = false;
        airZephyrScythes = false;
        airHeavenlyDowndraft = false;

        earth = false;
        earthHolyBastion = false;
        earthAegisPillar = false;
        earthCrystalChrysalis = false;

        water = false;
        space = false;
        time = false;
    }

    private ClientUnlockState() {}
}