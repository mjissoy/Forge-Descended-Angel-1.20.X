package net.normlroyal.descendedangel.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.flight.AngelicFlightController;
import net.normlroyal.descendedangel.flight.ClientFlightState;
import net.normlroyal.descendedangel.flight.FlightInput;
import net.normlroyal.descendedangel.haloabilities.ClientAbilityState;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.FlightInputC2SPacket;
import net.normlroyal.descendedangel.network.packets.RequestAbilityCooldownC2SPacket;
import net.normlroyal.descendedangel.network.packets.ToggleFlightC2SPacket;
import net.normlroyal.descendedangel.network.packets.TryStartGlideC2SPacket;
import net.normlroyal.descendedangel.network.packets.UseHaloAbilityC2SPacket;
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

    private static float lastForward = 0.0F;
    private static float lastStrafe = 0.0F;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            ClientFlightState.reset();
            resetLastFlightInput();
            return;
        }

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
            if (selected == null) {
                return;
            }

            ModNetwork.CHANNEL.sendToServer(new UseHaloAbilityC2SPacket(selected.ordinal()));
        }

        ticksSinceLastJumpPress++;

        boolean jumpDown = mc.options.keyJump.isDown();
        boolean jumpPressedThisTick = jumpDown && !wasJumpDown;
        wasJumpDown = jumpDown;

        // Custom Flight Toggle / T1 Glide
        if (jumpPressedThisTick) {
            ItemStack wings = WingUtils.getEquippedWings(mc.player);

            if (!wings.isEmpty()) {
                int tier = WingLogic.getWingTier(wings);

                if (tier == 1) {
                    // Start elytra-style glide.
                    if (mc.screen == null && canStartGlide(mc.player)) {
                        mc.player.startFallFlying();
                        ModNetwork.CHANNEL.sendToServer(new TryStartGlideC2SPacket());
                    }
                    ticksSinceLastJumpPress = 999;
                } else if (WingLogic.allowsCustomFlight(wings)) {
                    // Start/stop custom flight with the existing double-tap gesture.
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

        ItemStack wings = WingUtils.getEquippedWings(mc.player);
        boolean hasCustomFlightWings = !wings.isEmpty() && WingLogic.allowsCustomFlight(wings);

        if (!hasCustomFlightWings || !mc.player.isAlive()) {
            if (ClientFlightState.isActive()) {
                ClientFlightState.setActive(false);
            }
            resetLastFlightInput();
            return;
        }

        FlightInput input = readFlightInput(mc);
        ClientFlightState.setInput(input);

        if (ClientFlightState.isActive()) {
            // Multiplayer fix: predict the same motion locally that the server applies authoritatively.
            // Without this, vanilla client gravity pulls the local player down between server corrections.
            AngelicFlightController.apply(mc.player, ClientFlightState.state(), input);

            // Send every active tick. This prevents stale input after activation, packet loss, or lag spikes.
            sendFlightInput(input);
        } else if (inputChanged(input)) {
            // Not active yet, but keep the server warm when the user changes keys.
            sendFlightInput(input);
        }
    }

    private static FlightInput readFlightInput(Minecraft mc) {
        boolean ascend = mc.options.keyJump.isDown();
        boolean descend = mc.options.keyShift.isDown();
        boolean boost = ClientKeybinds.FLIGHT_BOOST.isDown();

        float forward = 0.0F;
        if (mc.options.keyUp.isDown()) {
            forward += 1.0F;
        }
        if (mc.options.keyDown.isDown()) {
            forward -= 1.0F;
        }

        float strafe = 0.0F;
        if (mc.options.keyLeft.isDown()) {
            strafe -= 1.0F;
        }
        if (mc.options.keyRight.isDown()) {
            strafe += 1.0F;
        }

        return new FlightInput(ascend, descend, boost, forward, strafe).sanitized();
    }

    private static boolean inputChanged(FlightInput input) {
        return input.ascend() != lastAscend
                || input.descend() != lastDescend
                || input.boost() != lastBoost
                || input.forward() != lastForward
                || input.strafe() != lastStrafe;
    }

    private static void sendFlightInput(FlightInput input) {
        lastAscend = input.ascend();
        lastDescend = input.descend();
        lastBoost = input.boost();
        lastForward = input.forward();
        lastStrafe = input.strafe();

        ModNetwork.CHANNEL.sendToServer(new FlightInputC2SPacket(
                input.ascend(),
                input.descend(),
                input.boost(),
                input.forward(),
                input.strafe()
        ));
    }

    private static void resetLastFlightInput() {
        lastAscend = false;
        lastDescend = false;
        lastBoost = false;
        lastForward = 0.0F;
        lastStrafe = 0.0F;
    }

    private static boolean canStartGlide(LocalPlayer player) {
        return !player.onGround()
                && !player.isInWater()
                && !player.hasEffect(MobEffects.LEVITATION)
                && !player.isPassenger()
                && !player.isFallFlying()
                && player.getDeltaMovement().y < 0.0D;
    }
}
