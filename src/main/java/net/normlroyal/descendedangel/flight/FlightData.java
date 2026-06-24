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

    public static void clear(ServerPlayer sp) {
        if (sp != null) {
            DATA.remove(sp.getUUID());
        }
    }

    public static void clear(UUID uuid) {
        if (uuid != null) {
            DATA.remove(uuid);
        }
    }

    private static class SimpleFlightData implements IFlightData {
        private final FlightState state = new FlightState();

        @Override
        public FlightState state() {
            return state;
        }

        @Override
        public void sync(ServerPlayer player) {
        }
    }
}
