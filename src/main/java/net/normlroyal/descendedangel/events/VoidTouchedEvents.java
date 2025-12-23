package net.normlroyal.descendedangel.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.item.ModItems;
import net.normlroyal.descendedangel.particle.ModParticles;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoidTouchedEvents {

    // Spawn mobs in Void_Touched
    public static final String VOID_TOUCHED = "descendedangel:void_touched";

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getEntity() instanceof Monster mob)) return;

        var tag = mob.getPersistentData();
        if (tag.getBoolean(VOID_TOUCHED)) return;

        double chance = ModConfigs.COMMON.voidTouchedSpawnChance.get();
        if (mob.getRandom().nextDouble() < chance) {
            tag.putBoolean(VOID_TOUCHED, true);
        }
    }

    // Void_touched Particles
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity e = event.getEntity();

        if (!(e.level() instanceof ServerLevel level)) return;
        if (!(e instanceof Monster)) return;

        if (!e.getPersistentData().getBoolean(VOID_TOUCHED)) return;
        if ((e.tickCount % 20) != 0) return;

        level.sendParticles(ModParticles.VOID_TOUCHED.get(),
                e.getX(), e.getY() + 1.0, e.getZ(),
                5, 0.25, 0.4, 0.25, 0.005);
    }

    // Void Tear drops from void_touched mobs
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity dead = event.getEntity();

        if (!(dead.level() instanceof ServerLevel level)) return;
        if (!(dead instanceof Monster)) return;
        if (!dead.getPersistentData().getBoolean(VOID_TOUCHED)) return;

        int looting = 0;
        Entity src = event.getSource().getEntity();

        if (src instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            looting = EnchantmentHelper.getEnchantments(weapon)
                    .getOrDefault(Enchantments.MOB_LOOTING, 0);
        }

        int count = 1 + dead.getRandom().nextInt(looting + 1);
        ItemStack drop = new ItemStack(ModItems.VOIDTEAR.get(), count);

        event.getDrops().add(new ItemEntity(level, dead.getX(), dead.getY(), dead.getZ(), drop));
    }

}