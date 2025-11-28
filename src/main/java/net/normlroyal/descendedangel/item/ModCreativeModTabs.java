package net.normlroyal.descendedangel.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DescendedAngel.MOD_ID);

    public static final RegistryObject<CreativeModeTab> Descended_Angel_Tab = CREATIVE_MOD_TABS.register("angel_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ANGELFEATHER.get()))
                    .title(Component.translatable("creativetab.angel_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.ANGELFEATHER.get());
                        output.accept(ModItems.HALO.get());
                    })
                    .build());

    public static void register(IEventBus eventbus) {
        CREATIVE_MOD_TABS.register(eventbus);
    }
}
