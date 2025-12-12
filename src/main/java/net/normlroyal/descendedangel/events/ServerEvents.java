package net.normlroyal.descendedangel.events;

import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.config.ModConfigs;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        var common = ModConfigs.COMMON;

        double healPerTier   = common.HALO_HEAL_BONUS_PER_TIER.get();
        double dmgPerTier    = common.HALO_UNDEAD_DAMAGE_BONUS_PER_TIER.get();
        double voidTearChance = common.VOID_TEAR_DROP_CHANCE.get();

        double healthBase    = common.HALO_HEALTH_BASE.get();
        double healthMulti   = common.HALO_HEALTH_MULTI.get();
        double armorBase     = common.HALO_ARMOR_BASE.get();
        double armorMulti    = common.HALO_ARMOR_MULTI.get();
        double globalMult    = common.HALO_EFFECTIVENESS_MULTIPLIER.get();

        DescendedAngel.LOGGER.info(
                "[Descended Angel] Loaded halo config: " +
                        "healPerTier={} , undeadDmgPerTier={} , voidTearChance={} , " +
                        "healthBase={} , healthMulti={} , armorBase={} , armorMulti={} , globalMult={}",
                healPerTier,
                dmgPerTier,
                voidTearChance,
                healthBase,
                healthMulti,
                armorBase,
                armorMulti,
                globalMult
        );
    }
}
