package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.haloabilities.DominionAbilities;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.haloabilities.PowerAbilities;
import net.normlroyal.descendedangel.util.HaloUtils;

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
            HaloAbility[] values = HaloAbility.values();
            if (ord < 0 || ord >= values.length) return;

            HaloAbility ability = values[ord];

            int tier = HaloUtils.getEquippedHaloTier(sp);

            switch (tier) {
                case 4 -> PowerAbilities.tryUse(sp, ability);
                case 6 -> DominionAbilities.tryUse(sp, ability);
                case 7, 8, 9 -> {
                    PowerAbilities.tryUse(sp, ability);
                    DominionAbilities.tryUse(sp, ability);
                }
                default -> {}
            }

        });

        ctx.get().setPacketHandled(true);
    }
}
