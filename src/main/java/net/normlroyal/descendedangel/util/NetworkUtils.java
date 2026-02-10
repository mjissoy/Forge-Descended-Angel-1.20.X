package net.normlroyal.descendedangel.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.ClientMsgS2CPacket;

public class NetworkUtils {
    public static void actionbar(ServerPlayer sp, String text) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new ClientMsgS2CPacket(text));
    }
}