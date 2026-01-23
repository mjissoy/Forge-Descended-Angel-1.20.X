package net.normlroyal.descendedangel.events;

import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.custom.writings.SacredWritReloadListener;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.writs.SyncWritDisplaysS2CPacket;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WritDisplaySyncEvents {

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        var packet = new SyncWritDisplaysS2CPacket(SacredWritReloadListener.displays());

        if (event.getPlayer() != null) {
            ModNetwork.CHANNEL.sendTo(packet, event.getPlayer().connection.connection, net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
        } else {
            ModNetwork.CHANNEL.send(net.minecraftforge.network.PacketDistributor.ALL.noArg(), packet);
        }
    }
}
