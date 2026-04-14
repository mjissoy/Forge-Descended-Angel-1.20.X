package net.normlroyal.descendedangel.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.haloabilities.DominionAbilities;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.haloabilities.PowerAbilities;
import net.normlroyal.descendedangel.haloabilities.helpers.CooldownSnapshots;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.util.HaloUtils;

import java.util.function.Supplier;

public record UseHaloAbilityC2SPacket(int abilityOrdinal) {

    public static void encode(UseHaloAbilityC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.abilityOrdinal());
    }

    public static UseHaloAbilityC2SPacket decode(FriendlyByteBuf buf) {
        return new UseHaloAbilityC2SPacket(buf.readVarInt());
    }

    public static void handle(UseHaloAbilityC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var sp = ctx.get().getSender();
            if (sp == null) return;

            int ord = msg.abilityOrdinal();
            HaloAbility[] values = HaloAbility.values();
            if (ord < 0 || ord >= values.length) return;

            DescendedAngel.LOGGER.info("UseHaloAbilityC2SPacket received for ordinal {}", msg.abilityOrdinal());

            HaloAbility ability = values[ord];

            if (!ability.canUse(sp)) {
                return;
            }

            DescendedAngel.LOGGER.info("Resolved halo ability {}", ability);

            int tier = HaloUtils.getEquippedHaloTier(sp);

            DescendedAngel.LOGGER.info("Equipped halo tier = {}", tier);

            boolean used = false;

            switch (tier) {
                case 4 -> used = PowerAbilities.tryUse(sp, ability);

                case 6 -> used = DominionAbilities.tryUse(sp, ability);

                case 7, 8, 9 -> {
                    boolean p = PowerAbilities.tryUse(sp, ability);
                    boolean d = DominionAbilities.tryUse(sp, ability);
                    used = p || d;
                }
            }

            var snap = CooldownSnapshots.getCooldown(sp, ability);

            DescendedAngel.LOGGER.info("Cooldown snapshot for {}: total={}, until={}, gameTime={}",
                    ability, snap.total(), snap.until(), sp.level().getGameTime());

            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> sp),
                    new AbilityCooldownS2CPacket(ability.ordinal(), snap.until(), snap.total())
            );

            DescendedAngel.LOGGER.info("Sending prayer animation packet for player {}", sp.getGameProfile().getName());

            if (used) {
                DescendedAngel.LOGGER.info("Sending prayer animation (ability succeeded)");

                ModNetwork.sendToTrackingAndSelf(
                        new PlayPrayerAnimS2CPacket(sp.getUUID()),
                        sp
                );
            } else {
                DescendedAngel.LOGGER.info("Ability failed or on cooldown — no animation");
            }

        });

        ctx.get().setPacketHandled(true);
    }

}
