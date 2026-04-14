package net.normlroyal.descendedangel.client.animation;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.DescendedAngel;

public final class ModPlayerAnimations {

    private static KeyframeAnimation prayerAnimation;

    private ModPlayerAnimations() {}

    public static void load() {
        try {
            ResourceLocation loc = new ResourceLocation(
                    DescendedAngel.MOD_ID,
                    "prayer"
            );

            prayerAnimation = PlayerAnimationRegistry.getAnimation(loc);

            DescendedAngel.LOGGER.info("Tried to load prayer animation with id {}", loc);
            DescendedAngel.LOGGER.info("Prayer animation object = {}", prayerAnimation);

            if (prayerAnimation == null) {
                DescendedAngel.LOGGER.error("Prayer animation was null: {}", loc);
            } else {
                DescendedAngel.LOGGER.info("Loaded player animation {}", loc);
            }
        } catch (Exception e) {
            DescendedAngel.LOGGER.error("Failed to load prayer animation", e);
        }
    }

    public static KeyframeAnimation getPrayer() {
        return prayerAnimation;
    }
}