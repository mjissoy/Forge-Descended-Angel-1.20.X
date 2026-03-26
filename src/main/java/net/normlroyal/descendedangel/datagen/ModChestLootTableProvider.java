package net.normlroyal.descendedangel.datagen;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.ModItems;

import java.util.function.BiConsumer;

public class ModChestLootTableProvider implements LootTableSubProvider {

    public static final ResourceLocation TABLET_BARREL_LOOT = new ResourceLocation(
            DescendedAngel.MOD_ID, "chests/tablet_barrel"
    );

    public static final ResourceLocation NETHER_TABLET_BARREL_LOOT = new ResourceLocation(
            DescendedAngel.MOD_ID, "chests/nether_tablet_barrel"
    );

    public static final ResourceLocation END_TABLET_BARREL_LOOT = new ResourceLocation(
            DescendedAngel.MOD_ID, "chests/end_tablet_barrel"
    );

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> pOutput) {

        LootTable.Builder tablet_table = LootTable.lootTable()
                // common materials
                .withPool(LootPool.lootPool()
                        .name("common_materials")
                        .setRolls(UniformGenerator.between(2.0f, 4.0f))
                        .add(LootItem.lootTableItem(Items.STRING)
                                .setWeight(8)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 10.0f))))
                        .add(LootItem.lootTableItem(Items.BONE)
                                .setWeight(4)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 6.0f))))
                        .add(LootItem.lootTableItem(Items.COAL)
                                .setWeight(5)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 8.0f))))
                )

                // gold and emeralds
                .withPool(LootPool.lootPool()
                        .name("gold_and_emeralds")
                        .setRolls(UniformGenerator.between(1.0f, 2.0f))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .setWeight(6)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 10.0f))))
                        .add(LootItem.lootTableItem(Items.EMERALD)
                                .setWeight(5)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 6.0f))))
                )

                // descendedangel core items
                .withPool(LootPool.lootPool()
                        .name("descendedangel_core_items")
                        .setRolls(UniformGenerator.between(1.0f, 2.0f))
                        .add(LootItem.lootTableItem(ModItems.VOIDTEAR.get())
                                .setWeight(6)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 11.0f))))
                        .add(LootItem.lootTableItem(ModItems.ANGELFEATHER.get())
                                .setWeight(4)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                        .add(LootItem.lootTableItem(ModItems.SACRED_BLOOD.get())
                                .setWeight(1))
                )

                // rare ancient bits
                .withPool(LootPool.lootPool()
                        .name("rare_ancient_bits")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.ECHO_SHARD)
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                        .add(LootItem.lootTableItem(Items.DIAMOND)
                                .setWeight(1)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                        .add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE)
                                .setWeight(1))
                );

        // Nether Tablet Loot Table
        LootTable.Builder nether_tablet_table = LootTable.lootTable()
                // nether common materials
                .withPool(LootPool.lootPool()
                        .name("nether_common_materials")
                        .setRolls(UniformGenerator.between(2.0f, 4.0f))
                        .add(LootItem.lootTableItem(Items.NETHER_WART)
                                .setWeight(8)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 10.0f))))
                        .add(LootItem.lootTableItem(Items.BONE)
                                .setWeight(4)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 6.0f))))
                        .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST)
                                .setWeight(6)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 10.0f))))
                        .add(LootItem.lootTableItem(Items.CRIMSON_ROOTS)
                                .setWeight(5)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 8.0f))))
                        .add(LootItem.lootTableItem(Items.WARPED_ROOTS)
                                .setWeight(5)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 8.0f))))
                )

                // gold and obsidian
                .withPool(LootPool.lootPool()
                        .name("gold_and_obsidian")
                        .setRolls(UniformGenerator.between(1.0f, 2.0f))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .setWeight(6)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 10.0f))))
                        .add(LootItem.lootTableItem(Items.OBSIDIAN)
                                .setWeight(5)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 6.0f))))
                        .add(LootItem.lootTableItem(Items.CRYING_OBSIDIAN)
                                .setWeight(3)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 6.0f))))
                        .add(LootItem.lootTableItem(Items.FIRE_CHARGE)
                                .setWeight(4)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                )

                // descendedangel core items
                .withPool(LootPool.lootPool()
                        .name("descendedangel_core_items")
                        .setRolls(UniformGenerator.between(1.0f, 2.0f))
                        .add(LootItem.lootTableItem(ModItems.VOIDTEAR.get())
                                .setWeight(6)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 11.0f))))
                        .add(LootItem.lootTableItem(ModItems.ANGELFEATHER.get())
                                .setWeight(4)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                        .add(LootItem.lootTableItem(ModItems.SACRED_BLOOD.get())
                                .setWeight(1))
                )

                // nether rare ancient bits
                .withPool(LootPool.lootPool()
                        .name("nether_rare_ancient_bits")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE)
                                .setWeight(2))
                        .add(LootItem.lootTableItem(ModItems.SPEARHEAD.get())
                                .setWeight(1))
                        .add(LootItem.lootTableItem(ModItems.SPEARSHAFT.get())
                                .setWeight(1))
                );


        // End Tablet Loot Table
        LootTable.Builder end_tablet_table = LootTable.lootTable()
                // end common materials
                .withPool(LootPool.lootPool()
                        .name("nether_common_materials")
                        .setRolls(UniformGenerator.between(2.0f, 4.0f))
                        .add(LootItem.lootTableItem(Items.CHORUS_FRUIT)
                                .setWeight(8)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 10.0f))))
                        .add(LootItem.lootTableItem(Items.END_STONE)
                                .setWeight(4)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 6.0f))))
                        .add(LootItem.lootTableItem(Items.PURPUR_BLOCK)
                                .setWeight(6)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 10.0f))))
                )

                // gold and obsidian
                .withPool(LootPool.lootPool()
                        .name("gold_and_obsidian")
                        .setRolls(UniformGenerator.between(1.0f, 2.0f))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .setWeight(6)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 10.0f))))
                        .add(LootItem.lootTableItem(Items.ENDER_PEARL)
                                .setWeight(5)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 6.0f))))
                        .add(LootItem.lootTableItem(Items.PHANTOM_MEMBRANE)
                                .setWeight(3)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                )

                // descendedangel core items
                .withPool(LootPool.lootPool()
                        .name("descendedangel_core_items")
                        .setRolls(UniformGenerator.between(1.0f, 2.0f))
                        .add(LootItem.lootTableItem(ModItems.VOIDTEAR.get())
                                .setWeight(6)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 11.0f))))
                        .add(LootItem.lootTableItem(ModItems.ANGELFEATHER.get())
                                .setWeight(4)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                        .add(LootItem.lootTableItem(ModItems.SACRED_BLOOD.get())
                                .setWeight(1))
                )

                // end rare ancient bits
                .withPool(LootPool.lootPool()
                        .name("end_rare_ancient_bits")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE)
                                .setWeight(2))
                        .add(LootItem.lootTableItem(Items.SHULKER_BOX)
                                .setWeight(1))
                        .add(LootItem.lootTableItem(Items.DRAGON_HEAD)
                                .setWeight(1))
                );


        pOutput.accept(TABLET_BARREL_LOOT, tablet_table);
        pOutput.accept(NETHER_TABLET_BARREL_LOOT, nether_tablet_table);
        pOutput.accept(END_TABLET_BARREL_LOOT, end_tablet_table);

    }
}
