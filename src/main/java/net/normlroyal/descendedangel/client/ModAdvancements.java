package net.normlroyal.descendedangel.client;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.block.ModBlocks;
import net.normlroyal.descendedangel.entity.ModEntities;
import net.normlroyal.descendedangel.item.ModItems;

import java.util.function.Consumer;

public class ModAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {

    private static String id(String path) {
        return DescendedAngel.MOD_ID + ":" + path;
    }

    private static DisplayInfo display(ItemLike icon, String title, String desc, FrameType frame) {
        return new DisplayInfo(
                icon.asItem().getDefaultInstance(),
                Component.literal(title),
                Component.literal(desc),
                null,
                frame,
                true,
                true,
                false
        );
    }


    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        Advancement root = Advancement.Builder.advancement()
                .display(
                        ModItems.REALANGELFEATHER.get(),
                        Component.literal("Descended Angel"),
                        Component.literal("A small guide curated by Heavens' own Metatron"),
                        ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "textures/gui/advancements/backgrounds/holybg.png"),
                        FrameType.TASK,
                        false, false, false
                )
                .addCriterion(
                        "tick", PlayerTrigger.TriggerInstance.tick()
                )
                .save(saver, DescendedAngel.MOD_ID + ":main/root");

        Advancement findVoidDrop = Advancement.Builder.advancement()
                .parent(root)
                .display(display(ModItems.VOIDTEAR.get(),
                        "The Void-Touched",
                        "Collect a Drop of the Void from Void-Touched foes.",
                        FrameType.TASK))
                .addCriterion("void_drop",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                ItemPredicate.Builder.item()
                                        .of(ModItems.VOIDTEAR.get())
                                        .build()
                        ))
                .save(saver, id("main/find_void_drop"));

        Advancement craftArtFeather = Advancement.Builder.advancement()
                .parent(findVoidDrop)
                .display(display(ModItems.ANGELFEATHER.get(),
                        "An Imitation of Light",
                        "Craft an Artificial Angel Feather",
                        FrameType.TASK))
                .addCriterion("craft_feather",
                        RecipeUnlockedTrigger.unlocked(
                                ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "angel_feather_from_crafting")
                        ))
                .save(saver, id("main/craft_art_angel_feather"));

        ResourceKey<Structure> cathedral =
                ResourceKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "ruined_cathedral"));

        Advancement findAltarStructure = Advancement.Builder.advancement()
                .parent(craftArtFeather)
                .display(display(ModBlocks.ALTAR.get(),
                        "Holy Remnants",
                        "Throughout worlds are scattered the Altars of the Lord",
                        FrameType.GOAL))
                .addCriterion("enter_shrine",
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location().setStructure(cathedral).build()
                        ))
                .save(saver, id("main/find_altar_structure"));

        Advancement craftAngelHalo = Advancement.Builder.advancement()
                .parent(findAltarStructure)
                .display(display(ModItems.HALO_T1.get(),
                        "An Angel's Grace",
                        "Craft a Halo befitting an Angel",
                        FrameType.CHALLENGE))
                .addCriterion("craft_halo",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t1_rite")
                        ))
                .save(saver, id("main/craft_angel_halo"));

        Advancement craftArchangelHalo = Advancement.Builder.advancement()
                .parent(craftAngelHalo)
                .display(display(ModItems.HALO_T2.get(),
                        "An Archangel's Ladder",
                        "The powers of Heaven guide your ascension",
                        FrameType.TASK))
                .addCriterion("craft_halo",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t2_rite")
                        ))
                .save(saver, id("main/craft_archangel_halo"));

        Advancement craftPrincipalityHalo = Advancement.Builder.advancement()
                .parent(craftArchangelHalo)
                .display(display(ModItems.HALO_T3.get(),
                        "Fundamental Princeps",
                        "The Principles of Divinity bind you",
                        FrameType.TASK))
                .addCriterion("craft_halo",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t3_rite")
                        ))
                .save(saver, id("main/craft_principality_halo"));

        Advancement craftPowerHalo = Advancement.Builder.advancement()
                .parent(craftPrincipalityHalo)
                .display(display(ModItems.HALO_T4.get(),
                        "Authority by the Lord",
                        "The Lord's Power floods your soul",
                        FrameType.GOAL))
                .addCriterion("craft_halo",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t4_rite")
                        ))
                .save(saver, id("main/craft_power_halo"));

        Advancement craftVirtueHalo = Advancement.Builder.advancement()
                .parent(craftPowerHalo)
                .display(display(ModItems.HALO_T5.get(),
                        "Righteous Purity",
                        "You embody the Seven Heavenly Virtues",
                        FrameType.GOAL))
                .addCriterion("craft_halo",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t5_rite")
                        ))
                .save(saver, id("main/craft_virtue_halo"));

        Advancement craftDominionHalo = Advancement.Builder.advancement()
                .parent(craftVirtueHalo)
                .display(display(ModItems.HALO_T6.get(),
                        "Graceful Domain",
                        "The Order of Heaven carves you a Dominion",
                        FrameType.GOAL))
                .addCriterion("craft_halo",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t6_rite")
                        ))
                .save(saver, id("main/craft_dominion_halo"));

        Advancement craftCherubimHalo = Advancement.Builder.advancement()
                .parent(craftDominionHalo)
                .display(display(ModItems.HALO_T7.get(),
                        "Fount of the World",
                        "You are the Chalice of the Lord",
                        FrameType.CHALLENGE))
                .addCriterion("craft_halo",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t7_rite")
                        ))
                .save(saver, id("main/craft_cherubim_halo"));

        Advancement craftThroneHalo = Advancement.Builder.advancement()
                .parent(craftCherubimHalo)
                .display(display(ModItems.HALO_T8.get(),
                        "Throne of the Lord",
                        "The Might of the Lord sits with you",
                        FrameType.CHALLENGE))
                .addCriterion("craft_halo",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t8_rite")
                        ))
                .save(saver, id("main/craft_throne_halo"));

        Advancement craftSeraphimHalo = Advancement.Builder.advancement()
                .parent(craftThroneHalo)
                .display(display(ModItems.HALO_T9.get(),
                        "A Seraphim's Perfection",
                        "Your Eyes burns with Divine Mercy",
                        FrameType.CHALLENGE))
                .addCriterion("craft_halo",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/halo_t9_rite")
                        ))
                .save(saver, id("main/craft_seraphim_halo"));

        Advancement.Builder sacredWritingBuilder = Advancement.Builder.advancement()
                .parent(craftArchangelHalo)
                .display(display(ModItems.SACRED_WRITINGS.get(),
                        "The Scriptures of God",
                        "Imbue upon a book knowledge from the Lord",
                        FrameType.TASK))
                .addCriterion("rainfall", RecipeCraftedTrigger.TriggerInstance.craftedItem(
                        ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_text_rainfall")))
                .addCriterion("clearskies", RecipeCraftedTrigger.TriggerInstance.craftedItem(
                        ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_text_clearskies")))
                .addCriterion("villager_spawn", RecipeCraftedTrigger.TriggerInstance.craftedItem(
                        ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_text_villager_spawn")))
                .addCriterion("village_spawn", RecipeCraftedTrigger.TriggerInstance.craftedItem(
                        ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_text_village_spawn")))
                .addCriterion("ruined_portal_spawn", RecipeCraftedTrigger.TriggerInstance.craftedItem(
                        ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_text_ruined_portal_spawn")))
                .addCriterion("wither_spawn", RecipeCraftedTrigger.TriggerInstance.craftedItem(
                        ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar/sacred_text_wither_spawn")));

        sacredWritingBuilder.requirements(RequirementsStrategy.OR);

        sacredWritingBuilder.save(saver, id("main/craft_sacred_writing"));

        Advancement findSacredOre = Advancement.Builder.advancement()
                .parent(findVoidDrop)
                .display(display(ModItems.SACREDORERAW.get(),
                        "Remenants of Divine Essence",
                        "Scattered at the depths of the World exists Sacred Ore, solidified Divine Essence",
                        FrameType.TASK))
                .addCriterion("pickup_sacredore",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                ItemPredicate.Builder.item().of(ModItems.SACREDORERAW.get()).build()
                        ))
                .save(saver, id("main/find_sacred_ore"));

        Advancement smeltSacredOre = Advancement.Builder.advancement()
                .parent(findSacredOre)
                .display(display(ModItems.SACREDOREINGOT.get(),
                        "Fit to Ascend",
                        "Smelting the Raw Sacred Ore returns its purified form, suitable for empowering a Halo",
                        FrameType.TASK))
                .addCriterion("pickup_sacredingot",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                ItemPredicate.Builder.item().of(ModItems.SACREDOREINGOT.get()).build()
                        ))
                .save(saver, id("main/smelt_sacred_ore"));

        Advancement killedImp = Advancement.Builder.advancement()
                .parent(findVoidDrop)
                .display(display(ModItems.DEMONHEART.get(),
                        "Wild Hellspawn",
                        "Oftentimes, the lowest level of Hell find ways to creep into the Nether.",
                        FrameType.GOAL))
                .addCriterion("kill_imp",
                        KilledTrigger.TriggerInstance.playerKilledEntity(
                                EntityPredicate.Builder.entity().of(ModEntities.IMP.get())
                        ))
                .save(saver, id("main/killed_imp"));

        Advancement killedVoidAnomaly = Advancement.Builder.advancement()
                .parent(findVoidDrop)
                .display(display(ModItems.VOIDTEAR.get(),
                        "Leaking Void",
                        "The Void leaks into worlds, corrupting its inhabitants and distorting them.",
                        FrameType.GOAL))
                .addCriterion("kill_void_anomaly",
                        KilledTrigger.TriggerInstance.playerKilledEntity(
                                EntityPredicate.Builder.entity().of(ModEntities.VOID_ANOMALY.get())
                        ))
                .save(saver, id("main/killed_void_anomaly"));
    }
}
