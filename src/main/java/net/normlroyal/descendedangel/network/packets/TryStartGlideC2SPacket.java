package net.normlroyal.descendedangel.network.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.util.WingLogic;
import net.normlroyal.descendedangel.util.WingUtils;

import java.util.function.Supplier;

public record TryStartGlideC2SPacket() {

    public static void encode(TryStartGlideC2SPacket msg, net.minecraft.network.FriendlyByteBuf buf) {
    }

    public static TryStartGlideC2SPacket decode(net.minecraft.network.FriendlyByteBuf buf) {
        return new TryStartGlideC2SPacket();
    }

    public static void handle(TryStartGlideC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sp = ctx.get().getSender();
            if (sp == null) return;

            ItemStack wings = WingUtils.getEquippedWings(sp);
            if (wings.isEmpty()) return;
            if (WingLogic.getWingTier(wings) != 1) return;

            if (canStartGlide(sp)) {
                sp.startFallFlying();
                sp.fallDistance = 0;
            }
        });

        ctx.get().setPacketHandled(true);
    }

    private static boolean canStartGlide(ServerPlayer sp) {
        return !sp.onGround()
                && !sp.isInWater()
                && !sp.hasEffect(MobEffects.LEVITATION)
                && !sp.isPassenger()
                && !sp.isFallFlying()
                && sp.getDeltaMovement().y < 0.0;
    }
}