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
        public final ForgeConfigSpec.DoubleValue VOID_TEAR_DROP_CHANCE;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Balance Settings");

            HALO_HEAL_BONUS_PER_TIER = builder
                    .comment("Extra healing received per halo tier (e.g. 0.05 = +5% per tier).")
                    .defineInRange("haloHealingBonusPerTier", 0.05D, 0.0D, 10.0D);

            HALO_UNDEAD_DAMAGE_BONUS_PER_TIER = builder
                    .comment("Extra damage vs undead per halo tier (e.g. 0.10 = +10% per tier).")
                    .defineInRange("haloUndeadDamageBonusPerTier", 0.10D, 0.0D, 10.0D);

            VOID_TEAR_DROP_CHANCE = builder
                    .comment("Chance for hostile mobs killed by a player to drop a Void Tear (e.g. 0.01 = 1%).")
                    .defineInRange("voidTearDropChance", 0.01D, 0.0D, 1.0D);

            builder.pop();
        }
    }
}
