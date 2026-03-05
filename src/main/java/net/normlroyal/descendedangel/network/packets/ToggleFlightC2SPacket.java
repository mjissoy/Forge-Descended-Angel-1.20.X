package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.normlroyal.descendedangel.flight.FlightData;
import net.normlroyal.descendedangel.flight.FlightState;
import net.normlroyal.descendedangel.flight.FlightSystem;
import net.normlroyal.descendedangel.flight.IFlightData;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.util.WingLogic;
import net.normlroyal.descendedangel.util.WingUtils;

import java.util.function.Supplier;

public record ToggleFlightC2SPacket() {

    public static void encode(ToggleFlightC2SPacket msg, FriendlyByteBuf buf) {}
    public static ToggleFlightC2SPacket decode(FriendlyByteBuf buf) { return new ToggleFlightC2SPacket(); }

    public static void handle(ToggleFlightC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sp = ctx.get().getSender();
            if (sp == null) return;

            if (sp.isCreative() || sp.isSpectator()) return;

            var wings = WingUtils.getEquippedWings(sp);
            if (wings.isEmpty() || !WingLogic.allowsCustomFlight(wings)) return;

            IFlightData data = FlightData.get(sp);
            FlightState st = data.state();

            if (!st.active) {
                if (sp.isFallFlying()) {
                    sp.stopFallFlying();
                }

                sp.getAbilities().flying = false;
                sp.onUpdateAbilities();

                st.active = true;
                FlightSystem.CONTROLLER.onStart(sp, st);
            } else {
                st.active = false;
                FlightSystem.CONTROLLER.onStop(sp, st);
            }

            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> sp),
                    new FlightActiveS2CPacket(st.active)
            );

        });

        ctx.get().setPacketHandled(true);
    }
}
