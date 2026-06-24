package net.normlroyal.descendedangel.common.halohierarchy;

public class HaloHierarchyGlowState {
    private static boolean enabled = true;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        HaloHierarchyGlowState.enabled = enabled;
    }
}