package net.normlroyal.descendedangel.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.client.ClientKeybinds;
import net.normlroyal.descendedangel.flight.ClientFlightState;
import net.normlroyal.descendedangel.flight.WingFlightStats;
import net.normlroyal.descendedangel.util.WingLogic;
import net.normlroyal.descendedangel.util.WingUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientFlightRenderEvents {

    private ClientFlightRenderEvents() {}

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();

        if (player != Minecraft.getInstance().player) return;

        if (!ClientFlightState.isActive()) {
            ClientFlightRenderState.reset();
            return;
        }

        PoseStack pose = event.getPoseStack();

        float bodyYaw = Mth.rotLerp(event.getPartialTick(), player.yBodyRotO, player.yBodyRot);
        float yawRad = bodyYaw * Mth.DEG_TO_RAD;

        Vec3 vel = player.getDeltaMovement();
        double horizontalSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);

        ItemStack wings = WingUtils.getEquippedWings(player);
        int tier = WingLogic.getWingTier(wings);

        double tierSpeedMul = WingFlightStats.speedMultiplier(tier);
        boolean boosting = ClientKeybinds.FLIGHT_BOOST != null && ClientKeybinds.FLIGHT_BOOST.isDown();

        double baseMaxSpeed = boosting ? 1.25 : 0.95;
        double maxExpectedSpeed = baseMaxSpeed * tierSpeedMul;

        float speedAlpha = (float) Mth.clamp(horizontalSpeed / maxExpectedSpeed, 0.0, 1.0);

        float minLeanDeg = 1f;
        float maxLeanDeg = 35f;

        float targetPitchDeg = Mth.lerp(speedAlpha, minLeanDeg, maxLeanDeg);

        Vec3 forward = new Vec3(-Mth.sin(yawRad), 0.0, Mth.cos(yawRad));
        Vec3 right = new Vec3(Mth.cos(yawRad), 0.0, Mth.sin(yawRad));

        double sidewaysSpeed = vel.x * right.x + vel.z * right.z;
        float bankAlpha = (float) Mth.clamp(sidewaysSpeed / maxExpectedSpeed, -1.0, 1.0);

        if (Math.abs(bankAlpha) < 0.08f) {
            bankAlpha = 0f;
        }

        float maxBankDeg = 8f;
        float targetBankDeg = -bankAlpha * maxBankDeg;

        ClientFlightRenderState.tickToward(targetPitchDeg, targetBankDeg);

        float pitchLeanDeg = ClientFlightRenderState.getPitchDeg();
        float bankDeg = ClientFlightRenderState.getBankDeg();

        Vector3f rightAxis = new Vector3f((float) right.x, 0f, (float) right.z);
        Vector3f forwardAxis = new Vector3f((float) forward.x, 0f, (float) forward.z);

        pose.translate(0.0D, 0.8D, 0.0D);

        pose.mulPose(new Quaternionf().fromAxisAngleDeg(rightAxis, pitchLeanDeg));

        pose.mulPose(new Quaternionf().fromAxisAngleDeg(forwardAxis, bankDeg));

        pose.translate(0.0D, -0.8D, 0.0D);
    }
}