package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.flight.ClientFlightState;

import java.util.function.Supplier;

public record FlightActiveS2CPacket(boolean active) {
    public static void encode(FlightActiveS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.active);
    }
    public static FlightActiveS2CPacket decode(FriendlyByteBuf buf) {
        return new FlightActiveS2CPacket(buf.readBoolean());
    }
    public static void handle(FlightActiveS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientFlightState.setActive(msg.active));
        ctx.get().setPacketHandled(true);
    }
}
