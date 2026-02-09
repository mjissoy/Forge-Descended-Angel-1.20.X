package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.haloabilities.DominionAbilities;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;

import java.util.function.Supplier;

public record UseHaloAbilityC2SPacket(int abilityOrdinal) {

    public static void encode(UseHaloAbilityC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.abilityOrdinal);
    }

    public static UseHaloAbilityC2SPacket decode(FriendlyByteBuf buf) {
        return new UseHaloAbilityC2SPacket(buf.readVarInt());
    }

    public static void handle(UseHaloAbilityC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var sp = ctx.get().getSender();
            if (sp == null) return;

            int ord = msg.abilityOrdinal();
            if (ord < 0 || ord >= HaloAbility.values().length) return;

            HaloAbility ability = HaloAbility.values()[ord];
            DominionAbilities.tryUse(sp, ability);
        });
        ctx.get().setPacketHandled(true);
    }
}
