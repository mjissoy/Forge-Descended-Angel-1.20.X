package net.normlroyal.descendedangel.common.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.normlroyal.descendedangel.DescendedAngel;

public class VoidSkeletonAnomalyRenderer extends SkeletonRenderer {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(DescendedAngel.MOD_ID, "textures/entity/void_anomaly_skeleton.png");

    public VoidSkeletonAnomalyRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractSkeleton entity) {
        return TEXTURE;
    }
}
