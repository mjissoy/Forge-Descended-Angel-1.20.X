package net.normlroyal.descendedangel.client.animation;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.DescendedAngel;

import java.util.IdentityHashMap;
import java.util.Map;

public final class PrayerAnimationController {

    private static final Map<AbstractClientPlayer, ModifierLayer<IAnimation>> LAYERS =
            new IdentityHashMap<>();

    private PrayerAnimationController() {}

    public static ModifierLayer<IAnimation> getLayer(AbstractClientPlayer player) {
        ModifierLayer<IAnimation> layer = LAYERS.get(player);
        if (layer != null) {
            return layer;
        }

        layer = new ModifierLayer<>();
        PlayerAnimationAccess.getPlayerAnimLayer(player).addAnimLayer(1000, layer);
        LAYERS.put(player, layer);
        return layer;
    }

    public static void playPrayer(AbstractClientPlayer player) {
        var animation = PlayerAnimationRegistry.getAnimation(
                new ResourceLocation(DescendedAngel.MOD_ID, "prayer")
        );

        DescendedAngel.LOGGER.info("Live prayer animation lookup = {}", animation);

        if (animation == null) {
            DescendedAngel.LOGGER.error("Prayer animation lookup returned null");
            return;
        }

        DescendedAngel.LOGGER.info("Attempting to play prayer animation on {}", player.getGameProfile().getName());

        ModifierLayer<IAnimation> layer = getLayer(player);
        layer.setAnimation(new KeyframeAnimationPlayer(animation));

        DescendedAngel.LOGGER.info("Prayer animation applied to layer");
    }

    public static void clear(AbstractClientPlayer player) {
        LAYERS.remove(player);
    }
}