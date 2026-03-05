package net.normlroyal.descendedangel.flight;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.FlightActiveS2CPacket;
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
            st.active = false;
            FlightSystem.CONTROLLER.onStop(sp, st);
            data.sync(sp);
            return;
        }

        if (sp.onGround()) {
            st.active = false;
            FlightSystem.CONTROLLER.onStop(sp, st);

            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> sp),
                    new FlightActiveS2CPacket(false)
            );

            return;
        }

        if (sp.isCreative() || sp.isSpectator()) {
            st.active = false;
            FlightSystem.CONTROLLER.onStop(sp, st);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                    new FlightActiveS2CPacket(false));
            return;
        }

        FlightInput input = FlightSystem.getInput(sp);
        FlightSystem.CONTROLLER.tick(sp, st, input);

        sp.fallDistance = 0;
        sp.hurtMarked = true;
    }
}
