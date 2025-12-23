package net.normlroyal.descendedangel.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class Keybinds {
    public static KeyMapping T3_SPEED_UP;
    public static KeyMapping T3_SPEED_DOWN;

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {

        T3_SPEED_UP = new KeyMapping(
                "key.descendedangel.t3_speed_up",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_EQUAL,
                "key.categories.descendedangel"
        );

        T3_SPEED_DOWN = new KeyMapping(
                "key.descendedangel.t3_speed_down",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS,
                "key.categories.descendedangel"
        );

        event.register(T3_SPEED_UP);
        event.register(T3_SPEED_DOWN);
    }
}
