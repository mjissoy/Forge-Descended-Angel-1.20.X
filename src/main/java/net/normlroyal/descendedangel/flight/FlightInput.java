package net.normlroyal.descendedangel.flight;

public record FlightInput(
        boolean ascend,
        boolean descend,
        boolean boost,
        float forward,
        float strafe) { }

