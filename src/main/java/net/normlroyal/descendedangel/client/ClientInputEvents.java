package net.normlroyal.descendedangel.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.haloabilities.ClientAbilityState;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.UseHaloAbilityC2SPacket;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientInputEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Cycle ability
        while (ClientKeybinds.OPEN_WHEEL.consumeClick()) {
            ClientAbilityState.cycle();

            mc.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            "Selected: " + ClientAbilityState.get().name()
                    ),
                    true
            );
        }

        // Use ability
        while (ClientKeybinds.USE_ABILITY.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(
                    new UseHaloAbilityC2SPacket(
                            ClientAbilityState.get().ordinal()
                    )
            );
        }
    }
}
