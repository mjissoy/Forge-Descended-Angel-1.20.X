package net.normlroyal.descendedangel.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.normlroyal.descendedangel.DescendedAngel;
import net.minecraftforge.common.Tags;
import net.normlroyal.descendedangel.block.ModBlocks;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, DescendedAngel.MOD_ID, existingFileHelper);
    }

    public static final TagKey<Block> SACRED_BLOCKS =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "sacred_blocks"));

    public static final TagKey<Block> BLESSED_BLOCKS =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "blessed_blocks"));

    public static final TagKey<Block> ASHEN_BLOCKS =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "ashen_blocks"));

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        // Block Tags
        this.tag(SACRED_BLOCKS).add(
                ModBlocks.SACRED_ORE.get(),
                ModBlocks.SACRED_INGOT_BLOCK.get(),
                ModBlocks.RAW_SACRED_ORE_BLOCK.get()
        );

        this.tag(BLESSED_BLOCKS).add(
                ModBlocks.BLESSED_ROCK.get(),
                ModBlocks.BLESSED_ROCK_SLAB.get(),
                ModBlocks.BLESSED_ROCK_STAIR.get(),
                ModBlocks.BLESSED_ROCK_WALL.get(),
                ModBlocks.POLISHED_BLESSED_ROCK.get(),
                ModBlocks.POLISHED_BLESSED_ROCK_SLAB.get(),
                ModBlocks.POLISHED_BLESSED_ROCK_STAIR.get(),
                ModBlocks.POLISHED_BLESSED_ROCK_WALL.get()
        );

        this.tag(ASHEN_BLOCKS).add(
                ModBlocks.ASHEN_ROCK.get(),
                ModBlocks.ASHEN_ROCK_SLAB.get(),
                ModBlocks.ASHEN_ROCK_STAIR.get(),
                ModBlocks.ASHEN_ROCK_WALL.get(),
                ModBlocks.POLISHED_ASHEN_ROCK.get(),
                ModBlocks.POLISHED_ASHEN_ROCK_SLAB.get(),
                ModBlocks.POLISHED_ASHEN_ROCK_STAIR.get(),
                ModBlocks.POLISHED_ASHEN_ROCK_WALL.get()
        );

        // Mining Tags
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .addTag(BLESSED_BLOCKS)
                .addTag(ASHEN_BLOCKS);

        this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
                .addTag(SACRED_BLOCKS);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(SACRED_BLOCKS)
                .addTag(BLESSED_BLOCKS)
                .addTag(ASHEN_BLOCKS);
    }
}
