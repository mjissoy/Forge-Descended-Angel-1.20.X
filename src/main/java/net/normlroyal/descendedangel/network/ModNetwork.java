package net.normlroyal.descendedangel.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.network.packets.*;

public final class ModNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DescendedAngel.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int id = 0;

    public static void registerPackets() {
        CHANNEL.messageBuilder(SyncWritDisplaysS2CPacket.class, id++)
                .encoder(SyncWritDisplaysS2CPacket::encode)
                .decoder(SyncWritDisplaysS2CPacket::decode)
                .consumerMainThread(SyncWritDisplaysS2CPacket::handle)
                .add();

        CHANNEL.messageBuilder(UseHaloAbilityC2SPacket.class, id++)
                .encoder(UseHaloAbilityC2SPacket::encode)
                .decoder(UseHaloAbilityC2SPacket::decode)
                .consumerMainThread(UseHaloAbilityC2SPacket::handle)
                .add();

        CHANNEL.messageBuilder(ShardPopS2CPacket.class, id++)
                .encoder(ShardPopS2CPacket::encode)
                .decoder(ShardPopS2CPacket::decode)
                .consumerMainThread(ShardPopS2CPacket::handle)
                .add();

        CHANNEL.messageBuilder(ClientMsgS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientMsgS2CPacket::encode)
                .decoder(ClientMsgS2CPacket::decode)
                .consumerMainThread(ClientMsgS2CPacket::handle)
                .add();

        CHANNEL.messageBuilder(RequestAbilityCooldownC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(RequestAbilityCooldownC2SPacket::encode)
                .decoder(RequestAbilityCooldownC2SPacket::decode)
                .consumerMainThread(RequestAbilityCooldownC2SPacket::handle)
                .add();

        CHANNEL.messageBuilder(AbilityCooldownS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(AbilityCooldownS2CPacket::encode)
                .decoder(AbilityCooldownS2CPacket::decode)
                .consumerMainThread(AbilityCooldownS2CPacket::handle)
                .add();

        CHANNEL.messageBuilder(ToggleFlightC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ToggleFlightC2SPacket::encode)
                .decoder(ToggleFlightC2SPacket::decode)
                .consumerMainThread(ToggleFlightC2SPacket::handle)
                .add();

        CHANNEL.messageBuilder(FlightInputC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(FlightInputC2SPacket::encode)
                .decoder(FlightInputC2SPacket::decode)
                .consumerMainThread(FlightInputC2SPacket::handle)
                .add();

        CHANNEL.messageBuilder(FlightActiveS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(FlightActiveS2CPacket::encode)
                .decoder(FlightActiveS2CPacket::decode)
                .consumerMainThread(FlightActiveS2CPacket::handle)
                .add();

        CHANNEL.messageBuilder(PlayMarkActivationS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PlayMarkActivationS2CPacket::encode)
                .decoder(PlayMarkActivationS2CPacket::decode)
                .consumerMainThread(PlayMarkActivationS2CPacket::handle)
                .add();

        CHANNEL.messageBuilder(UnlockAbilitiesS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(UnlockAbilitiesS2CPacket::encode)
                .decoder(UnlockAbilitiesS2CPacket::decode)
                .consumerMainThread(UnlockAbilitiesS2CPacket::handle)
                .add();
    }

    public static void sendToPlayer(Object packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    private ModNetwork() {}

}
