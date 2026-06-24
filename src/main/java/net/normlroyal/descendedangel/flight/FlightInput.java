package net.normlroyal.descendedangel.flight;

public record FlightInput(
        boolean ascend,
        boolean descend,
        boolean boost,
        float forward,
        float strafe
) {
    public static final FlightInput ZERO = new FlightInput(false, false, false, 0.0F, 0.0F);

    public FlightInput sanitized() {
        boolean cleanAscend = ascend;
        boolean cleanDescend = descend;

        // Opposing vertical inputs should not let the packet create odd server-side states.
        if (cleanAscend && cleanDescend) {
            cleanDescend = false;
        }

        return new FlightInput(
                cleanAscend,
                cleanDescend,
                boost,
                cleanAxis(forward),
                cleanAxis(strafe)
        );
    }

    public static float cleanAxis(float value) {
        if (!Float.isFinite(value)) {
            return 0.0F;
        }

        if (value > 1.0F) {
            return 1.0F;
        }

        if (value < -1.0F) {
            return -1.0F;
        }

        return value;
    }
}
