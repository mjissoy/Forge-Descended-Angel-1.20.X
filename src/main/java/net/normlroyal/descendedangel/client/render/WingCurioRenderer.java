package net.normlroyal.descendedangel.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.events.useful.WingRenderContext;
import net.normlroyal.descendedangel.item.custom.TieredWingItem;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class WingCurioRenderer implements ICurioRenderer {

    private final WingItemRenderer geo = new WingItemRenderer();

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource buffer,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        LivingEntity living = slotContext.entity();
        if (!(stack.getItem() instanceof TieredWingItem)) return;

        poseStack.pushPose();
        try {
            if (renderLayerParent.getModel() instanceof HumanoidModel<?> humanoid) {
                humanoid.body.translateAndRotate(poseStack);
            }

            poseStack.translate(-0.535D, 1.2D, 0.25D);
           poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

            WingRenderContext.setEntity(living);
            try {
                geo.renderByItem(stack, ItemDisplayContext.NONE, poseStack, buffer, light, OverlayTexture.NO_OVERLAY);
            } finally {
                WingRenderContext.clear();
            }

        } catch (Exception ex) {
        } finally {
            poseStack.popPose();
        }
    }
}
