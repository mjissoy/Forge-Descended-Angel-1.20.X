package net.normlroyal.descendedangel.potions.custom;

import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class DivineGraceEffect extends InstantenousMobEffect {
    public DivineGraceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFF4B8);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        healDivinely(entity, amplifier, 1.0D);
    }

    @Override
    public void applyInstantenousEffect(
            @Nullable Entity source,
            @Nullable Entity indirectSource,
            LivingEntity entity,
            int amplifier,
            double healthMultiplier
    ) {
        healDivinely(entity, amplifier, healthMultiplier);
    }

    private void healDivinely(LivingEntity entity, int amplifier, double healthMultiplier) {
        float maxHealth = entity.getMaxHealth();

        float percentHeal = switch (amplifier) {
            case 0 -> 0.60F; // Divine Grace I: 60% max health
            case 1 -> 0.85F; // Divine Grace II: 85% max health
            default -> 1.00F; // Higher amplifiers: full heal
        };

        float flatBonus = 8.0F + amplifier * 4.0F;
        float healAmount = (maxHealth * percentHeal + flatBonus) * (float) healthMultiplier;

        entity.heal(healAmount);
    }
}