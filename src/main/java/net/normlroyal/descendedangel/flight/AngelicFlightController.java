package net.normlroyal.descendedangel.flight;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.normlroyal.descendedangel.util.WingLogic;
import net.normlroyal.descendedangel.util.WingUtils;

public class AngelicFlightController implements FlightController {

    @Override
    public void onStart(ServerPlayer p, FlightState state) {
        state.resetMotion();
        p.fallDistance = 0;
    }

    @Override
    public void onStop(ServerPlayer p, FlightState state) {
        state.resetMotion();
    }

    @Override
    public void tick(ServerPlayer p, FlightState state, FlightInput input) {
        ItemStack wings = WingUtils.getEquippedWings(p);
        int tier = WingLogic.getWingTier(wings);

        double tierSpeedMul = WingFlightStats.speedMultiplier(tier);
        double tierAccelMul = WingFlightStats.accelMultiplier(tier);

        // Horizontal
        final double BASE_MAX_SPEED = input.boost() ? 1.25 : 0.95;
        final float BASE_ACCEL = input.boost() ? 0.08f : 0.06f;
        final double DRAG = 0.88;

        final double MAX_SPEED = BASE_MAX_SPEED * tierSpeedMul;
        final float ACCEL = (float) (BASE_ACCEL * tierAccelMul);

        // Vertical
        final double HOVER_TARGET = 0.0;
        final double HOVER_STEP = 0.08;
        final double FALL_DAMP = 0.08;

        final double ASCEND_TARGET = input.boost() ? 0.50 : 0.38;
        final double DESCEND_TARGET = input.boost() ? -0.55 : -0.42;
        final double VERT_STEP = 0.12;

        // Hover bob
        final double IDLE_BOB_AMPLITUDE = 0.012;
        final double IDLE_BOB_FREQ = 0.18;

        Vec3 current = p.getDeltaMovement();

        // Look-relative horizontal basis
        Vec3 look = p.getLookAngle();
        Vec3 forwardDir = new Vec3(look.x, 0, look.z);
        if (forwardDir.lengthSqr() < 1.0e-6) forwardDir = new Vec3(0, 0, 1);
        forwardDir = forwardDir.normalize();

        Vec3 rightDir = new Vec3(-forwardDir.z, 0, forwardDir.x);

        // Build movement vector from WASD input
        Vec3 desiredMove = forwardDir.scale(input.forward()).add(rightDir.scale(input.strafe()));
        boolean hasHorizontalInput = desiredMove.lengthSqr() > 1.0e-4;

        if (hasHorizontalInput) {
            desiredMove = desiredMove.normalize();
        }

        float targetSpeed = hasHorizontalInput ? (float) MAX_SPEED : 0f;
        state.forwardSpeed = approach(state.forwardSpeed, targetSpeed, ACCEL);

        Vec3 desiredHoriz = hasHorizontalInput
                ? desiredMove.scale(state.forwardSpeed)
                : Vec3.ZERO;

        Vec3 currentHoriz = new Vec3(current.x, 0, current.z);
        Vec3 newHoriz = currentHoriz.scale(DRAG).add(desiredHoriz.scale(1.0 - DRAG));

        double hLen = newHoriz.length();
        if (hLen > MAX_SPEED) {
            newHoriz = newHoriz.normalize().scale(MAX_SPEED);
        }

        // Vertical
        double y = current.y;
        boolean idleHover = !input.ascend() && !input.descend() && !hasHorizontalInput;

        if (!input.ascend() && !input.descend()) {
            if (y < 0) y *= FALL_DAMP;
            y = approachDouble(y, HOVER_TARGET, HOVER_STEP);

            // subtle bob when idle in place
            if (idleHover) {
                y += Math.sin(p.tickCount * IDLE_BOB_FREQ) * IDLE_BOB_AMPLITUDE;
            }
        } else if (input.ascend()) {
            y = approachDouble(y, ASCEND_TARGET, VERT_STEP);
        } else {
            y = approachDouble(y, DESCEND_TARGET, VERT_STEP);
        }

        p.setDeltaMovement(new Vec3(newHoriz.x, y, newHoriz.z));
        p.hurtMarked = true;
        p.fallDistance = 0;
    }

    private static float approach(float cur, float target, float step) {
        float diff = target - cur;
        if (Math.abs(diff) <= step) return target;
        return cur + Math.copySign(step, diff);
    }

    private static double approachDouble(double cur, double target, double step) {
        double diff = target - cur;
        if (Math.abs(diff) <= step) return target;
        return cur + Math.copySign(step, diff);
    }
}
