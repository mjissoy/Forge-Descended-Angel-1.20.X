package net.normlroyal.descendedangel.flight;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FlightSystem {
    public static final FlightController CONTROLLER = new AngelicFlightController();

    private static final Map<UUID, FlightInput> INPUTS = new ConcurrentHashMap<>();

    private FlightSystem() {}

    public static FlightInput getInput(ServerPlayer sp) {
        return INPUTS.getOrDefault(sp.getUUID(), new FlightInput(false, false, false));
    }

    public static void setInput(ServerPlayer sp, FlightInput input) {
        INPUTS.put(sp.getUUID(), input);
    }

    public static void clear(ServerPlayer sp) {
        INPUTS.remove(sp.getUUID());
    }
}