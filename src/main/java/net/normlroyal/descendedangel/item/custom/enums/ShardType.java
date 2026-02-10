package net.normlroyal.descendedangel.item.custom.enums;

import net.normlroyal.descendedangel.haloabilities.PowerAbilities;

public enum ShardType {
    FIRE(PowerAbilities.TAG_FIRE),
    WATER(PowerAbilities.TAG_WATER),
    EARTH(PowerAbilities.TAG_EARTH),
    AIR(PowerAbilities.TAG_AIR);

    private final String unlockTag;

    ShardType(String unlockTag) {
        this.unlockTag = unlockTag;
    }

    public String tag() {
        return unlockTag;
    }
}
