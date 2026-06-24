package net.normlroyal.descendedangel.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.content.dimension.VoidPocketManager;
import net.normlroyal.descendedangel.content.entity.VoidAnomalyEntity;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoidPocketEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preventVoidPocketDeath(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!VoidPocketManager.isVoidPocket(player.level())) {
            return;
        }

        if (player.getHealth() - event.getAmount() > 0.0F) {
            return;
        }

        event.setCanceled(true);
        player.setHealth(1.0F);
        VoidPocketManager.ejectPlayer(
                player,
                Component.literal("The void refuses your death and casts you out.").withStyle(ChatFormatting.DARK_PURPLE)
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preventVoidPocketDeathFallback(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!VoidPocketManager.isVoidPocket(player.level())) {
            return;
        }

        event.setCanceled(true);
        player.setHealth(1.0F);
        VoidPocketManager.ejectPlayer(
                player,
                Component.literal("The void refuses your death and casts you out.").withStyle(ChatFormatting.DARK_PURPLE)
        );
    }

    @SubscribeEvent
    public static void onVoidAnomalyKilled(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof VoidAnomalyEntity anomaly)) {
            return;
        }

        if (!(anomaly.level() instanceof ServerLevel level) || !VoidPocketManager.isVoidPocket(level)) {
            return;
        }

        Entity killer = event.getSource().getEntity();
        if (killer instanceof ServerPlayer player) {
            VoidPocketManager.recordAnomalyKill(level, anomaly.blockPosition(), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        if (event.player instanceof ServerPlayer player) {
            VoidPocketManager.tickPlayer(player);
        }
    }
}
