package net.normlroyal.descendedangel.worldgens;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.common.data.ExistingFileHelper;

import net.normlroyal.descendedangel.DescendedAngel;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagsProvider extends BiomeTagsProvider {

    public static final TagKey<Biome> HAS_ABOVEGROUND_TABLET =
            TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "has_aboveground_tablet"));
    public static final TagKey<Biome> HAS_NETHER_TABLET =
            TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "has_nether_tablet"));
    public static final TagKey<Biome> HAS_END_TABLET =
            TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "has_end_tablet"));
    public static final TagKey<Biome> HAS_RUINED_CATHEDRAL =
            TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "has_ruined_cathedral"));

    public ModBiomeTagsProvider(PackOutput output,
                                CompletableFuture<HolderLookup.Provider> lookupProvider,
                                ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, DescendedAngel.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(HAS_ABOVEGROUND_TABLET)
                .addTag(TagKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("has_structure/village_plains")))
                .addTag(TagKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("has_structure/village_savanna")))
                .addTag(TagKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("has_structure/village_snowy")))
                .addTag(TagKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("has_structure/village_taiga")));

        tag(HAS_RUINED_CATHEDRAL)
                .addOptional(ResourceLocation.withDefaultNamespace("plains"))
                .addOptional(ResourceLocation.withDefaultNamespace("sunflower_plains"))
                .addOptional(ResourceLocation.withDefaultNamespace("savanna"));

        tag(HAS_NETHER_TABLET)
                .addOptional(ResourceLocation.withDefaultNamespace("crimson_forest"))
                .addOptional(ResourceLocation.withDefaultNamespace("nether_wastes"))
                .addOptional(ResourceLocation.withDefaultNamespace("soul_sand_valley"))
                .addOptional(ResourceLocation.withDefaultNamespace("warped_forest"));

        tag(HAS_END_TABLET)
                .addOptional(ResourceLocation.withDefaultNamespace("end_barrens"))
                .addOptional(ResourceLocation.withDefaultNamespace("end_highlands"))
                .addOptional(ResourceLocation.withDefaultNamespace("end_midlands"));
    }
}
