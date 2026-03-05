package net.normlroyal.descendedangel.flight;

public final class ClientFlightState {
    private static boolean active;

    public static boolean isActive() { return active; }
    public static void setActive(boolean a) { active = a; }

    private ClientFlightState() {}
}
