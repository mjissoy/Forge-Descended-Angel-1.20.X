package net.normlroyal.descendedangel.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientMsgS2CPacket(String text) {

    public static void encode(ClientMsgS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.text());
    }

    public static ClientMsgS2CPacket decode(FriendlyByteBuf buf) {
        return new ClientMsgS2CPacket(buf.readUtf(256));
    }

    public static void handle(ClientMsgS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            mc.player.displayClientMessage(Component.literal(msg.text()), true);
        });
        ctx.get().setPacketHandled(true);
    }
}
