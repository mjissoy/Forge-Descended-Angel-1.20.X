package net.normlroyal.descendedangel.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.network.writs.SyncWritDisplaysS2CPacket;

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
    }

    private ModNetwork() {}

}
