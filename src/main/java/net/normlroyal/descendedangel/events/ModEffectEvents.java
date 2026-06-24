package net.normlroyal.descendedangel.events;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.potions.ModEffects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEffectEvents {
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity droppedEntity = event.getEntity();

        if (droppedEntity instanceof Player) {
            return;
        }

        Entity sourceEntity = event.getSource().getEntity();

        if (!(sourceEntity instanceof LivingEntity killer)) {
            return;
        }

        MobEffectInstance providence = killer.getEffect(ModEffects.PROVIDENCE.get());

        if (providence == null) {
            return;
        }

        Collection<ItemEntity> originalDrops = event.getDrops();

        if (originalDrops.isEmpty()) {
            return;
        }

        Level level = droppedEntity.level();
        RandomSource random = level.random;

        int amplifier = providence.getAmplifier();
        float bonusMultiplier = 0.5F + amplifier * 0.25F;

        List<ItemEntity> bonusDrops = new ArrayList<>();

        for (ItemEntity drop : originalDrops) {
            ItemStack originalStack = drop.getItem();

            if (originalStack.isEmpty()) {
                continue;
            }

            int bonusCount = getBonusCount(originalStack.getCount(), bonusMultiplier, random);

            if (bonusCount <= 0) {
                continue;
            }

            ItemStack bonusStack = originalStack.copy();
            bonusStack.setCount(Math.min(bonusCount, bonusStack.getMaxStackSize()));

            ItemEntity bonusDrop = new ItemEntity(
                    level,
                    drop.getX(),
                    drop.getY(),
                    drop.getZ(),
                    bonusStack
            );

            bonusDrop.setDeltaMovement(drop.getDeltaMovement());
            bonusDrops.add(bonusDrop);
        }

        originalDrops.addAll(bonusDrops);
    }

    private static int getBonusCount(int originalCount, float multiplier, RandomSource random) {
        float rawBonus = originalCount * multiplier;
        int guaranteedBonus = (int) rawBonus;
        float fractionalBonus = rawBonus - guaranteedBonus;

        if (random.nextFloat() < fractionalBonus) {
            guaranteedBonus++;
        }

        return guaranteedBonus;
    }

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();

        if (!entity.hasEffect(ModEffects.BLESSED_FAVOUR.get())) {
            return;
        }

        MobEffectInstance incomingEffect = event.getEffectInstance();

        if (incomingEffect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            event.setResult(Event.Result.DENY);
        }
    }
}