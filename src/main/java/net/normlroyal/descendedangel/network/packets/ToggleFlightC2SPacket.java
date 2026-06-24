package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.normlroyal.descendedangel.flight.FlightData;
import net.normlroyal.descendedangel.flight.FlightInput;
import net.normlroyal.descendedangel.flight.FlightState;
import net.normlroyal.descendedangel.flight.FlightSystem;
import net.normlroyal.descendedangel.flight.IFlightData;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.util.WingLogic;
import net.normlroyal.descendedangel.util.WingUtils;

import java.util.function.Supplier;

public record ToggleFlightC2SPacket() {

    public static void encode(ToggleFlightC2SPacket msg, FriendlyByteBuf buf) {
    }

    public static ToggleFlightC2SPacket decode(FriendlyByteBuf buf) {
        return new ToggleFlightC2SPacket();
    }

    public static void handle(ToggleFlightC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sp = ctx.get().getSender();
            if (sp == null) {
                return;
            }

            var wings = WingUtils.getEquippedWings(sp);
            if (wings.isEmpty() || !WingLogic.allowsCustomFlight(wings)) {
                return;
            }

            IFlightData data = FlightData.get(sp);
            FlightState st = data.state();

            if (!st.active) {
                if (!canStartCustomFlight(sp)) {
                    sendActive(sp, false);
                    return;
                }

                if (sp.isFallFlying()) {
                    sp.stopFallFlying();
                }

                sp.getAbilities().flying = false;
                sp.onUpdateAbilities();

                st.active = true;
                FlightSystem.setInput(sp, FlightInput.ZERO);
                FlightSystem.CONTROLLER.onStart(sp, st);
            } else {
                st.active = false;
                FlightSystem.CONTROLLER.onStop(sp, st);
                FlightSystem.clear(sp);
            }

            sendActive(sp, st.active);
            data.sync(sp);
        });

        ctx.get().setPacketHandled(true);
    }

    private static boolean canStartCustomFlight(ServerPlayer sp) {
        return sp.isAlive()
                && !sp.isCreative()
                && !sp.isSpectator()
                && !sp.isPassenger()
                && !sp.hasEffect(MobEffects.LEVITATION);
    }

    private static void sendActive(ServerPlayer sp, boolean active) {
        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> sp),
                new FlightActiveS2CPacket(active)
        );
    }
}
