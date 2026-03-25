package net.normlroyal.descendedangel.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.client.model.DestinySpearItemModel;
import net.normlroyal.descendedangel.item.custom.DestinySpearItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DestinySpearRenderer extends GeoItemRenderer<DestinySpearItem> {
    public DestinySpearRenderer() {
        super(new DestinySpearItemModel());
    }

    @Override
    public void renderByItem(ItemStack stack,
                             ItemDisplayContext context,
                             PoseStack poseStack,
                             MultiBufferSource bufferSource,
                             int packedLight,
                             int packedOverlay) {

        poseStack.pushPose();

        if (context == ItemDisplayContext.GUI) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(0.4D, -0.5D, 0.0D);
        } else if (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            poseStack.translate(0.0D, -12.0D / 16.0D, 0.0D);
        }

        super.renderByItem(stack, context, poseStack, bufferSource, packedLight, packedOverlay);

        poseStack.popPose();
    }
}