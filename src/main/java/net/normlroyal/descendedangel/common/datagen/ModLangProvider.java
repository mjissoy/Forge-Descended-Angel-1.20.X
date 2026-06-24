package net.normlroyal.descendedangel.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        // Core materials
        add("item.descendedangel.angel_feather", "Artificial Angel Feather");
        add("item.descendedangel.angel_feather_real", "Sacred Angel Feather");
        add("item.descendedangel.demon_heart", "Demon Heart");
        add("item.descendedangel.purified_demon_heart", "Purified Demon Heart");
        add("item.descendedangel.void_tear", "Void Droplet");
        add("item.descendedangel.compressed_void", "Void Sphere");
        add("item.descendedangel.void_matrix", "Void Matrix");
        add("item.descendedangel.spatial_core", "Core of Space");
        add("item.descendedangel.sacred_writings", "Sacred Text");
        add("item.descendedangel.sacred_blood", "Sacred Blood");
        add("item.descendedangel.angels_tears", "Angel Tears");

        // Destiny spear
        add("item.descendedangel.destiny_spear_head", "Destiny Spear Head");
        add("item.descendedangel.destiny_spear_shaft", "Destiny Spear Shaft");
        add("item.descendedangel.destiny_spear", "Destiny Spear");

        // Entities and spawn eggs
        add("entity.descendedangel.void_anomaly", "Void Anomaly");
        add("item.descendedangel.void_anomaly_spawn_egg", "Void Anomaly Spawn Egg");
        add("entity.descendedangel.imp", "Imp");
        add("item.descendedangel.imp_spawn_egg", "Imp Spawn Egg");
        add("entity.descendedangel.seraphic_mirage", "Seraphic Mirage");

        // Plants and decorative blocks
        add("block.descendedangel.angel_weeping", "Angel Weeping");

        // Sacred ore set
        add("item.descendedangel.sacred_ore_ingot", "Sacred Ingot");
        add("item.descendedangel.sacred_ore_raw", "Raw Sacred Ore");
        add("block.descendedangel.sacred_ingot_block", "Sacred Ingot Block");
        add("block.descendedangel.raw_sacred_ore_block", "Raw Sacred Ore Block");
        add("block.descendedangel.sacred_ore", "Sacred Ore");

        // Blessed stone set
        add("block.descendedangel.blessed_rock", "Blessed Rock");
        add("block.descendedangel.blessed_rock_slab", "Blessed Rock Slab");
        add("block.descendedangel.blessed_rock_stair", "Blessed Rock Stair");
        add("block.descendedangel.blessed_rock_wall", "Blessed Rock Wall");
        add("block.descendedangel.blessed_rock_bricks", "Blessed Rock Bricks");
        add("block.descendedangel.mossy_blessed_rock_bricks", "Mossy Blessed Bricks");
        add("block.descendedangel.blessed_rock_brick_slab", "Blessed Brick Slab");
        add("block.descendedangel.blessed_rock_brick_stair", "Blessed Brick Stair");
        add("block.descendedangel.blessed_rock_brick_wall", "Blessed Brick Wall");
        add("block.descendedangel.polished_blessed_rock", "Polished Blessed Rock");
        add("block.descendedangel.polished_blessed_rock_slab", "Polished Blessed Rock Slab");
        add("block.descendedangel.polished_blessed_rock_stair", "Polished Blessed Rock Stair");
        add("block.descendedangel.polished_blessed_rock_wall", "Polished Blessed Rock Wall");

        // Ashen stone set
        add("block.descendedangel.ashen_rock", "Ashen Rock");
        add("block.descendedangel.ashen_rock_slab", "Ashen Rock Slab");
        add("block.descendedangel.ashen_rock_stair", "Ashen Rock Stair");
        add("block.descendedangel.ashen_rock_wall", "Ashen Rock Wall");
        add("block.descendedangel.ashen_rock_bricks", "Ashen Rock Bricks");
        add("block.descendedangel.mossy_ashen_rock_bricks", "Mossy Ashen Bricks");
        add("block.descendedangel.ashen_rock_brick_slab", "Ashen Brick Slab");
        add("block.descendedangel.ashen_rock_brick_stair", "Ashen Brick Stair");
        add("block.descendedangel.ashen_rock_brick_wall", "Ashen Brick Wall");
        add("block.descendedangel.polished_ashen_rock", "Polished Ashen Rock");
        add("block.descendedangel.polished_ashen_rock_slab", "Polished Ashen Rock Slab");
        add("block.descendedangel.polished_ashen_rock_stair", "Polished Ashen Rock Stair");
        add("block.descendedangel.polished_ashen_rock_wall", "Polished Ashen Rock Wall");

        // Void blocks
        add("block.descendedangel.void_cave_wall", "Void Wall");

        // Altar
        add("container.descendedangel.altar", "Altar");
        add("block.descendedangel.altar", "Altar");
        add("jei.descendedangel.altar_rite", "Altar Rite");
        add("altar.descendedangel.ascendance", "Ascend");
        add("altar.descendedangel.consecration", "Consecrate");
        add("altar.descendedangel.imbuement", "Imbue");
        add("altar.descendedangel.rite", "Bless");

        // Halos
        add("item.descendedangel.halo_t1", "Halo");
        add("item.descendedangel.halo_t2", "Archangel Halo");
        add("item.descendedangel.halo_t3", "Principality Halo");
        add("item.descendedangel.halo_t4", "Power Halo");
        add("item.descendedangel.halo_t5", "Virtue Halo");
        add("item.descendedangel.halo_t6", "Dominion Halo");
        add("item.descendedangel.halo_t7", "Cherubim Halo");
        add("item.descendedangel.halo_t8", "Throne Halo");
        add("item.descendedangel.halo_t9", "Seraphim Halo");

        // Wings
        add("item.descendedangel.wing_t1", "Holy Wings");
        add("item.descendedangel.wing_t2", "Angelic Wings");
        add("item.descendedangel.wing_t3", "Sacred Wings");

        // Necklaces
        add("item.descendedangel.holy_necklace", "Holy Necklace");
        add("item.descendedangel.messenger_pendant", "Messenger's Pendant");
        add("tooltip.descendedangel.messenger_pendant.lore", "A gilded pendant once bestowed upon Sacred Gabriel.");
        add("item.descendedangel.nanos_lantern", "Lantern of Nagel");
        add("tooltip.descendedangel.nanos_lantern.lore", "A blessed lantern prayed for by a Saint.");
        add("tooltip.descendedangel.nanos_lantern.effect", "Grants night vision.");
        add("item.descendedangel.alchemy_chain", "Alchemical Locket");
        add("tooltip.descendedangel.alchemy_chain.lore", "The divine Authority of BOOST descends.");
        add("tooltip.descendedangel.alchemy_chain.effect", "Boosts most Potion Effects.");

        // Rings
        add("item.descendedangel.holy_ring", "Holy Ring");
        add("item.descendedangel.cloud_ring", "Storm Ring");
        add("tooltip.descendedangel.cloud_ring.lore", "Born of the Wrath of Heaven, forged by the Dominions.");
        add("item.descendedangel.flame_ring", "Burning Ring");
        add("tooltip.descendedangel.flame_ring.lore", "Modeled after Sacred Michael's Sword, the Ring ignites the wearer's foes.");
        add("item.descendedangel.cure_ring", "Life Ring");
        add("tooltip.descendedangel.cure_ring.lore", "The Love and Grace of the Lord cures all your aliments.");

        // Creative tabs
        add("creativetab.angel_tab", "Descended Angel Items");
        add("creativetab.angel_blocks_tab", "Descended Angel Blocks");

        // Halo tooltips
        add("tooltip.descendedangel.halo.when_worn", "When worn:");
        add("tooltip.descendedangel.halo.undead_damage", "+%s%% Damage vs Undead");
        add("tooltip.descendedangel.halo.healing_bonus", "+%s%% Healing Received");
        add("tooltip.descendedangel.halo.hold_shift", "Hold [SHIFT] for lore.");
        add("tooltip.descendedangel.halo_t1.lore", "A new light ignites the world.");
        add("tooltip.descendedangel.halo_t2.lore", "The foundation of a new order.");
        add("tooltip.descendedangel.halo_t3.lore", "Rules bind your higher existence.");
        add("tooltip.descendedangel.halo_t4.lore", "Light now courses through every remnant of your being.");
        add("tooltip.descendedangel.halo_t5.lore", "Willpower shapes light into its truest form.");
        add("tooltip.descendedangel.halo_t6.lore", "Illuminate the darkness with your transcendent light.");
        add("tooltip.descendedangel.halo_t7.lore", "You stand as a Gateway for the Lord.");
        add("tooltip.descendedangel.halo_t8.lore", "With You the Lord begins to move.");
        add("tooltip.descendedangel.halo_t9.lore", "An attendant of the Lord, You behold His utmost radiance.");

        // Wing tooltips
        add("tooltip.descendedangel.wing_t1.lore", "Holy Wings powered by a newborn Divinity.");
        add("tooltip.descendedangel.wing_t2.lore", "True Wings, belonging in the Host of Heavenly Angels.");
        add("tooltip.descendedangel.wing_t3.lore", "Sacred and Adored, the Love of the Lord floods these Wings.");

        // Mark of Cain
        add("item.descendedangel.mark_component1", "Slice of Cain's Mark");
        add("item.descendedangel.mark_component2", "Part of Cain's Mark");
        add("item.descendedangel.mark_component3", "Fragment of Cain's Mark");
        add("item.descendedangel.mark_of_cain", "Mark Of Cain (Replica)");
        add("item.descendedangel.purified_mark_of_cain", "Cleansed Mark of Cain");
        add("tooltip.descendedangel.mark_of_cain_lore", "A Replica of the Curse cast upon the first mortal Killer.");
        add("tooltip.descendedangel.mark_of_cain_not_ready", "Insufficient Charge.");
        add("tooltip.descendedangel.mark_of_cain_ready", "Fuelled and Ready.");

        // JEI halo requirements
        add("altar_jei.halo_t0.name", "No Halo Requirements");
        add("altar_jei.halo_t1.name", "Requires Angel Halo or higher");
        add("altar_jei.halo_t2.name", "Requires Archangel Halo or higher");
        add("altar_jei.halo_t3.name", "Requires Principality Halo or higher");
        add("altar_jei.halo_t4.name", "Requires Power Halo or higher");
        add("altar_jei.halo_t5.name", "Requires Virtue Halo or higher");
        add("altar_jei.halo_t6.name", "Requires Dominion Halo or higher");
        add("altar_jei.halo_t7.name", "Requires Cherubim Halo or higher");
        add("altar_jei.halo_t8.name", "Requires Throne Halo or higher");
        add("altar_jei.halo_t9.name", "Requires Seraphim Halo");

        // Base ability unlock messages
        add("ability.descendedangel.space", "The Dominion of Space shatters around the layers of Your Divinity.");
        add("ability.descendedangel.time", "The Dominion of Time streams into the river of Your Divinity.");
        add("ability.descendedangel.celestial", "The Dominion of Celestial radiance opens above Your Divinity.");
        add("ability.descendedangel.resonance", "The Dominion of Resonance rings through Your Divinity.");
        add("ability.descendedangel.fire", "The Power of Fire strengthens Your Divinity.");
        add("ability.descendedangel.water", "The Power of Water strengthens Your Divinity.");
        add("ability.descendedangel.earth", "The Power of Earth strengthens Your Divinity.");
        add("ability.descendedangel.air", "The Power of Air strengthens Your Divinity.");
        add("message.descendedangel.shard_already_unlocked", "Your Divinity has traces of this Power already.");
        add("message.descendedangel.dominion_limit_reached", "Your Divinity can only bear two Dominions.");
        add("message.descendedangel.dominion_already_unlocked", "Your Divinity already bears this Dominion.");


        // Ability unlock items
        add("item.descendedangel.fruit_of_space", "Fruit of Space");
        add("item.descendedangel.fruit_of_time", "Fruit of Time");
        add("item.descendedangel.fruit_of_celestial", "Fruit of Celestial");
        add("item.descendedangel.fruit_of_resonance", "Fruit of Resonance");
        add("item.descendedangel.fire_shard", "Fire Shard");
        add("item.descendedangel.water_shard", "Water Shard");
        add("item.descendedangel.earth_shard", "Earth Shard");
        add("item.descendedangel.air_shard", "Air Shard");

        // Temporary spell blocks
        add("block.descendedangel.temp_earth_wall", "Earth Wall Block");
        add("block.descendedangel.temp_holy_block", "Holy Spell Block");

        // Keybinds
        add("key.categories.descendedangel", "Descended Angel");
        add("key.descendedangel.open_halo_wheel", "Open Halo Wheel");
        add("key.descendedangel.use_halo_ability", "Use Selected Halo Ability");

        // Baptismal font
        add("block.descendedangel.baptismal_font", "Baptismal Font");
        add("message.descendedangel.font_already_blooded", "The Baptismal Font is already filled with Sacred Blood.");
        add("message.descendedangel.font_blooded", "The Baptismal Font drinks the Sacred Blood.");
        add("message.descendedangel.font_requires_blood", "The Baptismal Font waits for Sacred Blood.");
        add("message.descendedangel.font_fire_empowered", "The Fire Shard has been empowered.");
        add("message.descendedangel.font_air_empowered", "The Air Shard has been empowered.");
        add("message.descendedangel.font_earth_empowered", "The Earth Shard has been empowered.");
        add("message.descendedangel.font_water_empowered", "The Water Shard has been empowered.");

        // Empowered shards
        add("item.descendedangel.empowered_fire_shard", "Empowered Fire Shard");
        add("item.descendedangel.empowered_air_shard", "Empowered Air Shard");
        add("item.descendedangel.empowered_earth_shard", "Empowered Earth Shard");
        add("item.descendedangel.empowered_water_shard", "Empowered Water Shard");
        add("message.descendedangel.empowered_shard_requires_cherubim", "This empowered shard requires a Cherubim Halo or higher.");
        add("message.descendedangel.empowered_shard_future", "This empowered shard is not ready yet.");

        // Fire evolutions
        add("ability.descendedangel.sacred_flare", "Sacred Flare");
        add("ability.descendedangel.sol_corona", "Sol Corona");
        add("ability.descendedangel.pillars_of_radiance", "Pillars of Radiance");
        add("ability.descendedangel.unknown_fire_evolution", "Unknown Fire Evolution");
        add("ability.descendedangel.fire_evolved", "Your Fire Power evolves into %s.");

        // Wind evolutions
        add("ability.descendedangel.vacuum_vortex", "Vacuum Vortex");
        add("ability.descendedangel.zephyr_scythes", "Zephyr Scythes");
        add("ability.descendedangel.heavenly_downdraft", "Heavenly Downdraft");
        add("ability.descendedangel.unknown_air_evolution", "Unknown Wind Evolution");
        add("ability.descendedangel.air_evolved", "Your Wind Power evolves into %s.");

        // Earth evolutions
        add("ability.descendedangel.holy_bastion", "Holy Bastion");
        add("ability.descendedangel.aegis_pillar", "Aegis Pillar");
        add("ability.descendedangel.crystal_chrysalis", "Crystal Chrysalis");
        add("ability.descendedangel.unknown_earth_evolution", "Unknown Earth Evolution");
        add("ability.descendedangel.earth_evolved", "Your Earth Power evolves into %s.");

        // Water evolutions
        add("ability.descendedangel.moving_field_of_mist", "Moving Field of Mist");
        add("ability.descendedangel.seraphic_mirage", "Seraphic Mirage");
        add("ability.descendedangel.divine_serenity", "Divine Serenity");
        add("ability.descendedangel.water_evolved", "Your Water Power evolves into %s.");
        add("ability.descendedangel.unknown_evolution", "Unknown Evolution");

        // Celestial and Resonance Abilities
        add("ability.descendedangel.astral_lance", "Astral Lance");
        add("ability.descendedangel.heavens_map", "Heaven's Map");
        add("ability.descendedangel.resonance_pulse", "Resonance Pulse");
        add("ability.descendedangel.sacred_silence", "Sacred Silence");

        // Resonance Damage Type
        add("death.attack.resonance", "%1$s was shattered by sacred resonance.");
        add("death.attack.resonance.player", "%1$s was shattered by %2$s's sacred resonance.");
    }
}