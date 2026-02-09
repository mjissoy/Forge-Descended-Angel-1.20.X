package net.normlroyal.descendedangel.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;

import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, value = net.minecraftforge.api.distmarker.Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientKeybinds {
    public static KeyMapping OPEN_WHEEL;
    public static KeyMapping USE_ABILITY;

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        OPEN_WHEEL = new KeyMapping(
                "key.descendedangel.open_halo_wheel",
                GLFW.GLFW_KEY_R,
                "key.categories.descendedangel"
        );

        USE_ABILITY = new KeyMapping(
                "key.descendedangel.use_halo_ability",
                GLFW.GLFW_KEY_V,
                "key.categories.descendedangel"
        );

        event.register(OPEN_WHEEL);
        event.register(USE_ABILITY);
    }
}
