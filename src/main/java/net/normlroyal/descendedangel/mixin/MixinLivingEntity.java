package net.normlroyal.descendedangel.mixin;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.normlroyal.descendedangel.util.WingLogic;
import net.normlroyal.descendedangel.util.WingUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    public MixinLivingEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @ModifyArg(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;setSharedFlag(IZ)V"
            )
    )
    private boolean descendedangel$allowCurioGlide(boolean vanillaValue) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!(self instanceof Player player)) {
            return vanillaValue;
        }

        if (vanillaValue) {
            return true;
        }

        if (!this.getSharedFlag(7)) {
            return false;
        }

        var wings = WingUtils.getEquippedWings(player);
        if (wings.isEmpty()) {
            return false;
        }

        if (WingLogic.getWingTier(wings) != 1) {
            return false;
        }

        return !player.onGround()
                && !player.isPassenger()
                && !player.hasEffect(MobEffects.LEVITATION);
    }
}