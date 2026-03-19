package net.normlroyal.descendedangel.events;

public final class ClientFlightRenderState {
    private static float pitchDeg = 0f;
    private static float bankDeg = 0f;

    public static float getPitchDeg() {
        return pitchDeg;
    }

    public static float getBankDeg() {
        return bankDeg;
    }

    public static void tickToward(float targetPitchDeg, float targetBankDeg) {
        pitchDeg = approach(pitchDeg, targetPitchDeg, 1.2f);
        bankDeg = approach(bankDeg, targetBankDeg, 1.0f);
    }

    public static void reset() {
        pitchDeg = 0f;
        bankDeg = 0f;
    }

    private static float approach(float cur, float target, float step) {
        float diff = target - cur;
        if (Math.abs(diff) <= step) return target;
        return cur + Math.copySign(step, diff);
    }

    private ClientFlightRenderState() {}
}