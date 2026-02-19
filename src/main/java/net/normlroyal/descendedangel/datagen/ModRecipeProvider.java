package net.normlroyal.descendedangel.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.block.ModBlocks;
import net.normlroyal.descendedangel.datagen.helpers.AltarRiteRecipeBuilder;
import net.normlroyal.descendedangel.datagen.helpers.AltarRiteRecipeBuilder.RingEntry;
import net.normlroyal.descendedangel.item.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> out) {

        // Shaped Crafting Recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HOLY_NECKLACE.get())
                .pattern("AAA")
                .pattern("AAA")
                .pattern(" B ")
                .define('A', Items.IRON_NUGGET)
                .define('B', Items.GOLD_NUGGET)
                .unlockedBy("has_gold_nugget", has(Items.GOLD_NUGGET))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "holy_necklace_crafting")
                );

        // Blessed Rock and Blessed Rock Crafting
        slabBuilder(RecipeCategory.BUILDING_BLOCKS,
                ModBlocks.BLESSED_ROCK_SLAB.get(),
                Ingredient.of(ModBlocks.BLESSED_ROCK.get()))
                .unlockedBy("has_blessed_rock", has(ModBlocks.BLESSED_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "blessed_rock_slab_crafting")
                );

        stairBuilder(ModBlocks.BLESSED_ROCK_STAIR.get(),
                Ingredient.of(ModBlocks.BLESSED_ROCK.get()))
                .unlockedBy("has_blessed_rock", has(ModBlocks.BLESSED_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "blessed_rock_stair_crafting")
                );

        wallBuilder(RecipeCategory.BUILDING_BLOCKS,
                ModBlocks.BLESSED_ROCK_WALL.get(),
                Ingredient.of(ModBlocks.BLESSED_ROCK.get()))
                .unlockedBy("has_blessed_rock", has(ModBlocks.BLESSED_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "blessed_rock_wall_crafting")
                );

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.POLISHED_BLESSED_ROCK.get(), 4)
                .pattern("AA")
                .pattern("AA")
                .define('A', ModBlocks.BLESSED_ROCK.get())
                .unlockedBy("has_blessed_rock",
                        has(ModBlocks.BLESSED_ROCK.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "polished_blessed_rock_crafting")
                );

        slabBuilder(RecipeCategory.BUILDING_BLOCKS,
                ModBlocks.POLISHED_BLESSED_ROCK_SLAB.get(),
                Ingredient.of(ModBlocks.POLISHED_BLESSED_ROCK.get()))
                .unlockedBy("has_blessed_rock", has(ModBlocks.BLESSED_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "polished_blessed_rock_slab_crafting")
                );

        stairBuilder(ModBlocks.POLISHED_BLESSED_ROCK_STAIR.get(),
                Ingredient.of(ModBlocks.POLISHED_BLESSED_ROCK.get()))
                .unlockedBy("has_blessed_rock", has(ModBlocks.BLESSED_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "polished_blessed_rock_stair_crafting")
                );

        wallBuilder(RecipeCategory.BUILDING_BLOCKS,
                ModBlocks.POLISHED_BLESSED_ROCK_WALL.get(),
                Ingredient.of(ModBlocks.POLISHED_BLESSED_ROCK.get()))
                .unlockedBy("has_blessed_rock", has(ModBlocks.BLESSED_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "polished_blessed_rock_wall_crafting")
                );

        slabBuilder(RecipeCategory.BUILDING_BLOCKS,
                ModBlocks.ASHEN_ROCK_SLAB.get(),
                Ingredient.of(ModBlocks.ASHEN_ROCK.get()))
                .unlockedBy("has_ashen_rock", has(ModBlocks.ASHEN_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "ashen_rock_slab_crafting")
                );

        stairBuilder(ModBlocks.ASHEN_ROCK_STAIR.get(),
                Ingredient.of(ModBlocks.ASHEN_ROCK.get()))
                .unlockedBy("has_ashen_rock", has(ModBlocks.ASHEN_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "ashen_rock_stair_crafting")
                );

        wallBuilder(RecipeCategory.BUILDING_BLOCKS,
                ModBlocks.ASHEN_ROCK_WALL.get(),
                Ingredient.of(ModBlocks.ASHEN_ROCK.get()))
                .unlockedBy("has_ashen_rock", has(ModBlocks.ASHEN_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "ashen_rock_wall_crafting")
                );

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.POLISHED_ASHEN_ROCK.get(), 4)
                .pattern("AA")
                .pattern("AA")
                .define('A', ModBlocks.ASHEN_ROCK.get())
                .unlockedBy("has_ashen_rock",
                        has(ModBlocks.ASHEN_ROCK.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "polished_ashen_rock_crafting")
                );

        slabBuilder(RecipeCategory.BUILDING_BLOCKS,
                ModBlocks.POLISHED_ASHEN_ROCK_SLAB.get(),
                Ingredient.of(ModBlocks.POLISHED_ASHEN_ROCK.get()))
                .unlockedBy("has_ashen_rock", has(ModBlocks.ASHEN_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "polished_ashen_rock_slab_crafting")
                );

        stairBuilder(ModBlocks.POLISHED_ASHEN_ROCK_STAIR.get(),
                Ingredient.of(ModBlocks.POLISHED_ASHEN_ROCK.get()))
                .unlockedBy("has_ashen_rock", has(ModBlocks.ASHEN_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "polished_ashen_rock_stair_crafting")
                );

        wallBuilder(RecipeCategory.BUILDING_BLOCKS,
                ModBlocks.POLISHED_ASHEN_ROCK_WALL.get(),
                Ingredient.of(ModBlocks.POLISHED_ASHEN_ROCK.get()))
                .unlockedBy("has_ashen_rock", has(ModBlocks.ASHEN_ROCK.get()))
                .save(out,  ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "polished_ashen_rock_wall_crafting")
                );

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COMPRESSEDVOID.get())
                .pattern("VVV")
                .pattern("VVV")
                .pattern("VVV")
                .define('V', ModItems.VOIDTEAR.get())
                .unlockedBy("has_void_tear", has(ModItems.VOIDTEAR.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "compressed_void_from_void_tear"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.VOIDTEAR.get(), 9)
                .requires(ModItems.COMPRESSEDVOID.get())
                .unlockedBy("has_void_tear", has(ModItems.VOIDTEAR.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "void_tear_from_compressed_void"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.VOIDMATRIX.get())
                .pattern("VVV")
                .pattern("VVV")
                .pattern("VVV")
                .define('V', ModItems.COMPRESSEDVOID.get())
                .unlockedBy("has_void_tear", has(ModItems.VOIDTEAR.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "void_matrix_from_compressed_void"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.COMPRESSEDVOID.get(), 9)
                .requires(ModItems.VOIDMATRIX.get())
                .unlockedBy("has_void_tear", has(ModItems.VOIDTEAR.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "compressed_void_from_void_matrix"));


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HOLY_RING.get())
                .pattern("ABA")
                .pattern("A A")
                .pattern("AAA")
                .define('A', Items.IRON_NUGGET)
                .define('B', Items.GOLD_NUGGET)
                .unlockedBy("has_gold_nugget", has(Items.GOLD_NUGGET))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "holy_ring_crafting")
                );

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ANGELFEATHER.get())
                .pattern("ACA")
                .pattern("DBD")
                .pattern("ACA")
                .define('A', Items.GOLD_INGOT)
                .define('B', Items.FEATHER)
                .define('C', Items.GLOWSTONE_DUST)
                .define('D', ModItems.VOIDTEAR.get())
                .unlockedBy("has_void_tear", has(ModItems.VOIDTEAR.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "angel_feather_crafting")
                );

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RAW_SACRED_ORE_BLOCK.get())
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.SACREDORERAW.get())
                .unlockedBy("has_sacred_ore_raw", has(ModItems.SACREDORERAW.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "sacred_ore_raw_block_crafting")
                );

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SACRED_INGOT_BLOCK.get())
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.SACREDOREINGOT.get())
                .unlockedBy("has_sacred_ore_ingot", has(ModItems.SACREDOREINGOT.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "sacred_ore_ingot_block_crafting")
                );

        // Smelting and Blasting Recipes
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(ModItems.SACREDORERAW.get()),
                RecipeCategory.MISC,
                ModItems.SACREDOREINGOT.get(),
                1.0f,
                200)
                .unlockedBy("has_sacred_ore_raw", has(ModItems.SACREDORERAW.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "sacred_ingot_smelting")
                );

        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(ModItems.SACREDORERAW.get()),
                RecipeCategory.MISC,
                ModItems.SACREDOREINGOT.get(),
                1.0f,
                100)
                .unlockedBy("has_sacred_ore_raw", has(ModItems.SACREDORERAW.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "sacred_ingot_blasting")
                );

        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(ModBlocks.RAW_SACRED_ORE_BLOCK.get()),
                RecipeCategory.MISC,
                ModBlocks.SACRED_INGOT_BLOCK.get(),
                1.0f,
                200)
                .unlockedBy("has_sacred_ore_raw", has(ModItems.SACREDORERAW.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "sacred_ingot_block_smelting")
                );

        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(ModBlocks.RAW_SACRED_ORE_BLOCK.get()),
                RecipeCategory.MISC,
                        ModBlocks.SACRED_INGOT_BLOCK.get(),
                1.0f,
                100)
                .unlockedBy("has_sacred_ore_raw", has(ModItems.SACREDORERAW.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "sacred_ingot_block_blasting")
                );

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModItems.DEMONHEART.get()),
                        RecipeCategory.MISC,
                        ModItems.REALDEMONHEART.get(),
                        5.0f,
                        200)
                .unlockedBy("has_demon_heart", has(ModItems.DEMONHEART.get()))
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "purified_demon_heart_from_smelting")
                );


        // Altar Rite Recipes
        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.ANGELFEATHER.get()),
                        new ItemStack(ModItems.HALO_T1.get(), 1)
                )
                .displayType("altar.descendedangel.ascendance")
                .requiredHaloTier(0)
                .ring(
                        RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                        RingEntry.of(Ingredient.of(Items.GOLD_INGOT)),
                        RingEntry.of(Ingredient.of(Items.GOLD_INGOT)),
                        RingEntry.of(Ingredient.of(Items.GOLD_INGOT)),
                        RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                        RingEntry.of(Ingredient.of(Items.GOLD_INGOT)),
                        RingEntry.of(Ingredient.of(Items.GOLD_INGOT)),
                        RingEntry.of(Ingredient.of(Items.GOLD_INGOT))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t1_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HALO_T1.get()),
                        new ItemStack(ModItems.HALO_T2.get(), 1)
                )
                .displayType("altar.descendedangel.ascendance")
                .requiredHaloTier(1)
                .ring(
                        RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                        RingEntry.of(Ingredient.of(Items.GOLD_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                        RingEntry.of(Ingredient.of(Items.GOLD_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                        RingEntry.of(Ingredient.of(Items.GOLD_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                        RingEntry.of(Ingredient.of(Items.GOLD_BLOCK))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t2_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HALO_T2.get()),
                        new ItemStack(ModItems.HALO_T3.get(), 1)
                )
                .displayType("altar.descendedangel.ascendance")
                .requiredHaloTier(2)
                .ring(
                        RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                        RingEntry.of(Ingredient.of(Items.DIAMOND_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                        RingEntry.of(Ingredient.of(Items.GOLD_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                        RingEntry.of(Ingredient.of(Items.DIAMOND_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                        RingEntry.of(Ingredient.of(Items.GOLD_BLOCK))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t3_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HALO_T3.get()),
                        new ItemStack(ModItems.HALO_T4.get(), 1)
                )
                .displayType("altar.descendedangel.ascendance")
                .requiredHaloTier(3)
                .ring(
                        RingEntry.of(Ingredient.of(ModItems.REALDEMONHEART.get())),
                        RingEntry.of(Ingredient.of(Items.DIAMOND_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.COMPRESSEDVOID.get())),
                        RingEntry.of(Ingredient.of(Items.DIAMOND_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.REALDEMONHEART.get())),
                        RingEntry.of(Ingredient.of(Items.DIAMOND_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.COMPRESSEDVOID.get())),
                        RingEntry.of(Ingredient.of(Items.DIAMOND_BLOCK))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t4_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HALO_T4.get()),
                        new ItemStack(ModItems.HALO_T5.get(), 1)
                )
                .displayType("altar.descendedangel.ascendance")
                .requiredHaloTier(4)
                .ring(
                        RingEntry.of(Ingredient.of(ModItems.REALDEMONHEART.get())),
                        RingEntry.of(Ingredient.of(Items.DIAMOND_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.COMPRESSEDVOID.get())),
                        RingEntry.of(Ingredient.of(Items.NETHERITE_INGOT)),
                        RingEntry.of(Ingredient.of(ModItems.REALDEMONHEART.get())),
                        RingEntry.of(Ingredient.of(Items.DIAMOND_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.COMPRESSEDVOID.get())),
                        RingEntry.of(Ingredient.of(Items.NETHERITE_INGOT))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t5_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HALO_T5.get()),
                        new ItemStack(ModItems.HALO_T6.get(), 1)
                )
                .displayType("altar.descendedangel.ascendance")
                .requiredHaloTier(5)
                .ring(
                        RingEntry.of(Ingredient.of(ModItems.COMPRESSEDVOID.get())),
                        RingEntry.of(Ingredient.of(Items.NETHERITE_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.REALDEMONHEART.get())),
                        RingEntry.of(Ingredient.of(Items.NETHERITE_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.COMPRESSEDVOID.get())),
                        RingEntry.of(Ingredient.of(Items.NETHERITE_BLOCK)),
                        RingEntry.of(Ingredient.of(ModItems.REALDEMONHEART.get())),
                        RingEntry.of(Ingredient.of(Items.NETHERITE_BLOCK))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t6_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HALO_T6.get()),
                        new ItemStack(ModItems.HALO_T7.get(), 1)
                )
                .displayType("altar.descendedangel.ascendance")
                .requiredHaloTier(6)
                .ring(
                        RingEntry.of(Ingredient.of(ModItems.SPATIALCORE.get())),
                        RingEntry.of(Ingredient.of(Items.NETHERITE_BLOCK)),
                        RingEntry.of(Ingredient.of(Items.NETHER_STAR)),
                        RingEntry.of(Ingredient.of(ModItems.SACREDOREINGOT.get())),
                        RingEntry.of(Ingredient.of(ModItems.SPATIALCORE.get())),
                        RingEntry.of(Ingredient.of(Items.NETHERITE_BLOCK)),
                        RingEntry.of(Ingredient.of(Items.NETHER_STAR)),
                        RingEntry.of(Ingredient.of(ModItems.SACREDOREINGOT.get()))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t7_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HALO_T7.get()),
                        new ItemStack(ModItems.HALO_T8.get(), 1)
                )
                .displayType("altar.descendedangel.ascendance")
                .requiredHaloTier(7)
                .ring(
                        RingEntry.of(Ingredient.of(ModItems.SPATIALCORE.get())),
                        RingEntry.of(Ingredient.of(ModBlocks.SACRED_INGOT_BLOCK.get())),
                        RingEntry.of(Ingredient.of(Items.DRAGON_BREATH)),
                        RingEntry.of(Ingredient.of(ModItems.VOIDMATRIX.get())),
                        RingEntry.of(Ingredient.of(Items.NETHER_STAR)),
                        RingEntry.of(Ingredient.of(ModItems.VOIDMATRIX.get())),
                        RingEntry.of(Ingredient.of(Items.DRAGON_BREATH)),
                        RingEntry.of(Ingredient.of(ModBlocks.SACRED_INGOT_BLOCK.get()))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t8_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HALO_T8.get()),
                        new ItemStack(ModItems.HALO_T9.get(), 1)
                )
                .displayType("altar.descendedangel.ascendance")
                .requiredHaloTier(8)
                .ring(
                        RingEntry.of(Ingredient.of(Items.DRAGON_EGG)),
                        RingEntry.of(Ingredient.of(ModBlocks.SACRED_INGOT_BLOCK.get())),
                        RingEntry.of(Ingredient.of(ModItems.VOIDMATRIX.get())),
                        RingEntry.of(Ingredient.of(ModBlocks.SACRED_INGOT_BLOCK.get())),
                        RingEntry.of(Ingredient.of(ModItems.SPATIALCORE.get())),
                        RingEntry.of(Ingredient.of(ModBlocks.SACRED_INGOT_BLOCK.get())),
                        RingEntry.of(Ingredient.of(ModItems.VOIDMATRIX.get())),
                        RingEntry.of(Ingredient.of(ModBlocks.SACRED_INGOT_BLOCK.get()))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t9_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HOLY_RING.get()),
                        new ItemStack(ModItems.CLOUD_RING.get(), 1)
                )
                .displayType("altar.descendedangel.consecration")
                .requiredHaloTier(5)
                .ring(
                      RingEntry.of(Ingredient.of(Items.LIGHTNING_ROD)),
                      RingEntry.of(Ingredient.of(Items.WATER_BUCKET)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.SNOW_BLOCK)),
                      RingEntry.of(Ingredient.of(Items.LIGHTNING_ROD)),
                      RingEntry.of(Ingredient.of(Items.SNOW_BLOCK)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.WATER_BUCKET))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/cloud_ring_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HOLY_RING.get()),
                        new ItemStack(ModItems.FLAME_RING.get(), 1)
                )
                .displayType("altar.descendedangel.consecration")
                .requiredHaloTier(5)
                .ring(
                      RingEntry.of(Ingredient.of(Items.FIRE_CHARGE)),
                      RingEntry.of(Ingredient.of(Items.GUNPOWDER)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.BLAZE_ROD)),
                      RingEntry.of(Ingredient.of(Items.FIRE_CHARGE)),
                      RingEntry.of(Ingredient.of(Items.BLAZE_ROD)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.GUNPOWDER))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/flame_ring_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HOLY_RING.get()),
                        new ItemStack(ModItems.CURE_RING.get(), 1)
                )
                .displayType("altar.descendedangel.consecration")
                .requiredHaloTier(5)
                .ring(
                      RingEntry.of(Ingredient.of(Items.AMETHYST_SHARD)),
                      RingEntry.of(Ingredient.of(Items.GHAST_TEAR)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.GOLD_INGOT)),
                      RingEntry.of(Ingredient.of(Items.AMETHYST_SHARD)),
                      RingEntry.of(Ingredient.of(Items.GOLD_INGOT)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.GHAST_TEAR))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/cure_ring_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HOLY_NECKLACE.get()),
                        new ItemStack(ModItems.MESSENGER_PENDANT.get(), 1)
                )
                .displayType("altar.descendedangel.consecration")
                .requiredHaloTier(5)
                .ring(
                      RingEntry.of(Ingredient.of(Items.GOLD_BLOCK)),
                      RingEntry.of(Ingredient.of(Items.GOLD_BLOCK)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.RABBIT_FOOT)),
                      RingEntry.of(Ingredient.of(Items.GOLD_BLOCK)),
                      RingEntry.of(Ingredient.of(Items.GOLD_BLOCK)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.RABBIT_FOOT))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/messenger_pendant_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HOLY_NECKLACE.get()),
                        new ItemStack(ModItems.LIGHTNESS_NECKLACE.get(), 1)
                )
                .displayType("altar.descendedangel.consecration")
                .requiredHaloTier(5)
                .ring(
                      RingEntry.of(Ingredient.of(Items.IRON_BLOCK)),
                      RingEntry.of(Ingredient.of(Items.IRON_BLOCK)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.GOLDEN_CARROT)),
                      RingEntry.of(Ingredient.of(Items.BLAZE_POWDER)),
                      RingEntry.of(Ingredient.of(Items.BLAZE_POWDER)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.GOLDEN_CARROT))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/nanos_lantern_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(ModItems.HOLY_NECKLACE.get()),
                        new ItemStack(ModItems.BOOSTER_NECKLACE.get(), 1)
                )
                .displayType("altar.descendedangel.consecration")
                .requiredHaloTier(5)
                .ring(
                      RingEntry.of(Ingredient.of(Items.BLAZE_ROD)),
                      RingEntry.of(Ingredient.of(Items.BLAZE_ROD)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.GLOWSTONE)),
                      RingEntry.of(Ingredient.of(Items.REDSTONE)),
                      RingEntry.of(Ingredient.of(Items.REDSTONE)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.GLOWSTONE))
                )
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/alchemy_chain_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(Items.BOOK),
                        new ItemStack(ModItems.SACRED_WRITINGS.get(), 1)
                )
                .displayType("altar.descendedangel.imbuement")
                .requiredHaloTier(6)
                .ring(
                      RingEntry.of(Ingredient.of(Items.WITHER_SKELETON_SKULL)),
                      RingEntry.of(Ingredient.of(Items.WITHER_SKELETON_SKULL)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(Items.SOUL_SAND)),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.WITHER_SKELETON_SKULL))
                )
                .resultNbt("{descendedangel:{writ_id:\"descendedangel:spawn_wither\",uses:3}}")
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_writing_spawn_wither_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(Items.BOOK),
                        new ItemStack(ModItems.SACRED_WRITINGS.get(), 1)
                )
                .displayType("altar.descendedangel.imbuement")
                .requiredHaloTier(2)
                .ring(
                      RingEntry.of(Ingredient.of(Items.BUCKET)),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(Items.KELP)),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.empty()
                )
                .resultNbt("{descendedangel:{writ_id:\"descendedangel:clear_weather\",uses:3}}")
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_writing_clearskies_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(Items.BOOK),
                        new ItemStack(ModItems.SACRED_WRITINGS.get(), 1)
                )
                .displayType("altar.descendedangel.imbuement")
                .requiredHaloTier(2)
                .ring(
                      RingEntry.of(Ingredient.of(Items.SNOWBALL)),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(Items.PRISMARINE_SHARD)),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.empty()
                )
                .resultNbt("{descendedangel:{writ_id:\"descendedangel:rain_weather\",uses:1}}")
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_writing_rainfall_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(Items.BOOK),
                        new ItemStack(ModItems.SACRED_WRITINGS.get(), 1)
                )
                .displayType("altar.descendedangel.imbuement")
                .requiredHaloTier(4)
                .ring(
                      RingEntry.of(Ingredient.of(Items.FLINT_AND_STEEL)),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.OBSIDIAN)),
                      RingEntry.of(Ingredient.of(Items.OBSIDIAN)),
                      RingEntry.of(Ingredient.of(Items.OBSIDIAN)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.empty()
                )
                .resultNbt("{descendedangel:{writ_id:\"descendedangel:spawn_ruined_portal\",uses:1}}")
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_writing_ruined_portal_spawn_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(Items.BOOK),
                        new ItemStack(ModItems.SACRED_WRITINGS.get(), 1)
                )
                .displayType("altar.descendedangel.imbuement")
                .requiredHaloTier(6)
                .ring(
                      RingEntry.of(Ingredient.of(Items.EMERALD_BLOCK)),
                      RingEntry.of(Ingredient.of(Items.EMERALD_BLOCK)),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(Items.GOLDEN_CARROT)),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.of(Ingredient.of(Items.EMERALD_BLOCK))
                )
                .resultNbt("{descendedangel:{writ_id:\"descendedangel:spawn_villages\",uses:1}}")
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_writing_village_summon_rite"));

        AltarRiteRecipeBuilder.altar(
                        Ingredient.of(Items.BOOK),
                        new ItemStack(ModItems.SACRED_WRITINGS.get(), 1)
                )
                .displayType("altar.descendedangel.imbuement")
                .requiredHaloTier(3)
                .ring(
                      RingEntry.of(Ingredient.of(Items.EMERALD)),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(Items.BREAD)),
                      RingEntry.empty(),
                      RingEntry.of(Ingredient.of(ModItems.VOIDTEAR.get())),
                      RingEntry.empty()
                )
                .resultNbt("{descendedangel:{writ_id:\"descendedangel:spawn_villager\",uses:1}}")
                .save(out, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_writing_villager_summon_rite"));

    }
}
