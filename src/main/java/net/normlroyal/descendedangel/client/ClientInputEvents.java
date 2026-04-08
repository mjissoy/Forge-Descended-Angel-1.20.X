package net.normlroyal.descendedangel.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.haloabilities.ClientAbilityState;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.*;
import net.normlroyal.descendedangel.util.HaloUtils;
import net.normlroyal.descendedangel.util.WingLogic;
import net.normlroyal.descendedangel.util.WingUtils;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientInputEvents {

    private static int lastTier = -1;

    private static final int DOUBLE_TAP_WINDOW_TICKS = 7;

    private static boolean wasJumpDown = false;
    private static int ticksSinceLastJumpPress = 999;

    private static boolean lastAscend = false;
    private static boolean lastDescend = false;
    private static boolean lastBoost = false;

    private static float lastForward = 0f;
    private static float lastStrafe = 0f;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ClientAbilityState.clamp(mc.player);

        int tierNow = HaloUtils.getEquippedHaloTier(mc.player);
        if (tierNow != lastTier) {
            lastTier = tierNow;
            ClientAbilityState.reset();
        }

        // Cycle ability
        while (ClientKeybinds.OPEN_WHEEL.consumeClick()) {
            ClientAbilityState.cycle(mc.player);

            HaloAbility selected = ClientAbilityState.get(mc.player);
            if (selected != null) {
                ModNetwork.CHANNEL.sendToServer(new RequestAbilityCooldownC2SPacket(selected.ordinal()));

            }
        }

        // Use ability
        while (ClientKeybinds.USE_ABILITY.consumeClick()) {
            HaloAbility selected = ClientAbilityState.get(mc.player);
            if (selected == null) return;

            ModNetwork.CHANNEL.sendToServer(new UseHaloAbilityC2SPacket(selected.ordinal()));

        }

        ticksSinceLastJumpPress++;

        boolean jumpDown = mc.options.keyJump.isDown();
        boolean jumpPressedThisTick = jumpDown && !wasJumpDown;
        wasJumpDown = jumpDown;

        // Custom Flight Toggle
        if (jumpPressedThisTick) {
            ItemStack wings = WingUtils.getEquippedWings(mc.player);

            if (!wings.isEmpty()) {
                int tier = WingLogic.getWingTier(wings);

                if (tier == 1) {
                    // start elytra glide
                    if (mc.screen == null && canStartGlide(mc.player)) {
                        mc.player.startFallFlying();
                        ModNetwork.CHANNEL.sendToServer(new TryStartGlideC2SPacket());
                    }
                    ticksSinceLastJumpPress = 999;
                } else if (WingLogic.allowsCustomFlight(wings)) {
                    // start custom flight
                    if (mc.screen == null && !mc.player.isFallFlying()) {
                        if (ticksSinceLastJumpPress <= DOUBLE_TAP_WINDOW_TICKS) {
                            ModNetwork.CHANNEL.sendToServer(new ToggleFlightC2SPacket());
                            ticksSinceLastJumpPress = 999;
                        } else {
                            ticksSinceLastJumpPress = 0;
                        }
                    }
                } else {
                    ticksSinceLastJumpPress = 999;
                }
            } else {
                ticksSinceLastJumpPress = 999;
            }
        }


        // Ascend-Descend-Boost while in custom flight
        ItemStack wings = WingUtils.getEquippedWings(mc.player);
        if (!wings.isEmpty() && WingLogic.allowsCustomFlight(wings)) {
            boolean ascend = mc.options.keyJump.isDown();
            boolean descend = mc.options.keyShift.isDown();
            boolean boost = ClientKeybinds.FLIGHT_BOOST.isDown();

            float forward = 0f;
            if (mc.options.keyUp.isDown()) forward += 1f;
            if (mc.options.keyDown.isDown()) forward -= 1f;

            float strafe = 0f;
            if (mc.options.keyLeft.isDown()) strafe -= 1f;
            if (mc.options.keyRight.isDown()) strafe += 1f;

            if (ascend != lastAscend
                    || descend != lastDescend
                    || boost != lastBoost
                    || forward != lastForward
                    || strafe != lastStrafe) {

                lastAscend = ascend;
                lastDescend = descend;
                lastBoost = boost;
                lastForward = forward;
                lastStrafe = strafe;

                ModNetwork.CHANNEL.sendToServer(new FlightInputC2SPacket(
                        ascend, descend, boost, forward, strafe
                ));
            }
        } else {
            lastAscend = lastDescend = lastBoost = false;
            lastForward = lastStrafe = 0f;
        }
    }

    private static boolean canStartGlide(LocalPlayer player) {
        return !player.onGround()
                && !player.isInWater()
                && !player.hasEffect(MobEffects.LEVITATION)
                && !player.isPassenger()
                && !player.isFallFlying()
                && player.getDeltaMovement().y < 0.0;
    }

}


