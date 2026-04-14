package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.halohierarchy.HaloHierarchyGlowState;

import java.util.function.Supplier;

public class HaloHierarchyGlowS2CPacket {
    private final boolean enabled;

    public HaloHierarchyGlowS2CPacket(boolean enabled) {
        this.enabled = enabled;
    }

    public static void encode(HaloHierarchyGlowS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.enabled);
    }

    public static HaloHierarchyGlowS2CPacket decode(FriendlyByteBuf buf) {
        return new HaloHierarchyGlowS2CPacket(buf.readBoolean());
    }

    public static void handle(HaloHierarchyGlowS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> HaloHierarchyGlowState.setEnabled(msg.enabled));
        ctx.get().setPacketHandled(true);
    }
}