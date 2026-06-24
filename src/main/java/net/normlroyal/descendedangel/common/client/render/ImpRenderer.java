package net.normlroyal.descendedangel.common.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.normlroyal.descendedangel.common.client.model.ImpModel;
import net.normlroyal.descendedangel.content.entity.ImpEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ImpRenderer extends GeoEntityRenderer<ImpEntity> {
    public ImpRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ImpModel());
        this.shadowRadius = 0.25F;
    }
}
