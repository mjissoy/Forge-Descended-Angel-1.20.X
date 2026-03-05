package net.normlroyal.descendedangel.flight;

import net.minecraft.server.level.ServerPlayer;

public interface FlightController {

    default boolean canStart(ServerPlayer p) { return true; }
    default void onStart(ServerPlayer p, FlightState state) {}
    default void onStop(ServerPlayer p, FlightState state) {}

    void tick(ServerPlayer p, FlightState state, FlightInput input);

}
