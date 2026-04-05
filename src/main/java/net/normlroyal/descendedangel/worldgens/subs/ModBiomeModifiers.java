package net.normlroyal.descendedangel.worldgens.subs;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.entity.ModEntities;

import java.util.List;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> SACRED_ORE =
            registerKey("sacred_ore");
    public static final ResourceKey<BiomeModifier> IMP_SPAWN =
            registerKey("imp_spawn");
    public static final ResourceKey<BiomeModifier> VOID_ANOMALY_SPAWN  =
            registerKey("void_anomaly_spawn");
    public static final ResourceKey<BiomeModifier> BLESSED_ROCK_PATCH  =
            registerKey("blessed_rock_patch");
    public static final ResourceKey<BiomeModifier> ASHEN_ROCK_PATCH  =
            registerKey("ashen_rock_patch");
    public static final ResourceKey<BiomeModifier> ANGEL_WEEPING_PATCH =
            registerKey("angel_weeping_patch");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(SACRED_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SACRED_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        context.register(BLESSED_ROCK_PATCH, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BLESSED_ROCK_PATCH_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        context.register(ASHEN_ROCK_PATCH, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ASHEN_ROCK_PATCH_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        context.register(ANGEL_WEEPING_PATCH, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ANGEL_WEEPING_PATCH_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(IMP_SPAWN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                List.of(new MobSpawnSettings.SpawnerData(
                        ModEntities.IMP.get(),
                        60, 2, 3
                ))
        ));

        context.register(VOID_ANOMALY_SPAWN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.DEEP_DARK)),
                List.of(new MobSpawnSettings.SpawnerData(
                        ModEntities.VOID_ANOMALY.get(),
                        5, 1, 2
                ))
        ));
    }


    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(
                ForgeRegistries.Keys.BIOME_MODIFIERS,
                new ResourceLocation(DescendedAngel.MOD_ID, name)
        );
    }
}
