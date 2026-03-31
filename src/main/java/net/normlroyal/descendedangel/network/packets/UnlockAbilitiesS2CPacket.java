package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.haloabilities.helpers.ClientUnlockState;

import java.util.function.Supplier;

public record UnlockAbilitiesS2CPacket(
        boolean fire,
        boolean air,
        boolean earth,
        boolean water,
        boolean space,
        boolean time
) {

    public static void encode(UnlockAbilitiesS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.fire());
        buf.writeBoolean(msg.air());
        buf.writeBoolean(msg.earth());
        buf.writeBoolean(msg.water());
        buf.writeBoolean(msg.space());
        buf.writeBoolean(msg.time());
    }

    public static UnlockAbilitiesS2CPacket decode(FriendlyByteBuf buf) {
        return new UnlockAbilitiesS2CPacket(
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public static void handle(UnlockAbilitiesS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientUnlockState.set(
                    msg.fire(),
                    msg.air(),
                    msg.earth(),
                    msg.water(),
                    msg.space(),
                    msg.time()
            );
        });
        ctx.get().setPacketHandled(true);
    }
}