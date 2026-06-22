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
                        data.getBoolean(PowerAbilities.TAG_FIRE_SACRED_FLARE),
                        data.getBoolean(PowerAbilities.TAG_FIRE_SOL_CORONA),
                        data.getBoolean(PowerAbilities.TAG_FIRE_PILLARS_OF_RADIANCE),

                        data.getBoolean(PowerAbilities.TAG_AIR),
                        data.getBoolean(PowerAbilities.TAG_AIR_VACUUM_VORTEX),
                        data.getBoolean(PowerAbilities.TAG_AIR_ZEPHYR_SCYTHES),
                        data.getBoolean(PowerAbilities.TAG_AIR_HEAVENLY_DOWNDRAFT),

                        data.getBoolean(PowerAbilities.TAG_EARTH),
                        data.getBoolean(PowerAbilities.TAG_EARTH_HOLY_BASTION),
                        data.getBoolean(PowerAbilities.TAG_EARTH_AEGIS_PILLAR),
                        data.getBoolean(PowerAbilities.TAG_EARTH_CRYSTAL_CHRYSALIS),

                        data.getBoolean(PowerAbilities.TAG_WATER),
                        data.getBoolean(PowerAbilities.TAG_WATER_MOVING_FIELD_OF_MIST),
                        data.getBoolean(PowerAbilities.TAG_WATER_SERAPHIC_MIRAGE),
                        data.getBoolean(PowerAbilities.TAG_WATER_DIVINE_SERENITY),

                        data.getBoolean(DominionAbilities.TAG_SPACE),
                        data.getBoolean(DominionAbilities.TAG_TIME)
                )
        );
    }
}