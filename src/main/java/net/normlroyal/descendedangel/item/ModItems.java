package net.normlroyal.descendedangel.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DescendedAngel.MOD_ID);

    public static final RegistryObject<Item> ANGELFEATHER = ITEMS.register("angel_feather",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> HALO = ITEMS.register("angel_halo",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventbus) {
        ITEMS.register(eventbus);
    }
}
