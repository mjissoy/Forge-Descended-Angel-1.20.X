package net.normlroyal.descendedangel.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.haloabilities.DominionAbilities;
import net.normlroyal.descendedangel.haloabilities.PowerAbilities;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerCloneEvents {

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

    }

    private static void copyBool(CompoundTag from, CompoundTag to, String key) {
        if (from.contains(key)) {
            to.putBoolean(key, from.getBoolean(key));
        }
    }

}
