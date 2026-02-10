package net.normlroyal.descendedangel.block;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.block.altar.AltarBlock;
import net.normlroyal.descendedangel.block.tempwall.TempEarthWallBlock;

import java.util.function.Supplier;

import static net.normlroyal.descendedangel.item.ModItems.ITEMS;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, DescendedAngel.MOD_ID);

    public static final RegistryObject<Block> SACRED_INGOT_BLOCK = registerBlock("sacred_ingot_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK)));
    public static final RegistryObject<Block> RAW_SACRED_ORE_BLOCK = registerBlock("raw_sacred_ore_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ANCIENT_DEBRIS)));
    public static final RegistryObject<Block> SACRED_ORE = registerBlock("sacred_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE)
                    .strength(5f).requiresCorrectToolForDrops(), UniformInt.of(5, 10)));

    public static final RegistryObject<Block> ALTAR = registerBlock("altar",
            () -> new AltarBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0F, 3600000.0F)
                    .noOcclusion()));

    public static final RegistryObject<Block> TEMP_EARTH_WALL = registerBlock("temp_earth_wall",
            () -> new TempEarthWallBlock(BlockBehaviour.Properties.of()
                    .strength(2.5F, 6.0F)
                    .noLootTable()
                    .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> BLESSED_ROCK = registerBlock("blessed_rock",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ANDESITE)));
    public static final RegistryObject<Block> BLESSED_ROCK_SLAB = registerBlock("blessed_rock_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE_SLAB)));
    public static final RegistryObject<Block> BLESSED_ROCK_STAIR = registerBlock("blessed_rock_stair",
            () -> new StairBlock(() -> ModBlocks.BLESSED_ROCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.ANDESITE_STAIRS)));
    public static final RegistryObject<Block> BLESSED_ROCK_WALL = registerBlock("blessed_rock_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE_WALL)));

    public static final RegistryObject<Block> POLISHED_BLESSED_ROCK = registerBlock("polished_blessed_rock",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE)));
    public static final RegistryObject<Block> POLISHED_BLESSED_ROCK_SLAB = registerBlock("polished_blessed_rock_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE_SLAB)));
    public static final RegistryObject<Block> POLISHED_BLESSED_ROCK_STAIR = registerBlock("polished_blessed_rock_stair",
            () -> new StairBlock(() -> ModBlocks.POLISHED_BLESSED_ROCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE_STAIRS)));
    public static final RegistryObject<Block> POLISHED_BLESSED_ROCK_WALL = registerBlock("polished_blessed_rock_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_WALL)));

    public static final RegistryObject<Block> ASHEN_ROCK = registerBlock("ashen_rock",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ANDESITE)));
    public static final RegistryObject<Block> ASHEN_ROCK_SLAB = registerBlock("ashen_rock_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE_SLAB)));
    public static final RegistryObject<Block> ASHEN_ROCK_STAIR = registerBlock("ashen_rock_stair",
            () -> new StairBlock(() -> ModBlocks.ASHEN_ROCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.ANDESITE_STAIRS)));
    public static final RegistryObject<Block> ASHEN_ROCK_WALL = registerBlock("ashen_rock_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE_WALL)));

    public static final RegistryObject<Block> POLISHED_ASHEN_ROCK = registerBlock("polished_ashen_rock",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE)));
    public static final RegistryObject<Block> POLISHED_ASHEN_ROCK_SLAB = registerBlock("polished_ashen_rock_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE_SLAB)));
    public static final RegistryObject<Block> POLISHED_ASHEN_ROCK_STAIR = registerBlock("polished_ashen_rock_stair",
            () -> new StairBlock(() -> ModBlocks.POLISHED_ASHEN_ROCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE_STAIRS)));
    public static final RegistryObject<Block> POLISHED_ASHEN_ROCK_WALL = registerBlock("polished_ashen_rock_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_WALL)));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        if (!name.equals("altar")) {
            registerBlockItem(name, toReturn);
        }

        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(
            String name, RegistryObject<T> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
