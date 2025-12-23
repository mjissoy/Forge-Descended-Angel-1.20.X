package net.normlroyal.descendedangel.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.config.DANBTS.WingNBTs;
import net.normlroyal.descendedangel.util.WingUtils;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WingEvents {

    private enum WingState { NONE, GLIDE_T1, BOOST_T2, FLIGHT_T3 }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (player.level().isClientSide) return;
        if (player.isCreative() || player.isSpectator()) return;

        int tier = WingUtils.getWingTier(player);
        WingState state = stateFromTier(tier);

        boolean changed = applyWingState(player, state);

        if (changed) player.onUpdateAbilities();
    }

    private static WingState stateFromTier(int tier) {
        if (tier >= 3) return WingState.FLIGHT_T3;
        if (tier == 2) return WingState.BOOST_T2;
        if (tier == 1) return WingState.GLIDE_T1;
        return WingState.NONE;
    }

    private static boolean applyWingState(Player player, WingState state) {
        boolean changed = false;

        if (state != WingState.GLIDE_T1 && state != WingState.BOOST_T2) {
            player.getPersistentData().remove(WingNBTs.T1_GLIDE_UNTIL);
        }

        switch (state) {
            case NONE -> {
                changed |= disableFlight(player);
            }

            case GLIDE_T1 -> {
                handleT1Glide(player);
            }

            case BOOST_T2 -> {
                // Placeholder for T1 glide + boost keybind handling
                handleT1Glide(player);
            }

            case FLIGHT_T3 -> {
                changed |= enableCreativeFlightAndSpeed(player);
            }
        }

        return changed;
    }

    private static boolean enableCreativeFlightAndSpeed(Player player) {
        boolean changed = false;

        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            changed = true;
        }

        float desired = (float) WingUtils.getOrInitT3Speed(player);
        float current = player.getAbilities().getFlyingSpeed();

        if (Math.abs(current - desired) > 1.0e-6F) {
            player.getAbilities().setFlyingSpeed(desired);
            changed = true;
        }

        return changed;
    }

    private static boolean disableFlight(Player player) {
        boolean changed = false;

        if (player.getAbilities().mayfly) { player.getAbilities().mayfly = false; changed = true; }
        if (player.getAbilities().flying) { player.getAbilities().flying = false; changed = true; }

        float current = player.getAbilities().getFlyingSpeed();
        if (Math.abs(current - 0.05F) > 1.0e-6F) {
            player.getAbilities().setFlyingSpeed(0.05F);
            changed = true;
        }

        return changed;
    }

    private static void handleT1Glide(Player player) {
        long now = player.level().getGameTime();
        var data = player.getPersistentData();
        long until = data.getLong(WingNBTs.T1_GLIDE_UNTIL);

        if (player.isFallFlying()) {
            data.putLong(WingNBTs.T1_GLIDE_UNTIL, now + 200); // or config
            until = now + 200;
        }

        if (now <= until) {
            if (player.onGround() || player.isInWater() || player.isInLava() || player.isPassenger() || player.getAbilities().flying) {
                data.remove(WingNBTs.T1_GLIDE_UNTIL);
            } else {
                if (!player.isFallFlying()) {
                    if (player.fallDistance < 1.0F) return;
                    if (player.getDeltaMovement().y >= -0.05D) return;
                }
                player.startFallFlying();
            }
        } else {
            data.remove(WingNBTs.T1_GLIDE_UNTIL);
        }
    }
}
