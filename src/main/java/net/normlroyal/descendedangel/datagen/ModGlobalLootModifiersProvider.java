package net.normlroyal.descendedangel.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.config.lootmodifier.AddItemModifier;
import net.normlroyal.descendedangel.config.lootmodifier.SpatialCoreFromEndCityTreasureModifier;
import net.normlroyal.descendedangel.config.lootmodifier.SpatialCoreFromEndermanModifier;
import net.normlroyal.descendedangel.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
        public ModGlobalLootModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, DescendedAngel.MOD_ID);
        }

        @Override
        protected void start() {
            // Enderman Drop Injection
            add("spatial_core_from_enderman",
                    new SpatialCoreFromEndermanModifier(new LootItemCondition[] {
                            LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/enderman")).build()
                    })
            );

            // End City Treasure Injection
            add("spatial_core_in_end_city_treasure",
                    new SpatialCoreFromEndCityTreasureModifier(new LootItemCondition[] {
                            LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/end_city_treasure")).build()
                    })
            );

            // Bastion Treasure Injection
            add("fruit_of_space_bastion_treasure",
                    new AddItemModifier(
                            new LootItemCondition[]{
                                    LootTableIdCondition.builder(
                                            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_treasure")
                                    ).build(),
                                    LootItemRandomChanceCondition.randomChance(0.10f).build()
                            },
                            ModItems.SPACE_FRUIT.get()
                    )
            );

            add("fruit_of_time_bastion_treasure",
                    new AddItemModifier(
                            new LootItemCondition[]{
                                    LootTableIdCondition.builder(
                                            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_treasure")
                                    ).build(),
                                    LootItemRandomChanceCondition.randomChance(0.10f).build()
                            },
                            ModItems.TIME_FRUIT.get()
                    )
            );



        }
    }

