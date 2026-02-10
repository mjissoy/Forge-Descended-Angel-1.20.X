package net.normlroyal.descendedangel.item.custom.enums;

import net.normlroyal.descendedangel.haloabilities.DominionAbilities;

public enum FruitType {
    SPACE(DominionAbilities.TAG_SPACE),
    TIME(DominionAbilities.TAG_TIME);

    private final String unlockTag;

    FruitType(String unlockTag) {
        this.unlockTag = unlockTag;
    }

    public String tag() {
        return unlockTag;
    }
}
