package net.normlroyal.descendedangel.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.custom.enums.RingVariants;
import net.normlroyal.descendedangel.util.RingNecklaceUtils;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RingEvents {

    // Flame Ring Event
    public static final int FIRE_SECONDS = 4;
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;
        Entity src = event.getSource().getEntity();
        if (!(src instanceof Player player)) return;
        if (event.getSource().getDirectEntity() != player) return;
        if (!RingNecklaceUtils.hasRing(player, RingVariants.FLAME)) return;
        event.getEntity().setSecondsOnFire(FIRE_SECONDS);
    }

}
