package net.normlroyal.descendedangel.flight;

public final class ClientFlightState {
    private static boolean active;
    private static final FlightState STATE = new FlightState();
    private static FlightInput input = FlightInput.ZERO;

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean value) {
        if (active != value) {
            STATE.resetMotion();
        }

        active = value;
        STATE.active = value;

        if (!value) {
            input = FlightInput.ZERO;
        }
    }

    public static FlightState state() {
        return STATE;
    }

    public static FlightInput input() {
        return input;
    }

    public static void setInput(FlightInput value) {
        input = value == null ? FlightInput.ZERO : value.sanitized();
    }

    public static void reset() {
        active = false;
        input = FlightInput.ZERO;
        STATE.active = false;
        STATE.resetMotion();
    }

    private ClientFlightState() {}
}
