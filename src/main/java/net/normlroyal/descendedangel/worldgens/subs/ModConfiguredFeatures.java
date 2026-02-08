package net.normlroyal.descendedangel.worldgens.subs;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.block.ModBlocks;

import java.util.List;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> SACRED_ORE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "sacred_ore"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> BLESSED_ROCK_PATCH =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "blessed_rock_patch"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> ASHEN_ROCK_PATCH =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "ashen_rock_patch"));


    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        // Sacred Ore
        OreConfiguration.TargetBlockState sacredtarget = OreConfiguration.target(
                deepslateReplaceables,
                ModBlocks.SACRED_ORE.get().defaultBlockState()
        );
        OreConfiguration sacredconfig = new OreConfiguration(List.of(sacredtarget), 4, 0.5F);
        ctx.register(SACRED_ORE, new ConfiguredFeature<>(Feature.ORE, sacredconfig));

        // Blessed Rock
        List<OreConfiguration.TargetBlockState> blessedtargets = List.of(
                OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                        ModBlocks.BLESSED_ROCK.get().defaultBlockState())
        );

        int blessedblobSize = 33;
        ctx.register(BLESSED_ROCK_PATCH,
                new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(blessedtargets, blessedblobSize)));

        // Ashen Rock
        List<OreConfiguration.TargetBlockState> ashentargets = List.of(
                OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES),
                        ModBlocks.ASHEN_ROCK.get().defaultBlockState())
        );

        int ashenblobSize = 33;
        ctx.register(ASHEN_ROCK_PATCH,
                new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ashentargets, ashenblobSize)));
    }
}
