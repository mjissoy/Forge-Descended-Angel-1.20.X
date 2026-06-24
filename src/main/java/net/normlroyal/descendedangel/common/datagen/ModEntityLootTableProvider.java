package net.normlroyal.descendedangel.common.datagen;

import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.normlroyal.descendedangel.content.entity.ModEntities;
import net.normlroyal.descendedangel.content.item.ModItems;

import java.util.stream.Stream;

public class ModEntityLootTableProvider extends EntityLootSubProvider {

    public ModEntityLootTableProvider() {
        super(FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate() {
        this.add(ModEntities.VOID_ANOMALY.get(),
                LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0f))
                        .add(LootItem.lootTableItem(ModItems.VOIDTEAR.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0f, 9.0f)))
                        )
                )
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0f))
                        .when(LootItemRandomChanceCondition.randomChance(0.08f))
                        .add(LootItem.lootTableItem(ModItems.VOID_HEART_LINK.get()))
                ));
        this.add(ModEntities.IMP.get(),
                LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0f))
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.10f))
                        .add(LootItem.lootTableItem(ModItems.DEMONHEART.get())
                        )
                )
        );

    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return Stream.of(
                ModEntities.IMP.get(),
                ModEntities.VOID_ANOMALY.get()
        );
    }
}
