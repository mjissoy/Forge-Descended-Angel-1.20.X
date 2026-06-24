package net.normlroyal.descendedangel.common.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class VoidSkeletonAnomalyRenderer extends SkeletonRenderer {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/skeleton/skeleton.png");

    public VoidSkeletonAnomalyRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractSkeleton entity) {
        return TEXTURE;
    }
}
