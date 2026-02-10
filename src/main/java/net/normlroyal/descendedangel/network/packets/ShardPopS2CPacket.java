package net.normlroyal.descendedangel.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShardPopS2CPacket {
    private final ItemStack stack;

    public ShardPopS2CPacket(ItemStack stack) {
        this.stack = stack;
    }

    public static void encode(ShardPopS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeItem(msg.stack);
    }

    public static ShardPopS2CPacket decode(FriendlyByteBuf buf) {
        return new ShardPopS2CPacket(buf.readItem());
    }

    public static void handle(ShardPopS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.gameRenderer != null) {
                mc.gameRenderer.displayItemActivation(msg.stack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
