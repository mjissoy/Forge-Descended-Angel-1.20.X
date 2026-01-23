package net.normlroyal.descendedangel.events;

import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.recipe.ModRecipeTypes;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RecipeDebugEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        var server = event.getServer();
        int count = server.getRecipeManager().getAllRecipesFor(ModRecipeTypes.ALTAR_RITE.get()).size();
        LOGGER.info("[DescendedAngel] Altar rite recipes loaded: {}", count);
    }
}
