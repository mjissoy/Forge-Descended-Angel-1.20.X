package net.normlroyal.descendedangel.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.util.WingUtils;

import java.util.function.Supplier;

public class RequestT1GlideC2SPacket {
    public static void encode(RequestT1GlideC2SPacket msg, FriendlyByteBuf buf) {}
    public static RequestT1GlideC2SPacket decode(FriendlyByteBuf buf) { return new RequestT1GlideC2SPacket(); }

    public static void handle(RequestT1GlideC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (WingUtils.getWingTier(player) != 1) return;

            long now = player.level().getGameTime();

            player.getPersistentData().putLong("da_t1_glide_until", now + 200);

            // player.displayClientMessage(Component.literal("T1 glide request received"), true);
        });
        ctx.get().setPacketHandled(true);
    }

}
