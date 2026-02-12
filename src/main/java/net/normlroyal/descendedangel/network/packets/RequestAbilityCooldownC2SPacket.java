package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.haloabilities.helpers.CooldownSnapshots;
import net.normlroyal.descendedangel.network.ModNetwork;

import java.util.function.Supplier;

public record RequestAbilityCooldownC2SPacket(int abilityOrdinal) {

    public static void encode(RequestAbilityCooldownC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.abilityOrdinal());
    }

    public static RequestAbilityCooldownC2SPacket decode(FriendlyByteBuf buf) {
        return new RequestAbilityCooldownC2SPacket(buf.readVarInt());
    }

    public static void handle(RequestAbilityCooldownC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer sp = context.getSender();
            if (sp == null) return;

            int ord = msg.abilityOrdinal();
            HaloAbility[] values = HaloAbility.values();
            if (ord < 0 || ord >= values.length) return;

            HaloAbility a = values[ord];

            CooldownSnapshots.CooldownSnapshot snap = CooldownSnapshots.getCooldown(sp, a);

            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> sp),
                    new AbilityCooldownS2CPacket(a.ordinal(), snap.until(), snap.total())
            );
        });
        context.setPacketHandled(true);
    }
}
