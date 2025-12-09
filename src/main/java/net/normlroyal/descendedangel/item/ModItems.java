package net.normlroyal.descendedangel.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.custom.TieredHaloItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DescendedAngel.MOD_ID);

    //Items
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

    //Halos
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

    //Wings


    public static void register(IEventBus eventbus) {
        ITEMS.register(eventbus);
    }
}
