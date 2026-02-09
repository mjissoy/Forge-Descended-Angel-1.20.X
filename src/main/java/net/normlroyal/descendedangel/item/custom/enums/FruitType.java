package net.normlroyal.descendedangel.item.custom.enums;

public enum FruitType {
    SPACE("da_unlocked_space"),
    TIME("da_unlocked_time");

    private final String unlockTag;

    FruitType(String unlockTag) {
        this.unlockTag = unlockTag;
    }

    public String tag() {
        return unlockTag;
    }
}
