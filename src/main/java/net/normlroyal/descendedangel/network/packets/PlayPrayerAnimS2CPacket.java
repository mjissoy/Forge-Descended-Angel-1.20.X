package net.normlroyal.descendedangel.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.client.animation.PrayerAnimationController;

import java.util.UUID;
import java.util.function.Supplier;

public record PlayPrayerAnimS2CPacket(UUID playerId) {

    public static void encode(PlayPrayerAnimS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerId());
    }

    public static PlayPrayerAnimS2CPacket decode(FriendlyByteBuf buf) {
        return new PlayPrayerAnimS2CPacket(buf.readUUID());
    }

    public static void handle(PlayPrayerAnimS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        DescendedAngel.LOGGER.info("Received PlayPrayerAnimS2CPacket for UUID {}", msg.playerId());
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            var entity = mc.level.getPlayerByUUID(msg.playerId());
            if (entity instanceof AbstractClientPlayer clientPlayer) {
                PrayerAnimationController.playPrayer(clientPlayer);
            }
            DescendedAngel.LOGGER.info("Resolved client player entity = {}", entity);
        });

        ctx.get().setPacketHandled(true);
    }
}