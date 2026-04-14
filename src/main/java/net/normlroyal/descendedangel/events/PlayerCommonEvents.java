package net.normlroyal.descendedangel.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.config.ModGameRules;
import net.normlroyal.descendedangel.haloabilities.DominionAbilities;
import net.normlroyal.descendedangel.haloabilities.PowerAbilities;
import net.normlroyal.descendedangel.halohierarchy.HaloHierarchyGlowSync;
import net.normlroyal.descendedangel.util.AbilityUtils;


@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerCommonEvents {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        var oldData = event.getOriginal().getPersistentData();
        var newData = event.getEntity().getPersistentData();

        copyBool(oldData, newData, PowerAbilities.TAG_FIRE);
        copyBool(oldData, newData, PowerAbilities.TAG_AIR);
        copyBool(oldData, newData, PowerAbilities.TAG_EARTH);
        copyBool(oldData, newData, PowerAbilities.TAG_WATER);

        copyBool(oldData, newData, DominionAbilities.TAG_SPACE);
        copyBool(oldData, newData, DominionAbilities.TAG_TIME);

        if (event.getEntity() instanceof ServerPlayer sp) {
            AbilityUtils.syncUnlocks(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            AbilityUtils.syncUnlocks(sp);
            HaloHierarchyGlowSync.syncToPlayer(sp);
        }
    }

    private static void copyBool(CompoundTag from, CompoundTag to, String key) {
        if (from.contains(key)) {
            to.putBoolean(key, from.getBoolean(key));
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            HaloHierarchyGlowSync.syncToPlayer(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            HaloHierarchyGlowSync.syncToPlayer(sp);
        }
    }

}