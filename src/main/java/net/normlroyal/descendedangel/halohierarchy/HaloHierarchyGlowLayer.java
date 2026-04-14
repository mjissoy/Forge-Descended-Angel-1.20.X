package net.normlroyal.descendedangel.halohierarchy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.client.render.ModRenderTypes;
import net.normlroyal.descendedangel.util.HaloHierarchyUtils;
import net.normlroyal.descendedangel.util.HaloUtils;

public class HaloHierarchyGlowLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation GLOW_TEXTURE =
            new ResourceLocation("descendedangel", "textures/misc/white.png");

    public HaloHierarchyGlowLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer target, float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer viewer = mc.player;

        if (!HaloHierarchyUtils.shouldRenderHierarchyGlow(viewer, target)) {
            return;
        }

        float intensity = HaloHierarchyUtils.getHierarchyGlowIntensity(viewer, target);
        float scale = HaloHierarchyUtils.getHierarchyGlowScale(viewer, target);
        float alpha1 = intensity * 0.10f;
        float alpha2 = intensity * 0.20f;
        float alpha3 = intensity * 0.13f;

        System.out.println("viewer=" + HaloUtils.getEquippedHaloTier(viewer)
                + " target=" + HaloUtils.getEquippedHaloTier(target)
                + " gap=" + HaloUtils.getTierGap(viewer, target)
                + " intensity=" + HaloHierarchyUtils.getHierarchyGlowIntensity(viewer, target));

        PlayerModel<AbstractClientPlayer> model = this.getParentModel();
        model.prepareMobModel(target, limbSwing, limbSwingAmount, partialTick);
        model.setupAnim(target, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        VertexConsumer consumer = buffer.getBuffer(ModRenderTypes.haloHierarchyGlow(GLOW_TEXTURE));

        poseStack.pushPose();
        poseStack.scale(scale + 0.05F, scale + 0.05F, scale + 0.05F);
        model.renderToBuffer(
                poseStack,
                consumer,
                0xF000F0,
                OverlayTexture.NO_OVERLAY,
                1.00F, 0.97F, 0.80F,
                alpha1
        );
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.scale(scale + 0.02F, scale + 0.02F, scale + 0.02F);
        model.renderToBuffer(
                poseStack,
                consumer,
                0xF000F0,
                OverlayTexture.NO_OVERLAY,
                1.00F, 0.88F, 0.35F,
                alpha2
        );
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        model.renderToBuffer(
                poseStack,
                consumer,
                0xF000F0,
                OverlayTexture.NO_OVERLAY,
                1.00F, 0.88F, 0.25F,
                alpha3
        );
        poseStack.popPose();

    }
}