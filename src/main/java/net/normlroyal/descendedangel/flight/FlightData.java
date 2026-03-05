package net.normlroyal.descendedangel.flight;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FlightData {
    private static final Map<UUID, SimpleFlightData> DATA = new ConcurrentHashMap<>();

    private FlightData() {}

    public static IFlightData get(ServerPlayer sp) {
        return DATA.computeIfAbsent(sp.getUUID(), id -> new SimpleFlightData());
    }

    private static class SimpleFlightData implements IFlightData {
        private final FlightState state = new FlightState();
        @Override public FlightState state() { return state; }
        @Override public void sync(ServerPlayer player) { }
    }
}