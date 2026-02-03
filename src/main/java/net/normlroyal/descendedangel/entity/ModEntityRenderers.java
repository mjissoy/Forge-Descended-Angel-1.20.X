package net.normlroyal.descendedangel.entity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.client.render.ImpRenderer;
import net.normlroyal.descendedangel.client.render.VoidAnomalyRenderer;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                ModEntities.VOID_ANOMALY.get(),
                VoidAnomalyRenderer::new
        );
        event.registerEntityRenderer(
                ModEntities.IMP.get(),
                ImpRenderer::new
        );
    }

}
