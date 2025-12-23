package net.normlroyal.descendedangel.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.network.AdjustT3SpeedC2SPacket;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.RequestT1GlideC2SPacket;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientKeyEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (Minecraft.getInstance().options.keyJump.consumeClick()) {
            var p = Minecraft.getInstance().player;
            if (p == null) break;

            if (p.onGround()) continue;
            if (p.isFallFlying()) continue;
            if (p.getAbilities().flying) continue;

            if (p.getDeltaMovement().y >= 0.0D) continue;

            if (p.fallDistance < 0.35F) continue;

            ModNetwork.CHANNEL.sendToServer(new RequestT1GlideC2SPacket());
        }


        while (Keybinds.T3_SPEED_UP.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new AdjustT3SpeedC2SPacket(+1));
        }
        while (Keybinds.T3_SPEED_DOWN.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new AdjustT3SpeedC2SPacket(-1));
        }
    }

}