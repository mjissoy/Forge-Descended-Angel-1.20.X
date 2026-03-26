package net.normlroyal.descendedangel.worldgens.subs;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

import net.normlroyal.descendedangel.DescendedAngel;

import java.util.List;

public class ModTemplatePools {

    public static final ResourceKey<StructureTemplatePool> ABOVEGROUND_TABLET_START =
            key("aboveground_tablet/start_pool");
    public static final ResourceKey<StructureTemplatePool> ANCIENTCITY_TABLET_START =
            key("ancientcity_tablet/start_pool");
    public static final ResourceKey<StructureTemplatePool>  NETHER_TABLET_START =
            key("nether_tablet/start_pool");
    public static final ResourceKey<StructureTemplatePool> END_TABLET_START =
            key("end_tablet/start_pool");
    public static final ResourceKey<StructureTemplatePool> RUINED_CATHEDRAL_START =
            key("ruined_cathedral/start_pool");

    private static ResourceKey<StructureTemplatePool> key(String id) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(DescendedAngel.MOD_ID, id));
    }

    public static void bootstrap(BootstapContext<StructureTemplatePool> ctx) {
        HolderGetter<StructureTemplatePool> pools = ctx.lookup(Registries.TEMPLATE_POOL);

        var empty = pools.getOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation("minecraft", "empty")));

        ctx.register(ABOVEGROUND_TABLET_START,
                new StructureTemplatePool(
                        empty,
                        List.of(Pair.of(
                                StructurePoolElement.single(new ResourceLocation(DescendedAngel.MOD_ID, "aboveground_tablet").toString())
                                        .apply(StructureTemplatePool.Projection.RIGID),
                                        1
                        )
                )
        ));

        ctx.register(ANCIENTCITY_TABLET_START,
                new StructureTemplatePool(
                        empty,
                        List.of(Pair.of(
                                        StructurePoolElement.single(new ResourceLocation(DescendedAngel.MOD_ID, "ancientcity_tablet").toString())
                                                .apply(StructureTemplatePool.Projection.RIGID),
                                        1
                                )
                        )
                ));

        ctx.register(NETHER_TABLET_START,
                new StructureTemplatePool(
                        empty,
                        List.of(Pair.of(
                                StructurePoolElement.single(new ResourceLocation(DescendedAngel.MOD_ID, "nether_tablet").toString())
                                        .apply(StructureTemplatePool.Projection.RIGID),
                                        1
                        )
                )
        ));

        ctx.register(END_TABLET_START,
                new StructureTemplatePool(
                        empty,
                        List.of(Pair.of(
                                        StructurePoolElement.single(new ResourceLocation(DescendedAngel.MOD_ID, "end_tablet").toString())
                                                .apply(StructureTemplatePool.Projection.RIGID),
                                        1
                                )
                        )
                ));

        ctx.register(RUINED_CATHEDRAL_START,
                new StructureTemplatePool(
                        empty,
                        List.of(Pair.of(
                                        StructurePoolElement.single(new ResourceLocation(DescendedAngel.MOD_ID, "ruined_cathedral").toString())
                                                .apply(StructureTemplatePool.Projection.RIGID),
                                        1
                                )
                        )
                ));
    }
}
