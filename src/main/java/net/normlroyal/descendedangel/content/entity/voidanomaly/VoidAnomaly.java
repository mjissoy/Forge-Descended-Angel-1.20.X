package net.normlroyal.descendedangel.content.entity.voidanomaly;

/**
 * Shared marker/contract for void-corrupted mobs.
 *
 * This is intentionally small so the void content can be moved into a reusable library mod later
 * without dragging Descended Angel-specific entity inheritance with it.
 */
public interface VoidAnomaly {
    default int getVoidPocketKillValue() {
        return 1;
    }
}
