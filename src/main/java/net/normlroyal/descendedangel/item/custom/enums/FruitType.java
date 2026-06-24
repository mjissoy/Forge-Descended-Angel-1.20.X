package net.normlroyal.descendedangel.item.custom.enums;

import net.normlroyal.descendedangel.haloabilities.DominionAbilities;

public enum FruitType {
    SPACE(DominionAbilities.TAG_SPACE),
    TIME(DominionAbilities.TAG_TIME),
    CELESTIAL(DominionAbilities.TAG_CELESTIAL),
    RESONANCE(DominionAbilities.TAG_RESONANCE);

    private final String tag;

    FruitType(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }
}