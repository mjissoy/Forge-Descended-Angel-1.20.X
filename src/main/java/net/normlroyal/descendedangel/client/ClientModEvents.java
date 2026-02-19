package net.normlroyal.descendedangel.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.block.ModBlockEntities;
import net.normlroyal.descendedangel.client.render.AltarRenderer;
import net.normlroyal.descendedangel.client.render.HaloCurioRenderer;
import net.normlroyal.descendedangel.client.render.WingCurioRenderer;
import net.normlroyal.descendedangel.item.ModItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;


@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Curios halo renderers
            CuriosRendererRegistry.register(ModItems.HALO_T1.get(), HaloCurioRenderer::new);
            CuriosRendererRegistry.register(ModItems.HALO_T2.get(), HaloCurioRenderer::new);
            CuriosRendererRegistry.register(ModItems.HALO_T3.get(), HaloCurioRenderer::new);
            CuriosRendererRegistry.register(ModItems.HALO_T4.get(), HaloCurioRenderer::new);
            CuriosRendererRegistry.register(ModItems.HALO_T5.get(), HaloCurioRenderer::new);
            CuriosRendererRegistry.register(ModItems.HALO_T6.get(), HaloCurioRenderer::new);
            CuriosRendererRegistry.register(ModItems.HALO_T7.get(), HaloCurioRenderer::new);
            CuriosRendererRegistry.register(ModItems.HALO_T8.get(), HaloCurioRenderer::new);
            CuriosRendererRegistry.register(ModItems.HALO_T9.get(), HaloCurioRenderer::new);

            // Altar Block Renderer
            BlockEntityRenderers.register(ModBlockEntities.ALTAR.get(), AltarRenderer::new);

            // Wings
            CuriosRendererRegistry.register(ModItems.WING1.get(), WingCurioRenderer::new);
            CuriosRendererRegistry.register(ModItems.WING2.get(), WingCurioRenderer::new);
            CuriosRendererRegistry.register(ModItems.WING3.get(), WingCurioRenderer::new);
        });
    }
}
