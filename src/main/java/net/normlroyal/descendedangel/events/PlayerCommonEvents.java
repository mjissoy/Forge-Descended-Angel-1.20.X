package net.normlroyal.descendedangel.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.common.halohierarchy.HaloHierarchyGlowSync;
import net.normlroyal.descendedangel.flight.FlightData;
import net.normlroyal.descendedangel.flight.FlightSystem;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.util.AbilityUtils;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerCommonEvents {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        var oldData = event.getOriginal().getPersistentData();
        var newData = event.getEntity().getPersistentData();

        // Copy every current unlock tag, including evolved powers and new dominions.
        // Duplicate tags are harmless because several abilities share the same unlock family.
        for (HaloAbility ability : HaloAbility.values()) {
            copyBool(oldData, newData, ability.unlockTag());
        }

        if (event.getEntity() instanceof ServerPlayer sp) {
            FlightSystem.clear(sp);
            FlightData.clear(sp);
            AbilityUtils.syncUnlocks(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            // Prevent stale static flight state from a previous server session.
            FlightSystem.clear(sp);
            FlightData.clear(sp);

            AbilityUtils.syncUnlocks(sp);
            HaloHierarchyGlowSync.syncToPlayer(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            FlightSystem.clear(sp);
            FlightData.clear(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            FlightSystem.clear(sp);
            FlightData.clear(sp);
            HaloHierarchyGlowSync.syncToPlayer(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            FlightSystem.stopFlight(sp);
            HaloHierarchyGlowSync.syncToPlayer(sp);
        }
    }

    private static void copyBool(CompoundTag from, CompoundTag to, String key) {
        if (from.contains(key)) {
            to.putBoolean(key, from.getBoolean(key));
        }
    }
}
