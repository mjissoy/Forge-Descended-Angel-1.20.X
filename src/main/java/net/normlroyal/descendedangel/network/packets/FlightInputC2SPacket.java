package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.flight.FlightData;
import net.normlroyal.descendedangel.flight.FlightInput;
import net.normlroyal.descendedangel.flight.FlightSystem;
import net.normlroyal.descendedangel.flight.IFlightData;
import net.normlroyal.descendedangel.util.WingLogic;
import net.normlroyal.descendedangel.util.WingUtils;

import java.util.function.Supplier;

public record FlightInputC2SPacket(boolean ascend, boolean descend, boolean boost) {

    public static void encode(FlightInputC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.ascend);
        buf.writeBoolean(msg.descend);
        buf.writeBoolean(msg.boost);
    }

    public static FlightInputC2SPacket decode(FriendlyByteBuf buf) {
        return new FlightInputC2SPacket(buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }

    public static void handle(FlightInputC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sp = ctx.get().getSender();
            if (sp == null) return;

            var wings = WingUtils.getEquippedWings(sp);
            if (wings.isEmpty() || !WingLogic.allowsCustomFlight(wings)) return;

            IFlightData data = FlightData.get(sp);
            if (!data.state().active) return;

            FlightSystem.setInput(sp, new FlightInput(msg.ascend, msg.descend, msg.boost));
        });

        ctx.get().setPacketHandled(true);
    }
}
