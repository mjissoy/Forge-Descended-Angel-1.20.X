package net.normlroyal.descendedangel.item.custom.enums;

public enum SpearMoves {
    THRUST, SWEEP, CUT;

    public SpearMoves next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
