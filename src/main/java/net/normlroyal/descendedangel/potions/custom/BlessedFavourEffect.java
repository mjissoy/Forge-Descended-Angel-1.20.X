package net.normlroyal.descendedangel.potions.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class BlessedFavourEffect extends MobEffect {
    public BlessedFavourEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFF7C2);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            return;
        }

        List<MobEffect> harmfulEffects = entity.getActiveEffects()
                .stream()
                .map(MobEffectInstance::getEffect)
                .filter(effect -> effect.getCategory() == MobEffectCategory.HARMFUL)
                .toList();

        for (MobEffect effect : harmfulEffects) {
            entity.removeEffect(effect);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}