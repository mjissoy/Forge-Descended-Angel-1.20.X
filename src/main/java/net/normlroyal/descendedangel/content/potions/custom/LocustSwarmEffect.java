package net.normlroyal.descendedangel.content.potions.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class LocustSwarmEffect extends MobEffect {
    public LocustSwarmEffect() {
        super(MobEffectCategory.HARMFUL, 0x4A3A19);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level();

        if (level.isClientSide) {
            return;
        }

        if (entity.tickCount % 20 == 0) {
            entity.hurt(entity.damageSources().magic(), 1.0F + amplifier);
        }

        if (entity.tickCount % 40 == 0) {
            double radius = 2.5D + amplifier;
            AABB area = entity.getBoundingBox().inflate(radius);

            List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(
                    LivingEntity.class,
                    area,
                    target -> target != entity
                            && target.isAlive()
                            && !target.hasEffect(this)
            );

            for (LivingEntity target : nearbyEntities) {
                target.addEffect(new MobEffectInstance(
                        this,
                        20 * 12,
                        amplifier,
                        false,
                        true,
                        true
                ));
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}