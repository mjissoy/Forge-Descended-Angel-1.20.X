package net.normlroyal.descendedangel.worldgens.subs;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import net.normlroyal.descendedangel.DescendedAngel;

import java.util.Map;
import java.util.Optional;

public class ModStructures {

    public static final ResourceKey<Structure> ABOVEGROUND_TABLET = key("aboveground_tablet");
    public static final ResourceKey<Structure> ANCIENTCITY_TABLET = key("ancientcity_tablet");
    public static final ResourceKey<Structure> RUINED_CATHEDRAL = key("ruined_cathedral");

    private static ResourceKey<Structure> key(String id) {
        return ResourceKey.create(Registries.STRUCTURE,
                ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, id));
    }

    public static void bootstrap(BootstapContext<Structure> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> pools = ctx.lookup(Registries.TEMPLATE_POOL);

        TagKey<Biome> HAS_ABOVEGROUND_TABLET =
                TagKey.create(Registries.BIOME,
                        ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "has_aboveground_tablet"));
        TagKey<Biome> HAS_RUINED_CATHEDRAL =
                TagKey.create(Registries.BIOME,
                        ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "has_ruined_cathedral"));
        TagKey<Biome> HAS_ANCIENT_CITY =
                TagKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("has_structure/ancient_city"));

        ctx.register(ABOVEGROUND_TABLET,
                new JigsawStructure(
                        new Structure.StructureSettings(
                                biomes.getOrThrow(HAS_ABOVEGROUND_TABLET),
                                Map.of(),
                                net.minecraft.world.level.levelgen.GenerationStep.Decoration.SURFACE_STRUCTURES,
                                TerrainAdjustment.BEARD_THIN
                        ),
                        pools.getOrThrow(ModTemplatePools.ABOVEGROUND_TABLET_START),
                        Optional.empty(),
                        1,
                        ConstantHeight.of(VerticalAnchor.absolute(0)),
                        false,
                        Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                        80
                )
        );

        ctx.register(ANCIENTCITY_TABLET,
                new JigsawStructure(
                        new Structure.StructureSettings(
                                biomes.getOrThrow(HAS_ANCIENT_CITY),
                                Map.of(),
                                net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
                                TerrainAdjustment.BURY
                        ),
                        pools.getOrThrow(ModTemplatePools.ANCIENTCITY_TABLET_START),
                        Optional.empty(),
                        1,
                        ConstantHeight.of(VerticalAnchor.absolute(-50)),
                        false,
                        Optional.empty(),
                        80
                )
        );

        ctx.register(RUINED_CATHEDRAL,
                new JigsawStructure(
                        new Structure.StructureSettings(
                                biomes.getOrThrow(HAS_RUINED_CATHEDRAL),
                                Map.of(),
                                net.minecraft.world.level.levelgen.GenerationStep.Decoration.SURFACE_STRUCTURES,
                                TerrainAdjustment.BEARD_THIN
                        ),
                        pools.getOrThrow(ModTemplatePools.RUINED_CATHEDRAL_START),
                        Optional.empty(),
                        1,
                        ConstantHeight.of(VerticalAnchor.absolute(0)),
                        false,
                        Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                        16
                )
        );
    }
}
