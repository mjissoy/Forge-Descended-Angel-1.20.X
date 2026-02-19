package net.normlroyal.descendedangel.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.client.model.WingItemModel;
import net.normlroyal.descendedangel.item.custom.TieredWingItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WingItemRenderer extends GeoItemRenderer<TieredWingItem> {

    public WingItemRenderer() {
        super(new WingItemModel());
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
            poseStack.translate(0.2F, -0.15F, 0.0F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }

        super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

}
