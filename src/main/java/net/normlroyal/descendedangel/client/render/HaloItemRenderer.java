package net.normlroyal.descendedangel.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.client.model.HaloItemModel;
import net.normlroyal.descendedangel.item.custom.TieredHaloItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class HaloItemRenderer extends GeoItemRenderer<TieredHaloItem> {
    public HaloItemRenderer() {
        super(new HaloItemModel());
    }

    @Override
    public void renderByItem(ItemStack stack,
                             ItemDisplayContext displayContext,
                             PoseStack poseStack,
                             MultiBufferSource buffer,
                             int packedLight,
                             int packedOverlay) {

        poseStack.pushPose();

        if (displayContext == ItemDisplayContext.GUI) {
            poseStack.translate(0.05F, -0.4F, 0.0F);
            poseStack.scale(0.9F, 0.9F, 0.9F);
        }

        super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

}
