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
    public static KeyMapping FLIGHT_BOOST;
    public static KeyMapping FLIGHT_DESCEND;

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

        FLIGHT_BOOST = new KeyMapping(
                "key.descendedangel.flight_boost",
                GLFW.GLFW_KEY_LEFT_CONTROL,
                "key.categories.descendedangel"
        );

        FLIGHT_DESCEND = new KeyMapping(
                "key.descendedangel.flight_descend",
                GLFW.GLFW_KEY_LEFT_SHIFT,
                "key.categories.descendedangel"
        );

        event.register(OPEN_WHEEL);
        event.register(USE_ABILITY);
        event.register(FLIGHT_BOOST);
        event.register(FLIGHT_DESCEND);
    }
}
