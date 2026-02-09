package net.normlroyal.descendedangel.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.block.ModBlocks;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DescendedAngel.MOD_ID);

    public static final RegistryObject<CreativeModeTab> Descended_Angel_Tab = CREATIVE_MOD_TABS.register("angel_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ANGELFEATHER.get()))
                    .title(Component.translatable("creativetab.angel_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModItems.ANGELFEATHER.get());
                        output.accept(ModItems.REALANGELFEATHER.get());
                        output.accept(ModItems.VOIDTEAR.get());
                        output.accept(ModItems.DEMONHEART.get());
                        output.accept(ModItems.SPATIALCORE.get());

                        output.accept(ModBlocks.SACRED_ORE.get());
                        output.accept(ModItems.SACREDORERAW.get());
                        output.accept(ModBlocks.RAW_SACRED_ORE_BLOCK.get());
                        output.accept(ModItems.SACREDOREINGOT.get());
                        output.accept(ModBlocks.SACRED_INGOT_BLOCK.get());

                        output.accept(ModItems.ALTAR.get());

                        output.accept(ModItems.HALO_T1.get());
                        output.accept(ModItems.HALO_T2.get());
                        output.accept(ModItems.HALO_T3.get());
                        output.accept(ModItems.HALO_T4.get());
                        output.accept(ModItems.HALO_T5.get());
                        output.accept(ModItems.HALO_T6.get());
                        output.accept(ModItems.HALO_T7.get());
                        output.accept(ModItems.HALO_T8.get());
                        output.accept(ModItems.HALO_T9.get());

                        output.accept(ModItems.HOLY_RING.get());
                        output.accept(ModItems.CLOUD_RING.get());
                        output.accept(ModItems.FLAME_RING.get());
                        output.accept(ModItems.CURE_RING.get());
                        output.accept(ModItems.HOLY_NECKLACE.get());
                        output.accept(ModItems.MESSENGER_PENDANT.get());
                        output.accept(ModItems.LIGHTNESS_NECKLACE.get());
                        output.accept(ModItems.BOOSTER_NECKLACE.get());

                        output.accept(ModBlocks.BLESSED_ROCK.get());
                        output.accept(ModBlocks.BLESSED_ROCK_STAIR.get());
                        output.accept(ModBlocks.BLESSED_ROCK_SLAB.get());
                        output.accept(ModBlocks.BLESSED_ROCK_WALL.get());
                        output.accept(ModBlocks.POLISHED_BLESSED_ROCK.get());
                        output.accept(ModBlocks.POLISHED_BLESSED_ROCK_STAIR.get());
                        output.accept(ModBlocks.POLISHED_BLESSED_ROCK_SLAB.get());
                        output.accept(ModBlocks.POLISHED_BLESSED_ROCK_WALL.get());

                        output.accept(ModBlocks.ASHEN_ROCK.get());
                        output.accept(ModBlocks.ASHEN_ROCK_STAIR.get());
                        output.accept(ModBlocks.ASHEN_ROCK_SLAB.get());
                        output.accept(ModBlocks.ASHEN_ROCK_WALL.get());
                        output.accept(ModBlocks.POLISHED_ASHEN_ROCK.get());
                        output.accept(ModBlocks.POLISHED_ASHEN_ROCK_STAIR.get());
                        output.accept(ModBlocks.POLISHED_ASHEN_ROCK_SLAB.get());
                        output.accept(ModBlocks.POLISHED_ASHEN_ROCK_WALL.get());

                        output.accept(ModItems.SACRED_WRITINGS.get());
                        output.accept(ModItems.VOID_ANOMALY_SPAWN_EGG.get());
                        output.accept(ModItems.IMP_SPAWN_EGG.get());

                    })
                    .build());

    public static void register(IEventBus eventbus) {
        CREATIVE_MOD_TABS.register(eventbus);
    }
}
