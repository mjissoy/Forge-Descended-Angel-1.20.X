package net.normlroyal.descendedangel.events;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.item.custom.enums.NecklaceVariants;
import net.normlroyal.descendedangel.util.RingNecklaceUtils;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NecklaceEvents {

    private static final int DURATION = 440;
    private static final int REAPPLY_THRESHOLD = DURATION / 2;


    // Lightness Necklace Event
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;
        if (RingNecklaceUtils.hasNecklace(player, NecklaceVariants.LIGHTNESS)) {
            MobEffectInstance cur = player.getEffect(MobEffects.NIGHT_VISION);
            if (cur == null || cur.getDuration() <= REAPPLY_THRESHOLD) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.NIGHT_VISION,
                        DURATION,
                        0,
                        true,
                        false,
                        false
                ));
            }
        }
    }

    // Booster Necklace Event
    @SubscribeEvent
    public static void onDrinkPotionFinish(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (!(entity instanceof Player player)) return;

        ItemStack stack = event.getItem();
        if (!(stack.getItem() instanceof PotionItem)) return;

        if (!RingNecklaceUtils.hasNecklace(player, NecklaceVariants.BOOSTER)) return;

        for (MobEffectInstance inst : PotionUtils.getMobEffects(stack)) {
            if (inst.getEffect().isInstantenous()) continue;

            int newAmp = inst.getAmplifier() + 1;
            int newDur = inst.getDuration() * 2;

            player.addEffect(new MobEffectInstance(
                    inst.getEffect(),
                    newDur,
                    newAmp,
                    inst.isAmbient(),
                    inst.isVisible(),
                    inst.showIcon()
            ));
        }
    }



}