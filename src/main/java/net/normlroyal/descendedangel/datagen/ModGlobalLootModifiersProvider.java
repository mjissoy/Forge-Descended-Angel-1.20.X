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
import net.normlroyal.descendedangel.config.lootmodifier.RandomShardLootModifier;
import net.normlroyal.descendedangel.config.lootmodifier.SpatialCoreFromEndCityTreasureModifier;
import net.normlroyal.descendedangel.config.lootmodifier.SpatialCoreFromEndermanModifier;
import net.normlroyal.descendedangel.item.ModItems;

import java.util.List;
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
                            new LootItemCondition[] {
                                    LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_treasure")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.10f).build()
                            },
                            ModItems.SPACE_FRUIT.get()
                    )
            );

            add("fruit_of_time_bastion_treasure",
                    new AddItemModifier(
                            new LootItemCondition[] {
                                    LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_treasure")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.10f).build()
                            },
                            ModItems.TIME_FRUIT.get()
                    )
            );

            // Desert Pyramid Shards Injection
            add("desert_pyramid_shards",
                    new RandomShardLootModifier(
                            new LootItemCondition[] {
                                    LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/desert_pyramid")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.10f).build()
                            },
                            List.of(
                                    ModItems.FIRE_SHARD.get(),
                                    ModItems.WATER_SHARD.get(),
                                    ModItems.EARTH_SHARD.get(),
                                    ModItems.AIR_SHARD.get()
                            ),
                            1, 1
                    )
            );

            // Ship wreak Cain Fragments Injection
            add("mark_component3_ship_wreak",
                    new AddItemModifier(
                            new LootItemCondition[] {
                                    LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/shipwreck_supply")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.075f).build()
                            },
                                    ModItems.MARK_PIECE3.get()
                    )
            );

            add("mark_component2_ship_wreak",
                    new AddItemModifier(
                            new LootItemCondition[] {
                                    LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/shipwreck_supply")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.05f).build()
                            },
                                    ModItems.MARK_PIECE2.get()
                    )
            );

            // Stronghold Library Cain Fragments Injection
            add("mark_component1_stronghold_library",
                    new AddItemModifier(
                            new LootItemCondition[] {
                                    LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_library")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.15f).build()
                            },
                                    ModItems.MARK_PIECE1.get()
                            )
            );
            add("mark_component2_stronghold_library",
                    new AddItemModifier(
                            new LootItemCondition[] {
                                    LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_library")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.10f).build()
                            },
                                    ModItems.MARK_PIECE2.get()
                            )
            );
            add("mark_component3_stronghold_library",
                    new AddItemModifier(
                            new LootItemCondition[] {
                                    LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_library")).build(),
                                    LootItemRandomChanceCondition.randomChance(0.01f).build()
                            },
                                    ModItems.MARK_PIECE3.get()
                            )
            );

        }
    }

