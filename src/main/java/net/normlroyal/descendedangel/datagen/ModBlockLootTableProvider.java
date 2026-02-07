package net.normlroyal.descendedangel.datagen;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
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

        // Ores
        add(ModBlocks.SACRED_ORE.get(), block -> createOreDrop(ModBlocks.SACRED_ORE.get(), ModItems.SACREDORERAW.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(
                ModBlocks.SACRED_INGOT_BLOCK.get(),
                ModBlocks.RAW_SACRED_ORE_BLOCK.get(),
                ModBlocks.SACRED_ORE.get()
        );
    }
}
