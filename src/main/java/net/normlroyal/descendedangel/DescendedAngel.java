package net.normlroyal.descendedangel;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.normlroyal.descendedangel.block.ModBlockEntities;
import net.normlroyal.descendedangel.block.ModBlocks;
import net.normlroyal.descendedangel.client.sounds.ModSounds;
import net.normlroyal.descendedangel.config.lootmodifier.ModLootModifiers;
import net.normlroyal.descendedangel.entity.ModEntities;
import net.normlroyal.descendedangel.item.custom.writings.WritTypeRegistry;
import net.normlroyal.descendedangel.menu.ModMenus;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.item.ModCreativeModTabs;
import net.normlroyal.descendedangel.item.ModItems;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.particle.ModParticles;
import net.normlroyal.descendedangel.recipe.ModRecipeSerializers;
import net.normlroyal.descendedangel.recipe.ModRecipeTypes;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

@Mod(DescendedAngel.MOD_ID)
public class DescendedAngel
{
    public static final String MOD_ID = "descendedangel";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DescendedAngel(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        context.registerConfig(
                ModConfig.Type.SERVER,
                ModConfigs.COMMON_SPEC,
                MOD_ID + "-server.toml"
        );

        ModCreativeModTabs.register(modEventBus);
        GeckoLib.initialize();

        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModMenus.register(modEventBus);
        ModRecipeSerializers.SERIALIZERS.register(modEventBus);
        ModRecipeTypes.TYPES.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        ModNetwork.registerPackets();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        ModParticles.PARTICLES.register(modEventBus);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(WritTypeRegistry::registerDefaults);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
