package net.normlroyal.descendedangel.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.haloabilities.ClientAbilityState;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.RequestAbilityCooldownC2SPacket;
import net.normlroyal.descendedangel.network.packets.UseHaloAbilityC2SPacket;
import net.normlroyal.descendedangel.util.HaloUtils;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientInputEvents {

    private static int lastTier = -1;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int tierNow = HaloUtils.getEquippedHaloTier(mc.player);
        if (tierNow != lastTier) {
            lastTier = tierNow;
            ClientAbilityState.reset();
        }

        // Cycle ability
        while (ClientKeybinds.OPEN_WHEEL.consumeClick()) {
            ClientAbilityState.cycle(mc.player);

            HaloAbility selected = ClientAbilityState.get(mc.player);
            if (selected != null) {
                ModNetwork.CHANNEL.sendToServer(new RequestAbilityCooldownC2SPacket(selected.ordinal()));

            }
        }

        // Use ability
        while (ClientKeybinds.USE_ABILITY.consumeClick()) {
            HaloAbility selected = ClientAbilityState.get(mc.player);
            if (selected == null) return;

            ModNetwork.CHANNEL.sendToServer(new UseHaloAbilityC2SPacket(selected.ordinal()));

        }
    }
}


