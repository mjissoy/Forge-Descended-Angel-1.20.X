package net.normlroyal.descendedangel.flight;

public class FlightState {
    public boolean active = false;

    public float forwardSpeed = 0f;
    public float verticalSpeed = 0f;
    public float flapAmount = 0f;

    public void resetMotion() {
        forwardSpeed = 0f;
        verticalSpeed = 0f;
        flapAmount = 0f;
    }

}
