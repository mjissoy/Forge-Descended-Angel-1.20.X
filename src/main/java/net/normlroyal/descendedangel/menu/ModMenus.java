package net.normlroyal.descendedangel.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, DescendedAngel.MOD_ID);

    public static final RegistryObject<MenuType<AltarMenu>> ALTAR_MENU =
            MENUS.register("altar_menu", () -> IForgeMenuType.create(AltarMenu::new));

    private ModMenus() {}

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
