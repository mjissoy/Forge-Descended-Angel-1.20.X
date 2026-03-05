package net.normlroyal.descendedangel.flight;

import net.minecraft.server.level.ServerPlayer;

public interface IFlightData {
    FlightState state();

    default boolean isActive() { return state().active; }
    default void setActive(boolean active) { state().active = active; }

    void sync(ServerPlayer player);
}
