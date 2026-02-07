package net.normlroyal.descendedangel.worldgens.subs;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import net.normlroyal.descendedangel.DescendedAngel;

import java.util.List;

public class ModStructureSets {

    public static final ResourceKey<StructureSet> ABOVEGROUND_TABLET = key("aboveground_tablet");
    public static final ResourceKey<StructureSet> ANCIENTCITY_TABLET = key("ancientcity_tablet");
    public static final ResourceKey<StructureSet> RUINED_CATHEDRAL = key("ruined_cathedral");

    private static ResourceKey<StructureSet> key(String id) {
        return ResourceKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, id));
    }

    public static void bootstrap(BootstapContext<StructureSet> ctx) {
        HolderGetter<Structure> structures = ctx.lookup(Registries.STRUCTURE);

        ctx.register(ABOVEGROUND_TABLET,
                new StructureSet(
                        List.of(StructureSet.entry(structures.getOrThrow(ModStructures.ABOVEGROUND_TABLET), 1)),
                        new RandomSpreadStructurePlacement(
                                32, 16, RandomSpreadType.LINEAR, 18472631
                        )
                )
        );

        ctx.register(ANCIENTCITY_TABLET,
                new StructureSet(
                        List.of(StructureSet.entry(structures.getOrThrow(ModStructures.ANCIENTCITY_TABLET), 1)),
                        new RandomSpreadStructurePlacement(
                                48, 24, RandomSpreadType.LINEAR, 91827364
                        )
                )
        );

        ctx.register(RUINED_CATHEDRAL,
                new StructureSet(
                        List.of(StructureSet.entry(structures.getOrThrow(ModStructures.RUINED_CATHEDRAL), 1)),
                        new RandomSpreadStructurePlacement(
                                36, 12, RandomSpreadType.LINEAR, 14285713
                        )
                )
        );
    }
}
