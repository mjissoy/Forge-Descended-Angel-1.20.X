package net.normlroyal.descendedangel.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ModConfigs {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static class Common {

        public final ForgeConfigSpec.DoubleValue HALO_HEAL_BONUS_PER_TIER;
        public final ForgeConfigSpec.DoubleValue HALO_UNDEAD_DAMAGE_BONUS_PER_TIER;

        public final ForgeConfigSpec.DoubleValue HALO_HEALTH_BASE;
        public final ForgeConfigSpec.DoubleValue HALO_HEALTH_MULTI;
        public final ForgeConfigSpec.DoubleValue HALO_ARMOR_BASE;
        public final ForgeConfigSpec.DoubleValue HALO_ARMOR_MULTI;
        public final ForgeConfigSpec.DoubleValue HALO_EFFECTIVENESS_MULTIPLIER;

        public final ForgeConfigSpec.DoubleValue voidTouchedSpawnChance;
        public final ForgeConfigSpec.DoubleValue spatialCoreEndermanDropChance;
        public final ForgeConfigSpec.DoubleValue spatialCoreEndCityChestChance;

        public final ForgeConfigSpec.DoubleValue CloudRing_MOVSpeedBoost;
        public final ForgeConfigSpec.DoubleValue CloudRing_ATKSpeedBoost;
        public final ForgeConfigSpec.DoubleValue Rings_Effectiveness;

        public final ForgeConfigSpec.DoubleValue MessengerPendant_LuckBoost;
        public final ForgeConfigSpec.DoubleValue Necklaces_Effectiveness;

        public final ForgeConfigSpec.BooleanValue ENABLE_SACRED_WRITINGS;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCKED_WRITINGS;

        public final ForgeConfigSpec.IntValue WEATHER_DURATION_TICKS;
        public final ForgeConfigSpec.IntValue ENTITY_SPAWN_COUNT;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Config Options");
            builder.pop();

            builder.push("--Halo Settings--");
            HALO_HEAL_BONUS_PER_TIER = builder
                    .comment("Extra healing received per halo tier (e.g. 0.05 = +5% per tier).")
                    .defineInRange("haloHealingBonusPerTier", 0.05D, 0.0D, 10.0D);
            HALO_UNDEAD_DAMAGE_BONUS_PER_TIER = builder
                    .comment("Extra damage vs undead per halo tier (e.g. 0.10 = +10% per tier).")
                    .defineInRange("haloUndeadDamageBonusPerTier", 0.10D, 0.0D, 10.0D);
            HALO_HEALTH_BASE = builder
                    .comment("Base health added by halos (before tier scaling). Default: 2")
                    .defineInRange("haloExtraHealthBase", 2.0D, 0.0D, 1024.0D);
            HALO_HEALTH_MULTI = builder
                    .comment("Multiplier for formula [(tier - 1) * tier] in the halo health calc. Default: 1.0")
                    .defineInRange("haloExtraHealthTermMultiplier", 1.0D, 0.0D, 1024.0D);
            HALO_ARMOR_BASE = builder
                    .comment("Base armor added by halos (before tier scaling). Default: 5")
                    .defineInRange("haloExtraArmorBase", 5.0D, 0.0D, 1024.0D);
            HALO_ARMOR_MULTI = builder
                    .comment("Multiplier for formula [(tier - 1) * tier] in the halo armor calc. Default: 1.0")
                    .defineInRange("haloExtraArmorTermMultiplier", 1.0D, 0.0D, 1024.0D);
            HALO_EFFECTIVENESS_MULTIPLIER = builder
                    .comment("Global multiplier for all halo stat bonuses. < 1.0 = weaker halos, > 1.0 = stronger.")
                    .defineInRange("haloEffectivenessMultiplier", 1.0D, 0.0D, 100.0D);
            builder.pop();

            builder.push("--Void Settings--");
            voidTouchedSpawnChance = builder
                    .comment("Chance for hostile mobs to spawn with Void Touched (e.g. 0.05 = 5%)")
                    .defineInRange("voidTouchedSpawnChance", 0.05D, 0.0D, 1.0D);
            spatialCoreEndermanDropChance = builder
                    .comment("Chance for Endermen to drop a Spatial Core on death. Default: 6%")
                    .defineInRange("enderman_drop_chance", 0.06, 0.0, 1.0);
            spatialCoreEndCityChestChance = builder
                    .comment("Chance for a Spatial Core to appear in End City chests. Default: 25%")
                    .defineInRange("end_city_chest_chance", 0.25, 0.0, 1.0);
            builder.pop();

            builder.push("--Ring Settings--");
            CloudRing_ATKSpeedBoost = builder
                    .comment("The increase in attack speed by the Storm Ring (e.g. 0.20 = +20%).")
                    .defineInRange("cloudRingATKSpeed_multi", 0.20D, 0.0D, 10.0D);
            CloudRing_MOVSpeedBoost = builder
                    .comment("The increase in movement speed by the Storm Ring (e.g. 0.30 = +30%).")
                    .defineInRange("cloudRingMOVSpeed_multi", 0.30D, 0.0D, 10.0D);
            Rings_Effectiveness = builder
                    .comment("Global multiplier for all Ring bonuses. < 1.0 = weaker ring, > 1.0 = stronger.")
                    .defineInRange("ringEffectivenessMultiplier", 1.0D, 0.0D, 100.0D);
            builder.pop();

            builder.push("--Necklace Settings--");
            MessengerPendant_LuckBoost = builder
                    .comment("The increase in luck by the Messenger Pendant (e.g. 0.50 = +50%).")
                    .defineInRange("messengerPendant_luckboost", 0.50D, 0.0D, 10.0D);
            Necklaces_Effectiveness = builder
                    .comment("Global multiplier for all Necklace bonuses. < 1.0 = weaker necklace, > 1.0 = stronger.")
                    .defineInRange("necklaceEffectivenessMultiplier", 1.0D, 0.0D, 100.0D);

            builder.pop();

            builder.push("--Sacred Writing Settings--");
            ENABLE_SACRED_WRITINGS = builder
                    .comment("Enable/disable Sacred Writings")
                    .define("enableSacredWritings", true);
            BLOCKED_WRITINGS = builder
                    .comment("List of blocked writ ids (e.g. \"descendedangel:spawn_villagers\")")
                    .defineListAllowEmpty("blockedWritings", List.of(), o -> o instanceof String);
            WEATHER_DURATION_TICKS = builder
                    .comment("Duration of weather writings")
                    .defineInRange("weather_writ_dura", 24000, 20, 1200000);
            ENTITY_SPAWN_COUNT = builder
                    .comment("Count of entities spawned")
                    .defineInRange("entity_writ_spawn", 10, 1, 50);


            builder.pop();

        }
    }
}
