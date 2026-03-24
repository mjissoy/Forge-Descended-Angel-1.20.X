package net.normlroyal.descendedangel.flight;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.util.WingLogic;
import net.normlroyal.descendedangel.util.WingUtils;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FlightTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (!(e.player instanceof ServerPlayer sp)) return;

        IFlightData data = FlightData.get(sp);
        FlightState st = data.state();
        if (!st.active) return;

        var wings = WingUtils.getEquippedWings(sp);
        if (wings.isEmpty() || !WingLogic.allowsCustomFlight(wings)) {
            FlightSystem.stopFlight(sp);
            return;
        }

        if (sp.onGround()) {
            FlightSystem.stopFlight(sp);
            return;
        }

        if (sp.isCreative() || sp.isSpectator()) {
            FlightSystem.stopFlight(sp);
            return;
        }

        FlightInput input = FlightSystem.getInput(sp);
        FlightSystem.CONTROLLER.tick(sp, st, input);

        sp.fallDistance = 0;
        sp.hurtMarked = true;
    }
}
