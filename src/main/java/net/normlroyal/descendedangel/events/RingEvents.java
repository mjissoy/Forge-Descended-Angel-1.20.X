package net.normlroyal.descendedangel.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.custom.enums.RingVariants;
import net.normlroyal.descendedangel.util.HaloUtils;
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

    // Cure Ring Event
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        if (!RingNecklaceUtils.hasRing(player, RingVariants.CURE)) return;

        int tier = HaloUtils.getEquippedHaloTier(player);
        if (tier <= 0) return;

        int interval = Math.max(10, 100 - tier * 10);

        var tag = player.getPersistentData();
        String key = "descendedangel_regen_ring_ticks";

        int ticks = tag.getInt(key) + 1;
        if (ticks >= interval) {
            ticks = 0;

            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(1.0F);
            }
        }
        tag.putInt(key, ticks);

        String cleanseKey = "descendedangel_cure_ring_cleanse_ticks";
        int cleanseTicks = tag.getInt(cleanseKey) + 1;

        if (cleanseTicks >= 100) {
            cleanseTicks = 0;

            var toRemove = new java.util.ArrayList<>(player.getActiveEffects());
            for (var inst : toRemove) {
                if (!inst.getEffect().isBeneficial()) {
                    player.removeEffect(inst.getEffect());
                }
            }
        }
        tag.putInt(cleanseKey, cleanseTicks);
    }

}
