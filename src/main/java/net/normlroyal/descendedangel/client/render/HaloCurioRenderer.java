package net.normlroyal.descendedangel.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class HaloCurioRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack poseStack,
            RenderLayerParent<T, M> parent,
            MultiBufferSource buffers,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        LivingEntity entity = slotContext.entity();

        poseStack.pushPose();


        double yOffset = entity.getBbHeight() - 2.5D; // tweak -0.2D to move halo up/down
        poseStack.translate(0.0D, yOffset, 0.0D);


        float time = ageInTicks + partialTicks;
        float bob = (float) Math.sin(time / 10.0F) * 0.05F;
        poseStack.translate(0.0D, bob, 0.0D);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                entity,
                stack,
                ItemDisplayContext.HEAD,
                false,
                poseStack,
                buffers,
                entity.level(),
                light,
                OverlayTexture.NO_OVERLAY,
                0
        );

        poseStack.popPose();
    }
}
