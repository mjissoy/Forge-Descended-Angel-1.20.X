package net.normlroyal.descendedangel.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.normlroyal.descendedangel.haloabilities.DominionAbilities;
import net.normlroyal.descendedangel.haloabilities.PowerAbilities;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.UnlockAbilitiesS2CPacket;

public final class AbilityUtils {
    private AbilityUtils() {}

    public static void syncUnlocks(ServerPlayer sp) {
        var data = sp.getPersistentData();

        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> sp),
                new UnlockAbilitiesS2CPacket(
                        data.getBoolean(PowerAbilities.TAG_FIRE),
                        data.getBoolean(PowerAbilities.TAG_AIR),
                        data.getBoolean(PowerAbilities.TAG_EARTH),
                        data.getBoolean(PowerAbilities.TAG_WATER),
                        data.getBoolean(DominionAbilities.TAG_SPACE),
                        data.getBoolean(DominionAbilities.TAG_TIME)
                )
        );
    }

}
