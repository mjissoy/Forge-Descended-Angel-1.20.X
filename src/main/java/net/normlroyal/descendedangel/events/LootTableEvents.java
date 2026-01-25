package net.normlroyal.descendedangel.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.item.ModItems;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootTableEvents {
    private static final ResourceLocation END_CITY_TREASURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/end_city_treasure");

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (EffectiveSide.get().isClient()) return;

        if (!event.getName().equals(END_CITY_TREASURE)) return;

        float chance = ModConfigs.COMMON.spatialCoreEndCityChestChance.get().floatValue();

        LootPool pool = LootPool.lootPool()
                .name("spatial_core_inject")
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(ModItems.SPATIALCORE.get()).setWeight(1))
                .build();

        event.getTable().addPool(pool);

    }
}
