package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.haloabilities.helpers.ClientUnlockState;

import java.util.function.Supplier;

public record UnlockAbilitiesS2CPacket(
        boolean fire,
        boolean fireSacredFlare,
        boolean fireSolCorona,
        boolean firePillarsOfRadiance,

        boolean air,
        boolean airVacuumVortex,
        boolean airZephyrScythes,
        boolean airHeavenlyDowndraft,

        boolean earth,
        boolean earthHolyBastion,
        boolean earthAegisPillar,
        boolean earthCrystalChrysalis,

        boolean water,
        boolean waterMovingFieldOfMist,
        boolean waterSeraphicMirage ,
        boolean waterDivineSerenity,

        boolean space,
        boolean time
) {

    public static void encode(UnlockAbilitiesS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.fire());
        buf.writeBoolean(msg.fireSacredFlare());
        buf.writeBoolean(msg.fireSolCorona());
        buf.writeBoolean(msg.firePillarsOfRadiance());

        buf.writeBoolean(msg.air());
        buf.writeBoolean(msg.airVacuumVortex());
        buf.writeBoolean(msg.airZephyrScythes());
        buf.writeBoolean(msg.airHeavenlyDowndraft());

        buf.writeBoolean(msg.earth());
        buf.writeBoolean(msg.earthHolyBastion());
        buf.writeBoolean(msg.earthAegisPillar());
        buf.writeBoolean(msg.earthCrystalChrysalis());

        buf.writeBoolean(msg.water());
        buf.writeBoolean(msg.waterMovingFieldOfMist());
        buf.writeBoolean(msg.waterSeraphicMirage());
        buf.writeBoolean(msg.waterDivineSerenity());

        buf.writeBoolean(msg.space());
        buf.writeBoolean(msg.time());
    }

    public static UnlockAbilitiesS2CPacket decode(FriendlyByteBuf buf) {
        return new UnlockAbilitiesS2CPacket(
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),

                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),

                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),

                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),

                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public static void handle(UnlockAbilitiesS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientUnlockState.set(
                    msg.fire(),
                    msg.fireSacredFlare(),
                    msg.fireSolCorona(),
                    msg.firePillarsOfRadiance(),

                    msg.air(),
                    msg.airVacuumVortex(),
                    msg.airZephyrScythes(),
                    msg.airHeavenlyDowndraft(),

                    msg.earth(),
                    msg.earthHolyBastion(),
                    msg.earthAegisPillar(),
                    msg.earthCrystalChrysalis(),

                    msg.water(),
                    msg.waterMovingFieldOfMist(),
                    msg.waterSeraphicMirage(),
                    msg.waterDivineSerenity(),

                    msg.space(),
                    msg.time()
            );
        });

        ctx.get().setPacketHandled(true);
    }
}