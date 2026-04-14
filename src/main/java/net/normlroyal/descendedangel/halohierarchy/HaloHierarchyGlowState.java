package net.normlroyal.descendedangel.halohierarchy;

public class HaloHierarchyGlowState {
    private static boolean enabled = true;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        HaloHierarchyGlowState.enabled = enabled;
    }
}