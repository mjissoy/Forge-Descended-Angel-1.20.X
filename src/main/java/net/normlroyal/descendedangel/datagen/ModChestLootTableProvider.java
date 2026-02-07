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

    public static final ResourceLocation TABLET_BARREL_LOOT = ResourceLocation.fromNamespaceAndPath(
            DescendedAngel.MOD_ID, "chests/tablet_barrel"
    );

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> pOutput) {

        LootTable.Builder table = LootTable.lootTable()
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
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 8.0f))))
                        .add(LootItem.lootTableItem(ModItems.ANGELFEATHER.get())
                                .setWeight(4)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
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

        pOutput.accept(TABLET_BARREL_LOOT, table);

    }
}
