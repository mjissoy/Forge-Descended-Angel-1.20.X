package net.normlroyal.descendedangel.flight;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

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
        // Horizontal
        final double MAX_SPEED = input.boost() ? 1.25 : 0.95;
        final float ACCEL = input.boost() ? 0.08f : 0.06f;
        final double DRAG = 0.93;

        // Vertical
        final double HOVER_TARGET = 0.02;
        final double HOVER_STEP = 0.08;
        final double FALL_DAMP = 0.08;

        final double ASCEND_TARGET = input.boost() ? 0.50 : 0.38;
        final double DESCEND_TARGET = input.boost() ? -0.55 : -0.42;
        final double VERT_STEP = 0.12;

        // Flap pulse
        final double FLAP_AMPLITUDE = input.ascend() ? 0.02 : 0.012;
        final double FLAP_FREQ = 0.30;

        Vec3 current = p.getDeltaMovement();

        // Horizontal inertial movement
        Vec3 look = p.getLookAngle();
        Vec3 lookHoriz = new Vec3(look.x, 0, look.z);
        if (lookHoriz.lengthSqr() < 1.0e-6) lookHoriz = new Vec3(0, 0, 1);
        lookHoriz = lookHoriz.normalize();

        state.forwardSpeed = approach(state.forwardSpeed, (float) MAX_SPEED, ACCEL);

        Vec3 desiredHoriz = lookHoriz.scale(state.forwardSpeed);
        Vec3 currentHoriz = new Vec3(current.x, 0, current.z);
        Vec3 newHoriz = currentHoriz.scale(DRAG).add(desiredHoriz.scale(1.0 - DRAG));

        double hLen = newHoriz.length();
        if (hLen > MAX_SPEED) newHoriz = newHoriz.normalize().scale(MAX_SPEED);

        // Vertical buoyancy and flap
        double y = current.y;

        // gentle flap pulse
        boolean moving = hLen > 0.08;
        float flapTarget = (input.ascend() || moving) ? 1f : 0f;
        state.flapAmount = approach(state.flapAmount, flapTarget, 0.05f);

        double flap = 0.0;
        if (state.flapAmount > 0.001f) {
            flap = Math.sin(p.tickCount * FLAP_FREQ) * FLAP_AMPLITUDE * state.flapAmount;
        }

        if (!input.ascend() && !input.descend()) {
            if (y < 0) y *= FALL_DAMP;
            y = approachDouble(y, HOVER_TARGET, HOVER_STEP);
            y += flap;
        } else if (input.ascend()) {
            y = approachDouble(y, ASCEND_TARGET, VERT_STEP);
            y += flap;
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
