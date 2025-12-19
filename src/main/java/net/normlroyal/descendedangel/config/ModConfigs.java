package net.normlroyal.descendedangel.config;

import net.minecraftforge.common.ForgeConfigSpec;

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
        public final ForgeConfigSpec.DoubleValue voidTouchedSpawnChance;

        public final ForgeConfigSpec.DoubleValue HALO_HEALTH_BASE;
        public final ForgeConfigSpec.DoubleValue HALO_HEALTH_MULTI;
        public final ForgeConfigSpec.DoubleValue HALO_ARMOR_BASE;
        public final ForgeConfigSpec.DoubleValue HALO_ARMOR_MULTI;

        public final ForgeConfigSpec.DoubleValue HALO_EFFECTIVENESS_MULTIPLIER;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Balance Settings");

            HALO_HEAL_BONUS_PER_TIER = builder
                    .comment("Extra healing received per halo tier (e.g. 0.05 = +5% per tier).")
                    .defineInRange("haloHealingBonusPerTier", 0.05D, 0.0D, 10.0D);

            HALO_UNDEAD_DAMAGE_BONUS_PER_TIER = builder
                    .comment("Extra damage vs undead per halo tier (e.g. 0.10 = +10% per tier).")
                    .defineInRange("haloUndeadDamageBonusPerTier", 0.10D, 0.0D, 10.0D);

            voidTouchedSpawnChance = builder
                    .comment("Chance for hostile mobs to spawn with Void Touched (e.g. 0.05 = 5%)")
                    .defineInRange("voidTouchedSpawnChance", 0.05D, 0.0D, 1.0D);

            HALO_HEALTH_BASE = builder
                    .comment("Base health added by halos (before tier scaling). Default: 2")
                    .defineInRange("haloExtraHealthBase", 2.0D, 0.0D, 1024.0D);

            HALO_HEALTH_MULTI = builder
                    .comment("Multiplier for (tier - 1) * tier in the halo health formula. Default: 1.0")
                    .defineInRange("haloExtraHealthTermMultiplier", 1.0D, 0.0D, 1024.0D);

            HALO_ARMOR_BASE = builder
                    .comment("Base armor added by halos (before tier scaling). Default: 5")
                    .defineInRange("haloExtraArmorBase", 5.0D, 0.0D, 1024.0D);

            HALO_ARMOR_MULTI = builder
                    .comment("Multiplier for (tier - 1) * tier in the halo armor formula. Default: 1.0")
                    .defineInRange("haloExtraArmorTermMultiplier", 1.0D, 0.0D, 1024.0D);

            HALO_EFFECTIVENESS_MULTIPLIER = builder
                    .comment("Global multiplier for all halo stat bonuses. < 1.0 = weaker halos, > 1.0 = stronger.")
                    .defineInRange("haloEffectivenessMultiplier", 1.0D, 0.0D, 100.0D);

            builder.pop();
        }
    }
}
