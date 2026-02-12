package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.haloabilities.helpers.ClientCooldownState;

import java.util.function.Supplier;

public record AbilityCooldownS2CPacket(int abilityOrdinal, long until, int total) {

    public static void encode(AbilityCooldownS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.abilityOrdinal());
        buf.writeLong(msg.until());
        buf.writeVarInt(msg.total());
    }

    public static AbilityCooldownS2CPacket decode(FriendlyByteBuf buf) {
        int abilityOrdinal = buf.readVarInt();
        long until = buf.readLong();
        int total = buf.readVarInt();
        return new AbilityCooldownS2CPacket(abilityOrdinal, until, total);
    }

    public static void handle(AbilityCooldownS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            int ord = msg.abilityOrdinal();
            if (ord < 0 || ord >= HaloAbility.values().length) return;

            HaloAbility a = HaloAbility.values()[ord];
            ClientCooldownState.set(a, msg.until(), msg.total());
        });
        ctx.get().setPacketHandled(true);
    }
}
