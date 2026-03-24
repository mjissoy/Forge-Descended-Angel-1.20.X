package net.normlroyal.descendedangel.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayMarkActivationS2CPacket {
    private final ItemStack stack;

    public PlayMarkActivationS2CPacket(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack stack() {
        return stack;
    }

    public static void encode(PlayMarkActivationS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeItem(msg.stack);
    }

    public static PlayMarkActivationS2CPacket decode(FriendlyByteBuf buf) {
        return new PlayMarkActivationS2CPacket(buf.readItem());
    }

    public static void handle(PlayMarkActivationS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameRenderer != null) {
                mc.gameRenderer.displayItemActivation(msg.stack());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}