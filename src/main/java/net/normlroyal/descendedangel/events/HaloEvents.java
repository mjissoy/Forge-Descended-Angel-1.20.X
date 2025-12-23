package net.normlroyal.descendedangel.events;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.util.HaloUtils;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HaloEvents {

    // Increase Damage against undead while wearing halo
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        LivingEntity target = event.getEntity();
        if (target.getMobType() != MobType.UNDEAD) return;


        int tier = HaloUtils.getEquippedHaloTier(player);
        if (tier <= 0) return;

        double dmgPerTier = ModConfigs.COMMON.HALO_UNDEAD_DAMAGE_BONUS_PER_TIER.get();
        float bonusMultiplier = 1.0F + (float)(dmgPerTier * tier);

        event.setAmount(event.getAmount() * bonusMultiplier);
    }

    // Increase Healing while wearing Halo
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }

        int tier = HaloUtils.getEquippedHaloTier(player);
        if (tier <= 0) return;

        double healPerTier = ModConfigs.COMMON.HALO_HEAL_BONUS_PER_TIER.get();
        float healMultiplier = 1.0F + (float)(healPerTier * tier);

        float original = event.getAmount();
        event.setAmount(original * healMultiplier);
    }

}