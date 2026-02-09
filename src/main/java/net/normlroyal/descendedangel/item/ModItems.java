package net.normlroyal.descendedangel.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.block.ModBlocks;
import net.normlroyal.descendedangel.entity.ModEntities;
import net.normlroyal.descendedangel.item.custom.AltarItem;
import net.normlroyal.descendedangel.item.custom.NecklaceCuriosItem;
import net.normlroyal.descendedangel.item.custom.RingCuriosItem;
import net.normlroyal.descendedangel.item.custom.TieredHaloItem;
import net.normlroyal.descendedangel.item.custom.enums.NecklaceVariants;
import net.normlroyal.descendedangel.item.custom.enums.RingVariants;
import net.normlroyal.descendedangel.item.custom.writings.SacredWritingsItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DescendedAngel.MOD_ID);

    // Materials
    public static final RegistryObject<Item> ANGELFEATHER = ITEMS.register("angel_feather",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REALANGELFEATHER = ITEMS.register("angel_feather_real",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VOIDTEAR = ITEMS.register("void_tear",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DEMONHEART = ITEMS.register("demon_heart",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPATIALCORE = ITEMS.register("spatial_core",
            () -> new Item(new Item.Properties()));

    // Ores
    public static final RegistryObject<Item> SACREDOREINGOT = ITEMS.register("sacred_ore_ingot",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SACREDORERAW = ITEMS.register("sacred_ore_raw",
            () -> new Item(new Item.Properties()));

    // Halos
    public static final RegistryObject<Item> HALO_T1 = ITEMS.register("halo_t1",
            () -> new TieredHaloItem(new Item.Properties().stacksTo(1), 1));
    public static final RegistryObject<Item> HALO_T2 = ITEMS.register("halo_t2",
            () -> new TieredHaloItem(new Item.Properties().stacksTo(1), 2));
    public static final RegistryObject<Item> HALO_T3 = ITEMS.register("halo_t3",
            () -> new TieredHaloItem(new Item.Properties().stacksTo(1), 3));
    public static final RegistryObject<Item> HALO_T4 = ITEMS.register("halo_t4",
            () -> new TieredHaloItem(new Item.Properties().stacksTo(1), 4));
    public static final RegistryObject<Item> HALO_T5 = ITEMS.register("halo_t5",
            () -> new TieredHaloItem(new Item.Properties().stacksTo(1), 5));
    public static final RegistryObject<Item> HALO_T6 = ITEMS.register("halo_t6",
            () -> new TieredHaloItem(new Item.Properties().stacksTo(1), 6));
    public static final RegistryObject<Item> HALO_T7 = ITEMS.register("halo_t7",
            () -> new TieredHaloItem(new Item.Properties().stacksTo(1), 7));
    public static final RegistryObject<Item> HALO_T8 = ITEMS.register("halo_t8",
            () -> new TieredHaloItem(new Item.Properties().stacksTo(1), 8));
    public static final RegistryObject<Item> HALO_T9 = ITEMS.register("halo_t9",
            () -> new TieredHaloItem(new Item.Properties().stacksTo(1), 9));

    // Wings


    // Trinkets
    public static final RegistryObject<Item> HOLY_RING = ITEMS.register("holy_ring",
            () -> new RingCuriosItem(RingVariants.HOLY, new Item.Properties()));
    public static final RegistryObject<Item> CLOUD_RING = ITEMS.register("cloud_ring",
            () -> new RingCuriosItem(RingVariants.CLOUD, new Item.Properties()));
    public static final RegistryObject<Item> FLAME_RING = ITEMS.register("flame_ring",
            () -> new RingCuriosItem(RingVariants.FLAME, new Item.Properties()));
    public static final RegistryObject<Item> HOLY_NECKLACE = ITEMS.register("holy_necklace",
            () -> new NecklaceCuriosItem(NecklaceVariants.HOLY, new Item.Properties()));
    public static final RegistryObject<Item> MESSENGER_PENDANT = ITEMS.register("messenger_pendant",
            () -> new NecklaceCuriosItem(NecklaceVariants.MESSENGER, new Item.Properties()));
    public static final RegistryObject<Item> LIGHTNESS_NECKLACE = ITEMS.register("nanos_lantern",
            () -> new NecklaceCuriosItem(NecklaceVariants.LIGHTNESS, new Item.Properties()));

    // Altar Item
    public static final RegistryObject<Item> ALTAR = ITEMS.register("altar",
            () -> new AltarItem(ModBlocks.ALTAR.get(), new Item.Properties()));

    // Other Items
    public static final RegistryObject<Item> SACRED_WRITINGS = ITEMS.register("sacred_writings",
            () -> new SacredWritingsItem(new Item.Properties()));
    public static final RegistryObject<Item> VOID_ANOMALY_SPAWN_EGG = ITEMS.register("void_anomaly_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.VOID_ANOMALY,
                    0x320F3B,
                    0x6780AC,
                    new Item.Properties()));
    public static final RegistryObject<Item> IMP_SPAWN_EGG = ITEMS.register("imp_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.IMP,
                    0x7A1C1C,
                    0xD46B2E,
                    new Item.Properties()));

    public static void register(IEventBus eventbus) {
        ITEMS.register(eventbus);
    }
}
