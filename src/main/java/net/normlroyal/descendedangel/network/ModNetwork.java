package net.normlroyal.descendedangel.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DescendedAngel.MOD_ID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
    );

    private static int id = 0;

    public static void register() {

        CHANNEL.messageBuilder(AdjustT3SpeedC2SPacket.class, id++)
                .encoder(AdjustT3SpeedC2SPacket::encode)
                .decoder(AdjustT3SpeedC2SPacket::decode)
                .consumerMainThread(AdjustT3SpeedC2SPacket::handle)
                .add();

        CHANNEL.messageBuilder(RequestT1GlideC2SPacket.class, id++)
                .encoder(RequestT1GlideC2SPacket::encode)
                .decoder(RequestT1GlideC2SPacket::decode)
                .consumerMainThread(RequestT1GlideC2SPacket::handle)
                .add();
    }

}
