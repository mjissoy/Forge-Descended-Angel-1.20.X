package net.normlroyal.descendedangel.flight;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.FlightActiveS2CPacket;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FlightSystem {
    public static final FlightController CONTROLLER = new AngelicFlightController();

    private static final Map<UUID, FlightInput> INPUTS = new ConcurrentHashMap<>();

    private FlightSystem() {}

    public static FlightInput getInput(ServerPlayer sp) {
        return INPUTS.getOrDefault(sp.getUUID(), FlightInput.ZERO);
    }

    public static void setInput(ServerPlayer sp, FlightInput input) {
        INPUTS.put(sp.getUUID(), input == null ? FlightInput.ZERO : input.sanitized());
    }

    public static void clear(ServerPlayer sp) {
        if (sp != null) {
            INPUTS.remove(sp.getUUID());
        }
    }

    public static void syncInactive(ServerPlayer sp) {
        if (sp == null) {
            return;
        }

        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> sp),
                new FlightActiveS2CPacket(false)
        );
    }

    public static void clearAndSync(ServerPlayer sp) {
        clear(sp);
        if (sp != null) {
            IFlightData data = FlightData.get(sp);
            data.state().active = false;
            data.state().resetMotion();
            syncInactive(sp);
            data.sync(sp);
        }
    }

    public static void stopFlight(ServerPlayer sp) {
        IFlightData data = FlightData.get(sp);
        FlightState st = data.state();

        if (!st.active) {
            clearAndSync(sp);
            return;
        }

        st.active = false;
        CONTROLLER.onStop(sp, st);
        clear(sp);
        syncInactive(sp);

        data.sync(sp);
    }
}
