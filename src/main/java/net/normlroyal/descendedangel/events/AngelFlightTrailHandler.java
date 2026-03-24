package net.normlroyal.descendedangel.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.flight.ClientFlightState;
import net.normlroyal.descendedangel.particle.ModParticles;
import net.normlroyal.descendedangel.util.WingLogic;
import net.normlroyal.descendedangel.util.WingUtils;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, value = Dist.CLIENT)
public class AngelFlightTrailHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;

        if (player == null || level == null) return;
        if (!ClientFlightState.isActive()) return;

        ItemStack wings = WingUtils.getEquippedWings(player);
        if (wings.isEmpty() || !WingLogic.allowsCustomFlight(wings)) return;

        if (player.isSpectator() || player.isPassenger()) return;

        Vec3 velocity = player.getDeltaMovement();
        if (velocity.lengthSqr() < 0.01) return;

        if (level.random.nextFloat() > 0.45f) return;

        int count = 1 + level.random.nextInt(2);

        for (int i = 0; i < count; i++) {
            double x = player.getX() + (level.random.nextDouble() - 0.5) * 1.1;
            double y = player.getY() + 0.8 + (level.random.nextDouble() - 0.5) * 1.2;
            double z = player.getZ() + (level.random.nextDouble() - 0.5) * 1.1;

            double xd = -velocity.x * 0.12 + (level.random.nextDouble() - 0.5) * 0.02;
            double yd = -0.01 + level.random.nextDouble() * 0.02;
            double zd = -velocity.z * 0.12 + (level.random.nextDouble() - 0.5) * 0.02;

            level.addParticle(ModParticles.ANGEL_FLIGHT.get(), x, y, z, xd, yd, zd);
        }
    }
}