package net.normlroyal.descendedangel.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.custom.MarkOfCainItem;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.PlayMarkActivationS2CPacket;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID)
public class MarkOfCainEvents {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;
        if (player.isCreative() || player.isSpectator()) return;

        if (event.getSource().is(DamageTypes.GENERIC_KILL)) return;

        if (event.getAmount() < player.getHealth()) return;

        ItemStack mark = findChargedMark(player);
        if (mark.isEmpty()) return;

        if (!(mark.getItem() instanceof MarkOfCainItem markItem)) return;
        if (!markItem.canPopTotem(mark)) return;
        if (!markItem.consumeOneUse(mark)) return;

        event.setCanceled(true);

        float reviveHealth = Math.max(1.0F, player.getMaxHealth() * 0.25F);
        player.setHealth(reviveHealth);
        player.clearFire();
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));

        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );

        ItemStack displayStack = mark.copy();
        displayStack.setCount(1);
        ModNetwork.sendToPlayer(new PlayMarkActivationS2CPacket(displayStack), player);
    }

    private static ItemStack findChargedMark(ServerPlayer player) {
        ItemStack fallback = ItemStack.EMPTY;

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.getItem() instanceof MarkOfCainItem markItem && markItem.canPopTotem(stack)) {
                if (markItem.getMaxUses(stack) > 1) return stack;
                if (fallback.isEmpty()) fallback = stack;
            }
        }

        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof MarkOfCainItem markItem && markItem.canPopTotem(stack)) {
                if (markItem.getMaxUses(stack) > 1) return stack;
                if (fallback.isEmpty()) fallback = stack;
            }
        }

        return fallback;
    }
}