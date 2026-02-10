package net.normlroyal.descendedangel.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.block.ModBlocks;

import java.util.Objects;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DescendedAngel.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        stoneFamily(
                ModBlocks.BLESSED_ROCK.get(),
                (SlabBlock) ModBlocks.BLESSED_ROCK_SLAB.get(),
                (StairBlock) ModBlocks.BLESSED_ROCK_STAIR.get(),
                (WallBlock) ModBlocks.BLESSED_ROCK_WALL.get(),
                modLoc("block/blessed_rock")
        );

        stoneFamily(
                ModBlocks.POLISHED_BLESSED_ROCK.get(),
                (SlabBlock) ModBlocks.POLISHED_BLESSED_ROCK_SLAB.get(),
                (StairBlock) ModBlocks.POLISHED_BLESSED_ROCK_STAIR.get(),
                (WallBlock) ModBlocks.POLISHED_BLESSED_ROCK_WALL.get(),
                modLoc("block/polished_blessed_rock")
        );

        stoneFamily(
                ModBlocks.ASHEN_ROCK.get(),
                (SlabBlock) ModBlocks.ASHEN_ROCK_SLAB.get(),
                (StairBlock) ModBlocks.ASHEN_ROCK_STAIR.get(),
                (WallBlock) ModBlocks.ASHEN_ROCK_WALL.get(),
                modLoc("block/ashen_rock")
        );

        stoneFamily(
                ModBlocks.POLISHED_ASHEN_ROCK.get(),
                (SlabBlock) ModBlocks.POLISHED_ASHEN_ROCK_SLAB.get(),
                (StairBlock) ModBlocks.POLISHED_ASHEN_ROCK_STAIR.get(),
                (WallBlock) ModBlocks.POLISHED_ASHEN_ROCK_WALL.get(),
                modLoc("block/polished_ashen_rock")
        );

        simpleBlockWithItem(ModBlocks.SACRED_INGOT_BLOCK.get(),
                models().cubeAll("sacred_ingot_block", modLoc("block/sacred_ingot_block"))
        );
        simpleBlockWithItem(ModBlocks.RAW_SACRED_ORE_BLOCK.get(),
                models().cubeAll("raw_sacred_ore_block", modLoc("block/raw_sacred_ore_block"))
        );
        simpleBlockWithItem(ModBlocks.SACRED_ORE.get(),
                models().cubeAll("sacred_ore", modLoc("block/sacred_ore"))
        );

        simpleBlockWithItem(ModBlocks.TEMP_EARTH_WALL.get(),
                models().cubeAll("temp_earth_wall", modLoc("block/temp_earth_wall")));


    }

    private void stoneFamily(Block base, SlabBlock slab, StairBlock stairs, WallBlock wall, ResourceLocation tex) {
        simpleBlock(base, models().cubeAll(name(base), tex));
        simpleBlockItem(base, models().cubeAll(name(base), tex));

        stairsBlock(stairs, tex);
        itemModels().withExistingParent(name(stairs), modLoc("block/" + name(stairs)));

        slabBlock(slab, tex, tex);
        itemModels().withExistingParent(name(slab), modLoc("block/" + name(slab)));

        wallBlock(wall, tex);
        itemModels().wallInventory(name(wall), tex);    }

    private String name(Block b) {
        return Objects.requireNonNull(
                ForgeRegistries.BLOCKS.getKey(b),
                "Block not registered: " + b
        ).getPath();
    }
}
