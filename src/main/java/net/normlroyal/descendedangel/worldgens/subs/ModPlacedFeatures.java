package net.normlroyal.descendedangel.worldgens.subs;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.placement.*;

import net.normlroyal.descendedangel.DescendedAngel;

import java.util.List;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> SACRED_ORE =
            ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "sacred_ore"));

    public static final ResourceKey<PlacedFeature> BLESSED_ROCK_PATCH_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "blessed_rock_patch_placed"));

    public static final ResourceKey<PlacedFeature> ASHEN_ROCK_PATCH_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "ashen_rock_patch_placed"));

    public static void bootstrap(BootstapContext<PlacedFeature> ctx) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = ctx.lookup(Registries.CONFIGURED_FEATURE);

        // Sacred Ore
        Holder<ConfiguredFeature<?, ?>> sacredOre = configured.getOrThrow(ModConfiguredFeatures.SACRED_ORE);
        List<PlacementModifier> modifiers = List.of(
                RarityFilter.onAverageOnceEvery(3),
                CountPlacement.of(1),
                InSquarePlacement.spread(),
                HeightRangePlacement.of(
                        TrapezoidHeight.of(
                        VerticalAnchor.absolute(-64),
                        VerticalAnchor.absolute(-45)
                )
                ),
                BiomeFilter.biome()
        );
        ctx.register(SACRED_ORE, new PlacedFeature(sacredOre, modifiers));

        // Blessed Rock
        int BlessperChunk = 2;
        var Blessheight = HeightRangePlacement.uniform(
                VerticalAnchor.absolute(0),
                VerticalAnchor.absolute(128)
        );
        ctx.register(BLESSED_ROCK_PATCH_PLACED,
                new PlacedFeature(
                        configured.getOrThrow(ModConfiguredFeatures.BLESSED_ROCK_PATCH),
                        List.of(
                                CountPlacement.of(BlessperChunk),
                                InSquarePlacement.spread(),
                                Blessheight,
                                BiomeFilter.biome()
                        )
                )
        );

        // Ashen Rock
        int AshperChunk = 2;
        var Ashheight = HeightRangePlacement.uniform(
                VerticalAnchor.absolute(0),
                VerticalAnchor.absolute(128)
        );
        ctx.register(ASHEN_ROCK_PATCH_PLACED,
                new PlacedFeature(
                        configured.getOrThrow(ModConfiguredFeatures.ASHEN_ROCK_PATCH),
                        List.of(
                                CountPlacement.of(AshperChunk),
                                InSquarePlacement.spread(),
                                Ashheight,
                                BiomeFilter.biome()
                        )
                )
        );

    }
}
