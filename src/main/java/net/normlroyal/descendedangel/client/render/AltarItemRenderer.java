package net.normlroyal.descendedangel.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.client.model.AltarItemModel;
import net.normlroyal.descendedangel.item.custom.AltarItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AltarItemRenderer extends GeoItemRenderer<AltarItem> {
    public AltarItemRenderer() {
        super(new AltarItemModel());
    }

    @Override
    public void renderByItem(ItemStack stack,
                             ItemDisplayContext ctx,
                             PoseStack poseStack,
                             MultiBufferSource buffer,
                             int packedLight,
                             int packedOverlay) {

        poseStack.pushPose();

        switch (ctx) {
            case GUI -> {
                poseStack.translate(0.1D, -0.40D, 0.0D);
                poseStack.scale(0.80f, 0.80f, 0.80f);
            }
            case GROUND, FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND,
                 THIRD_PERSON_RIGHT_HAND -> {
                poseStack.scale(0.75f, 0.75f, 0.75f);
            }
            case FIXED -> {
                poseStack.scale(0.75f, 0.75f, 0.75f);
                poseStack.translate(0.15D, -0.4D, 0.0D);
            }
            default -> {
            }
        }

        super.renderByItem(stack, ctx, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
