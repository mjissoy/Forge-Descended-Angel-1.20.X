package net.normlroyal.descendedangel.content.entity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.common.client.render.ImpRenderer;
import net.normlroyal.descendedangel.common.client.render.SeraphicMirageRenderer;
import net.normlroyal.descendedangel.common.client.render.VoidAnomalyRenderer;
import net.normlroyal.descendedangel.common.client.render.VoidSkeletonAnomalyRenderer;
import net.normlroyal.descendedangel.common.client.render.VoidSlimeAnomalyRenderer;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                ModEntities.VOID_ANOMALY.get(),
                VoidAnomalyRenderer::new
        );
        event.registerEntityRenderer(
                ModEntities.VOID_SKELETON_ANOMALY.get(),
                VoidSkeletonAnomalyRenderer::new
        );
        event.registerEntityRenderer(
                ModEntities.VOID_SLIME_ANOMALY.get(),
                VoidSlimeAnomalyRenderer::new
        );
        event.registerEntityRenderer(
                ModEntities.IMP.get(),
                ImpRenderer::new
        );
        event.registerEntityRenderer(
                ModEntities.SERAPHIC_MIRAGE.get(),
                SeraphicMirageRenderer::new
        );
    }

}
