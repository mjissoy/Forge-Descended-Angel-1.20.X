package net.normlroyal.descendedangel.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.client.render.HaloAbilityHudOverlay;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientOverlays {

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(
                VanillaGuiOverlay.HOTBAR.id(),
                "halo_ability_icon",
                HaloAbilityHudOverlay.OVERLAY
        );
    }
}