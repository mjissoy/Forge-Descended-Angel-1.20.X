package net.normlroyal.descendedangel.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.client.screen.AltarScreen;
import net.normlroyal.descendedangel.menu.ModMenus;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.gui.screens.MenuScreens.register(
                    ModMenus.ALTAR_MENU.get(),
                    AltarScreen::new
            );
        });
    }
}
