package net.normlroyal.descendedangel.datagen;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.block.ModBlocks;
import net.normlroyal.descendedangel.item.ModItems;

import java.util.List;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    public ModBlockLootTableProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {

        // Blocks
        dropSelf(ModBlocks.SACRED_INGOT_BLOCK.get());
        dropSelf(ModBlocks.RAW_SACRED_ORE_BLOCK.get());

        dropSelf(ModBlocks.BLESSED_ROCK.get());
        dropSelf(ModBlocks.BLESSED_ROCK_STAIR.get());
        dropSelf(ModBlocks.BLESSED_ROCK_WALL.get());
        dropSelf(ModBlocks.POLISHED_BLESSED_ROCK.get());
        dropSelf(ModBlocks.POLISHED_BLESSED_ROCK_WALL.get());
        dropSelf(ModBlocks.POLISHED_BLESSED_ROCK_STAIR.get());

        dropSelf(ModBlocks.ASHEN_ROCK.get());
        dropSelf(ModBlocks.ASHEN_ROCK_STAIR.get());
        dropSelf(ModBlocks.ASHEN_ROCK_WALL.get());
        dropSelf(ModBlocks.POLISHED_ASHEN_ROCK.get());
        dropSelf(ModBlocks.POLISHED_ASHEN_ROCK_WALL.get());
        dropSelf(ModBlocks.POLISHED_ASHEN_ROCK_STAIR.get());

        add(ModBlocks.BLESSED_ROCK_SLAB.get(), block -> createSlabItemTable(ModBlocks.BLESSED_ROCK_SLAB.get()));
        add(ModBlocks.POLISHED_BLESSED_ROCK_SLAB.get(), block -> createSlabItemTable(ModBlocks.POLISHED_BLESSED_ROCK_SLAB.get()));
        add(ModBlocks.ASHEN_ROCK_SLAB.get(), block -> createSlabItemTable(ModBlocks.ASHEN_ROCK_SLAB.get()));
        add(ModBlocks.POLISHED_ASHEN_ROCK_SLAB.get(), block -> createSlabItemTable(ModBlocks.POLISHED_ASHEN_ROCK_SLAB.get()));

        // Ores
        add(ModBlocks.SACRED_ORE.get(), block -> createOreDrop(ModBlocks.SACRED_ORE.get(), ModItems.SACREDORERAW.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(
                ModBlocks.SACRED_INGOT_BLOCK.get(),
                ModBlocks.RAW_SACRED_ORE_BLOCK.get(),
                ModBlocks.SACRED_ORE.get(),
                ModBlocks.ASHEN_ROCK.get(),
                ModBlocks.ASHEN_ROCK_SLAB.get(),
                ModBlocks.ASHEN_ROCK_STAIR.get(),
                ModBlocks.ASHEN_ROCK_WALL.get(),
                ModBlocks.POLISHED_ASHEN_ROCK.get(),
                ModBlocks.POLISHED_ASHEN_ROCK_SLAB.get(),
                ModBlocks.POLISHED_ASHEN_ROCK_STAIR.get(),
                ModBlocks.POLISHED_ASHEN_ROCK_WALL.get(),
                ModBlocks.BLESSED_ROCK.get(),
                ModBlocks.BLESSED_ROCK_SLAB.get(),
                ModBlocks.BLESSED_ROCK_STAIR.get(),
                ModBlocks.BLESSED_ROCK_WALL.get(),
                ModBlocks.POLISHED_BLESSED_ROCK.get(),
                ModBlocks.POLISHED_BLESSED_ROCK_SLAB.get(),
                ModBlocks.POLISHED_BLESSED_ROCK_STAIR.get(),
                ModBlocks.POLISHED_BLESSED_ROCK_WALL.get()
        );
    }
}
