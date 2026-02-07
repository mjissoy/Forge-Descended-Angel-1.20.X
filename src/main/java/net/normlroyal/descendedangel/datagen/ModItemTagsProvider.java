package net.normlroyal.descendedangel.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.ModItems;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

    public static final TagKey<Item> HALOS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "halos"));

    public static final TagKey<Item> WINGS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "wings"));

    public static final TagKey<Item> RINGS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "rings"));

    public static final TagKey<Item> NECKLACES =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "necklaces"));

    public static final TagKey<Item> CURIOS_HALOS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", "halo"));

    public static final TagKey<Item> CURIOS_WINGS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", "wing"));

    public static final TagKey<Item> CURIOS_RINGS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", "ring"));

    public static final TagKey<Item> CURIOS_NECKLACES =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", "necklace"));

    public ModItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, blockTags, DescendedAngel.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        // Item Tags
        this.tag(HALOS).add(
                ModItems.HALO_T1.get(),
                ModItems.HALO_T2.get(),
                ModItems.HALO_T3.get(),
                ModItems.HALO_T4.get(),
                ModItems.HALO_T5.get(),
                ModItems.HALO_T6.get(),
                ModItems.HALO_T7.get(),
                ModItems.HALO_T8.get(),
                ModItems.HALO_T9.get()
        );

        this.tag(WINGS);

        this.tag(RINGS).add(
                ModItems.HOLY_RING.get(),
                ModItems.FLAME_RING.get(),
                ModItems.CLOUD_RING.get()
        );

        this.tag(NECKLACES).add(
                ModItems.HOLY_NECKLACE.get(),
                ModItems.MESSENGER_PENDANT.get(),
                ModItems.LIGHTNESS_NECKLACE.get()
        );

        // Curios Tags
        this.tag(CURIOS_HALOS).addTag(HALOS);
        this.tag(CURIOS_WINGS).addTag(WINGS);
        this.tag(CURIOS_RINGS).addTag(RINGS);
        this.tag(CURIOS_NECKLACES).addTag(NECKLACES);




    }
}
