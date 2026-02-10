package net.normlroyal.descendedangel.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.network.packets.ClientMsgS2CPacket;
import net.normlroyal.descendedangel.network.packets.ShardPopS2CPacket;
import net.normlroyal.descendedangel.network.packets.SyncWritDisplaysS2CPacket;
import net.normlroyal.descendedangel.network.packets.UseHaloAbilityC2SPacket;

public final class ModNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "main"),
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
    }

    private ModNetwork() {}

}
